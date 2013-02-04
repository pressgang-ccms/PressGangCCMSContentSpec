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
import org.jboss.pressgang.ccms.contentspec.sort.CSNodeSorter;
import org.jboss.pressgang.ccms.contentspec.wrapper.CSMetaDataInContentSpecWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.CSRelatedNodeWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.ContentSpecWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.utils.common.CollectionUtilities;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;

// TODO Figure out how to transform Process Relationships
public class CSTransformer {

    /**
     * A List of lower case metadata properties that should be ignored during transformation because they exist else where.
     */
    private final List<String> ignoreMetaData = CollectionUtilities.toArrayList("title", "product", "version");
    private Map<Integer, Node> nodes = new HashMap<Integer, Node>();
    private Map<String, List<SpecTopic>> specTopicMap = new HashMap<String, List<SpecTopic>>();
    private List<CSNodeWrapper> relationshipFromNodes = new ArrayList<CSNodeWrapper>();
    private List<Process> processes = new ArrayList<Process>();

    public ContentSpec transform(final ContentSpecWrapper spec) {
        // Reset local variables
        nodes = new HashMap<Integer, Node>();
        specTopicMap = new HashMap<String, List<SpecTopic>>();
        relationshipFromNodes = new ArrayList<CSNodeWrapper>();
        processes = new ArrayList<Process>();

        // Start the transformation
        final ContentSpec contentSpec = new ContentSpec();

        contentSpec.setId(spec.getId());
        contentSpec.setTitle(spec.getTitle());
        contentSpec.setProduct(spec.getProduct());
        contentSpec.setVersion(spec.getVersion());

        // Add a space between the base metadata and optional metadata
        contentSpec.appendChild(new TextNode("\n"));

        // Add all the metadata
        if (spec.getMetaData() != null) {
            final List<CSMetaDataInContentSpecWrapper> metaDatas = spec.getMetaData().getItems();
            for (final CSMetaDataInContentSpecWrapper metaData : metaDatas) {
                if (!ignoreMetaData.contains(metaData.getTitle().toLowerCase())) {
                    final KeyValueNode<?> metaDataNode = transformMetaData(metaData);
                    contentSpec.appendChild(metaDataNode);
                }
            }
        }

        // Add a space between the metadata and content
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
                    final SpecTopic topic = transformSpecTopic(childNode);
                    levelNodes.put(childNode, topic);
                } else if (childNode.getNodeType() == CommonConstants.CS_NODE_COMMENT) {
                    final Comment comment = transformComment(childNode);
                    levelNodes.put(childNode, comment);
                } else {
                    final Level level = transformLevel(childNode);
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
        applyRelationships();

        return contentSpec;
    }

    protected KeyValueNode<?> transformMetaData(final CSMetaDataInContentSpecWrapper arg) {
        if (arg.getTitle().equalsIgnoreCase(CSConstants.BOOK_TYPE_TITLE)) {
            return new KeyValueNode<BookType>(arg.getTitle(), BookType.getBookType(arg.getValue()));
        } else if (arg.getTitle().equalsIgnoreCase(CSConstants.INLINE_INJECTION_TITLE)) {
            return new KeyValueNode<InjectionOptions>(arg.getTitle(), new InjectionOptions(arg.getValue()));
        } else {
            return new KeyValueNode<String>(arg.getTitle(), arg.getValue());
        }
    }

    protected Level transformLevel(final CSNodeWrapper node) {
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
        level.setTargetId(node.getAlternateTitle());

        // Add all the levels/topics
        if (node.getChildren() != null && node.getChildren().getItems() != null) {
            final List<CSNodeWrapper> nodes = node.getChildren().getItems();
            final HashMap<CSNodeWrapper, Node> levelNodes = new HashMap<CSNodeWrapper, Node>();
            for (final CSNodeWrapper childNode : nodes) {
                if (childNode.getNodeType() == CommonConstants.CS_NODE_TOPIC) {
                    final SpecTopic topic = transformSpecTopic(childNode);
                    levelNodes.put(childNode, topic);
                } else if (childNode.getNodeType() == CommonConstants.CS_NODE_COMMENT) {
                    final Comment comment = transformComment(childNode);
                    levelNodes.put(childNode, comment);
                } else {
                    final Level childLevel = transformLevel(childNode);
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
        this.nodes.put(node.getId(), level);

        return level;
    }

    protected SpecTopic transformSpecTopic(final CSNodeWrapper node) {
        final SpecTopic specTopic;
        if (node.getNodeType() == CommonConstants.CS_NODE_TOPIC) {
            specTopic = new SpecTopic(node.getTopicId(), node.getTitle());
        } else {
            throw new IllegalArgumentException("The passed node is not a Spec Topic");
        }

        specTopic.setRevision(node.getTopicRevision());
        specTopic.setConditionStatement(node.getCondition());
        specTopic.setTargetId(node.getAlternateTitle());

        if (node.getRelatedToNodes() != null && node.getRelatedToNodes().getItems() != null && !node.getRelatedToNodes().getItems()
                .isEmpty()) {
            relationshipFromNodes.add(node);
        }

        // Add the node to the list of processed nodes so that the relationships can be added once everything is processed
        this.nodes.put(node.getId(), specTopic);
        if (!specTopicMap.containsKey(node.getTopicId().toString())) {
            specTopicMap.put(node.getTopicId().toString(), new ArrayList<SpecTopic>());
        }
        specTopicMap.get(node.getTopicId().toString()).add(specTopic);

        return specTopic;
    }

    protected Comment transformComment(CSNodeWrapper node) {
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
    protected void applyRelationships() {
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
    }
}
