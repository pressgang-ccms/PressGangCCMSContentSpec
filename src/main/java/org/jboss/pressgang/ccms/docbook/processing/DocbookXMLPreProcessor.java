package org.jboss.pressgang.ccms.docbook.processing;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.google.code.regexp.NamedMatcher;
import com.google.code.regexp.NamedPattern;
import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.SpecNode;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.entities.BugzillaOptions;
import org.jboss.pressgang.ccms.contentspec.entities.Relationship;
import org.jboss.pressgang.ccms.contentspec.entities.TargetRelationship;
import org.jboss.pressgang.ccms.contentspec.entities.TopicRelationship;
import org.jboss.pressgang.ccms.contentspec.wrapper.PropertyTagInTagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.base.BaseTopicWrapper;
import org.jboss.pressgang.ccms.docbook.compiling.DocbookBuildingOptions;
import org.jboss.pressgang.ccms.docbook.constants.DocbookBuilderConstants;
import org.jboss.pressgang.ccms.docbook.sort.TopicTitleComparator;
import org.jboss.pressgang.ccms.docbook.sort.TopicTitleSorter;
import org.jboss.pressgang.ccms.docbook.structures.GenericInjectionPoint;
import org.jboss.pressgang.ccms.docbook.structures.GenericInjectionPointDatabase;
import org.jboss.pressgang.ccms.docbook.structures.InjectionListData;
import org.jboss.pressgang.ccms.docbook.structures.InjectionTopicData;
import org.jboss.pressgang.ccms.docbook.structures.TocTopicDatabase;
import org.jboss.pressgang.ccms.utils.common.CollectionUtilities;
import org.jboss.pressgang.ccms.utils.common.DocBookUtilities;
import org.jboss.pressgang.ccms.utils.common.ExceptionUtilities;
import org.jboss.pressgang.ccms.utils.common.XMLUtilities;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.utils.sort.ExternalListSort;
import org.jboss.pressgang.ccms.utils.structures.Pair;
import org.jboss.pressgang.ccms.zanata.ZanataDetails;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * This class takes the XML from a topic and modifies it to include and injected content.
 */
public class DocbookXMLPreProcessor {
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
    public static final String CUSTOM_INJECTION_SEQUENCE_RE =
    /*
     * start xml comment and 'InjectSequence:' surrounded by optional white space
     */
            "\\s*InjectSequence:\\s*" +
    /*
     * an optional comma separated list of digit blocks, and at least one digit block with an optional comma
     */
                    "(?<" + TOPICIDS_RE_NAMED_GROUP + ">(\\s*" + OPTIONAL_TOPIC_ID_RE + "\\s*,)*(\\s*" + OPTIONAL_TOPIC_ID_RE + ",?))" +
    /* xml comment end */
                    "\\s*";

    /**
     * A regular expression that matches an InjectList custom injection point
     */
    public static final String CUSTOM_INJECTION_LIST_RE =
    /* start xml comment and 'InjectList:' surrounded by optional white space */
            "\\s*InjectList:\\s*" +
    /*
     * an optional comma separated list of digit blocks, and at least one digit block with an optional comma
     */
                    "(?<" + TOPICIDS_RE_NAMED_GROUP + ">(\\s*" + OPTIONAL_TOPIC_ID_RE + "\\s*,)*(\\s*" + OPTIONAL_TOPIC_ID_RE + ",?))" +
    /* xml comment end */
                    "\\s*";

    public static final String CUSTOM_INJECTION_LISTITEMS_RE =
    /* start xml comment and 'InjectList:' surrounded by optional white space */
            "\\s*InjectListItems:\\s*" +
    /*
     * an optional comma separated list of digit blocks, and at least one digit block with an optional comma
     */
                    "(?<" + TOPICIDS_RE_NAMED_GROUP + ">(\\s*" + OPTIONAL_TOPIC_ID_RE + "\\s*,)*(\\s*" + OPTIONAL_TOPIC_ID_RE + ",?))" +
    /* xml comment end */
                    "\\s*";

    public static final String CUSTOM_ALPHA_SORT_INJECTION_LIST_RE =
    /*
     * start xml comment and 'InjectListAlphaSort:' surrounded by optional white space
     */
            "\\s*InjectListAlphaSort:\\s*" +
    /*
     * an optional comma separated list of digit blocks, and at least one digit block with an optional comma
     */
                    "(?<" + TOPICIDS_RE_NAMED_GROUP + ">(\\s*" + OPTIONAL_TOPIC_ID_RE + "\\s*,)*(\\s*" + OPTIONAL_TOPIC_ID_RE + ",?))" +
    /* xml comment end */
                    "\\s*";

    /**
     * A regular expression that matches an Inject custom injection point
     */
    public static final String CUSTOM_INJECTION_SINGLE_RE =
    /* start xml comment and 'Inject:' surrounded by optional white space */
            "\\s*Inject:\\s*" +
    /* one digit block */
                    "(?<" + TOPICIDS_RE_NAMED_GROUP + ">(" + OPTIONAL_TOPIC_ID_RE + "))" +
    /* xml comment end */
                    "\\s*";

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

    protected final Properties translations;

    public DocbookXMLPreProcessor() {
        this.translations = new Properties();
    }

    public DocbookXMLPreProcessor(final Properties translationStrings) {
        this.translations = translationStrings;
    }

    public void processTopicBugzillaLink(final SpecTopic specTopic, final Document document, final BugzillaOptions bzOptions,
            final DocbookBuildingOptions docbookBuildingOptions, final String buildName, final Date buildDate) {
        final BaseTopicWrapper<?> topic = specTopic.getTopic();

        /* SIMPLESECT TO HOLD OTHER LINKS */
        final Element bugzillaSection = document.createElement("simplesect");
        document.getDocumentElement().appendChild(bugzillaSection);

        final Element bugzillaSectionTitle = document.createElement("title");
        bugzillaSectionTitle.setTextContent("");
        bugzillaSection.appendChild(bugzillaSectionTitle);

        /* BUGZILLA LINK */
        try {
            final String instanceNameProperty = System.getProperty(CommonConstants.INSTANCE_NAME_PROPERTY);
            final String fixedInstanceNameProperty = instanceNameProperty == null ? "Not Defined" : instanceNameProperty;

            final Element bugzillaPara = document.createElement("para");
            bugzillaPara.setAttribute("role", ROLE_CREATE_BUG_PARA);

            final Element bugzillaULink = document.createElement("ulink");

            final String reportBugTranslation = translations.getProperty(REPORT_A_BUG_PROPERTY);
            bugzillaULink.setTextContent(reportBugTranslation == null ? DEFAULT_REPORT_A_BUG : reportBugTranslation);

            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            String specifiedBuildName = "";
            if (docbookBuildingOptions != null && docbookBuildingOptions.getBuildName() != null)
                specifiedBuildName = docbookBuildingOptions.getBuildName();

            /* build up the elements that go into the bugzilla URL */
            String bugzillaProduct = null;
            String bugzillaComponent = null;
            String bugzillaVersion = null;
            String bugzillaKeywords = null;
            String bugzillaAssignedTo = null;
            final String bugzillaEnvironment = URLEncoder.encode(
                    "Instance Name: " + fixedInstanceNameProperty + "\n" + "Build: " + buildName + "\n" + "Build Name: " +
                            specifiedBuildName + "\n" + "Build Date: " + formatter.format(buildDate), "UTF-8");
            final String bugzillaSummary = URLEncoder.encode(topic.getTitle(), "UTF-8");
            final StringBuilder bugzillaBuildID = new StringBuilder();
            bugzillaBuildID.append(topic.getBugzillaBuildId());

            if (specTopic.getRevision() == null) {
                bugzillaBuildID.append(" [Latest]");
            } else {
                bugzillaBuildID.append(" [Specified]");
            }

            /* look for the bugzilla options */
            if (topic.getTags() != null && topic.getTags() != null) {
                final List<TagWrapper> tags = topic.getTags().getItems();
                for (final TagWrapper tag : tags) {
                    final PropertyTagInTagWrapper bugzillaProductTag = tag.getProperty(CommonConstants.BUGZILLA_PRODUCT_PROP_TAG_ID);
                    final PropertyTagInTagWrapper bugzillaComponentTag = tag.getProperty(CommonConstants.BUGZILLA_COMPONENT_PROP_TAG_ID);
                    final PropertyTagInTagWrapper bugzillaKeywordsTag = tag.getProperty(CommonConstants.BUGZILLA_KEYWORDS_PROP_TAG_ID);
                    final PropertyTagInTagWrapper bugzillaVersionTag = tag.getProperty(CommonConstants.BUGZILLA_VERSION_PROP_TAG_ID);
                    final PropertyTagInTagWrapper bugzillaAssignedToTag = tag.getProperty(CommonConstants.BUGZILLA_PROFILE_PROPERTY);

                    if (bugzillaProduct == null && bugzillaProductTag != null)
                        bugzillaProduct = URLEncoder.encode(bugzillaProductTag.getValue(), "UTF-8");

                    if (bugzillaComponent == null && bugzillaComponentTag != null)
                        bugzillaComponent = URLEncoder.encode(bugzillaComponentTag.getValue(), "UTF-8");

                    if (bugzillaKeywords == null && bugzillaKeywordsTag != null)
                        bugzillaKeywords = URLEncoder.encode(bugzillaKeywordsTag.getValue(), "UTF-8");

                    if (bugzillaVersion == null && bugzillaVersionTag != null)
                        bugzillaVersion = URLEncoder.encode(bugzillaVersionTag.getValue(), "UTF-8");

                    if (bugzillaAssignedTo == null && bugzillaAssignedToTag != null)
                        bugzillaAssignedTo = URLEncoder.encode(bugzillaAssignedToTag.getValue(), "UTF-8");
                }
            }

            /* build the bugzilla url options */
            String bugzillaURLComponents = "";

            bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
            bugzillaURLComponents += "cf_environment=" + bugzillaEnvironment;

            bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
            bugzillaURLComponents += "cf_build_id=" + URLEncoder.encode(bugzillaBuildID.toString(), "UTF-8");

            bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
            bugzillaURLComponents += "short_desc=" + bugzillaSummary;

            if (bugzillaAssignedTo != null) {
                bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
                bugzillaURLComponents += "assigned_to=" + bugzillaAssignedTo;
            }

            /* check the content spec options first */
            if (bzOptions != null && bzOptions.getProduct() != null) {
                bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
                bugzillaURLComponents += "product=" + URLEncoder.encode(bzOptions.getProduct(), "UTF-8");

                if (bzOptions.getComponent() != null) {
                    bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
                    bugzillaURLComponents += "component=" + URLEncoder.encode(bzOptions.getComponent(), "UTF-8");
                }

                if (bzOptions.getVersion() != null) {
                    bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
                    bugzillaURLComponents += "version=" + URLEncoder.encode(bzOptions.getVersion(), "UTF-8");
                }
            }
            /* we need at least a product */
            else if (bugzillaProduct != null) {
                bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
                bugzillaURLComponents += "product=" + bugzillaProduct;

                if (bugzillaComponent != null) {
                    bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
                    bugzillaURLComponents += "component=" + bugzillaComponent;
                }

                if (bugzillaVersion != null) {
                    bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
                    bugzillaURLComponents += "version=" + bugzillaVersion;
                }

                if (bugzillaKeywords != null) {
                    bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
                    bugzillaURLComponents += "keywords=" + bugzillaKeywords;
                }
            }

            /* build the bugzilla url with the base components */
            String bugzillaUrl = "https://bugzilla.redhat.com/enter_bug.cgi" + bugzillaURLComponents;

            bugzillaULink.setAttribute("url", bugzillaUrl);

            /*
             * only add the elements to the XML DOM if there was no exception (not that there should be one
             */
            bugzillaSection.appendChild(bugzillaPara);
            bugzillaPara.appendChild(bugzillaULink);
        } catch (final Exception ex) {
            ExceptionUtilities.handleException(ex);
        }
    }

    /**
     * Adds some debug information and links to the end of the topic
     */
    public void processTopicAdditionalInfo(final SpecTopic specTopic, final Document document, final BugzillaOptions bzOptions,
            final DocbookBuildingOptions docbookBuildingOptions, final String buildName, final Date buildDate,
            final ZanataDetails zanataDetails) {
        final BaseTopicWrapper<?> topic = specTopic.getTopic();

        if ((docbookBuildingOptions != null && (docbookBuildingOptions.getInsertSurveyLink() || docbookBuildingOptions
                .getInsertEditorLinks()))) {
            /* SIMPLESECT TO HOLD OTHER LINKS */
            final Element bugzillaSection = document.createElement("simplesect");
            document.getDocumentElement().appendChild(bugzillaSection);

            final Element bugzillaSectionTitle = document.createElement("title");
            bugzillaSectionTitle.setTextContent("");
            bugzillaSection.appendChild(bugzillaSectionTitle);

            // SURVEY LINK
            if (docbookBuildingOptions != null && docbookBuildingOptions.getInsertSurveyLink()) {
                final Element surveyPara = document.createElement("para");
                surveyPara.setAttribute("role", ROLE_CREATE_BUG_PARA);
                bugzillaSection.appendChild(surveyPara);

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

            // EDITOR LINK
            if (docbookBuildingOptions != null && docbookBuildingOptions.getInsertEditorLinks()) {
                final String editorUrl = topic.getEditorURL(zanataDetails);

                final Element editorLinkPara = document.createElement("para");
                editorLinkPara.setAttribute("role", ROLE_CREATE_BUG_PARA);
                bugzillaSection.appendChild(editorLinkPara);

                if (editorUrl != null) {
                    final Element surveyULink = document.createElement("ulink");
                    editorLinkPara.appendChild(surveyULink);
                    surveyULink.setTextContent("Edit this topic");
                    surveyULink.setAttribute("url", editorUrl);
                } else {
                    /*
                     * Since the returnEditorURL method only returns null for translations we don't need to check the topic
                     * type.
                     */
                    editorLinkPara.setTextContent("No editor available for this topic, as it hasn't been pushed for translation.");
                }
            }
        }

        // BUGZILLA LINK
        if (docbookBuildingOptions != null && docbookBuildingOptions.getInsertBugzillaLinks()) {
            processTopicBugzillaLink(specTopic, document, bzOptions, docbookBuildingOptions, buildName, buildDate);
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
        for (int i = 0; i < topicIDs.length; ++i) {
            final String topicId = topicIDs[i].replaceAll(OPTIONAL_MARKER, "").trim();
            final boolean optional = topicIDs[i].indexOf(OPTIONAL_MARKER) != -1;

            try {
                final InjectionTopicData topicData = new InjectionTopicData(Integer.parseInt(topicId), optional);
                retValue.add(topicData);
            } catch (final Exception ex) {
                /*
                 * these lists are discovered by a regular expression so we shouldn't have any trouble here with Integer.parse
                 */
                ExceptionUtilities.handleException(ex);
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
                CUSTOM_INJECTION_SEQUENCE_RE, null, docbookBuildingOptions, relatedTopicDatabase, usedFixedUrls));
        errorTopics.addAll(processInjections(level, topic, customInjectionIds, customInjections, XREF_INJECTION_POINT, xmlDocument,
                CUSTOM_INJECTION_SINGLE_RE, null, docbookBuildingOptions, relatedTopicDatabase, usedFixedUrls));
        errorTopics.addAll(processInjections(level, topic, customInjectionIds, customInjections, ITEMIZEDLIST_INJECTION_POINT, xmlDocument,
                CUSTOM_INJECTION_LIST_RE, null, docbookBuildingOptions, relatedTopicDatabase, usedFixedUrls));
        errorTopics.addAll(processInjections(level, topic, customInjectionIds, customInjections, ITEMIZEDLIST_INJECTION_POINT, xmlDocument,
                CUSTOM_ALPHA_SORT_INJECTION_LIST_RE, new TopicTitleSorter(), docbookBuildingOptions, relatedTopicDatabase, usedFixedUrls));
        errorTopics.addAll(processInjections(level, topic, customInjectionIds, customInjections, LIST_INJECTION_POINT, xmlDocument,
                CUSTOM_INJECTION_LISTITEMS_RE, null, docbookBuildingOptions, relatedTopicDatabase, usedFixedUrls));

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
            final String regularExpression, final ExternalListSort<Integer, BaseTopicWrapper<?>, InjectionTopicData> sortComparator,
            final DocbookBuildingOptions docbookBuildingOptions, final TocTopicDatabase relatedTopicsDatabase,
            final boolean usedFixedUrls) {
        final List<Integer> retValue = new ArrayList<Integer>();

        if (xmlDocument == null) return retValue;

        /* loop over all of the comments in the document */
        for (final Node comment : XMLUtilities.getComments(xmlDocument)) {
            final String commentContent = comment.getNodeValue();

            /* compile the regular expression */
            final NamedPattern injectionSequencePattern = NamedPattern.compile(regularExpression);
            /* find any matches */
            final NamedMatcher injectionSequencematcher = injectionSequencePattern.matcher(commentContent);

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
                                final String url = relatedTopic.getInternalURL();
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

    @SuppressWarnings("unchecked")
    public List<Integer> processGenericInjections(final Level level, final SpecTopic specTopic, final Document xmlDocument,
            final ArrayList<Integer> customInjectionIds, final List<Pair<Integer, String>> topicTypeTagIDs,
            final DocbookBuildingOptions docbookBuildingOptions, final boolean usedFixedUrls) {
        final List<Integer> errors = new ArrayList<Integer>();

        if (xmlDocument == null) return errors;

        /*
         * this collection will hold the lists of related topics
         */
        final GenericInjectionPointDatabase relatedLists = new GenericInjectionPointDatabase();

        // Get the topic instance
        final BaseTopicWrapper<?> topic = specTopic.getTopic();

        /* wrap each related topic in a listitem tag */
        if (topic.getOutgoingRelationships() != null && topic.getOutgoingRelationships() != null) {
            final List<BaseTopicWrapper<?>> relatedTopics = (List<BaseTopicWrapper<?>>) topic.getOutgoingRelationships();
            for (final BaseTopicWrapper<?> relatedTopic : relatedTopics) {
                final Integer topicId = relatedTopic.getTopicId();

                /*
                 * don't process those topics that were injected into custom injection points
                 */
                if (!customInjectionIds.contains(topicId)) {
                    /* make sure the topic is available to be linked to */
                    if (level != null && !level.isSpecTopicInLevelByTopicID(topicId)) {
                        if ((docbookBuildingOptions != null && !docbookBuildingOptions.getIgnoreMissingCustomInjections()))
                            errors.add(relatedTopic.getTopicId());
                    } else {
                        // loop through the topic type tags
                        for (final Pair<Integer, String> primaryTopicTypeTag : topicTypeTagIDs) {
                            /*
                             * see if we have processed a related topic with one of the topic type tags this may never be true
                             * if not processing all related topics
                             */
                            if (relatedTopic.hasTag(primaryTopicTypeTag.getFirst())) {
                                relatedLists.addInjectionTopic(primaryTopicTypeTag, relatedTopic);

                                break;
                            }
                        }
                    }
                }
            }
        }

        insertGenericInjectionLinks(level, specTopic, xmlDocument, relatedLists, docbookBuildingOptions, usedFixedUrls);

        return errors;
    }

    /**
     * The generic injection points are placed in well defined locations within a topics xml structure. This function takes the
     * list of related topics and the topic type tags that are associated with them and injects them into the xml document.
     */
    private void insertGenericInjectionLinks(final Level level, final SpecTopic topic, final Document xmlDoc,
            final GenericInjectionPointDatabase relatedLists, final DocbookBuildingOptions docbookBuildingOptions,
            final boolean usedFixedUrls) {
        /* all related topics are placed before the first simplesect */
        final NodeList nodes = xmlDoc.getDocumentElement().getChildNodes();
        Node simplesectNode = null;
        for (int i = 0; i < nodes.getLength(); ++i) {
            final Node node = nodes.item(i);
            if (node.getNodeType() == 1 && node.getNodeName().equals("simplesect")) {
                simplesectNode = node;
                break;
            }
        }

        /*
         * place the topics at the end of the topic. They will appear in the reverse order as the call to toArrayList()
         */
        for (final Integer topTag : CollectionUtilities.toArrayList(DocbookBuilderConstants.REFERENCE_TAG_ID,
                DocbookBuilderConstants.TASK_TAG_ID, DocbookBuilderConstants.CONCEPT_TAG_ID,
                DocbookBuilderConstants.CONCEPTUALOVERVIEW_TAG_ID)) {
            for (final GenericInjectionPoint genericInjectionPoint : relatedLists.getInjectionPoints()) {
                if (genericInjectionPoint.getCategoryIDAndName().getFirst() == topTag) {
                    final List<BaseTopicWrapper<?>> relatedTopics = genericInjectionPoint.getTopics();

                    /* don't add an empty list */
                    if (relatedTopics.size() != 0) {
                        final Node itemizedlist = DocBookUtilities.createRelatedTopicItemizedList(xmlDoc,
                                "Related " + genericInjectionPoint.getCategoryIDAndName().getSecond() + "s");

                        Collections.sort(relatedTopics, new TopicTitleComparator());

                        for (final BaseTopicWrapper<?> relatedTopic : relatedTopics) {
                            if (level == null) {
                                final String internalURL = relatedTopic.getInternalURL();
                                DocBookUtilities.createRelatedTopicULink(xmlDoc, internalURL, relatedTopic.getTitle(), itemizedlist);
                            } else {
                                final Integer topicId = relatedTopic.getTopicId();

                                final SpecTopic closestSpecTopic = topic.getClosestTopicByDBId(topicId, true);
                                DocBookUtilities.createRelatedTopicXRef(xmlDoc, closestSpecTopic.getUniqueLinkId(usedFixedUrls),
                                        itemizedlist);
                            }

                        }

                        if (simplesectNode != null) xmlDoc.getDocumentElement().insertBefore(itemizedlist, simplesectNode);
                        else xmlDoc.getDocumentElement().appendChild(itemizedlist);
                    }
                }
            }
        }
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
                final String previousStepsTranslation = translations.getProperty(PREVIOUS_STEPS_PROPERTY);
                translatedString = previousStepsTranslation == null ? DEFAULT_PREVIOUS_STEPS : previousStepsTranslation;
            } else {
                final String previousStepTranslation = translations.getProperty(PREVIOUS_STEP_PROPERTY);
                translatedString = previousStepTranslation == null ? DEFAULT_PREVIOUS_STEP : previousStepTranslation;
            }

            /*
             * The translated String will have a format marker to specify where the link should be placed. So we need to split
             * the translated string on that marker and add content where it should be.
             */
            String[] split = translatedString.split("\\%s");

            // Add the first part of the translated string if any exists
            if (!split[0].trim().isEmpty()) {
                linkTitleEle.appendChild(doc.createTextNode(split[0]));
            }

            // Create the title link
            final Element titleXrefItem = doc.createElement("link");
            titleXrefItem.setTextContent(topic.getParent().getTitle());
            titleXrefItem.setAttribute("linkend", topic.getParent().getUniqueLinkId(useFixedUrls));
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
            final String nextStepsTranslation = translations.getProperty(NEXT_STEPS_PROPERTY);
            translatedString = nextStepsTranslation == null ? DEFAULT_NEXT_STEPS : nextStepsTranslation;
        } else {
            final String nextStepTranslation = translations.getProperty(NEXT_STEP_PROPERTY);
            translatedString = nextStepTranslation == null ? DEFAULT_NEXT_STEP : nextStepTranslation;
        }

        /*
         * The translated String will have a format marker to specify where the link should be placed. So we need to split the
         * translated string on that marker and add content where it should be.
         */
        String[] split = translatedString.split("\\%s");

        // Add the first part of the translated string if any exists
        if (!split[0].trim().isEmpty()) {
            linkTitleEle.appendChild(doc.createTextNode(split[0]));
        }

        // Create the title link
        final Element titleXrefItem = doc.createElement("link");
        titleXrefItem.setTextContent(topic.getParent().getTitle());
        titleXrefItem.setAttribute("linkend", topic.getParent().getUniqueLinkId(useFixedUrls));
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
            final Element formalParaEle = doc.createElement("formalpara");
            formalParaEle.setAttribute("role", ROLE_PREREQUISITE_LIST);
            final Element formalParaTitleEle = doc.createElement("title");

            final String prerequisiteTranslation = translations.getProperty(PREREQUISITE_PROPERTY);
            formalParaTitleEle.setTextContent(prerequisiteTranslation == null ? DEFAULT_PREREQUISITE : prerequisiteTranslation);

            formalParaEle.appendChild(formalParaTitleEle);
            final List<List<Element>> list = new LinkedList<List<Element>>();

            // Add the Relationships
            for (final Relationship prereq : topic.getPrerequisiteRelationships()) {
                if (prereq instanceof TopicRelationship) {
                    final SpecTopic relatedTopic = ((TopicRelationship) prereq).getSecondaryRelationship();

                    list.add(DocBookUtilities.buildXRef(doc, relatedTopic.getUniqueLinkId(useFixedUrls), ROLE_PREREQUISITE));
                } else {
                    final SpecNode specNode = ((TargetRelationship) prereq).getSecondaryElement();

                    list.add(DocBookUtilities.buildXRef(doc, specNode.getUniqueLinkId(useFixedUrls), ROLE_PREREQUISITE));
                }
            }

            // Wrap the items into an itemized list
            final List<Element> items = DocBookUtilities.wrapItemizedListItemsInPara(doc, list);
            for (final Element ele : items) {
                formalParaEle.appendChild(ele);
            }

            // Add the paragraph and list after the title node
            Node nextNode = titleEle.getNextSibling();
            while (nextNode != null && nextNode.getNodeType() != Node.ELEMENT_NODE && nextNode.getNodeType() != Node.COMMENT_NODE) {
                nextNode = nextNode.getNextSibling();
            }

            doc.getDocumentElement().insertBefore(formalParaEle, nextNode);
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
        final Element formalParaEle = doc.createElement("formalpara");
        formalParaEle.setAttribute("role", ROLE_SEE_ALSO_LIST);
        final Element formalParaTitleEle = doc.createElement("title");

        final String seeAlsoTranslation = translations.getProperty(SEE_ALSO_PROPERTY);
        formalParaTitleEle.setTextContent(seeAlsoTranslation == null ? DEFAULT_SEE_ALSO : seeAlsoTranslation);

        formalParaEle.appendChild(formalParaTitleEle);
        final List<List<Element>> list = new LinkedList<List<Element>>();

        // Add the Relationships
        for (final Relationship seeAlso : topic.getRelatedRelationships()) {
            if (seeAlso instanceof TopicRelationship) {
                final SpecTopic relatedTopic = ((TopicRelationship) seeAlso).getSecondaryRelationship();

                list.add(DocBookUtilities.buildXRef(doc, relatedTopic.getUniqueLinkId(useFixedUrls), ROLE_SEE_ALSO));
            } else {
                final SpecNode specNode = ((TargetRelationship) seeAlso).getSecondaryElement();

                list.add(DocBookUtilities.buildXRef(doc, specNode.getUniqueLinkId(useFixedUrls), ROLE_SEE_ALSO));
            }
        }

        // Wrap the items into an itemized list
        final List<Element> items = DocBookUtilities.wrapItemizedListItemsInPara(doc, list);
        for (final Element ele : items) {
            formalParaEle.appendChild(ele);
        }

        // Add the paragraph and list after at the end of the xml data
        doc.getDocumentElement().appendChild(formalParaEle);
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
        final Element formalParaEle = doc.createElement("formalpara");
        formalParaEle.setAttribute("role", ROLE_LINK_LIST_LIST);
        final Element formalParaTitleEle = doc.createElement("title");
        formalParaTitleEle.setTextContent("");
        formalParaEle.appendChild(formalParaTitleEle);
        final List<List<Element>> list = new LinkedList<List<Element>>();

        // Add the Relationships
        for (final Relationship linkList : topic.getLinkListRelationships()) {
            if (linkList instanceof TopicRelationship) {
                final SpecTopic relatedTopic = ((TopicRelationship) linkList).getSecondaryRelationship();

                list.add(DocBookUtilities.buildXRef(doc, relatedTopic.getUniqueLinkId(useFixedUrls), ROLE_LINK_LIST));
            } else {
                final SpecNode specNode = ((TargetRelationship) linkList).getSecondaryElement();

                list.add(DocBookUtilities.buildXRef(doc, specNode.getUniqueLinkId(useFixedUrls), ROLE_LINK_LIST));
            }
        }

        // Wrap the items into an itemized list
        final List<Element> items = DocBookUtilities.wrapItemizedListItemsInPara(doc, list);
        for (final Element ele : items) {
            formalParaEle.appendChild(ele);
        }

        // Add the paragraph and list after at the end of the xml data
        doc.getDocumentElement().appendChild(formalParaEle);
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
