package org.jboss.pressgang.ccms.contentspec.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.pressgang.ccms.contentspec.Appendix;
import org.jboss.pressgang.ccms.contentspec.Chapter;
import org.jboss.pressgang.ccms.contentspec.Comment;
import org.jboss.pressgang.ccms.contentspec.ContentSpec;
import org.jboss.pressgang.ccms.contentspec.File;
import org.jboss.pressgang.ccms.contentspec.FileList;
import org.jboss.pressgang.ccms.contentspec.InitialContent;
import org.jboss.pressgang.ccms.contentspec.KeyValueNode;
import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.Node;
import org.jboss.pressgang.ccms.contentspec.Part;
import org.jboss.pressgang.ccms.contentspec.Preface;
import org.jboss.pressgang.ccms.contentspec.Process;
import org.jboss.pressgang.ccms.contentspec.Section;
import org.jboss.pressgang.ccms.contentspec.SpecNodeWithRelationships;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.TextNode;
import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;
import org.jboss.pressgang.ccms.contentspec.entities.InjectionOptions;
import org.jboss.pressgang.ccms.contentspec.enums.BookType;
import org.jboss.pressgang.ccms.contentspec.enums.LevelType;
import org.jboss.pressgang.ccms.contentspec.enums.RelationshipType;
import org.jboss.pressgang.ccms.contentspec.enums.TopicType;
import org.jboss.pressgang.ccms.contentspec.sort.CSNodeSorter;
import org.jboss.pressgang.ccms.contentspec.sort.CSRelatedNodeSorter;
import org.jboss.pressgang.ccms.contentspec.sort.EntityWrapperIDComparator;
import org.jboss.pressgang.ccms.provider.DataProviderFactory;
import org.jboss.pressgang.ccms.provider.ServerSettingsProvider;
import org.jboss.pressgang.ccms.provider.TopicProvider;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.CSRelatedNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.ContentSpecWrapper;
import org.jboss.pressgang.ccms.wrapper.TagWrapper;

public class CSTransformer {

    /**
     * A List of lower case metadata properties that should be ignored during transformation because they exist else where.
     */
    private static final List<String> IGNORE_META_DATA = Arrays.asList(CommonConstants.CS_ID_TITLE.toLowerCase(),
            CommonConstants.CS_CHECKSUM_TITLE.toLowerCase());

    /**
     * Transforms a content spec datasource entity into a generic content spec object.
     *
     * @param spec            The content spec entity to be transformed.
     * @param providerFactory
     * @return The generic Content Spec object that was transformed from the entity.
     */
    public static ContentSpec transform(final ContentSpecWrapper spec, final DataProviderFactory providerFactory,
            final boolean includeChecksum) {
        // local variables that are used to map transformed content
        Map<Integer, Node> nodes = new HashMap<Integer, Node>();
        Map<String, SpecTopic> topicTargets = new HashMap<String, SpecTopic>();
        List<CSNodeWrapper> relationshipFromNodes = new ArrayList<CSNodeWrapper>();
        List<Process> processes = new ArrayList<Process>();

        // Start the transformation
        final ContentSpec contentSpec = new ContentSpec();

        contentSpec.setId(spec.getId());
        transformGlobalOptions(spec, contentSpec);

        // Add all the levels/topics
        if (spec.getChildren() != null) {
            final List<CSNodeWrapper> childNodes = spec.getChildren().getItems();
            final HashMap<CSNodeWrapper, Node> levelNodes = new HashMap<CSNodeWrapper, Node>();
            for (final CSNodeWrapper childNode : childNodes) {
                if (childNode.getNodeType() == CommonConstants.CS_NODE_TOPIC) {
                    final SpecTopic topic = transformSpecTopic(childNode, nodes, topicTargets, relationshipFromNodes);
                    levelNodes.put(childNode, topic);
                } else if (childNode.getNodeType() == CommonConstants.CS_NODE_COMMENT) {
                    final Comment comment = transformComment(childNode);
                    levelNodes.put(childNode, comment);
                } else if (childNode.getNodeType() == CommonConstants.CS_NODE_META_DATA || childNode.getNodeType() == CommonConstants
                        .CS_NODE_META_DATA_TOPIC) {
                    if (!IGNORE_META_DATA.contains(childNode.getTitle().toLowerCase())) {
                        final KeyValueNode<?> metaDataNode = transformMetaData(childNode, nodes, topicTargets, relationshipFromNodes);
                        levelNodes.put(childNode, metaDataNode);
                    }
                } else {
                    final Level level = transformLevel(childNode, nodes, topicTargets, relationshipFromNodes, processes);
                    levelNodes.put(childNode, level);
                }
            }

            // Sort the level nodes so that they are in the right order based on next/prev values.
            final LinkedHashMap<CSNodeWrapper, Node> sortedMap = CSNodeSorter.sortMap(levelNodes);

            // Add the child nodes to the content spec now that they are in the right order.
            boolean addToBaseLevel = false;
            final Iterator<Map.Entry<CSNodeWrapper, Node>> iter = sortedMap.entrySet().iterator();
            while (iter.hasNext()) {
                final Map.Entry<CSNodeWrapper, Node> entry = iter.next();

                // If a level or spec topic is found then start adding to the base level instead of the content spec
                if ((entry.getValue() instanceof Level || entry.getValue() instanceof SpecTopic) && !addToBaseLevel) {
                    addToBaseLevel = true;
                    // Add a space between the base metadata and optional metadata
                    contentSpec.appendChild(new TextNode("\n"));
                }

                // Add the node to the right component.
                if (addToBaseLevel) {
                    contentSpec.getBaseLevel().appendChild(entry.getValue());
                    // Add a new line to separate chapters/parts
                    if (isNodeASeparatorLevel(entry.getValue()) && iter.hasNext()) {
                        contentSpec.getBaseLevel().appendChild(new TextNode("\n"));
                    }
                } else {
                    contentSpec.appendChild(entry.getValue());
                }
            }
        }

        // Apply the relationships to the nodes
        applyRelationships(contentSpec, nodes, topicTargets, relationshipFromNodes, processes, providerFactory);

        // Set the line numbers
        setLineNumbers(contentSpec, includeChecksum ? 2 : 1);

        return contentSpec;
    }

    private static int setLineNumbers(final Node node, int current) {
        if (node instanceof ContentSpec) {
            for (final Node childNode : ((ContentSpec) node).getNodes()) {
                current = setLineNumbers(childNode, current);
            }

            final Level baseLevel = ((ContentSpec) node).getBaseLevel();
            if (!baseLevel.getTags(false).isEmpty() || baseLevel.getConditionStatement() != null) {
                current++;
            }

            for (final Node childNode : ((ContentSpec) node).getChildNodes()) {
                current = setLineNumbers(childNode, current);
            }

            return current;
        } else {
            node.setLineNumber(current);
            current++;

            if (node instanceof Level) {
                for (final Node childNode : ((Level) node).getChildNodes()) {
                    current = setLineNumbers(childNode, current);
                }
            } else if (node instanceof FileList) {
                final List<File> files = ((FileList) node).getValue();
                current += files == null || files.isEmpty() ? 0 : (files.size() - 1);
            } else if (node instanceof KeyValueNode) {
                if (((KeyValueNode) node).getKey().equals(CommonConstants.CS_PUBLICAN_CFG_TITLE)) {
                    final String publicanCfg = (String) ((KeyValueNode) node).getValue();
                    current += StringUtils.countMatches(publicanCfg, "\n");
                }
            } else if (node instanceof SpecTopic && !((SpecTopic) node).getRelationships().isEmpty()) {
                int numPrereqs = ((SpecTopic) node).getPrerequisiteRelationships().size();
                int numReferTo = ((SpecTopic) node).getRelatedRelationships().size();
                int numLinkList = ((SpecTopic) node).getLinkListRelationships().size();

                if (numPrereqs > 0) {
                    current += numPrereqs + 1;
                }
                if (numReferTo > 0) {
                    current += numReferTo + 1;
                }
                if (numLinkList > 0) {
                    current += numLinkList + 1;
                }
            }

            return current;
        }
    }

    private static void transformGlobalOptions(final ContentSpecWrapper spec, final ContentSpec contentSpec) {
        if (spec.getCondition() != null) {
            contentSpec.getBaseLevel().setConditionStatement(spec.getCondition());
        }

        // Add all of the book tags
        if (spec.getBookTags() != null && spec.getBookTags().getItems() != null) {
            final List<String> tags = new ArrayList<String>();
            final List<TagWrapper> tagItems = spec.getBookTags().getItems();

            // Sort the tags to make sure they always appear the same
            Collections.sort(tagItems, new EntityWrapperIDComparator());

            // Add the tags
            for (final TagWrapper tag : tagItems) {
                tags.add(tag.getName());
            }
            contentSpec.setTags(tags);
        }
    }

    /**
     * Transforms a MetaData CSNode into a KeyValuePair that can be added to a ContentSpec object.
     *
     * @param node                  The CSNode to be transformed.
     * @param nodes                 A mapping of node entity ids to their transformed counterparts.
     * @param targetTopics          A mapping of target ids to SpecTopics.
     * @param relationshipFromNodes A list of CSNode entities that have relationships.
     * @return The transformed KeyValuePair object.
     */
    protected static KeyValueNode<?> transformMetaData(final CSNodeWrapper node, final Map<Integer, Node> nodes,
            final Map<String, SpecTopic> targetTopics, final List<CSNodeWrapper> relationshipFromNodes) {
        final KeyValueNode<?> keyValueNode;
        if (node.getTitle().equalsIgnoreCase(CommonConstants.CS_BOOK_TYPE_TITLE)) {
            keyValueNode = new KeyValueNode<BookType>(node.getTitle(), BookType.getBookType(node.getAdditionalText()));
        } else if (node.getTitle().equalsIgnoreCase(CommonConstants.CS_INLINE_INJECTION_TITLE)) {
            keyValueNode = new KeyValueNode<InjectionOptions>(node.getTitle(), new InjectionOptions(node.getAdditionalText()));
        } else if (node.getNodeType().equals(CommonConstants.CS_NODE_META_DATA_TOPIC)) {
            final SpecTopic specTopic = transformSpecTopicWithoutTypeCheck(node, nodes, targetTopics, relationshipFromNodes);
            keyValueNode = new KeyValueNode<SpecTopic>(node.getTitle(), specTopic);
        } else if (node.getTitle().equalsIgnoreCase(CommonConstants.CS_FILE_SHORT_TITLE) || node.getTitle().equalsIgnoreCase(
                CommonConstants.CS_FILE_TITLE)) {
            keyValueNode = transformFileList(node);
        } else {
            keyValueNode = new KeyValueNode<String>(node.getTitle(), node.getAdditionalText());
        }
        keyValueNode.setUniqueId(node.getId() == null ? null : node.getId().toString());

        return keyValueNode;
    }

    /**
     * Transforms a MetaData CSNode into a FileList that can be added to a ContentSpec object.
     *
     * @param node The CSNode to be transformed.
     * @return The transformed FileList object.
     */
    protected static FileList transformFileList(final CSNodeWrapper node) {
        final List<File> files = new LinkedList<File>();

        // Add all the child files
        if (node.getChildren() != null && node.getChildren().getItems() != null) {
            final List<CSNodeWrapper> childNodes = node.getChildren().getItems();
            final HashMap<CSNodeWrapper, File> fileNodes = new HashMap<CSNodeWrapper, File>();
            for (final CSNodeWrapper childNode : childNodes) {
                fileNodes.put(childNode, transformFile(childNode));
            }

            // Sort the file list nodes so that they are in the right order based on next/prev values.
            final LinkedHashMap<CSNodeWrapper, File> sortedMap = CSNodeSorter.sortMap(fileNodes);

            // Add the child nodes to the file list now that they are in the right order.
            final Iterator<Map.Entry<CSNodeWrapper, File>> iter = sortedMap.entrySet().iterator();
            while (iter.hasNext()) {
                final Map.Entry<CSNodeWrapper, File> entry = iter.next();
                files.add(entry.getValue());
            }
        }

        return new FileList(CommonConstants.CS_FILE_TITLE, files);
    }

    private static File transformFile(final CSNodeWrapper node) {
        final File file = new File(node.getTitle(), node.getEntityId());

        // Basic data
        file.setRevision(node.getEntityRevision());
        file.setUniqueId(node.getId() == null ? null : node.getId().toString());

        return file;
    }

    /**
     * Transform a Level CSNode entity object into a Level Object that can be added to a Content Specification.
     *
     * @param node                  The CSNode entity object to be transformed.
     * @param nodes                 A mapping of node entity ids to their transformed counterparts.
     * @param targetTopics          A mapping of target ids to SpecTopics.
     * @param relationshipFromNodes A list of CSNode entities that have relationships.
     * @param processes
     * @return The transformed level entity.
     */
    protected static Level transformLevel(final CSNodeWrapper node, final Map<Integer, Node> nodes,
            final Map<String, SpecTopic> targetTopics, final List<CSNodeWrapper> relationshipFromNodes, List<Process> processes) {
        final Level level;
        if (node.getNodeType() == CommonConstants.CS_NODE_APPENDIX) {
            level = new Appendix(node.getTitle());
        } else if (node.getNodeType() == CommonConstants.CS_NODE_CHAPTER) {
            level = new Chapter(node.getTitle());
        } else if (node.getNodeType() == CommonConstants.CS_NODE_PART) {
            level = new Part(node.getTitle());
        } else if (node.getNodeType() == CommonConstants.CS_NODE_PROCESS) {
            level = new Process(node.getTitle());
        } else if (node.getNodeType() == CommonConstants.CS_NODE_SECTION) {
            level = new Section(node.getTitle());
        } else if (node.getNodeType() == CommonConstants.CS_NODE_PREFACE) {
            level = new Preface(node.getTitle());
        } else if (node.getNodeType() == CommonConstants.CS_NODE_INITIAL_CONTENT) {
            level = new InitialContent();
        } else {
            throw new IllegalArgumentException("The passed node is not a Level");
        }

        level.setConditionStatement(node.getCondition());
        level.setTargetId(node.getTargetId());
        level.setUniqueId(node.getId() == null ? null : node.getId().toString());

        if (node.getRelatedToNodes() != null && node.getRelatedToNodes().getItems() != null && !node.getRelatedToNodes().getItems()
                .isEmpty()) {
            relationshipFromNodes.add(node);
        }

        // Add all the levels/topics
        if (node.getChildren() != null && node.getChildren().getItems() != null) {
            final List<CSNodeWrapper> childNodes = node.getChildren().getItems();
            final HashMap<CSNodeWrapper, Node> levelNodes = new HashMap<CSNodeWrapper, Node>();
            final HashMap<CSNodeWrapper, SpecTopic> initialContentNodes = new HashMap<CSNodeWrapper, SpecTopic>();
            for (final CSNodeWrapper childNode : childNodes) {
                if (childNode.getNodeType() == CommonConstants.CS_NODE_TOPIC) {
                    final SpecTopic topic = transformSpecTopic(childNode, nodes, targetTopics, relationshipFromNodes);
                    levelNodes.put(childNode, topic);
                } else if (childNode.getNodeType() == CommonConstants.CS_NODE_COMMENT) {
                    final Comment comment = transformComment(childNode);
                    levelNodes.put(childNode, comment);
                } else if (childNode.getNodeType() == CommonConstants.CS_NODE_INITIAL_CONTENT_TOPIC) {
                    final SpecTopic initialContentTopic = transformSpecTopicWithoutTypeCheck(childNode, nodes, targetTopics,
                            relationshipFromNodes);
                    if (level instanceof InitialContent) {
                        levelNodes.put(childNode, initialContentTopic);
                    } else {
                        initialContentNodes.put(childNode, initialContentTopic);
                    }
                } else {
                    final Level childLevel = transformLevel(childNode, nodes, targetTopics, relationshipFromNodes, processes);
                    levelNodes.put(childNode, childLevel);
                }
            }

            // Sort the level nodes so that they are in the right order based on next/prev values.
            final LinkedHashMap<CSNodeWrapper, Node> sortedMap = CSNodeSorter.sortMap(levelNodes);

            // PressGang 1.4+ stores the initial content inside it's own container, instead of on the level
            if (!(level instanceof InitialContent) && !initialContentNodes.isEmpty()) {
                final LinkedHashMap<CSNodeWrapper, SpecTopic> sortedInitialContentMap = CSNodeSorter.sortMap(initialContentNodes);

                final InitialContent initialContent = new InitialContent();
                level.appendChild(initialContent);

                // Add the initial content topics to the level now that they are in the right order.
                final Iterator<Map.Entry<CSNodeWrapper, SpecTopic>> frontMatterIter = sortedInitialContentMap.entrySet().iterator();
                while (frontMatterIter.hasNext()) {
                    final Map.Entry<CSNodeWrapper, SpecTopic> entry = frontMatterIter.next();
                    initialContent.appendSpecTopic(entry.getValue());
                }
            }

            // Add the child nodes to the level now that they are in the right order.
            final Iterator<Map.Entry<CSNodeWrapper, Node>> iter = sortedMap.entrySet().iterator();
            while (iter.hasNext()) {
                final Map.Entry<CSNodeWrapper, Node> entry = iter.next();

                level.appendChild(entry.getValue());
                // Add a new line to separate chapters/parts
                if (isNodeASeparatorLevel(entry.getValue()) && iter.hasNext()) {
                    level.appendChild(new TextNode("\n"));
                }
            }
        }

        // Add the node to the list of processed nodes so that the relationships can be added once everything is processed
        nodes.put(node.getId(), level);

        // We need to keep track of processes to process their relationships
        if (level instanceof Process) {
            processes.add((Process) level);
        }

        return level;
    }

    /**
     * Transform a Topic CSNode entity object into a SpecTopic Object that can be added to a Content Specification.
     *
     * @param node                  The CSNode entity object to be transformed.
     * @param nodes                 A mapping of node entity ids to their transformed counterparts.
     * @param targetTopics          A mapping of target ids to SpecTopics.
     * @param relationshipFromNodes A list of CSNode entities that have relationships.
     * @return The transformed SpecTopic entity.
     */
    protected static SpecTopic transformSpecTopic(final CSNodeWrapper node, final Map<Integer, Node> nodes,
            final Map<String, SpecTopic> targetTopics, final List<CSNodeWrapper> relationshipFromNodes) {
        if (node.getNodeType() != CommonConstants.CS_NODE_TOPIC) {
            throw new IllegalArgumentException("The passed node is not a Spec Topic");
        }

        return transformSpecTopicWithoutTypeCheck(node, nodes, targetTopics, relationshipFromNodes);
    }

    /**
     * Transform a Topic CSNode entity object into a SpecTopic Object that can be added to a Content Specification.
     *
     * @param node                  The CSNode entity object to be transformed.
     * @param nodes                 A mapping of node entity ids to their transformed counterparts.
     * @param targetTopics          A mapping of target ids to SpecTopics.
     * @param relationshipFromNodes A list of CSNode entities that have relationships.
     * @return The transformed SpecTopic entity.
     */
    private static SpecTopic transformSpecTopicWithoutTypeCheck(final CSNodeWrapper node, final Map<Integer, Node> nodes,
            final Map<String, SpecTopic> targetTopics, final List<CSNodeWrapper> relationshipFromNodes) {
        final SpecTopic specTopic = new SpecTopic(node.getEntityId(), node.getTitle());

        // Basic data
        specTopic.setRevision(node.getEntityRevision());
        specTopic.setConditionStatement(node.getCondition());
        specTopic.setTargetId(node.getTargetId());
        specTopic.setUniqueId(node.getId() == null ? null : node.getId().toString());

        if (node.getRelatedToNodes() != null && node.getRelatedToNodes().getItems() != null && !node.getRelatedToNodes().getItems()
                .isEmpty()) {
            relationshipFromNodes.add(node);
        }

        // Add the node to the list of processed nodes so that the relationships can be added once everything is processed
        nodes.put(node.getId(), specTopic);

        // If there is a target add it to the list
        if (node.getTargetId() != null) {
            targetTopics.put(node.getTargetId(), specTopic);
        }

        return specTopic;
    }

    /**
     * Transform a Comment CSNode entity into a Comment object that can be added to a Content Specification.
     *
     * @param node The CSNode to be transformed.
     * @return The transformed Comment object.
     */
    protected static Comment transformComment(CSNodeWrapper node) {
        final Comment comment;
        if (node.getNodeType() == CommonConstants.CS_NODE_COMMENT) {
            comment = new Comment(node.getTitle());
        } else {
            throw new IllegalArgumentException("The passed node is not a Comment");
        }

        comment.setUniqueId(node.getId() == null ? null : node.getId().toString());

        return comment;
    }

    /**
     * Apply the relationships to all of the nodes in the content spec. This should be the last step since all nodes have to be converted
     * to levels and topics before this method can work.
     */
    protected static void applyRelationships(final ContentSpec contentSpec, final Map<Integer, Node> nodes,
            final Map<String, SpecTopic> targetTopics, final List<CSNodeWrapper> relationshipFromNodes, final List<Process> processes,
            final DataProviderFactory providerFactory) {
        // Apply the user defined relationships stored in the database
        for (final CSNodeWrapper node : relationshipFromNodes) {
            boolean initialContentTopic = node.getNodeType() == CommonConstants.CS_NODE_INITIAL_CONTENT_TOPIC;
            boolean level = EntityUtilities.isNodeALevel(node);

            // In 1.3 or lower initial content relationships were stored on the topic, however in 1.4+ they are now on the initial
            // content level, so do the migration here
            final SpecNodeWithRelationships fromNode;
            if (initialContentTopic) {
                final CSNodeWrapper parentNode = node.getParent();
                final SpecNodeWithRelationships parent = (SpecNodeWithRelationships) nodes.get(parentNode.getId());
                if (parent instanceof InitialContent) {
                    fromNode = parent;
                } else {
                    final Level parentLevel = (Level) parent;
                    if (parentLevel.getFirstSpecNode() instanceof InitialContent) {
                        fromNode = (SpecNodeWithRelationships) parentLevel.getFirstSpecNode();
                    } else {
                        fromNode = new InitialContent(parent.getLineNumber(), CSConstants.LEVEL_INITIAL_CONTENT + ":");
                        if (parentLevel.getChildNodes().isEmpty()) {
                            parentLevel.appendChild(fromNode);
                        } else {
                            parentLevel.insertBefore(fromNode, parentLevel.getChildNodes().get(0));
                        }
                    }
                }
            } else {
                fromNode = (SpecNodeWithRelationships) nodes.get(node.getId());
            }

            // Check if we have any relationships to process
            if (node.getRelatedToNodes() == null || node.getRelatedToNodes().isEmpty()) continue;
            final List<CSRelatedNodeWrapper> relatedToNodes = node.getRelatedToNodes().getItems();

            // Sort the relationships into the correct order based on the sort variable
            Collections.sort(relatedToNodes, new CSRelatedNodeSorter());

            // Add the relationships to the topic
            for (final CSRelatedNodeWrapper relatedToNode : relatedToNodes) {
                final Node toNode = nodes.get(relatedToNode.getId());
                if (toNode == null) {
                    throw new IllegalStateException("The related node does not exist in the content specification");
                } else if (toNode instanceof Level) {
                    // Relationships to levels
                    final Level toLevel = (Level) toNode;

                    // Ensure that the level has a target id if not create one.
                    if (toLevel.getTargetId() == null) {
                        toLevel.setTargetId("T00" + relatedToNode.getId());
                    }

                    // Add the relationship
                    fromNode.addRelationshipToTarget(toLevel, RelationshipType.getRelationshipType(relatedToNode.getRelationshipType()),
                            initialContentTopic || level ? null : toLevel.getTitle());
                } else {
                    // Relationships to topics
                    final SpecTopic toSpecTopic = (SpecTopic) toNode;
                    final String title = TopicType.INITIAL_CONTENT.equals(
                            toSpecTopic.getTopicType()) && toSpecTopic.getParent() instanceof Level ? ((Level) toSpecTopic.getParent())
                            .getTitle() : toSpecTopic.getTitle();

                    // Add the relationship
                    if (relatedToNode.getRelationshipMode().equals(CommonConstants.CS_RELATIONSHIP_MODE_TARGET)) {
                        fromNode.addRelationshipToTarget(toSpecTopic,
                                RelationshipType.getRelationshipType(relatedToNode.getRelationshipType()),
                                initialContentTopic || level ? null : title);
                    } else {
                        fromNode.addRelationshipToTopic(toSpecTopic,
                                RelationshipType.getRelationshipType(relatedToNode.getRelationshipType()),
                                initialContentTopic || level ? null : title);
                    }
                }
            }
        }

        // Create the unique id map
        final Map<String, SpecTopic> uniqueIdSpecTopicMap = ContentSpecUtilities.getUniqueIdSpecTopicMap(contentSpec);

        // Apply the process relationships
        for (final Process process : processes) {
            process.processTopics(uniqueIdSpecTopicMap, targetTopics, providerFactory.getProvider(TopicProvider.class),
                    providerFactory.getProvider(ServerSettingsProvider.class));
        }
    }

    protected static boolean isNodeASeparatorLevel(final Node node) {
        if (node instanceof Chapter || node instanceof Part || node instanceof Appendix || node instanceof Preface) {
            return true;
        } else if (node instanceof Process) {
            final Process process = (Process) node;
            return process.getParent() != null && (process.getParent().getLevelType() == LevelType.BASE || process.getParent()
                    .getLevelType() == LevelType.PART);
        } else {
            return false;
        }
    }
}