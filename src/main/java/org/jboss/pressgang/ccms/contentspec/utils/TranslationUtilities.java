package org.jboss.pressgang.ccms.contentspec.utils;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.pressgang.ccms.provider.DataProviderFactory;
import org.jboss.pressgang.ccms.provider.TranslatedCSNodeProvider;
import org.jboss.pressgang.ccms.provider.TranslatedContentSpecProvider;
import org.jboss.pressgang.ccms.provider.TranslatedTopicProvider;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.ContentSpecWrapper;
import org.jboss.pressgang.ccms.wrapper.TopicWrapper;
import org.jboss.pressgang.ccms.wrapper.TranslatedCSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.TranslatedContentSpecWrapper;
import org.jboss.pressgang.ccms.wrapper.TranslatedTopicWrapper;
import org.jboss.pressgang.ccms.wrapper.collection.UpdateableCollectionWrapper;

public class TranslationUtilities {

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
        translatedTopic.setHtml(topic.getHtml());
        translatedTopic.setHtmlUpdated(new Date());
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
        final Set<CSNodeWrapper> nodes = getAllContentSpecNodes(contentSpecEntity);

        final UpdateableCollectionWrapper<TranslatedCSNodeWrapper> translatedNodes = createCSTranslatedNodes(providerFactory, nodes);
        translatedContentSpec.setTranslatedNodes(translatedNodes);

        return translatedContentSpec;
    }

    protected static Set<CSNodeWrapper> getAllContentSpecNodes(final ContentSpecWrapper contentSpec) {
        final Set<CSNodeWrapper> nodes = new HashSet<CSNodeWrapper>();
        final List<CSNodeWrapper> childrenNodes = contentSpec.getChildren().getItems();
        for (CSNodeWrapper childNode : childrenNodes) {
            nodes.add(childNode);
            addAllCSNodeChildren(childNode, nodes);
        }
        return nodes;
    }

    protected static void addAllCSNodeChildren(final CSNodeWrapper csNode, final Set<CSNodeWrapper> nodes) {
        if (csNode.getChildren() != null) {
            final List<CSNodeWrapper> childrenNodes = csNode.getChildren().getItems();
            for (CSNodeWrapper childNode : childrenNodes) {
                nodes.add(childNode);
                addAllCSNodeChildren(childNode, nodes);
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
        } else if (node.getNodeType() == CommonConstants.CS_NODE_COMMENT || node.getNodeType() == CommonConstants.CS_NODE_TOPIC) {
            sourceString = null;
        } else {
            sourceString = node.getTitle();
        }

        final TranslatedCSNodeWrapper translatedNode = providerFactory.getProvider(TranslatedCSNodeProvider.class).newTranslatedCSNode();
        translatedNode.setNodeId(node.getId());
        translatedNode.setNodeRevision(node.getRevision());
        translatedNode.setOriginalString(sourceString);

        return translatedNode;
    }
}