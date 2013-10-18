package org.jboss.pressgang.ccms.docbook.processing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;
import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.SpecNode;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.buglinks.BaseBugLinkStrategy;
import org.jboss.pressgang.ccms.contentspec.buglinks.BugLinkOptions;
import org.jboss.pressgang.ccms.contentspec.entities.Relationship;
import org.jboss.pressgang.ccms.contentspec.entities.TargetRelationship;
import org.jboss.pressgang.ccms.contentspec.entities.TopicRelationship;
import org.jboss.pressgang.ccms.contentspec.enums.TopicType;
import org.jboss.pressgang.ccms.docbook.compiling.DocbookBuildingOptions;
import org.jboss.pressgang.ccms.docbook.sort.TopicTitleSorter;
import org.jboss.pressgang.ccms.docbook.structures.InjectionListData;
import org.jboss.pressgang.ccms.docbook.structures.InjectionTopicData;
import org.jboss.pressgang.ccms.docbook.structures.TocTopicDatabase;
import org.jboss.pressgang.ccms.utils.common.DocBookUtilities;
import org.jboss.pressgang.ccms.utils.common.XMLUtilities;
import org.jboss.pressgang.ccms.utils.sort.ExternalListSort;
import org.jboss.pressgang.ccms.wrapper.TranslatedTopicWrapper;
import org.jboss.pressgang.ccms.wrapper.base.BaseTopicWrapper;
import org.jboss.pressgang.ccms.zanata.ZanataDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * This class takes the XML from a topic and modifies it to include and injected content.
 */
public class DocbookXMLPreProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(DocbookXMLPreProcessor.class);
    /**
     * Used to identify that an <orderedlist> should be generated for the injection point
     */
    protected static final int ORDEREDLIST_INJECTION_POINT = 1;
    /**
     * Used to identify that an <itemizedlist> should be generated for the injection point
     */
    protected static final int ITEMIZEDLIST_INJECTION_POINT = 2;
    /**
     * Used to identify that an <xref> should be generated for the injection point
     */
    protected static final int XREF_INJECTION_POINT = 3;
    /**
     * Used to identify that an <xref> should be generated for the injection point
     */
    protected static final int LIST_INJECTION_POINT = 4;
    /**
     * Identifies a named regular expression group
     */
    protected static final String TOPICIDS_RE_NAMED_GROUP = "TopicIDs";
    /**
     * This text identifies an option task in a list
     */
    protected static final String OPTIONAL_MARKER = "OPT:";
    /**
     * The text to be prefixed to a list item if a topic is optional
     */
    protected static final String OPTIONAL_LIST_PREFIX = "Optional: ";
    /**
     * A regular expression that identifies a topic id
     */
    protected static final String OPTIONAL_TOPIC_ID_RE = "(" + OPTIONAL_MARKER + "\\s*)?\\d+";
    /**
     * A regular expression that identifies a topic id
     */
    protected static final String TOPIC_ID_RE = "\\d+";

    /**
     * A regular expression that matches an InjectSequence custom injection point
     */
    public static final Pattern CUSTOM_INJECTION_SEQUENCE_RE =
    /*
     * start xml comment and 'InjectSequence:' surrounded by optional white space
     */
            Pattern.compile("^\\s*InjectSequence:\\s*" +
    /*
     * an optional comma separated list of digit blocks, and at least one digit block with an optional comma
     */
                    "(?<" + TOPICIDS_RE_NAMED_GROUP + ">(\\s*" + OPTIONAL_TOPIC_ID_RE + "\\s*,)*(\\s*" + OPTIONAL_TOPIC_ID_RE + ",?))" +
    /* xml comment end */
                    "\\s*$");

    /**
     * A regular expression that matches an InjectList custom injection point
     */
    public static final Pattern CUSTOM_INJECTION_LIST_RE =
    /* start xml comment and 'InjectList:' surrounded by optional white space */
            Pattern.compile("^\\s*InjectList:\\s*" +
    /*
     * an optional comma separated list of digit blocks, and at least one digit block with an optional comma
     */
                    "(?<" + TOPICIDS_RE_NAMED_GROUP + ">(\\s*" + OPTIONAL_TOPIC_ID_RE + "\\s*,)*(\\s*" + OPTIONAL_TOPIC_ID_RE + ",?))" +
    /* xml comment end */
                    "\\s*$");

    public static final Pattern CUSTOM_INJECTION_LISTITEMS_RE =
    /* start xml comment and 'InjectList:' surrounded by optional white space */
            Pattern.compile("^\\s*InjectListItems:\\s*" +
    /*
     * an optional comma separated list of digit blocks, and at least one digit block with an optional comma
     */
                    "(?<" + TOPICIDS_RE_NAMED_GROUP + ">(\\s*" + OPTIONAL_TOPIC_ID_RE + "\\s*,)*(\\s*" + OPTIONAL_TOPIC_ID_RE + ",?))" +
    /* xml comment end */
                    "\\s*$");

    public static final Pattern CUSTOM_ALPHA_SORT_INJECTION_LIST_RE =
    /*
     * start xml comment and 'InjectListAlphaSort:' surrounded by optional white space
     */
            Pattern.compile("^\\s*InjectListAlphaSort:\\s*" +
    /*
     * an optional comma separated list of digit blocks, and at least one digit block with an optional comma
     */
                    "(?<" + TOPICIDS_RE_NAMED_GROUP + ">(\\s*" + OPTIONAL_TOPIC_ID_RE + "\\s*,)*(\\s*" + OPTIONAL_TOPIC_ID_RE + ",?))" +
    /* xml comment end */
                    "\\s*$");

    /**
     * A regular expression that matches an Inject custom injection point
     */
    public static final Pattern CUSTOM_INJECTION_SINGLE_RE =
    /* start xml comment and 'Inject:' surrounded by optional white space */
            Pattern.compile("^\\s*Inject:\\s*" +
    /* one digit block */
                    "(?<" + TOPICIDS_RE_NAMED_GROUP + ">(" + OPTIONAL_TOPIC_ID_RE + "))" +
    /* xml comment end */
                    "\\s*$");

    /**
     * The noinject value for the role attribute indicates that an element should not be included in the Topic Fragment
     */
    protected static final String NO_INJECT_ROLE = "noinject";

    // DEFAULT STRING CONSTANTS
    protected static final String DEFAULT_PREREQUISITE = "Prerequisite:";
    protected static final String PREREQUISITE_PROPERTY = "PREREQUISITE";
    protected static final String DEFAULT_SEE_ALSO = "See Also:";
    protected static final String SEE_ALSO_PROPERTY = "SEE_ALSO";
    protected static final String DEFAULT_REPORT_A_BUG = "Report a bug";
    protected static final String REPORT_A_BUG_PROPERTY = "REPORT_A_BUG";
    protected static final String DEFAULT_PREVIOUS_STEP = "Previous Step in %s";
    protected static final String PREVIOUS_STEP_PROPERTY = "PREVIOUS_STEP";
    protected static final String DEFAULT_PREVIOUS_STEPS = "Previous Steps in %s";
    protected static final String PREVIOUS_STEPS_PROPERTY = "PREVIOUS_STEPS";
    protected static final String DEFAULT_NEXT_STEP = "Next Step in %s";
    protected static final String NEXT_STEP_PROPERTY = "NEXT_STEP";
    protected static final String DEFAULT_NEXT_STEPS = "Next Step in %s";
    protected static final String NEXT_STEPS_PROPERTY = "NEXT_STEPS";

    // ROLE/STYLE CONSTANTS
    /**
     * The Docbook role (which becomes a CSS class) for the bug link para
     */
    protected static final String ROLE_CREATE_BUG_PARA = "RoleCreateBugPara";
    protected static final String ROLE_PREREQUISITE_LIST = "prereqs-list";
    protected static final String ROLE_PREREQUISITE = "prereq";
    protected static final String ROLE_SEE_ALSO_LIST = "see-also-list";
    protected static final String ROLE_SEE_ALSO = "see-also";
    protected static final String ROLE_LINK_LIST_LIST = "link-list-list";
    protected static final String ROLE_LINK_LIST = "link-list";
    protected static final String ROLE_PROCESS_NEXT_ITEMIZED_LIST = "process-next-itemizedlist";
    protected static final String ROLE_PROCESS_NEXT_TITLE = "process-next-title";
    protected static final String ROLE_PROCESS_NEXT_TITLE_LINK = "process-next-title-link";
    protected static final String ROLE_PROCESS_NEXT_LINK = "process-next-link";
    protected static final String ROLE_PROCESS_NEXT_LISTITEM = "process-next-listitem";
    protected static final String ROLE_PROCESS_PREVIOUS_ITEMIZED_LIST = "process-previous-itemizedlist";
    protected static final String ROLE_PROCESS_PREVIOUS_TITLE = "process-previous-title";
    protected static final String ROLE_PROCESS_PREVIOUS_TITLE_LINK = "process-previous-title-link";
    protected static final String ROLE_PROCESS_PREVIOUS_LINK = "process-previous-link";
    protected static final String ROLE_PROCESS_PREVIOUS_LISTITEM = "process-previous-listitem";

    protected static final String ENCODING = "UTF-8";

    protected final ResourceBundle translations;
    protected final BaseBugLinkStrategy bugLinkStrategy;

    public DocbookXMLPreProcessor(final ResourceBundle translationStrings, final BaseBugLinkStrategy bugLinkStrategy) {
        translations = translationStrings;
        this.bugLinkStrategy = bugLinkStrategy;
    }

    /**
     * Creates the wrapper element for bug or editor links and adds it to the document.
     *
     * @param document The document to add the wrapper/link to.
     * @param cssClass The css class name to use for the wrapper.
     * @return The wrapper element that links can be added to.
     */
    protected Element createLinkWrapperElement(final Document document, final String cssClass) {
        // Create the bug/editor link root element
        final Element linkElement = document.createElement("para");
        if (cssClass != null) {
            linkElement.setAttribute("role", cssClass);
        }

        /* Create the wrapper if needed.
         *
         * refentry's or nested sections will make the XML invalid if we just use a para, so we need to wrap the para in a simplesect.
         *
         * This does mean though that the XML could be invalid for inner level topics.
         */
        final NodeList refEntries = document.getDocumentElement().getElementsByTagName("refentry");
        final NodeList sections = document.getDocumentElement().getElementsByTagName("section");
        if (refEntries.getLength() > 0 || sections.getLength() > 0) {
            final Element bugzillaSection = document.createElement("simplesect");
            document.getDocumentElement().appendChild(bugzillaSection);

            final Element bugzillaSectionTitle = document.createElement("title");
            bugzillaSectionTitle.setTextContent("");
            bugzillaSection.appendChild(bugzillaSectionTitle);

            bugzillaSection.appendChild(linkElement);
        } else {
            document.getDocumentElement().appendChild(linkElement);
        }

        return linkElement;
    }

    public void processTopicBugLink(final SpecTopic specTopic, final Document document, final BugLinkOptions bugOptions,
            final DocbookBuildingOptions docbookBuildingOptions, final Date buildDate) {
        // BUG LINK
        try {
            final Element bugzillaULink = document.createElement("ulink");

            final String reportBugTranslation = translations.getString(REPORT_A_BUG_PROPERTY);
            bugzillaULink.setTextContent(reportBugTranslation == null ? DEFAULT_REPORT_A_BUG : reportBugTranslation);

            String specifiedBuildName = "";
            if (docbookBuildingOptions != null && docbookBuildingOptions.getBuildName() != null)
                specifiedBuildName = docbookBuildingOptions.getBuildName();

            // build the bug link url with the base components
            String bugzillaUrl = bugLinkStrategy.generateUrl(bugOptions, specTopic, specifiedBuildName, buildDate);

            bugzillaULink.setAttribute("url", bugzillaUrl);

            /*
             * only add the elements to the XML DOM if there was no exception (not that there should be one
             */
            final Element bugzillaSection = createLinkWrapperElement(document, ROLE_CREATE_BUG_PARA);
            bugzillaSection.appendChild(bugzillaULink);
        } catch (final Exception ex) {
            LOG.error("Failed to insert Bug Links into the DOM Document", ex);
        }
    }

    public void processTopicEditorLinks(final SpecTopic specTopic, final Document document,
            final DocbookBuildingOptions docbookBuildingOptions, final ZanataDetails zanataDetails) {
        // EDITOR LINK
        if (docbookBuildingOptions != null && docbookBuildingOptions.getInsertEditorLinks()) {
            final BaseTopicWrapper<?> topic = specTopic.getTopic();
            final String editorUrl = topic.getEditorURL(zanataDetails);

            final Element editorLinkPara = createLinkWrapperElement(document, ROLE_CREATE_BUG_PARA);

            if (editorUrl != null) {
                final Element editorULink = document.createElement("ulink");
                editorLinkPara.appendChild(editorULink);
                editorULink.setTextContent("Edit this topic");
                editorULink.setAttribute("url", editorUrl);
            } else {
                /*
                 * Since the getEditorURL method only returns null for translations we don't need to check the topic
                 * type.
                 */
                editorLinkPara.setTextContent("No editor available for this topic, as it hasn't been pushed for translation.");
            }

            // Add a link for additional translated content for Revision Histories and author groups
            if (topic instanceof TranslatedTopicWrapper && (specTopic.getTopicType() == TopicType.REVISION_HISTORY || specTopic
                    .getTopicType() == TopicType.AUTHOR_GROUP)) {
                final String additionalXMLEditorUrl = topic.getPressGangURL();

                if (additionalXMLEditorUrl != null) {
                    final Element additionalXMLEditorLinkPara = createLinkWrapperElement(document, ROLE_CREATE_BUG_PARA);

                    final Element editorULink = document.createElement("ulink");
                    additionalXMLEditorLinkPara.appendChild(editorULink);
                    editorULink.setTextContent("Edit the Additional Translated XML");
                    editorULink.setAttribute("url", additionalXMLEditorUrl);
                }
            }
        }
    }

    /**
     * Adds some debug information and links to the end of the topic
     */
    public void processTopicAdditionalInfo(final SpecTopic specTopic, final Document document, final BugLinkOptions bugOptions,
            final DocbookBuildingOptions docbookBuildingOptions, final Date buildDate, final ZanataDetails zanataDetails) {
        if ((docbookBuildingOptions != null && (docbookBuildingOptions.getInsertSurveyLink() || docbookBuildingOptions
                .getInsertEditorLinks()))) {

            // SURVEY LINK
            if (docbookBuildingOptions != null && docbookBuildingOptions.getInsertSurveyLink()) {
                final Element surveyPara = document.createElement("para");
                surveyPara.setAttribute("role", ROLE_CREATE_BUG_PARA);
                document.getDocumentElement().appendChild(surveyPara);

                final Text startSurveyText = document.createTextNode(
                        "Thank you for evaluating the new documentation format for JBoss Enterprise Application Platform. Let us know " +
                                "what you think by taking a short ");
                surveyPara.appendChild(startSurveyText);

                final Element surveyULink = document.createElement("ulink");
                surveyPara.appendChild(surveyULink);
                surveyULink.setTextContent("survey");
                surveyULink.setAttribute("url", "https://www.keysurvey.com/survey/380730/106f/");

                final Text endSurveyText = document.createTextNode(".");
                surveyPara.appendChild(endSurveyText);
            }

            if (specTopic.getTopicType() != TopicType.AUTHOR_GROUP) {
                processTopicEditorLinks(specTopic, document, docbookBuildingOptions, zanataDetails);
            }
        }

        // Only include a bugzilla link for normal topics
        if (specTopic.getTopicType() == TopicType.NORMAL || specTopic.getTopicType() == TopicType.LEVEL) {
            // BUGZILLA LINK
            if (docbookBuildingOptions != null && docbookBuildingOptions.getInsertBugLinks()) {
                processTopicBugLink(specTopic, document, bugOptions, docbookBuildingOptions, buildDate);
            }
        }
    }

    /**
     * Takes a comma separated list of ints, and returns an array of Integers. This is used when processing custom injection
     * points.
     */
    private static List<InjectionTopicData> processTopicIdList(final String list) {
        /* find the individual topic ids */
        final String[] topicIDs = list.split(",");

        List<InjectionTopicData> retValue = new ArrayList<InjectionTopicData>(topicIDs.length);

        /* clean the topic ids */
        for (final String topicID : topicIDs) {
            final String topicId = topicID.replaceAll(OPTIONAL_MARKER, "").trim();
            final boolean optional = topicID.contains(OPTIONAL_MARKER);

            try {
                final InjectionTopicData topicData = new InjectionTopicData(Integer.parseInt(topicId), optional);
                retValue.add(topicData);
            } catch (final NumberFormatException ex) {
                /*
                 * these lists are discovered by a regular expression so we shouldn't have any trouble here with Integer.parse
                 */
                LOG.debug("Unable to convert Injection Point ID into a Number", ex);
                retValue.add(new InjectionTopicData(-1, false));
            }
        }

        return retValue;
    }

    @SuppressWarnings("unchecked")
    public List<Integer> processInjections(final Level level, final SpecTopic topic, final ArrayList<Integer> customInjectionIds,
            final Document xmlDocument, final DocbookBuildingOptions docbookBuildingOptions, final TocTopicDatabase relatedTopicsDatabase,
            final boolean usedFixedUrls) {
        TocTopicDatabase relatedTopicDatabase = relatedTopicsDatabase;
        if (relatedTopicDatabase == null) {
            /*
             * get the outgoing relationships
             */
            final BaseTopicWrapper<?> topicWrapper = topic.getTopic();
            if (topicWrapper.getOutgoingRelationships() != null) {
                final List<? extends BaseTopicWrapper<?>> relatedTopics = topicWrapper.getOutgoingRelationships().getItems();

                /*
                 * Create a TocTopicDatabase to hold the related topics. The TocTopicDatabase provides a convenient way to access
                 * these topics
                 */
                relatedTopicDatabase = new TocTopicDatabase();
                relatedTopicDatabase.setTopics(relatedTopics);
            }
        }

        /*
         * this collection keeps a track of the injection point markers and the docbook lists that we will be replacing them
         * with
         */
        final HashMap<Node, InjectionListData> customInjections = new HashMap<Node, InjectionListData>();

        final List<Integer> errorTopics = new ArrayList<Integer>();

        errorTopics.addAll(processInjections(level, topic, customInjectionIds, customInjections, ORDEREDLIST_INJECTION_POINT, xmlDocument,
                CUSTOM_INJECTION_SEQUENCE_RE, null, relatedTopicDatabase, usedFixedUrls));
        errorTopics.addAll(processInjections(level, topic, customInjectionIds, customInjections, XREF_INJECTION_POINT, xmlDocument,
                CUSTOM_INJECTION_SINGLE_RE, null, relatedTopicDatabase, usedFixedUrls));
        errorTopics.addAll(processInjections(level, topic, customInjectionIds, customInjections, ITEMIZEDLIST_INJECTION_POINT, xmlDocument,
                CUSTOM_INJECTION_LIST_RE, null, relatedTopicDatabase, usedFixedUrls));
        errorTopics.addAll(processInjections(level, topic, customInjectionIds, customInjections, ITEMIZEDLIST_INJECTION_POINT, xmlDocument,
                CUSTOM_ALPHA_SORT_INJECTION_LIST_RE, new TopicTitleSorter(), relatedTopicDatabase, usedFixedUrls));
        errorTopics.addAll(processInjections(level, topic, customInjectionIds, customInjections, LIST_INJECTION_POINT, xmlDocument,
                CUSTOM_INJECTION_LISTITEMS_RE, null, relatedTopicDatabase, usedFixedUrls));

        /*
         * If we are not ignoring errors, return the list of topics that could not be injected
         */
        if (errorTopics.size() != 0 && docbookBuildingOptions != null && !docbookBuildingOptions.getIgnoreMissingCustomInjections())
            return errorTopics;

        /* now make the custom injection point substitutions */
        for (final Node customInjectionCommentNode : customInjections.keySet()) {
            final InjectionListData injectionListData = customInjections.get(customInjectionCommentNode);
            List<Element> list = null;

            /*
             * this may not be true if we are not building all related topics
             */
            if (injectionListData.listItems.size() != 0) {
                if (injectionListData.listType == ORDEREDLIST_INJECTION_POINT) {
                    list = DocBookUtilities.wrapOrderedListItemsInPara(xmlDocument, injectionListData.listItems);
                } else if (injectionListData.listType == XREF_INJECTION_POINT) {
                    list = injectionListData.listItems.get(0);
                } else if (injectionListData.listType == ITEMIZEDLIST_INJECTION_POINT) {
                    list = DocBookUtilities.wrapItemizedListItemsInPara(xmlDocument, injectionListData.listItems);
                } else if (injectionListData.listType == LIST_INJECTION_POINT) {
                    list = DocBookUtilities.wrapItemsInListItems(xmlDocument, injectionListData.listItems);
                }
            }

            if (list != null) {
                for (final Element element : list) {
                    customInjectionCommentNode.getParentNode().insertBefore(element, customInjectionCommentNode);
                }

                customInjectionCommentNode.getParentNode().removeChild(customInjectionCommentNode);
            }
        }

        return errorTopics;
    }

    public List<Integer> processInjections(final Level level, final SpecTopic topic, final ArrayList<Integer> customInjectionIds,
            final HashMap<Node, InjectionListData> customInjections, final int injectionPointType, final Document xmlDocument,
            final Pattern regularExpression, final ExternalListSort<Integer, BaseTopicWrapper<?>, InjectionTopicData> sortComparator,
            final TocTopicDatabase relatedTopicsDatabase, final boolean usedFixedUrls) {
        final List<Integer> retValue = new ArrayList<Integer>();

        if (xmlDocument == null) return retValue;

        /* loop over all of the comments in the document */
        for (final Node comment : XMLUtilities.getComments(xmlDocument)) {
            final String commentContent = comment.getNodeValue();

            /* find any matches */
            final Matcher injectionSequencematcher = regularExpression.matcher(commentContent);

            /* loop over the regular expression matches */
            while (injectionSequencematcher.find()) {
                /*
                 * get the list of topics from the named group in the regular expression match
                 */
                final String reMatch = injectionSequencematcher.group(TOPICIDS_RE_NAMED_GROUP);

                /* make sure we actually found a matching named group */
                if (reMatch != null) {
                    /* get the sequence of ids */
                    final List<InjectionTopicData> sequenceIDs = processTopicIdList(reMatch);

                    /* sort the InjectionTopicData list if required */
                    if (sortComparator != null) {
                        sortComparator.sort(relatedTopicsDatabase.getTopics(), sequenceIDs);
                    }

                    /* loop over all the topic ids in the injection point */
                    for (final InjectionTopicData sequenceID : sequenceIDs) {
                        /*
                         * topics that are injected into custom injection points are excluded from the generic related topic
                         * lists at the beginning and end of a topic. adding the topic id here means that when it comes time to
                         * generate the generic related topic lists, we can skip this topic
                         */
                        customInjectionIds.add(sequenceID.topicId);

                        /*
                         * Pull the topic out of the list of related topics
                         */
                        final BaseTopicWrapper<?> relatedTopic = relatedTopicsDatabase.getTopic(sequenceID.topicId);

                        /*
                         * See if the topic is also available in the main database (if the main database is available)
                         */
                        final boolean isInDatabase = level == null ? true : level.isSpecTopicInLevelByTopicID(sequenceID.topicId);

                        /*
                         * It is possible that the topic id referenced in the injection point has not been related, or has not
                         * been included in the list of topics to process. This is a validity error
                         */
                        if (relatedTopic != null && isInDatabase) {
                            /*
                             * build our list
                             */
                            List<List<Element>> list = new ArrayList<List<Element>>();

                            /*
                             * each related topic is added to a string, which is stored in the customInjections collection. the
                             * customInjections key is the custom injection text from the source xml. this allows us to match
                             * the xrefs we are generating for the related topic with the text in the xml file that these xrefs
                             * will eventually replace
                             */
                            if (customInjections.containsKey(comment)) list = customInjections.get(comment).listItems;

                            /* if the toc is null, we are building an internal page */
                            if (level == null) {
                                final String url = relatedTopic.getPressGangURL();
                                if (sequenceID.optional) {
                                    list.add(DocBookUtilities.buildEmphasisPrefixedULink(xmlDocument, OPTIONAL_LIST_PREFIX, url,
                                            relatedTopic.getTitle()));
                                } else {
                                    list.add(DocBookUtilities.buildULink(xmlDocument, url, relatedTopic.getTitle()));
                                }
                            } else {
                                final Integer topicId = relatedTopic.getTopicId();

                                final SpecTopic closestSpecTopic = topic.getClosestTopicByDBId(topicId, true);
                                if (sequenceID.optional) {
                                    list.add(DocBookUtilities.buildEmphasisPrefixedXRef(xmlDocument, OPTIONAL_LIST_PREFIX,
                                            closestSpecTopic.getUniqueLinkId(usedFixedUrls)));
                                } else {
                                    list.add(DocBookUtilities.buildXRef(xmlDocument, closestSpecTopic.getUniqueLinkId(usedFixedUrls)));
                                }
                            }

                            /*
                             * save the changes back into the customInjections collection
                             */
                            customInjections.put(comment, new InjectionListData(list, injectionPointType));
                        } else {
                            retValue.add(sequenceID.topicId);
                        }
                    }
                }
            }
        }

        return retValue;
    }

    /**
     * Insert a itemized list into the start of the topic, below the title with any PREVIOUS relationships that exists for the
     * Spec Topic. The title for the list is set to "Previous Step(s) in <TOPIC_PARENT_NAME>".
     *
     * @param topic        The topic to process the injection for.
     * @param doc          The DOM Document object that represents the topics XML.
     * @param useFixedUrls Whether fixed URL's should be used in the injected links.
     */
    public void processPrevRelationshipInjections(final SpecTopic topic, final Document doc, final boolean useFixedUrls) {
        if (topic.getPrevTopicRelationships().isEmpty()) return;

        // Get the title element so that it can be used later to add the prev topic node
        Element titleEle = null;
        final NodeList titleList = doc.getDocumentElement().getElementsByTagName("title");
        for (int i = 0; i < titleList.getLength(); i++) {
            if (titleList.item(i).getParentNode().equals(doc.getDocumentElement())) {
                titleEle = (Element) titleList.item(i);
                break;
            }
        }

        if (titleEle != null) {
            // Attempt to get the previous topic and process it
            final List<TopicRelationship> prevList = topic.getPrevTopicRelationships();
            // Create the paragraph/itemizedlist and list of previous relationships.
            final Element rootEle = doc.createElement("itemizedlist");
            rootEle.setAttribute("role", ROLE_PROCESS_PREVIOUS_ITEMIZED_LIST);

            // Create the title
            final Element linkTitleEle = doc.createElement("title");
            linkTitleEle.setAttribute("role", ROLE_PROCESS_PREVIOUS_TITLE);
            final String translatedString;
            if (prevList.size() > 1) {
                final String previousStepsTranslation = translations.getString(PREVIOUS_STEPS_PROPERTY);
                translatedString = previousStepsTranslation == null ? DEFAULT_PREVIOUS_STEPS : previousStepsTranslation;
            } else {
                final String previousStepTranslation = translations.getString(PREVIOUS_STEP_PROPERTY);
                translatedString = previousStepTranslation == null ? DEFAULT_PREVIOUS_STEP : previousStepTranslation;
            }

            /*
             * The translated String will have a format marker to specify where the link should be placed. So we need to split
             * the translated string on that marker and add content where it should be.
             */
            String[] split = translatedString.split("%s");

            // Add the first part of the translated string if any exists
            if (!split[0].trim().isEmpty()) {
                linkTitleEle.appendChild(doc.createTextNode(split[0]));
            }

            // Create the title link
            final Element titleXrefItem = doc.createElement("link");
            final Level level = (Level) topic.getParent();
            if (level.getTranslatedTitle() != null && !level.getTranslatedTitle().isEmpty()) {
                titleXrefItem.setTextContent(level.getTranslatedTitle());
            } else {
                titleXrefItem.setTextContent(level.getTitle());
            }
            titleXrefItem.setAttribute("linkend", ((Level) topic.getParent()).getUniqueLinkId(useFixedUrls));
            titleXrefItem.setAttribute("xrefstyle", ROLE_PROCESS_PREVIOUS_TITLE_LINK);
            linkTitleEle.appendChild(titleXrefItem);

            // Add the last part of the translated string if any exists
            if (split.length > 1 && !split[1].trim().isEmpty()) {
                linkTitleEle.appendChild(doc.createTextNode(split[1]));
            }

            rootEle.appendChild(linkTitleEle);

            for (final TopicRelationship prev : prevList) {
                final Element prevEle = doc.createElement("para");
                final SpecTopic prevTopic = prev.getSecondaryRelationship();

                // Add the previous element to either the list or paragraph
                // Create the link element
                final Element xrefItem = doc.createElement("xref");
                xrefItem.setAttribute("linkend", prevTopic.getUniqueLinkId(useFixedUrls));
                xrefItem.setAttribute("xrefstyle", ROLE_PROCESS_PREVIOUS_LINK);
                prevEle.appendChild(xrefItem);

                final Element listitemEle = doc.createElement("listitem");
                listitemEle.setAttribute("role", ROLE_PROCESS_PREVIOUS_LISTITEM);
                listitemEle.appendChild(prevEle);
                rootEle.appendChild(listitemEle);
            }

            // Insert the node after the title node
            Node nextNode = titleEle.getNextSibling();
            while (nextNode != null && nextNode.getNodeType() != Node.ELEMENT_NODE && nextNode.getNodeType() != Node.COMMENT_NODE) {
                nextNode = nextNode.getNextSibling();
            }
            doc.getDocumentElement().insertBefore(rootEle, nextNode);
        }
    }

    /**
     * Insert a itemized list into the end of the topic with any NEXT relationships that exists for the Spec Topic. The title
     * for the list is set to "Next Step(s) in <TOPIC_PARENT_NAME>".
     *
     * @param topic        The topic to process the injection for.
     * @param doc          The DOM Document object that represents the topics XML.
     * @param useFixedUrls Whether fixed URL's should be used in the injected links.
     */
    public void processNextRelationshipInjections(final SpecTopic topic, final Document doc, final boolean useFixedUrls) {
        if (topic.getNextTopicRelationships().isEmpty()) return;

        // Attempt to get the previous topic and process it
        final List<TopicRelationship> nextList = topic.getNextTopicRelationships();
        // Create the paragraph/itemizedlist and list of next relationships.
        final Element rootEle = doc.createElement("itemizedlist");
        rootEle.setAttribute("role", ROLE_PROCESS_NEXT_ITEMIZED_LIST);

        // Create the title
        final Element linkTitleEle = doc.createElement("title");
        linkTitleEle.setAttribute("role", ROLE_PROCESS_NEXT_TITLE);
        final String translatedString;
        if (nextList.size() > 1) {
            final String nextStepsTranslation = translations.getString(NEXT_STEPS_PROPERTY);
            translatedString = nextStepsTranslation == null ? DEFAULT_NEXT_STEPS : nextStepsTranslation;
        } else {
            final String nextStepTranslation = translations.getString(NEXT_STEP_PROPERTY);
            translatedString = nextStepTranslation == null ? DEFAULT_NEXT_STEP : nextStepTranslation;
        }

        /*
         * The translated String will have a format marker to specify where the link should be placed. So we need to split the
         * translated string on that marker and add content where it should be.
         */
        String[] split = translatedString.split("%s");

        // Add the first part of the translated string if any exists
        if (!split[0].trim().isEmpty()) {
            linkTitleEle.appendChild(doc.createTextNode(split[0]));
        }

        // Create the title link
        final Element titleXrefItem = doc.createElement("link");
        final Level level = (Level) topic.getParent();
        if (level.getTranslatedTitle() != null && !level.getTranslatedTitle().isEmpty()) {
            titleXrefItem.setTextContent(level.getTranslatedTitle());
        } else {
            titleXrefItem.setTextContent(level.getTitle());
        }
        titleXrefItem.setAttribute("linkend", ((Level) topic.getParent()).getUniqueLinkId(useFixedUrls));
        titleXrefItem.setAttribute("xrefstyle", ROLE_PROCESS_NEXT_TITLE_LINK);
        linkTitleEle.appendChild(titleXrefItem);

        // Add the last part of the translated string if any exists
        if (split.length > 1 && !split[1].trim().isEmpty()) {
            linkTitleEle.appendChild(doc.createTextNode(split[1]));
        }

        rootEle.appendChild(linkTitleEle);

        for (final TopicRelationship next : nextList) {
            final Element nextEle = doc.createElement("para");
            final SpecTopic nextTopic = next.getSecondaryRelationship();

            // Add the next element to either the list or paragraph
            // Create the link element
            final Element xrefItem = doc.createElement("xref");
            xrefItem.setAttribute("linkend", nextTopic.getUniqueLinkId(useFixedUrls));
            xrefItem.setAttribute("xrefstyle", ROLE_PROCESS_NEXT_LINK);
            nextEle.appendChild(xrefItem);

            final Element listitemEle = doc.createElement("listitem");
            listitemEle.setAttribute("role", ROLE_PROCESS_NEXT_LISTITEM);
            listitemEle.appendChild(nextEle);
            rootEle.appendChild(listitemEle);
        }

        // Add the node to the end of the XML data
        doc.getDocumentElement().appendChild(rootEle);
    }

    /**
     * Insert a itemized list into the start of the topic, below the title with any PREREQUISITE relationships that exists for
     * the Spec Topic. The title for the list is set to the "PREREQUISITE" property or "Prerequisites:" by default.
     *
     * @param topic        The topic to process the injection for.
     * @param doc          The DOM Document object that represents the topics XML.
     * @param useFixedUrls Whether fixed URL's should be used in the injected links.
     */
    public void processPrerequisiteInjections(final SpecTopic topic, final Document doc, final boolean useFixedUrls) {
        if (topic.getPrerequisiteRelationships().isEmpty()) return;

        // Get the title element so that it can be used later to add the prerequisite topic nodes
        Element titleEle = null;
        final NodeList titleList = doc.getDocumentElement().getElementsByTagName("title");
        for (int i = 0; i < titleList.getLength(); i++) {
            if (titleList.item(i).getParentNode().equals(doc.getDocumentElement())) {
                titleEle = (Element) titleList.item(i);
                break;
            }
        }

        if (titleEle != null) {
            // Create the paragraph and list of prerequisites.
            final Element itemisedListEle = doc.createElement("itemizedlist");
            itemisedListEle.setAttribute("role", ROLE_PREREQUISITE_LIST);
            final Element itemisedListTitleEle = doc.createElement("title");

            final String prerequisiteTranslation = translations.getString(PREREQUISITE_PROPERTY);
            itemisedListTitleEle.setTextContent(prerequisiteTranslation == null ? DEFAULT_PREREQUISITE : prerequisiteTranslation);

            itemisedListEle.appendChild(itemisedListTitleEle);
            final List<List<Element>> list = new LinkedList<List<Element>>();

            // Add the Relationships
            for (final Relationship prereq : topic.getPrerequisiteRelationships()) {
                if (prereq instanceof TopicRelationship) {
                    final SpecTopic relatedTopic = ((TopicRelationship) prereq).getSecondaryRelationship();

                    list.add(DocBookUtilities.buildXRef(doc, relatedTopic.getUniqueLinkId(useFixedUrls), ROLE_PREREQUISITE));
                } else {
                    final SpecNode specNode = ((TargetRelationship) prereq).getSecondaryRelationship();

                    list.add(DocBookUtilities.buildXRef(doc, specNode.getUniqueLinkId(useFixedUrls), ROLE_PREREQUISITE));
                }
            }

            // Wrap the items into an itemized list
            final List<Element> items = DocBookUtilities.wrapItemsInListItems(doc, list);
            for (final Element ele : items) {
                itemisedListEle.appendChild(ele);
            }

            // Add the paragraph and list after the title node
            Node nextNode = titleEle.getNextSibling();
            while (nextNode != null && nextNode.getNodeType() != Node.ELEMENT_NODE && nextNode.getNodeType() != Node.COMMENT_NODE) {
                nextNode = nextNode.getNextSibling();
            }

            doc.getDocumentElement().insertBefore(itemisedListEle, nextNode);
        }
    }

    /**
     * Insert a itemized list into the end of the topic with any RELATED relationships that exists for the Spec Topic. The title
     * for the list is set to "See Also:".
     *
     * @param topic        The topic to process the injection for.
     * @param doc          The DOM Document object that represents the topics XML.
     * @param useFixedUrls Whether fixed URL's should be used in the injected links.
     */
    public void processSeeAlsoInjections(final SpecTopic topic, final Document doc, final boolean useFixedUrls) {
        // Create the paragraph and list of prerequisites.
        if (topic.getRelatedRelationships().isEmpty()) return;
        final Element itemisedListEle = doc.createElement("itemizedlist");
        itemisedListEle.setAttribute("role", ROLE_SEE_ALSO_LIST);
        final Element itemisedListTitleEle = doc.createElement("title");

        final String seeAlsoTranslation = translations.getString(SEE_ALSO_PROPERTY);
        itemisedListTitleEle.setTextContent(seeAlsoTranslation == null ? DEFAULT_SEE_ALSO : seeAlsoTranslation);

        itemisedListEle.appendChild(itemisedListTitleEle);
        final List<List<Element>> list = new LinkedList<List<Element>>();

        // Add the Relationships
        for (final Relationship seeAlso : topic.getRelatedRelationships()) {
            if (seeAlso instanceof TopicRelationship) {
                final SpecTopic relatedTopic = ((TopicRelationship) seeAlso).getSecondaryRelationship();

                list.add(DocBookUtilities.buildXRef(doc, relatedTopic.getUniqueLinkId(useFixedUrls), ROLE_SEE_ALSO));
            } else {
                final SpecNode specNode = ((TargetRelationship) seeAlso).getSecondaryRelationship();

                list.add(DocBookUtilities.buildXRef(doc, specNode.getUniqueLinkId(useFixedUrls), ROLE_SEE_ALSO));
            }
        }

        // Wrap the items into an itemized list
        final List<Element> items = DocBookUtilities.wrapItemsInListItems(doc, list);
        for (final Element ele : items) {
            itemisedListEle.appendChild(ele);
        }

        // Add the paragraph and list after at the end of the xml data
        doc.getDocumentElement().appendChild(itemisedListEle);
    }

    /**
     * Insert a itemized list into the end of the topic with the any LINKLIST relationships that exists for the Spec Topic.
     *
     * @param topic        The topic to process the injection for.
     * @param doc          The DOM Document object that represents the topics XML.
     * @param useFixedUrls Whether fixed URL's should be used in the injected links.
     */
    public void processLinkListRelationshipInjections(final SpecTopic topic, final Document doc, final boolean useFixedUrls) {
        // Create the paragraph and list of prerequisites.
        if (topic.getLinkListRelationships().isEmpty()) return;
        final Element itemisedListEle = doc.createElement("itemizedlist");
        itemisedListEle.setAttribute("role", ROLE_LINK_LIST_LIST);
        final Element itemisedListTitleEle = doc.createElement("title");
        itemisedListTitleEle.setTextContent("");
        itemisedListEle.appendChild(itemisedListTitleEle);
        final List<List<Element>> list = new LinkedList<List<Element>>();

        // Add the Relationships
        for (final Relationship linkList : topic.getLinkListRelationships()) {
            if (linkList instanceof TopicRelationship) {
                final SpecTopic relatedTopic = ((TopicRelationship) linkList).getSecondaryRelationship();

                list.add(DocBookUtilities.buildXRef(doc, relatedTopic.getUniqueLinkId(useFixedUrls), ROLE_LINK_LIST));
            } else {
                final SpecNode specNode = ((TargetRelationship) linkList).getSecondaryRelationship();

                list.add(DocBookUtilities.buildXRef(doc, specNode.getUniqueLinkId(useFixedUrls), ROLE_LINK_LIST));
            }
        }

        // Wrap the items into an itemized list
        final List<Element> items = DocBookUtilities.wrapItemsInListItems(doc, list);
        for (final Element ele : items) {
            itemisedListEle.appendChild(ele);
        }

        // Add the paragraph and list after at the end of the xml data
        doc.getDocumentElement().appendChild(itemisedListEle);
    }

    public static String processDocumentType(final String xml) {
        assert xml != null : "The xml parameter can not be null";

        if (XMLUtilities.findDocumentType(xml) == null) {
            final String preamble = XMLUtilities.findPreamble(xml);
            final String fixedPreamble = preamble == null ? "" : preamble + "\n";
            final String fixedXML = preamble == null ? xml : xml.replace(preamble, "");

            return fixedPreamble + "<!DOCTYPE section PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\" \"http://www.oasis-open" +
                    ".org/docbook/xml/4.5/docbookx.dtd\" []>\n" + fixedXML;
        }

        return xml;
    }
}
