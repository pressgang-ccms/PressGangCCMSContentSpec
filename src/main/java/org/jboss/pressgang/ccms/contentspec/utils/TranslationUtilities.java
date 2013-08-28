package org.jboss.pressgang.ccms.contentspec.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.pressgang.ccms.contentspec.ContentSpec;
import org.jboss.pressgang.ccms.contentspec.KeyValueNode;
import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.Node;
import org.jboss.pressgang.ccms.contentspec.structures.StringToCSNodeCollection;
import org.jboss.pressgang.ccms.provider.DataProviderFactory;
import org.jboss.pressgang.ccms.provider.TranslatedCSNodeProvider;
import org.jboss.pressgang.ccms.provider.TranslatedContentSpecProvider;
import org.jboss.pressgang.ccms.provider.TranslatedTopicProvider;
import org.jboss.pressgang.ccms.utils.common.CollectionUtilities;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.ContentSpecWrapper;
import org.jboss.pressgang.ccms.wrapper.TopicWrapper;
import org.jboss.pressgang.ccms.wrapper.TranslatedCSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.TranslatedContentSpecWrapper;
import org.jboss.pressgang.ccms.wrapper.TranslatedTopicWrapper;
import org.jboss.pressgang.ccms.wrapper.collection.UpdateableCollectionWrapper;

public class TranslationUtilities {
    private static final List<String> TRANSLATABLE_METADATA = CollectionUtilities.toArrayList(
            new String[]{CommonConstants.CS_TITLE_TITLE, CommonConstants.CS_PRODUCT_TITLE, CommonConstants.CS_SUBTITLE_TITLE, CommonConstants.CS_ABSTRACT_TITLE,
                    CommonConstants.CS_COPYRIGHT_HOLDER_TITLE, CommonConstants.CS_VERSION_TITLE, CommonConstants.CS_EDITION_TITLE});

    /**
     * Create a TranslatedTopic based on the content from a normal Topic.
     *
     * @param topic The topic to transform to a TranslatedTopic
     * @return The new TranslatedTopic initialised with data from the topic.
     */
    public static TranslatedTopicWrapper createTranslatedTopic(final DataProviderFactory providerFactory, final TopicWrapper topic,
            final TranslatedCSNodeWrapper translatedCSNode, final String condition) {
        final TranslatedTopicWrapper translatedTopic = providerFactory.getProvider(TranslatedTopicProvider.class).newTranslatedTopic();
        translatedTopic.setLocale(topic.getLocale());
        translatedTopic.setTranslationPercentage(100);
        translatedTopic.setTopicId(topic.getId());
        translatedTopic.setTopicRevision(topic.getRevision());
        translatedTopic.setXml(topic.getXml());
        if (translatedCSNode != null) {
            translatedTopic.setTranslatedCSNode(translatedCSNode);
        }
        if (condition != null) {
            translatedTopic.setTranslatedXMLCondition(condition);
        }
        return translatedTopic;
    }

    public static TranslatedContentSpecWrapper createTranslatedContentSpec(final DataProviderFactory providerFactory,
            final ContentSpecWrapper contentSpecEntity) {
        final TranslatedContentSpecWrapper translatedContentSpec = providerFactory.getProvider(
                TranslatedContentSpecProvider.class).newTranslatedContentSpec();
        translatedContentSpec.setContentSpecId(contentSpecEntity.getId());
        translatedContentSpec.setContentSpecRevision(contentSpecEntity.getRevision());

        // Get all the nodes to be translated
        final List<CSNodeWrapper> nodes = ContentSpecUtilities.getAllNodes(contentSpecEntity);

        final UpdateableCollectionWrapper<TranslatedCSNodeWrapper> translatedNodes = createCSTranslatedNodes(providerFactory, nodes);
        translatedContentSpec.setTranslatedNodes(translatedNodes);

        return translatedContentSpec;
    }

    protected static Set<CSNodeWrapper> getAllTranslatableContentSpecNodes(final ContentSpecWrapper contentSpec) {
        final Set<CSNodeWrapper> nodes = new HashSet<CSNodeWrapper>();
        final List<CSNodeWrapper> childrenNodes = contentSpec.getChildren().getItems();
        for (CSNodeWrapper childNode : childrenNodes) {
            if (ContentSpecUtilities.isNodeALevel(childNode)) {
                nodes.add(childNode);
            } else if (childNode.getNodeType() == CommonConstants.CS_NODE_META_DATA) {
                if (TRANSLATABLE_METADATA.contains(childNode.getTitle())) {
                    nodes.add(childNode);
                }
            }
            addAllTranslatableCSNodeChildren(childNode, nodes);
        }
        return nodes;
    }

    protected static void addAllTranslatableCSNodeChildren(final CSNodeWrapper csNode, final Set<CSNodeWrapper> nodes) {
        if (csNode.getChildren() != null) {
            final List<CSNodeWrapper> childrenNodes = csNode.getChildren().getItems();
            for (CSNodeWrapper childNode : childrenNodes) {
                if (ContentSpecUtilities.isNodeALevel(childNode)) {
                    nodes.add(childNode);
                } else if (childNode.getNodeType() == CommonConstants.CS_NODE_META_DATA) {
                    if (TRANSLATABLE_METADATA.contains(childNode.getTitle())) {
                        nodes.add(childNode);
                    }
                }
                addAllTranslatableCSNodeChildren(childNode, nodes);
            }
        }
    }

    /**
     * Creates a list of Content Spec Translated Node entities from a list of Content Spec node entities.
     *
     * @param providerFactory The factory to produce entity providers to lookup entity details.
     * @param nodes           The nodes to create the translated nodes for.
     * @return
     */
    public static UpdateableCollectionWrapper<TranslatedCSNodeWrapper> createCSTranslatedNodes(final DataProviderFactory providerFactory,
            final Collection<CSNodeWrapper> nodes) {
        final UpdateableCollectionWrapper<TranslatedCSNodeWrapper> translatedNodes = providerFactory.getProvider(
                TranslatedCSNodeProvider.class).newTranslatedCSNodeCollection();
        for (final CSNodeWrapper node : nodes) {
            translatedNodes.addNewItem(createCSTranslatedNode(providerFactory, node));
        }

        return translatedNodes;
    }

    /**
     * Creates a Translated Content Spec Node based on an original Content Spec Node.
     *
     * @param providerFactory The factory to produce entity providers to lookup entity details.
     * @param node            The node to create a translated node for.
     * @return A CSTranslatedNode entity that contains the information from the original node.
     */
    public static TranslatedCSNodeWrapper createCSTranslatedNode(final DataProviderFactory providerFactory, final CSNodeWrapper node) {
        final String sourceString;
        if (node.getNodeType() == CommonConstants.CS_NODE_META_DATA) {
            sourceString = node.getAdditionalText();
        } else if (ContentSpecUtilities.isNodeALevel(node)) {
            sourceString = node.getTitle();
        } else {
            // The node isn't a translatable node so it must be a topic or comment.
            sourceString = null;
        }

        final TranslatedCSNodeWrapper translatedNode = providerFactory.getProvider(TranslatedCSNodeProvider.class).newTranslatedCSNode();
        translatedNode.setNodeId(node.getId());
        translatedNode.setNodeRevision(node.getRevision());
        translatedNode.setOriginalString(sourceString);

        return translatedNode;
    }

    public static List<StringToCSNodeCollection> getTranslatableStrings(final ContentSpecWrapper contentSpec,
            final boolean allowDuplicates) {
        if (contentSpec == null) return null;

        final List<StringToCSNodeCollection> retValue = new ArrayList<StringToCSNodeCollection>();

        // Get all the translatable nodes and create the StringToCSNode collection
        final Set<CSNodeWrapper> contentSpecNodes = getAllTranslatableContentSpecNodes(contentSpec);
        for (final CSNodeWrapper node : contentSpecNodes) {
            addTranslationToNodeDetailsToCollection(node.getAdditionalText().toString(), node, allowDuplicates, retValue);
        }

        return retValue;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void replaceTranslatedStrings(final ContentSpecWrapper contentSpecEntity, final ContentSpec contentSpec,
            final Map<String, String> translations) {
        if (contentSpecEntity == null || translations == null || translations.size() == 0) return;

        /*
         * Get the translation strings and the nodes that the string maps to. We
         * assume that the text being provided here is an exact match for the
         * text that was supplied to getTranslatableStrings originally, which we
         * then assume matches the strings supplied as the keys in the
         * translations parameter.
         */
        final List<StringToCSNodeCollection> stringToNodeCollections = getTranslatableStrings(contentSpecEntity, false);

        if (stringToNodeCollections == null || stringToNodeCollections.size() == 0) return;

        for (final StringToCSNodeCollection stringToNodeCollection : stringToNodeCollections) {
            final String originalString = stringToNodeCollection.getTranslationString();
            final ArrayList<CSNodeWrapper> nodeCollections = stringToNodeCollection.getNodeCollections();

            if (nodeCollections != null && nodeCollections.size() != 0) {
                // Zanata will change the format of the strings that it returns. Here we account for any trimming that was done.
                final ZanataStringDetails fixedStringDetails = new ZanataStringDetails(translations, originalString);
                if (fixedStringDetails.getFixedString() != null) {
                    final String translation = translations.get(fixedStringDetails.getFixedString());

                    if (translation != null && !translation.isEmpty()) {
                        // Build up the padding that Zanata removed
                        final StringBuilder leftTrimPadding = new StringBuilder();
                        final StringBuilder rightTrimPadding = new StringBuilder();

                        for (int i = 0; i < fixedStringDetails.getLeftTrimCount(); ++i)
                            leftTrimPadding.append(" ");

                        for (int i = 0; i < fixedStringDetails.getRightTrimCount(); ++i)
                            rightTrimPadding.append(" ");

                        final String fixedTranslation = leftTrimPadding.toString() + translation + rightTrimPadding.toString();

                        for (final CSNodeWrapper node : nodeCollections) {
                            final Node contentSpecNode = ContentSpecUtilities.findMatchingContentSpecNode(contentSpec, node.getId());
                            if (contentSpecNode != null) {
                                if (contentSpecNode instanceof KeyValueNode) {
                                    ((KeyValueNode) contentSpecNode).setValue(fixedTranslation);
                                } else if (contentSpecNode instanceof Level) {
                                    ((Level) contentSpecNode).setTranslatedTitle(fixedTranslation);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void addTranslationToNodeDetailsToCollection(final String text, final CSNodeWrapper node, final boolean allowDuplicates,
            final List<StringToCSNodeCollection> translationStrings) {
        final ArrayList<CSNodeWrapper> nodes = new ArrayList<CSNodeWrapper>();
        nodes.add(node);
        addTranslationToNodeDetailsToCollection(text, nodes, allowDuplicates, translationStrings);
    }

    private static void addTranslationToNodeDetailsToCollection(final String text, final ArrayList<CSNodeWrapper> nodes,
            final boolean allowDuplicates, final List<StringToCSNodeCollection> translationStrings) {

        if (allowDuplicates) {
            translationStrings.add(new StringToCSNodeCollection(text).addNodeCollection(nodes));
        } else {
            final StringToCSNodeCollection stringToNodeCollection = findExistingText(text, translationStrings);

            if (stringToNodeCollection == null) translationStrings.add(new StringToCSNodeCollection(text).addNodeCollection(nodes));
            else stringToNodeCollection.addNodeCollection(nodes);
        }
    }

    private static StringToCSNodeCollection findExistingText(final String text, final List<StringToCSNodeCollection> translationStrings) {
        for (final StringToCSNodeCollection stringToNodeCollection : translationStrings) {
            if (stringToNodeCollection.getTranslationString().equals(text)) return stringToNodeCollection;
        }

        return null;
    }
}
