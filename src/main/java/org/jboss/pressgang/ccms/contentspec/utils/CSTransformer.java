package org.jboss.pressgang.ccms.contentspec.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.pressgang.ccms.contentspec.Appendix;
import org.jboss.pressgang.ccms.contentspec.Chapter;
import org.jboss.pressgang.ccms.contentspec.Comment;
import org.jboss.pressgang.ccms.contentspec.ContentSpec;
import org.jboss.pressgang.ccms.contentspec.KeyValueNode;
import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.Node;
import org.jboss.pressgang.ccms.contentspec.Part;
import org.jboss.pressgang.ccms.contentspec.Process;
import org.jboss.pressgang.ccms.contentspec.Section;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.TextNode;
import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;
import org.jboss.pressgang.ccms.contentspec.entities.InjectionOptions;
import org.jboss.pressgang.ccms.contentspec.enums.BookType;
import org.jboss.pressgang.ccms.contentspec.enums.RelationshipType;
import org.jboss.pressgang.ccms.contentspec.sort.CSNodeSorter;
import org.jboss.pressgang.ccms.contentspec.sort.CSRelatedNodeSorter;
import org.jboss.pressgang.ccms.provider.DataProviderFactory;
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
    private static final List<String> IGNORE_META_DATA = Arrays.asList(CSConstants.ID_TITLE.toLowerCase(),
            CSConstants.CHECKSUM_TITLE.toLowerCase());

    /**
     * Transforms a content spec datasource entity into a generic content spec object.
     *
     * @param spec            The content spec entity to be transformed.
     * @param providerFactory
     * @return The generic Content Spec object that was transformed from the entity.
     */
    public static ContentSpec transform(final ContentSpecWrapper spec, final DataProviderFactory providerFactory) {
        // local variables that are used to map transformed content
        Map<Integer, Node> nodes = new HashMap<Integer, Node>();
        Map<String, List<SpecTopic>> specTopicMap = new HashMap<String, List<SpecTopic>>();
        Map<String, SpecTopic> topicTargets = new HashMap<String, SpecTopic>();
        List<CSNodeWrapper> relationshipFromNodes = new ArrayList<CSNodeWrapper>();
        List<Process> processes = new ArrayList<Process>();

        // Start the transformation
        final ContentSpec contentSpec = new ContentSpec();

        contentSpec.setId(spec.getId());
        if (spec.getCondition() != null) {
            contentSpec.getBaseLevel().setConditionStatement(spec.getCondition());
        }

        // Add all of the tags
        if (spec.getTags() != null && spec.getTags().getItems() != null) {
            final List<String> tags = new ArrayList<String>();
            for (final TagWrapper tag : spec.getTags().getItems()) {
                tags.add(tag.getName());
            }
            contentSpec.setTags(tags);
        }

        // Add all the levels/topics
        if (spec.getChildren() != null) {
            final List<CSNodeWrapper> childNodes = spec.getChildren().getItems();
            final HashMap<CSNodeWrapper, Node> levelNodes = new HashMap<CSNodeWrapper, Node>();
            for (final CSNodeWrapper childNode : childNodes) {
                if (childNode.getNodeType() == CommonConstants.CS_NODE_TOPIC) {
                    final SpecTopic topic = transformSpecTopic(childNode, nodes, specTopicMap, topicTargets, relationshipFromNodes);
                    levelNodes.put(childNode, topic);
                } else if (childNode.getNodeType() == CommonConstants.CS_NODE_COMMENT) {
                    final Comment comment = transformComment(childNode);
                    levelNodes.put(childNode, comment);
                } else if (childNode.getNodeType() == CommonConstants.CS_NODE_META_DATA) {
                    if (!IGNORE_META_DATA.contains(childNode.getTitle().toLowerCase())) {
                        final KeyValueNode<?> metaDataNode = transformMetaData(childNode, nodes, specTopicMap, topicTargets,
                                relationshipFromNodes);
                        levelNodes.put(childNode, metaDataNode);
                    }
                } else {
                    final Level level = transformLevel(childNode, nodes, specTopicMap, topicTargets, relationshipFromNodes);
                    levelNodes.put(childNode, level);

                    // We need to keep track of processes to process their relationships
                    if (level instanceof Process) {
                        processes.add((Process) level);
                    }
                }
            }

            // Sort the level nodes so that they are in the right order based on next/prev values.
            final LinkedHashMap<CSNodeWrapper, Node> sortedMap = CSNodeSorter.sortMap(levelNodes);

            // Add the child nodes to the content spec now that they are in the right order.
            boolean addToBaseLevel = false;
            for (final Map.Entry<CSNodeWrapper, Node> entry : sortedMap.entrySet()) {
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
                    if (entry.getValue() instanceof Chapter || entry.getValue() instanceof Part) {
                        contentSpec.getBaseLevel().appendChild(new TextNode("\n"));
                    }
                } else {
                    contentSpec.appendChild(entry.getValue());
                }
            }
        }

        // Apply the relationships to the nodes
        applyRelationships(contentSpec, nodes, specTopicMap, topicTargets, relationshipFromNodes, processes, providerFactory);

        return contentSpec;
    }

    /**
     * Transforms a MetaData CSNode into a KeyValuePair that can be added to a ContentSpec object.
     *
     * @param node                  The CSNode to be transformed.
     * @param nodes                 A mapping of node entity ids to their transformed counterparts.
     * @param specTopicMap          A mapping of Topic Entity ids to their transformed SpecTopics.
     * @param targetTopics          A mapping of target ids to SpecTopics.
     * @param relationshipFromNodes A list of CSNode entities that have relationships.
     * @return The transformed KeyValuePair object.
     */
    protected static KeyValueNode<?> transformMetaData(final CSNodeWrapper node, final Map<Integer, Node> nodes,
            final Map<String, List<SpecTopic>> specTopicMap, final Map<String, SpecTopic> targetTopics,
            final List<CSNodeWrapper> relationshipFromNodes) {
        final KeyValueNode<?> keyValueNode;
        if (node.getTitle().equalsIgnoreCase(CSConstants.BOOK_TYPE_TITLE)) {
            keyValueNode = new KeyValueNode<BookType>(node.getTitle(), BookType.getBookType(node.getAdditionalText()));
        } else if (node.getTitle().equalsIgnoreCase(CSConstants.INLINE_INJECTION_TITLE)) {
            keyValueNode = new KeyValueNode<InjectionOptions>(node.getTitle(), new InjectionOptions(node.getAdditionalText()));
        } else if (ContentSpecUtilities.isSpecTopicMetaData(node.getTitle())) {
            final SpecTopic specTopic = transformSpecTopicWithoutTypeCheck(node, nodes, specTopicMap, targetTopics, relationshipFromNodes);
            keyValueNode = new KeyValueNode<SpecTopic>(node.getTitle(), specTopic);
        } else {
            keyValueNode = new KeyValueNode<String>(node.getTitle(), node.getAdditionalText());
        }
        keyValueNode.setUniqueId(node.getId().toString());

        return keyValueNode;
    }

    /**
     * Transform a Level CSNode entity object into a Level Object that can be added to a Content Specification.
     *
     * @param node                  The CSNode entity object to be transformed.
     * @param nodes                 A mapping of node entity ids to their transformed counterparts.
     * @param specTopicMap          A mapping of Topic Entity ids to their transformed SpecTopics.
     * @param targetTopics          A mapping of target ids to SpecTopics.
     * @param relationshipFromNodes A list of CSNode entities that have relationships.
     * @return The transformed level entity.
     */
    protected static Level transformLevel(final CSNodeWrapper node, final Map<Integer, Node> nodes,
            final Map<String, List<SpecTopic>> specTopicMap, final Map<String, SpecTopic> targetTopics,
            final List<CSNodeWrapper> relationshipFromNodes) {
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
        } else {
            throw new IllegalArgumentException("The passed node is not a Level");
        }

        level.setConditionStatement(node.getCondition());
        level.setTargetId(node.getTargetId());
        level.setUniqueId(node.getId().toString());

        // Add all the levels/topics
        if (node.getChildren() != null && node.getChildren().getItems() != null) {
            final List<CSNodeWrapper> childNodes = node.getChildren().getItems();
            final HashMap<CSNodeWrapper, Node> levelNodes = new HashMap<CSNodeWrapper, Node>();
            for (final CSNodeWrapper childNode : childNodes) {
                if (childNode.getNodeType() == CommonConstants.CS_NODE_TOPIC) {
                    final SpecTopic topic = transformSpecTopic(childNode, nodes, specTopicMap, targetTopics, relationshipFromNodes);
                    levelNodes.put(childNode, topic);
                } else if (childNode.getNodeType() == CommonConstants.CS_NODE_COMMENT) {
                    final Comment comment = transformComment(childNode);
                    levelNodes.put(childNode, comment);
                } else {
                    final Level childLevel = transformLevel(childNode, nodes, specTopicMap, targetTopics, relationshipFromNodes);
                    levelNodes.put(childNode, childLevel);
                }
            }

            // Sort the level nodes so that they are in the right order based on next/prev values.
            final LinkedHashMap<CSNodeWrapper, Node> sortedMap = CSNodeSorter.sortMap(levelNodes);

            // Add the child nodes to the level now that they are in the right order.
            for (final Map.Entry<CSNodeWrapper, Node> entry : sortedMap.entrySet()) {
                level.appendChild(entry.getValue());
                // Add a new line to separate chapters/parts
                if (entry.getValue() instanceof Chapter || entry.getValue() instanceof Part) {
                    level.appendChild(new TextNode("\n"));
                }
            }
        }

        // Add the node to the list of processed nodes so that the relationships can be added once everything is processed
        nodes.put(node.getId(), level);

        return level;
    }

    /**
     * Transform a Topic CSNode entity object into a SpecTopic Object that can be added to a Content Specification.
     *
     * @param node                  The CSNode entity object to be transformed.
     * @param nodes                 A mapping of node entity ids to their transformed counterparts.
     * @param specTopicMap          A mapping of Topic Entity ids to their transformed SpecTopics.
     * @param targetTopics          A mapping of target ids to SpecTopics.
     * @param relationshipFromNodes A list of CSNode entities that have relationships.
     * @return The transformed SpecTopic entity.
     */
    protected static SpecTopic transformSpecTopic(final CSNodeWrapper node, final Map<Integer, Node> nodes,
            final Map<String, List<SpecTopic>> specTopicMap, final Map<String, SpecTopic> targetTopics,
            final List<CSNodeWrapper> relationshipFromNodes) {
        if (node.getNodeType() != CommonConstants.CS_NODE_TOPIC) {
            throw new IllegalArgumentException("The passed node is not a Spec Topic");
        }

        return transformSpecTopicWithoutTypeCheck(node, nodes, specTopicMap, targetTopics, relationshipFromNodes);
    }

    /**
     * Transform a Topic CSNode entity object into a SpecTopic Object that can be added to a Content Specification.
     *
     * @param node                  The CSNode entity object to be transformed.
     * @param nodes                 A mapping of node entity ids to their transformed counterparts.
     * @param specTopicMap          A mapping of Topic Entity ids to their transformed SpecTopics.
     * @param targetTopics          A mapping of target ids to SpecTopics.
     * @param relationshipFromNodes A list of CSNode entities that have relationships.
     * @return The transformed SpecTopic entity.
     */
    private static SpecTopic transformSpecTopicWithoutTypeCheck(final CSNodeWrapper node, final Map<Integer, Node> nodes,
            final Map<String, List<SpecTopic>> specTopicMap, final Map<String, SpecTopic> targetTopics,
            final List<CSNodeWrapper> relationshipFromNodes) {
        final SpecTopic specTopic = new SpecTopic(node.getEntityId(), node.getTitle());

        // Basic data
        specTopic.setRevision(node.getEntityRevision());
        specTopic.setConditionStatement(node.getCondition());
        specTopic.setTargetId(node.getTargetId());
        specTopic.setUniqueId(node.getId() + "-" + node.getEntityId());

        if (node.getRelatedToNodes() != null && node.getRelatedToNodes().getItems() != null && !node.getRelatedToNodes().getItems()
                .isEmpty()) {
            relationshipFromNodes.add(node);
        }

        // Add the node to the list of processed nodes so that the relationships can be added once everything is processed
        nodes.put(node.getId(), specTopic);
        if (!specTopicMap.containsKey(node.getEntityId().toString())) {
            specTopicMap.put(node.getEntityId().toString(), new ArrayList<SpecTopic>());
        }
        specTopicMap.get(node.getEntityId().toString()).add(specTopic);

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

        return comment;
    }

    /**
     * Apply the relationships to all of the nodes in the content spec. This should be the last step since all nodes have to be converted
     * to levels and topics before this method can work.
     */
    protected static void applyRelationships(final ContentSpec contentSpec, final Map<Integer, Node> nodes,
            final Map<String, List<SpecTopic>> specTopicMap, final Map<String, SpecTopic> targetTopics,
            final List<CSNodeWrapper> relationshipFromNodes, final List<Process> processes, final DataProviderFactory providerFactory) {
        // Apply the user defined relationships stored in the database
        for (final CSNodeWrapper node : relationshipFromNodes) {
            final SpecTopic fromNode = (SpecTopic) nodes.get(node.getId());

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
                            toLevel.getTitle());
                } else {
                    // Relationships to topics
                    final SpecTopic toSpecTopic = (SpecTopic) toNode;
                    final List<SpecTopic> toSpecTopics = specTopicMap.get(Integer.toString(toSpecTopic.getDBId()));
                    // If the related topic is duplicated then use a target. Otherwise reference the topic directly.
                    if (toSpecTopics != null && toSpecTopics.size() > 1) {
                        // Ensure that the spec topic has a target id if not create one.
                        if (toSpecTopic.getTargetId() == null) {
                            toSpecTopic.setTargetId("T00" + relatedToNode.getId());
                        }

                        // Add the relationship
                        fromNode.addRelationshipToTarget(toSpecTopic,
                                RelationshipType.getRelationshipType(relatedToNode.getRelationshipType()), toSpecTopic.getTitle());
                    } else {
                        fromNode.addRelationshipToTopic(toSpecTopic,
                                RelationshipType.getRelationshipType(relatedToNode.getRelationshipType()), toSpecTopic.getTitle());
                    }
                }
            }
        }

        // Create the unique id map
        final Map<String, SpecTopic> uniqueIdSpecTopicMap = ContentSpecUtilities.getUniqueIdSpecTopicMap(contentSpec);

        // Apply the process relationships
        for (final Process process : processes) {
            process.processTopics(uniqueIdSpecTopicMap, targetTopics, providerFactory.getProvider(TopicProvider.class));
        }
    }
}