/*
  Copyright 2011-2014 Red Hat, Inc

  This file is part of PressGang CCMS.

  PressGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PressGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PressGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.contentspec.utils;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.jboss.pressgang.ccms.utils.common.StringUtilities;
import org.jboss.pressgang.ccms.utils.common.XMLUtilities;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.ContentSpecWrapper;
import org.jboss.pressgang.ccms.wrapper.TopicWrapper;
import org.jboss.pressgang.ccms.wrapper.TranslatedCSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.TranslatedContentSpecWrapper;
import org.jboss.pressgang.ccms.wrapper.TranslatedTopicWrapper;
import org.jboss.pressgang.ccms.wrapper.collection.UpdateableCollectionWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TranslationUtilities {
    public static final List<String> TRANSLATABLE_METADATA = Arrays.asList(CommonConstants.CS_TITLE_TITLE, CommonConstants.CS_PRODUCT_TITLE,
            CommonConstants.CS_SUBTITLE_TITLE, CommonConstants.CS_ABSTRACT_TITLE, CommonConstants.CS_COPYRIGHT_HOLDER_TITLE);

    /**
     * Create a TranslatedTopic based on the content from a normal Topic.
     *
     * @param topic          The topic to transform to a TranslatedTopic
     * @param customEntities
     * @return The new TranslatedTopic initialised with data from the topic.
     */
    public static TranslatedTopicWrapper createTranslatedTopic(final DataProviderFactory providerFactory, final TopicWrapper topic,
            final TranslatedCSNodeWrapper translatedCSNode, final String condition, String customEntities) {
        final TranslatedTopicWrapper translatedTopic = providerFactory.getProvider(TranslatedTopicProvider.class).newTranslatedTopic();
        translatedTopic.setLocale(topic.getLocale());
        translatedTopic.setTranslationPercentage(100);
        translatedTopic.setTopicId(topic.getId());
        translatedTopic.setTopicRevision(topic.getRevision());
        translatedTopic.setXml(topic.getXml());
        if (translatedCSNode != null && (!isNullOrEmpty(condition) || !isNullOrEmpty(customEntities))) {
            translatedTopic.setTranslatedCSNode(translatedCSNode);
            if (!isNullOrEmpty(condition)) {
                translatedTopic.setTranslatedXMLCondition(condition);
            }
            if (!isNullOrEmpty(customEntities)) {
                translatedTopic.setCustomEntities(customEntities);
            }
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
            if (EntityUtilities.isNodeALevel(childNode) && childNode.getNodeType() != CommonConstants.CS_NODE_INITIAL_CONTENT) {
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
                if (EntityUtilities.isNodeALevel(childNode) && childNode.getNodeType() != CommonConstants.CS_NODE_INITIAL_CONTENT) {
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
        if (node.getNodeType() == CommonConstants.CS_NODE_META_DATA && TRANSLATABLE_METADATA.contains(node.getTitle())) {
            sourceString = node.getAdditionalText();
        } else if (EntityUtilities.isNodeALevel(node)) {
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
            // The node will either be a meta data field or a level
            if (node.getNodeType() == CommonConstants.CS_NODE_META_DATA) {
                addTranslationToNodeDetailsToCollection(node.getAdditionalText(), node, allowDuplicates, retValue);
            } else {
                addTranslationToNodeDetailsToCollection(node.getTitle(), node, allowDuplicates, retValue);
            }
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
                final TranslationStringDetails fixedStringDetails = new TranslationStringDetails(translations, originalString);
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
                                    ((KeyValueNode) contentSpecNode).setTranslatedValue(fixedTranslation);
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

    public static boolean resolveCustomTopicEntities(final List<Entity> customEntities, final Document doc) throws SAXException {
        boolean entitiesResolved = false;
        /*
         * Loop over each entity and try to see if it exists in the doc. If it does then remove the EntityReference node and insert
         * the entities value.
         */
        for (final Entity entity : customEntities) {
            final List<org.w3c.dom.Node> entityReferences = XMLUtilities.getChildNodes(doc, entity.getNodeName());
            for (final org.w3c.dom.Node entityReference : entityReferences) {
                if (entityReference instanceof EntityReference) {
                    final String tempXml = "<tempRoot>" + entity.getTextContent() + "</tempRoot>";
                    final Document entityValueDoc = XMLUtilities.convertStringToDocument(tempXml);
                    final org.w3c.dom.Node entityValue = doc.importNode(entityValueDoc.getDocumentElement(), true);

                    final NodeList entityValueChildren = entityValue.getChildNodes();
                    for (int k = 0; k < entityValueChildren.getLength(); k++) {
                        entityReference.getParentNode().insertBefore(entityValueChildren.item(k), entityReference);
                    }
                    entityReference.getParentNode().removeChild(entityReference);

                    entitiesResolved = true;
                }
            }
        }

        return entitiesResolved;
    }

    public static void resolveCustomContentSpecEntities(final List<Entity> customEntities, final ContentSpecWrapper contentSpec) {
        final List<CSNodeWrapper> translatableNodes = new ArrayList<CSNodeWrapper>();

        // Find all the translatable nodes first
        for (final CSNodeWrapper node : contentSpec.getChildren().getItems()) {
            if (node.getNodeType() == CommonConstants.CS_NODE_META_DATA) {
                if (TRANSLATABLE_METADATA.contains(node.getTitle())) {
                    translatableNodes.add(node);
                }
            }
        }

        // Resolve any custom entities
        for (final Entity entity : customEntities) {
            final String entityString = "&" + entity.getNodeName() + ";";
            for (final CSNodeWrapper node : translatableNodes) {
                if (node.getAdditionalText().contains(entityString)) {
                    final String fixedValue = node.getAdditionalText().replace(entityString, entity.getTextContent());
                    node.setAdditionalText(fixedValue);
                }
            }
        }
    }

    public static void resolveCustomContentSpecEntities(final List<Entity> customEntities, final ContentSpec contentSpec) {
        final List<KeyValueNode<String>> translatableNodes = new ArrayList<KeyValueNode<String>>();

        // Find all the translatable nodes first
        for (final Node node : contentSpec.getNodes()) {
            if (node instanceof KeyValueNode) {
                final KeyValueNode<String> keyValueNode = (KeyValueNode<String>) node;
                if (TRANSLATABLE_METADATA.contains(keyValueNode.getKey())) {
                    translatableNodes.add(keyValueNode);
                }
            }
        }

        // Resolve any custom entities
        for (final Entity entity : customEntities) {
            final String entityString = "&" + entity.getNodeName() + ";";
            for (final KeyValueNode<String> node : translatableNodes) {
                if (node.getValue().contains(entityString)) {
                    final String fixedValue = node.getValue().replace(entityString, entity.getTextContent());
                    node.setValue(fixedValue);
                }
            }
        }
    }

    /**
     * Zanata will modify strings sent to it for translation. This class contains the info necessary to take a string from Zanata and match
     * it to the source XML.
     */
    private static class TranslationStringDetails {
        /**
         * The number of spaces that Zanata removed from the left
         */
        private final int leftTrimCount;
        /**
         * The number of spaces that Zanata removed from the right
         */
        private final int rightTrimCount;
        /**
         * The string that was matched to the one returned by Zanata. This will be null if there was no match.
         */
        private final String fixedString;

        TranslationStringDetails(final Map<String, String> translations, final String originalString) {
        /*
         * Here we account for any trimming that is done by Zanata.
         */
            final String lTrimtString = StringUtilities.ltrim(originalString);
            final String rTrimString = StringUtilities.rtrim(originalString);
            final String trimString = originalString.trim();

            final boolean containsExactMacth = translations.containsKey(originalString);
            final boolean lTrimMatch = translations.containsKey(lTrimtString);
            final boolean rTrimMatch = translations.containsKey(rTrimString);
            final boolean trimMatch = translations.containsKey(trimString);

        /* remember the details of the trimming, so we can add the padding back */
            if (containsExactMacth) {
                leftTrimCount = 0;
                rightTrimCount = 0;
                fixedString = originalString;
            } else if (lTrimMatch) {
                leftTrimCount = originalString.length() - lTrimtString.length();
                rightTrimCount = 0;
                fixedString = lTrimtString;
            } else if (rTrimMatch) {
                leftTrimCount = 0;
                rightTrimCount = originalString.length() - rTrimString.length();
                fixedString = rTrimString;
            } else if (trimMatch) {
                leftTrimCount = StringUtilities.ltrimCount(originalString);
                rightTrimCount = StringUtilities.rtrimCount(originalString);
                fixedString = trimString;
            } else {
                leftTrimCount = 0;
                rightTrimCount = 0;
                fixedString = null;
            }
        }

        public int getLeftTrimCount() {
            return leftTrimCount;
        }

        public int getRightTrimCount() {
            return rightTrimCount;
        }

        public String getFixedString() {
            return fixedString;
        }
    }
}
