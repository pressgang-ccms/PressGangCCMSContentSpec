package org.jboss.pressgang.ccms.contentspec.utils;

import java.util.ArrayList;
import java.util.List;

import org.jboss.pressgang.ccms.utils.common.DocBookUtilities;
import org.jboss.pressgang.ccms.utils.common.XMLUtilities;
import org.jboss.pressgang.ccms.utils.structures.DocBookVersion;
import org.jboss.pressgang.ccms.wrapper.ServerEntitiesWrapper;
import org.jboss.pressgang.ccms.wrapper.ServerSettingsWrapper;
import org.jboss.pressgang.ccms.wrapper.base.BaseTopicWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CustomTopicXMLValidator {
    private static final String[] DATE_FORMATS = new String[]{"MM-dd-yyyy", "MM/dd/yyyy", "yyyy-MM-dd", "yyyy/MM/dd", "EEE MMM dd yyyy",
            "EEE, MMM dd yyyy", "EEE MMM dd yyyy Z", "EEE dd MMM yyyy", "EEE, dd MMM yyyy", "EEE dd MMM yyyy Z", "yyyyMMdd",
            "yyyyMMdd'T'HHmmss.SSSZ"};

    /**
     * Checks to make sure that a topics xml content is valid for the specific topic. This involves checking the following:
     * <ul>
     *     <li>Table row entries match the cols attribute.</li>
     *     <li>The root XML Element matches the topic type.</li>
     *     <li>The topic has no invalid content for the specific topic type.</li>
     * </ul>
     *
     * @param serverSettings              The server settings.
     * @param topic                       The topic to check for invalid content.
     * @param topicDoc                    The topics XML DOM Document.
     * @param skipNestedSectionValidation Whether or not nested section validation should be performed.
     * @return A list of error messages for any invalid content found, otherwise an empty list.
     */
    public static List<String> checkTopicForInvalidContent(final ServerSettingsWrapper serverSettings, final BaseTopicWrapper<?> topic,
            final Document topicDoc, boolean skipNestedSectionValidation) {
        final List<String> xmlErrors = new ArrayList<String>();
        // Check to ensure that if the topic has a table, that the table isn't missing any entries
        if (!DocBookUtilities.validateTables(topicDoc)) {
            xmlErrors.add("Table column declaration doesn't match the number of entry elements.");
        }
        // Check that the root element matches the topic type
        xmlErrors.addAll(checkTopicRootElement(serverSettings, topic, topicDoc));

        // Check that the content matches the topic type
        xmlErrors.addAll(checkTopicContentBasedOnType(serverSettings, topic, topicDoc, skipNestedSectionValidation));

        return xmlErrors;
    }

    /**
     * Checks that the topics root element matches the topic type.
     *
     * @param topic The topic to validate the doc against.
     * @param doc   The topics XML DOM Document.
     * @return A list of error messages for any invalid content found, otherwise an empty list.
     */
    public static List<String> checkTopicRootElement(final ServerSettingsWrapper serverSettings, final BaseTopicWrapper<?> topic,
            final Document doc) {
        final List<String> xmlErrors = new ArrayList<String>();
        final ServerEntitiesWrapper serverEntities = serverSettings.getEntities();

        if (isTopicANormalTopic(topic, serverSettings)) {
            if (!doc.getDocumentElement().getNodeName().equals(DocBookUtilities.TOPIC_ROOT_NODE_NAME)) {
                xmlErrors.add("Topics must be a <" + DocBookUtilities.TOPIC_ROOT_NODE_NAME + ">.");
            }
        } else {
            if (topic.hasTag(serverEntities.getRevisionHistoryTagId())) {
                if (!doc.getDocumentElement().getNodeName().equals("appendix")) {
                    xmlErrors.add("Revision History topics must be an <appendix>.");
                }

                // Check to make sure that a revhistory entry exists
                final NodeList revHistoryList = doc.getElementsByTagName("revhistory");
                if (revHistoryList.getLength() == 0) {
                    xmlErrors.add("No <revhistory> element found. A <revhistory> must exist for Revision Histories.");
                }
            } else if (topic.hasTag(serverEntities.getLegalNoticeTagId())) {
                if (!doc.getDocumentElement().getNodeName().equals("legalnotice")) {
                    xmlErrors.add("Legal Notice topics must be a <legalnotice>.");
                }
            } else if (topic.hasTag(serverEntities.getAuthorGroupTagId())) {
                if (!doc.getDocumentElement().getNodeName().equals("authorgroup")) {
                    xmlErrors.add("Author Group topics must be an <authorgroup>.");
                }
            } else if (topic.hasTag(serverEntities.getAbstractTagId())) {
                if (!doc.getDocumentElement().getNodeName().equals("abstract")) {
                    xmlErrors.add("Abstract topics must be an <abstract>.");
                }
            } else if (topic.hasTag(serverEntities.getInfoTagId())) {
                if (DocBookVersion.DOCBOOK_50.getId().equals(topic.getXmlFormat())) {
                    if (!doc.getDocumentElement().getNodeName().equals("info")) {
                        xmlErrors.add("Info topics must be an <info>.");
                    }
                } else {
                    if (!doc.getDocumentElement().getNodeName().equals("sectioninfo")) {
                        xmlErrors.add("Info topics must be a <sectioninfo>.");
                    }
                }
            }
        }

        return xmlErrors;
    }

    /**
     * Check a topic and return an error messages if the content doesn't match the topic type.
     *
     * @param serverSettings              The server settings.
     * @param topic                       The topic to check the content for.
     * @param doc                         The topics XML DOM Document.
     * @param skipNestedSectionValidation Whether or not nested section validation should be performed.
     * @return A list of error messages for any invalid content found, otherwise an empty list.
     */
    public static List<String> checkTopicContentBasedOnType(final ServerSettingsWrapper serverSettings, final BaseTopicWrapper<?> topic,
            final Document doc, boolean skipNestedSectionValidation) {
        final ServerEntitiesWrapper serverEntities = serverSettings.getEntities();
        final List<String> xmlErrors = new ArrayList<String>();

        if (topic.hasTag(serverEntities.getRevisionHistoryTagId())) {
            // Check to make sure that a revhistory entry exists
            final String revHistoryErrors = DocBookUtilities.validateRevisionHistory(doc, DATE_FORMATS);
            if (revHistoryErrors != null) {
                xmlErrors.add(revHistoryErrors);
            }
        } else if (topic.hasTag(serverEntities.getInfoTagId())) {
            // Check that the info topic doesn't contain invalid fields
            if (DocBookUtilities.checkForInvalidInfoElements(doc)) {
                xmlErrors.add("Info topics cannot contain <title>, <subtitle> or <titleabbrev> elements.");
            }
        } else if (isTopicANormalTopic(topic, serverSettings)) {
            // Check that nested sections aren't used
            final List<Node> subSections = XMLUtilities.getDirectChildNodes(doc.getDocumentElement(), "section");
            if (subSections.size() > 0 && !skipNestedSectionValidation) {
                xmlErrors.add("Nested sections cannot be used in topics. Please consider breaking the content into multiple topics.");
            }
        }

        return xmlErrors;
    }

    /**
     * Check to see if a Topic is a normal topic, instead of a Revision History or Legal Notice
     *
     * @param topic The topic to be checked.
     * @return True if the topic is a normal topic, otherwise false.
     */
    public static boolean isTopicANormalTopic(final BaseTopicWrapper<?> topic, final ServerSettingsWrapper serverSettings) {
        return !(topic.hasTag(serverSettings.getEntities().getRevisionHistoryTagId()) || topic.hasTag(
                serverSettings.getEntities().getLegalNoticeTagId()) || topic.hasTag(
                serverSettings.getEntities().getAuthorGroupTagId()) || topic.hasTag(
                serverSettings.getEntities().getInfoTagId()) || topic.hasTag(serverSettings.getEntities().getAbstractTagId()));
    }
}
