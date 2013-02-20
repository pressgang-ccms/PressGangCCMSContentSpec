package org.jboss.pressgang.ccms.contentspec.utils;

import java.util.ArrayList;
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
import org.jboss.pressgang.ccms.contentspec.provider.DataProviderFactory;
import org.jboss.pressgang.ccms.contentspec.provider.TopicProvider;
import org.jboss.pressgang.ccms.contentspec.sort.CSNodeSorter;
import org.jboss.pressgang.ccms.contentspec.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.CSRelatedNodeWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.ContentSpecWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.utils.common.CollectionUtilities;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;

public class CSTransformer {

    /**
     * A List of lower case metadata properties that should be ignored during transformation because they exist else where.
     */
    private static final List<String> IGNORE_META_DATA = CollectionUtilities.toArrayList(CSConstants.TITLE_TITLE.toLowerCase(),
            CSConstants.PRODUCT_TITLE.toLowerCase(), CSConstants.VERSION_TITLE.toLowerCase());

    /**
     * Transforms a content spec datasource entity into a generic content spec object.
     *
     * @param spec            The content spec entity to be transformed.
     * @param providerFactory
     * @return The generic Content Spec object that was transformed from the entity.
     */
    public ContentSpec transform(final ContentSpecWrapper spec, final DataProviderFactory providerFactory) {
        // Reset local variables
        Map<Integer, Node> nodes = new HashMap<Integer, Node>();
        Map<String, List<SpecTopic>> specTopicMap = new HashMap<String, List<SpecTopic>>();
        Map<String, SpecTopic> topicTargets = new HashMap<String, SpecTopic>();
        List<CSNodeWrapper> relationshipFromNodes = new ArrayList<CSNodeWrapper>();
        List<Process> processes = new ArrayList<Process>();

        // Start the transformation
        final ContentSpec contentSpec = new ContentSpec();

        contentSpec.setId(spec.getId());
        if (spec.getTitle() != null) {
            contentSpec.setTitle(spec.getTitle());
        }
        if (spec.getProduct() != null) {
            contentSpec.setProduct(spec.getProduct());
        }
        if (spec.getVersion() != null) {
            contentSpec.setVersion(spec.getVersion());
        }

        // Add a space between the base metadata and optional metadata
        contentSpec.appendChild(new TextNode("\n"));

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
                        final KeyValueNode<?> metaDataNode = transformMetaData(childNode);
                        contentSpec.appendChild(metaDataNode);
                    }
                } else {
                    final Level level = transformLevel(childNode, nodes, specTopicMap, topicTargets, relationshipFromNodes, processes);
                    levelNodes.put(childNode, level);
                }
            }

            // Sort the level nodes so that they are in the right order based on next/prev values.
            final LinkedHashMap<CSNodeWrapper, Node> sortedMap = CSNodeSorter.sortMap(levelNodes);

            // Add the child nodes to the level now that they are in the right order.
            for (final Map.Entry<CSNodeWrapper, Node> entry : sortedMap.entrySet()) {
                contentSpec.getBaseLevel().appendChild(entry.getValue());
                // Add a new line to separate chapters/parts
                if (entry.getValue() instanceof Chapter || entry.getValue() instanceof Part) {
                    contentSpec.getBaseLevel().appendChild(new TextNode("\n"));
                }
            }
        }

        // Apply the relationships to the nodes
        applyRelationships(contentSpec, nodes, specTopicMap, topicTargets, relationshipFromNodes, processes, providerFactory);

        return contentSpec;
    }

    protected KeyValueNode<?> transformMetaData(final CSNodeWrapper node) {
        if (node.getTitle().equalsIgnoreCase(CSConstants.BOOK_TYPE_TITLE)) {
            return new KeyValueNode<BookType>(node.getTitle(), BookType.getBookType(node.getAdditionalText()));
        } else if (node.getTitle().equalsIgnoreCase(CSConstants.INLINE_INJECTION_TITLE)) {
            return new KeyValueNode<InjectionOptions>(node.getTitle(), new InjectionOptions(node.getAdditionalText()));
        } else {
            return new KeyValueNode<String>(node.getTitle(), node.getAdditionalText());
        }
    }

    protected Level transformLevel(final CSNodeWrapper node, final Map<Integer, Node> nodes,
            final Map<String, List<SpecTopic>> specTopicMap, final Map<String, SpecTopic> targetTopics,
            final List<CSNodeWrapper> relationshipFromNodes, final List<Process> processes) {
        final Level level;
        if (node.getNodeType() == CommonConstants.CS_NODE_APPENDIX) {
            level = new Appendix(node.getTitle());
        } else if (node.getNodeType() == CommonConstants.CS_NODE_CHAPTER) {
            level = new Chapter(node.getTitle());
        } else if (node.getNodeType() == CommonConstants.CS_NODE_PART) {
            level = new Part(node.getTitle());
        } else if (node.getNodeType() == CommonConstants.CS_NODE_PROCESS) {
            level = new Process(node.getTitle());
            processes.add((Process) level);
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
                    final Level childLevel = transformLevel(childNode, nodes, specTopicMap, targetTopics, relationshipFromNodes, processes);
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

    protected SpecTopic transformSpecTopic(final CSNodeWrapper node, final Map<Integer, Node> nodes,
            final Map<String, List<SpecTopic>> specTopicMap, final Map<String, SpecTopic> targetTopics,
            final List<CSNodeWrapper> relationshipFromNodes) {
        final SpecTopic specTopic;
        if (node.getNodeType() == CommonConstants.CS_NODE_TOPIC) {
            specTopic = new SpecTopic(node.getEntityId(), node.getTitle());
        } else {
            throw new IllegalArgumentException("The passed node is not a Spec Topic");
        }

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

    protected Comment transformComment(CSNodeWrapper node) {
        final Comment comment;
        if (node.getNodeType() == CommonConstants.CS_NODE_COMMENT) {
            comment = new Comment(node.getAdditionalText());
        } else {
            throw new IllegalArgumentException("The passed node is not a Comment");
        }

        return comment;
    }

    /**
     * Apply the relationships to all of the nodes in the content spec. This should be the last step since all nodes have to be converted
     * to levels and topics before this method can work.
     */
    protected void applyRelationships(final ContentSpec contentSpec, final Map<Integer, Node> nodes,
            final Map<String, List<SpecTopic>> specTopicMap, final Map<String, SpecTopic> targetTopics,
            final List<CSNodeWrapper> relationshipFromNodes, final List<Process> processes, final DataProviderFactory providerFactory) {
        // Apply the user defined relationships stored in the database
        for (final CSNodeWrapper node : relationshipFromNodes) {
            final SpecTopic fromNode = (SpecTopic) nodes.get(node.getId());
            for (final CSRelatedNodeWrapper relatedToNode : node.getRelatedToNodes().getItems()) {
                final Node toNode = nodes.get(relatedToNode.getId());
                if (toNode == null) {
                    throw new IllegalStateException("The related node does not exist in the content specification");
                } else if (toNode instanceof Level) {
                    // Relationships to levels
                    final Level toLevel = (Level) toNode;
                    fromNode.addRelationshipToTarget(toLevel, RelationshipType.getRelationshipType(relatedToNode.getRelationshipType()),
                            toLevel.getTitle());
                } else {
                    // Relationships to topics
                    final SpecTopic toSpecTopic = (SpecTopic) toNode;
                    final List<SpecTopic> toSpecTopics = specTopicMap.get(Integer.toString(toSpecTopic.getDBId()));
                    // If the related topic is duplicated then use a target. Otherwise reference the topic directly.
                    if (toSpecTopics != null && toSpecTopics.size() > 1) {
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
