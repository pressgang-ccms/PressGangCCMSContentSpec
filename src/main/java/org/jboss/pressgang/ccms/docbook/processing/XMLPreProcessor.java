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

import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.SpecNode;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.entities.BugzillaOptions;
import org.jboss.pressgang.ccms.contentspec.entities.Relationship;
import org.jboss.pressgang.ccms.contentspec.entities.TargetRelationship;
import org.jboss.pressgang.ccms.contentspec.entities.TopicRelationship;
import org.jboss.pressgang.ccms.docbook.compiling.DocbookBuildingOptions;
import org.jboss.pressgang.ccms.docbook.constants.DocbookBuilderConstants;
import org.jboss.pressgang.ccms.docbook.sort.TopicTitleSorter;
import org.jboss.pressgang.ccms.docbook.structures.GenericInjectionPoint;
import org.jboss.pressgang.ccms.docbook.structures.GenericInjectionPointDatabase;
import org.jboss.pressgang.ccms.docbook.structures.InjectionListData;
import org.jboss.pressgang.ccms.docbook.structures.InjectionTopicData;
import org.jboss.pressgang.ccms.docbook.structures.TocTopicDatabase;
import org.jboss.pressgang.ccms.rest.v1.components.ComponentBaseRESTEntityWithPropertiesV1;
import org.jboss.pressgang.ccms.rest.v1.components.ComponentBaseTopicV1;
import org.jboss.pressgang.ccms.rest.v1.components.ComponentTopicV1;
import org.jboss.pressgang.ccms.rest.v1.components.ComponentTranslatedTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTagV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTranslatedTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.base.RESTBaseTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.join.RESTAssignedPropertyTagV1;
import org.jboss.pressgang.ccms.rest.v1.sort.BaseTopicV1TitleComparator;
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

import com.google.code.regexp.NamedMatcher;
import com.google.code.regexp.NamedPattern;

/**
 * This class takes the XML from a topic and modifies it to include and injected content.
 */
public class XMLPreProcessor //<T extends RESTBaseTopicV1<T, ?, ?>>//<T extends RESTBaseTopicV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
{
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
	/** Identifies a named regular expression group */
	protected static final String TOPICIDS_RE_NAMED_GROUP = "TopicIDs";
	/** This text identifies an option task in a list */
	protected static final String OPTIONAL_MARKER = "OPT:";
	/** The text to be prefixed to a list item if a topic is optional */
	protected static final String OPTIONAL_LIST_PREFIX = "Optional: ";
	/** A regular expression that identifies a topic id */
	protected static final String OPTIONAL_TOPIC_ID_RE = "(" + OPTIONAL_MARKER + "\\s*)?\\d+";
	/** A regular expression that identifies a topic id */
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

	/** A regular expression that matches an InjectList custom injection point */
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

	/** A regular expression that matches an Inject custom injection point */
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

	public void processTopicBugzillaLink(final SpecTopic specTopic, final Document document, final BugzillaOptions bzOptions, final DocbookBuildingOptions docbookBuildingOptions, final String buildName, final String searchTagsUrl, final Date buildDate)
	{
		final RESTBaseTopicV1<?, ?, ?> topic = specTopic.getTopic();
		
		/* SIMPLESECT TO HOLD OTHER LINKS */
		final Element bugzillaSection = document.createElement("simplesect");
		document.getDocumentElement().appendChild(bugzillaSection);

		final Element bugzillaSectionTitle = document.createElement("title");
		bugzillaSectionTitle.setTextContent("");
		bugzillaSection.appendChild(bugzillaSectionTitle);

		/* BUGZILLA LINK */
		try
		{
			final String instanceNameProperty = System.getProperty(CommonConstants.INSTANCE_NAME_PROPERTY);
			final String fixedInstanceNameProperty = instanceNameProperty == null ? "Not Defined" : instanceNameProperty;

			final Element bugzillaPara = document.createElement("para");
			bugzillaPara.setAttribute("role", DocbookBuilderConstants.ROLE_CREATE_BUG_PARA);

			final Element bugzillaULink = document.createElement("ulink");

			bugzillaULink.setTextContent("Report a bug");

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
			        "Instance Name: " + fixedInstanceNameProperty + "\n" +
			        "Build: " + buildName + "\n" +
			        "Build Filter: " + searchTagsUrl + "\n" + 
			        "Build Name: " + specifiedBuildName + "\n" +
			        "Build Date: " + formatter.format(buildDate), "UTF-8");
			final String bugzillaSummary = URLEncoder.encode(topic.getTitle(), "UTF-8");
			final String bugzillaBuildID =  topic instanceof RESTTranslatedTopicV1 ? 
			        URLEncoder.encode(ComponentTranslatedTopicV1.returnBugzillaBuildId((RESTTranslatedTopicV1) topic), "UTF-8") : URLEncoder.encode(ComponentTopicV1.returnBugzillaBuildId((RESTTopicV1) topic), "UTF-8");

			/* look for the bugzilla options */
			if (topic.getTags() != null && topic.getTags().getItems() != null)
			{
			    final List<RESTTagV1> tags = topic.getTags().returnItems();
				for (final RESTTagV1 tag : tags)
				{
					final RESTAssignedPropertyTagV1 bugzillaProductTag = ComponentBaseRESTEntityWithPropertiesV1.returnProperty(tag, CommonConstants.BUGZILLA_PRODUCT_PROP_TAG_ID);
					final RESTAssignedPropertyTagV1 bugzillaComponentTag = ComponentBaseRESTEntityWithPropertiesV1.returnProperty(tag, CommonConstants.BUGZILLA_COMPONENT_PROP_TAG_ID);
					final RESTAssignedPropertyTagV1 bugzillaKeywordsTag = ComponentBaseRESTEntityWithPropertiesV1.returnProperty(tag, CommonConstants.BUGZILLA_KEYWORDS_PROP_TAG_ID);
					final RESTAssignedPropertyTagV1 bugzillaVersionTag = ComponentBaseRESTEntityWithPropertiesV1.returnProperty(tag, CommonConstants.BUGZILLA_VERSION_PROP_TAG_ID);
					final RESTAssignedPropertyTagV1 bugzillaAssignedToTag = ComponentBaseRESTEntityWithPropertiesV1.returnProperty(tag, CommonConstants.BUGZILLA_PROFILE_PROPERTY);

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
			bugzillaURLComponents += "cf_build_id=" + bugzillaBuildID;
			
			bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
			bugzillaURLComponents += "short_desc=" + bugzillaSummary;
			
			if (bugzillaAssignedTo != null)
			{
				bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
				bugzillaURLComponents += "assigned_to=" + bugzillaAssignedTo;
			}
			
			/* check the content spec options first */
			if (bzOptions != null && bzOptions.getProduct() != null)
			{
				bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
				bugzillaURLComponents += "product=" + URLEncoder.encode(bzOptions.getProduct(), "UTF-8");

				if (bzOptions.getComponent() != null)
				{
					bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
					bugzillaURLComponents += "component=" + URLEncoder.encode(bzOptions.getComponent(), "UTF-8");
				}

				if (bzOptions.getVersion() != null)
				{
					bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
					bugzillaURLComponents += "version=" + URLEncoder.encode(bzOptions.getVersion(), "UTF-8");
				}
			}
			/* we need at least a product*/
			else if (bugzillaProduct != null)
			{
				bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
				bugzillaURLComponents += "product=" + bugzillaProduct;

				if (bugzillaComponent != null)
				{
					bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
					bugzillaURLComponents += "component=" + bugzillaComponent;
				}

				if (bugzillaVersion != null)
				{
					bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
					bugzillaURLComponents += "version=" + bugzillaVersion;
				}

				if (bugzillaKeywords != null)
				{
					bugzillaURLComponents += bugzillaURLComponents.isEmpty() ? "?" : "&amp;";
					bugzillaURLComponents += "keywords=" + bugzillaKeywords;
				}
			}

			/* build the bugzilla url with the base components */
			String bugzillaUrl = "https://bugzilla.redhat.com/enter_bug.cgi" + bugzillaURLComponents;

			bugzillaULink.setAttribute("url", bugzillaUrl);

			/*
			 * only add the elements to the XML DOM if there was no exception
			 * (not that there should be one
			 */
			bugzillaSection.appendChild(bugzillaPara);
			bugzillaPara.appendChild(bugzillaULink);
		}
		catch (final Exception ex)
		{
			ExceptionUtilities.handleException(ex);
		}
	}

	/**
	 * Adds some debug information and links to the end of the topic
	 */
	public void processTopicAdditionalInfo(final SpecTopic specTopic, final Document document, final BugzillaOptions bzOptions, final DocbookBuildingOptions docbookBuildingOptions, final String buildName,
			final String searchTagsUrl, final Date buildDate, final ZanataDetails zanataDetails)
	{
		final RESTBaseTopicV1<?, ?, ?> topic = specTopic.getTopic();
		
		if ((docbookBuildingOptions != null && (docbookBuildingOptions.getInsertSurveyLink() || docbookBuildingOptions.getInsertEditorLinks())) || searchTagsUrl != null)
		{
			/* SIMPLESECT TO HOLD OTHER LINKS */
			final Element bugzillaSection = document.createElement("simplesect");
			document.getDocumentElement().appendChild(bugzillaSection);
	
			final Element bugzillaSectionTitle = document.createElement("title");
			bugzillaSectionTitle.setTextContent("");
			bugzillaSection.appendChild(bugzillaSectionTitle);
	
			// SURVEY LINK
			if (docbookBuildingOptions != null && docbookBuildingOptions.getInsertSurveyLink())
			{
				final Element surveyPara = document.createElement("para");
				surveyPara.setAttribute("role", DocbookBuilderConstants.ROLE_CREATE_BUG_PARA);
				bugzillaSection.appendChild(surveyPara);
	
				final Text startSurveyText = document.createTextNode("Thank you for evaluating the new documentation format for JBoss Enterprise Application Platform. Let us know what you think by taking a short ");
				surveyPara.appendChild(startSurveyText);
	
				final Element surveyULink = document.createElement("ulink");
				surveyPara.appendChild(surveyULink);
				surveyULink.setTextContent("survey");
				surveyULink.setAttribute("url", "https://www.keysurvey.com/survey/380730/106f/");
	
				final Text endSurveyText = document.createTextNode(".");
				surveyPara.appendChild(endSurveyText);
			}
			
			// EDITOR LINK
			if (docbookBuildingOptions != null && docbookBuildingOptions.getInsertEditorLinks())
			{
				final String editorUrl = topic instanceof RESTTopicV1 ? ComponentTopicV1.returnEditorURL((RESTTopicV1)topic) : ComponentTranslatedTopicV1.returnEditorURL((RESTTranslatedTopicV1)topic, zanataDetails);
				
				final Element editorLinkPara = document.createElement("para");
				editorLinkPara.setAttribute("role", DocbookBuilderConstants.ROLE_CREATE_BUG_PARA);
				bugzillaSection.appendChild(editorLinkPara);
	
				if (editorUrl != null)
				{
					final Element surveyULink = document.createElement("ulink");
					editorLinkPara.appendChild(surveyULink);
					surveyULink.setTextContent("Edit this topic");
					surveyULink.setAttribute("url", editorUrl);
				}
				else
				{
					/* 
					 * Since the returnEditorURL method only returns null for translations
					 * we don't need to check the topic type.
					 */
					editorLinkPara.setTextContent("No editor available for this topic, as it hasn't been pushed for translation.");
				}
			}
	
			/* searchTagsUrl will be null for internal (i.e. HTML rendering) builds */
			if (searchTagsUrl != null)
			{
				// VIEW IN SKYNET
	
				final Element skynetElement = document.createElement("remark");
				skynetElement.setAttribute("role", DocbookBuilderConstants.ROLE_VIEW_IN_SKYNET_PARA);
				bugzillaSection.appendChild(skynetElement);
	
				final Element skynetLinkULink = document.createElement("ulink");
				skynetElement.appendChild(skynetLinkULink);
				skynetLinkULink.setTextContent("View in Skynet");
				
				final String url = topic instanceof RESTTranslatedTopicV1 ? ComponentTranslatedTopicV1.returnSkynetURL((RESTTranslatedTopicV1)topic) : ComponentTopicV1.returnSkynetURL((RESTTopicV1)topic); 
				skynetLinkULink.setAttribute("url", url);
	
				// SKYNET VERSION
	
				final Element buildVersionElement = document.createElement("remark");
				buildVersionElement.setAttribute("role", DocbookBuilderConstants.ROLE_BUILD_VERSION_PARA);
				bugzillaSection.appendChild(buildVersionElement);
	
				final Element skynetVersionElementULink = document.createElement("ulink");
				buildVersionElement.appendChild(skynetVersionElementULink);
				skynetVersionElementULink.setTextContent("Built with " + buildName);
				skynetVersionElementULink.setAttribute("url", searchTagsUrl);
			}
		}
		
		// BUGZILLA LINK
		if (docbookBuildingOptions != null && docbookBuildingOptions.getInsertBugzillaLinks())
		{
			processTopicBugzillaLink(specTopic, document, bzOptions, docbookBuildingOptions, buildName, searchTagsUrl, buildDate);
		}
	}

	/**
	 * Takes a comma separated list of ints, and returns an array of Integers.
	 * This is used when processing custom injection points.
	 */
	private static List<InjectionTopicData> processTopicIdList(final String list)
	{
		/* find the individual topic ids */
		final String[] topicIDs = list.split(",");

		List<InjectionTopicData> retValue = new ArrayList<InjectionTopicData>(topicIDs.length);

		/* clean the topic ids */
		for (int i = 0; i < topicIDs.length; ++i)
		{
			final String topicId = topicIDs[i].replaceAll(OPTIONAL_MARKER, "").trim();
			final boolean optional = topicIDs[i].indexOf(OPTIONAL_MARKER) != -1;

			try
			{
				final InjectionTopicData topicData = new InjectionTopicData(Integer.parseInt(topicId), optional);
				retValue.add(topicData);
			}
			catch (final Exception ex)
			{
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
	public <T extends RESTBaseTopicV1<T, ?, ?>> List<Integer> processInjections(final Level level, final SpecTopic topic,
	        final ArrayList<Integer> customInjectionIds, final Document xmlDocument, final DocbookBuildingOptions docbookBuildingOptions,
	        final TocTopicDatabase<T> relatedTopicsDatabase, final boolean usedFixedUrls)
	{
		TocTopicDatabase<T> relatedTopicDatabase = relatedTopicsDatabase;
		if (relatedTopicDatabase == null)
		{
			/*
			 * get the outgoing relationships
			 */
			final List<T> relatedTopics = (List<T>) topic.getTopic().getOutgoingRelationships().returnItems();

			/*
			 * Create a TocTopicDatabase to hold the related topics. The
			 * TocTopicDatabase provides a convenient way to access
			 * these topics
			 */
			relatedTopicDatabase = new TocTopicDatabase<T>();
			relatedTopicDatabase.setTopics(relatedTopics);
		}
		
		/*
		 * this collection keeps a track of the injection point markers and the
		 * docbook lists that we will be replacing them with
		 */
		final HashMap<Node, InjectionListData> customInjections = new HashMap<Node, InjectionListData>();

		final List<Integer> errorTopics = new ArrayList<Integer>();

		errorTopics.addAll(processInjections(level, topic, customInjectionIds, customInjections, ORDEREDLIST_INJECTION_POINT, xmlDocument, CUSTOM_INJECTION_SEQUENCE_RE, null, docbookBuildingOptions, relatedTopicDatabase, usedFixedUrls));
		errorTopics.addAll(processInjections(level, topic, customInjectionIds, customInjections, XREF_INJECTION_POINT, xmlDocument, CUSTOM_INJECTION_SINGLE_RE, null, docbookBuildingOptions, relatedTopicDatabase, usedFixedUrls));
		errorTopics.addAll(processInjections(level, topic, customInjectionIds, customInjections, ITEMIZEDLIST_INJECTION_POINT, xmlDocument, CUSTOM_INJECTION_LIST_RE, null, docbookBuildingOptions, relatedTopicDatabase, usedFixedUrls));
		errorTopics.addAll(processInjections(level, topic, customInjectionIds, customInjections, ITEMIZEDLIST_INJECTION_POINT, xmlDocument, CUSTOM_ALPHA_SORT_INJECTION_LIST_RE, new TopicTitleSorter<T>(), docbookBuildingOptions, relatedTopicDatabase, usedFixedUrls));
		errorTopics.addAll(processInjections(level, topic, customInjectionIds, customInjections, LIST_INJECTION_POINT, xmlDocument, CUSTOM_INJECTION_LISTITEMS_RE, null, docbookBuildingOptions, relatedTopicDatabase, usedFixedUrls));

		/*
		 * If we are not ignoring errors, return the list of topics that could not be injected
		 */
		if (errorTopics.size() != 0 && docbookBuildingOptions != null && !docbookBuildingOptions.getIgnoreMissingCustomInjections())
			return errorTopics;

		/* now make the custom injection point substitutions */
		for (final Node customInjectionCommentNode : customInjections.keySet())
		{
			final InjectionListData injectionListData = customInjections.get(customInjectionCommentNode);
			List<Element> list = null;

			/*
			 * this may not be true if we are not building all related topics
			 */
			if (injectionListData.listItems.size() != 0)
			{
				if (injectionListData.listType == ORDEREDLIST_INJECTION_POINT)
				{
					list = DocBookUtilities.wrapOrderedListItemsInPara(xmlDocument, injectionListData.listItems);
				}
				else if (injectionListData.listType == XREF_INJECTION_POINT)
				{
					list = injectionListData.listItems.get(0);
				}
				else if (injectionListData.listType == ITEMIZEDLIST_INJECTION_POINT)
				{
					list = DocBookUtilities.wrapItemizedListItemsInPara(xmlDocument, injectionListData.listItems);
				}
				else if (injectionListData.listType == LIST_INJECTION_POINT)
				{
					list = DocBookUtilities.wrapItemsInListItems(xmlDocument, injectionListData.listItems);
				}
			}

			if (list != null)
			{
				for (final Element element : list)
				{
					customInjectionCommentNode.getParentNode().insertBefore(element, customInjectionCommentNode);
				}

				customInjectionCommentNode.getParentNode().removeChild(customInjectionCommentNode);
			}
		}

		return errorTopics;
	}

	public <T extends RESTBaseTopicV1<T, ?, ?>> List<Integer> processInjections(final Level level, final SpecTopic topic, final ArrayList<Integer> customInjectionIds,
	        final HashMap<Node, InjectionListData> customInjections, final int injectionPointType, final Document xmlDocument, final String regularExpression,
	        final ExternalListSort<Integer, T, InjectionTopicData> sortComparator, final DocbookBuildingOptions docbookBuildingOptions,
	        final TocTopicDatabase<T> relatedTopicsDatabase, final boolean usedFixedUrls)
	{
		final List<Integer> retValue = new ArrayList<Integer>();

		if (xmlDocument == null)
			return retValue;

		/* loop over all of the comments in the document */
		for (final Node comment : XMLUtilities.getComments(xmlDocument))
		{
			final String commentContent = comment.getNodeValue();

			/* compile the regular expression */
			final NamedPattern injectionSequencePattern = NamedPattern.compile(regularExpression);
			/* find any matches */
			final NamedMatcher injectionSequencematcher = injectionSequencePattern.matcher(commentContent);

			/* loop over the regular expression matches */
			while (injectionSequencematcher.find())
			{
				/*
				 * get the list of topics from the named group in the regular expression match
				 */
				final String reMatch = injectionSequencematcher.group(TOPICIDS_RE_NAMED_GROUP);

				/* make sure we actually found a matching named group */
				if (reMatch != null)
				{
					/* get the sequence of ids */
					final List<InjectionTopicData> sequenceIDs = processTopicIdList(reMatch);

					/* sort the InjectionTopicData list if required */
					if (sortComparator != null)
					{
						sortComparator.sort(relatedTopicsDatabase.getTopics(), sequenceIDs);
					}

					/* loop over all the topic ids in the injection point */
					for (final InjectionTopicData sequenceID : sequenceIDs)
					{
						/*
						 * topics that are injected into custom injection points
						 * are excluded from the generic related topic lists at
						 * the beginning and end of a topic. adding the topic id
						 * here means that when it comes time to generate the
						 * generic related topic lists, we can skip this topic
						 */
						customInjectionIds.add(sequenceID.topicId);

						/*
						 * Pull the topic out of the list of related topics
						 */
						final T relatedTopic = relatedTopicsDatabase.getTopic(sequenceID.topicId);

						/*
						 * See if the topic is also available in the main
						 * database (if the main database is available)
						 */
						final boolean isInDatabase = level == null ? true : level.isSpecTopicInLevelByTopicID(sequenceID.topicId);

						/*
						 * It is possible that the topic id referenced in the
						 * injection point has not been related, or has not been
						 * included in the list of topics to process. This is a
						 * validity error
						 */
						if (relatedTopic != null && isInDatabase)
						{
							/*
							 * build our list
							 */
							List<List<Element>> list = new ArrayList<List<Element>>();

							/*
							 * each related topic is added to a string, which is
							 * stored in the customInjections collection. the
							 * customInjections key is the custom injection text
							 * from the source xml. this allows us to match the
							 * xrefs we are generating for the related topic
							 * with the text in the xml file that these xrefs
							 * will eventually replace
							 */
							if (customInjections.containsKey(comment))
								list = customInjections.get(comment).listItems;

							/* if the toc is null, we are building an internal page */
							if (level == null)
							{
								final String url = relatedTopic instanceof RESTTranslatedTopicV1 ? ComponentTranslatedTopicV1.returnInternalURL((RESTTranslatedTopicV1) relatedTopic) : ComponentTopicV1.returnInternalURL((RESTTopicV1) relatedTopic);
								if (sequenceID.optional)
								{
									list.add(DocBookUtilities.buildEmphasisPrefixedULink(xmlDocument, OPTIONAL_LIST_PREFIX, url, relatedTopic.getTitle()));
								}
								else
								{
									list.add(DocBookUtilities.buildULink(xmlDocument, url, relatedTopic.getTitle()));
								}
							}
							else
							{
								final Integer topicId;
								if (relatedTopic instanceof RESTTranslatedTopicV1)
								{
									topicId = ((RESTTranslatedTopicV1) relatedTopic).getTopicId();
								}
								else
								{
									topicId = relatedTopic.getId();
								}

								final SpecTopic closestSpecTopic = topic.getClosestTopicByDBId(topicId, true);
								if (sequenceID.optional)
								{
									list.add(DocBookUtilities.buildEmphasisPrefixedXRef(xmlDocument, OPTIONAL_LIST_PREFIX, closestSpecTopic.getUniqueLinkId(usedFixedUrls)));
								}
								else
								{
									list.add(DocBookUtilities.buildXRef(xmlDocument, closestSpecTopic.getUniqueLinkId(usedFixedUrls)));
								}
							}

							/*
							 * save the changes back into the customInjections collection
							 */
							customInjections.put(comment, new InjectionListData(list, injectionPointType));
						}
						else
						{
							retValue.add(sequenceID.topicId);
						}
					}
				}
			}
		}

		return retValue;
	}

	@SuppressWarnings({ "unchecked" })
	public <T extends RESTBaseTopicV1<T, ?, ?>> List<Integer> processGenericInjections(final Level level, final SpecTopic topic, final Document xmlDocument,
			final ArrayList<Integer> customInjectionIds, final List<Pair<Integer, String>> topicTypeTagIDs, final DocbookBuildingOptions docbookBuildingOptions,
			final boolean usedFixedUrls)
	{
		final List<Integer> errors = new ArrayList<Integer>();

		if (xmlDocument == null)
			return errors;

		/*
		 * this collection will hold the lists of related topics
		 */
		final GenericInjectionPointDatabase<T> relatedLists = new GenericInjectionPointDatabase<T>();

		/* wrap each related topic in a listitem tag */
		if (topic.getTopic().getOutgoingRelationships() != null && topic.getTopic().getOutgoingRelationships().returnItems() != null)
		{
			for (final RESTBaseTopicV1<?, ?, ?> relatedTopic : topic.getTopic().getOutgoingRelationships().returnItems())
			{
				final Integer topicId;
				if (relatedTopic instanceof RESTTranslatedTopicV1)
				{
					topicId = ((RESTTranslatedTopicV1) relatedTopic).getTopicId();
				}
				else
				{
					topicId = relatedTopic.getId();
				}

				/*
				 * don't process those topics that were injected into custom injection points
				 */
				if (!customInjectionIds.contains(topicId))
				{
					/* make sure the topic is available to be linked to */
					if (level != null && !level.isSpecTopicInLevelByTopicID(topicId))
					{
						if ((docbookBuildingOptions != null && !docbookBuildingOptions.getIgnoreMissingCustomInjections()))
							errors.add(relatedTopic.getId());
					}
					else
					{
						// loop through the topic type tags
						for (final Pair<Integer, String> primaryTopicTypeTag : topicTypeTagIDs)
						{
							/*
							 * see if we have processed a related topic with one
							 * of the topic type tags this may never be true if
							 * not processing all related topics
							 */
							if (ComponentBaseTopicV1.hasTag(relatedTopic, primaryTopicTypeTag.getFirst()))
							{
								relatedLists.addInjectionTopic(primaryTopicTypeTag, (T) relatedTopic);

								break;
							}
						}
					}
				}
			}
		}

		insertGenericInjectionLinks(level, topic, xmlDocument, relatedLists, docbookBuildingOptions, usedFixedUrls);

		return errors;
	}

	/**
	 * The generic injection points are placed in well defined locations within
	 * a topics xml structure. This function takes the list of related topics
	 * and the topic type tags that are associated with them and injects them
	 * into the xml document.
	 */
	private <T extends RESTBaseTopicV1<T, ?, ?>> void insertGenericInjectionLinks(final Level  level, final SpecTopic topic, final Document xmlDoc,
	        final GenericInjectionPointDatabase<T> relatedLists, final DocbookBuildingOptions docbookBuildingOptions, final boolean usedFixedUrls)
	{
		/* all related topics are placed before the first simplesect */
		final NodeList nodes = xmlDoc.getDocumentElement().getChildNodes();
		Node simplesectNode = null;
		for (int i = 0; i < nodes.getLength(); ++i)
		{
			final Node node = nodes.item(i);
			if (node.getNodeType() == 1 && node.getNodeName().equals("simplesect"))
			{
				simplesectNode = node;
				break;
			}
		}

		/*
		 * place the topics at the end of the topic. They will appear in the
		 * reverse order as the call to toArrayList()
		 */
		for (final Integer topTag : CollectionUtilities.toArrayList(DocbookBuilderConstants.REFERENCE_TAG_ID, DocbookBuilderConstants.TASK_TAG_ID, DocbookBuilderConstants.CONCEPT_TAG_ID, DocbookBuilderConstants.CONCEPTUALOVERVIEW_TAG_ID))
		{
			for (final GenericInjectionPoint<T> genericInjectionPoint : relatedLists.getInjectionPoints())
			{
				if (genericInjectionPoint.getCategoryIDAndName().getFirst() == topTag)
				{
					final List<T> relatedTopics = genericInjectionPoint.getTopics();

					/* don't add an empty list */
					if (relatedTopics.size() != 0)
					{
						final Node itemizedlist = DocBookUtilities.createRelatedTopicItemizedList(xmlDoc, "Related " + genericInjectionPoint.getCategoryIDAndName().getSecond() + "s");

						Collections.sort(relatedTopics, new BaseTopicV1TitleComparator<T>());

						for (final T relatedTopic : relatedTopics)
						{
							if (level == null)
							{
								final String internalURL = relatedTopic instanceof RESTTranslatedTopicV1 ? ComponentTranslatedTopicV1.returnInternalURL((RESTTranslatedTopicV1) relatedTopic) : ComponentTopicV1.returnInternalURL((RESTTopicV1) relatedTopic);
								DocBookUtilities.createRelatedTopicULink(xmlDoc, internalURL, relatedTopic.getTitle(), itemizedlist);
							}
							else
							{
								final Integer topicId;
								if (relatedTopic instanceof RESTTranslatedTopicV1)
								{
									topicId = ((RESTTranslatedTopicV1) relatedTopic).getTopicId();
								}
								else
								{
									topicId = relatedTopic.getId();
								}

								final SpecTopic  closestSpecTopic = topic.getClosestTopicByDBId(topicId, true);
								DocBookUtilities.createRelatedTopicXRef(xmlDoc, closestSpecTopic.getUniqueLinkId(usedFixedUrls), itemizedlist);
							}

						}

						if (simplesectNode != null)
							xmlDoc.getDocumentElement().insertBefore(itemizedlist, simplesectNode);
						else
							xmlDoc.getDocumentElement().appendChild(itemizedlist);
					}
				}
			}
		}
	}

	/**
	 * Insert a itemized list into the start of the topic, below the title with any
	 * PREVIOUS relationships that exists for the Spec Topic. The title for
	 * the list is set to "Previous Step(s) in <TOPIC_PARENT_NAME>".
	 * 
	 * @param topic The topic to process the injection for.
	 * @param doc The DOM Document object that represents the topics XML.
	 * @param useFixedUrls Whether fixed URL's should be used in the injected links.
	 */
	public void processPrevRelationshipInjections(final SpecTopic topic, final Document doc, final boolean useFixedUrls)
	{
		if (topic.getPrevTopicRelationships().isEmpty()) return;
		
		// Get the title element so that it can be used later to add the prev topic node
		Element titleEle = null;
		final NodeList titleList = doc.getDocumentElement().getElementsByTagName("title");
		for (int i = 0; i < titleList.getLength(); i++)
		{
			if (titleList.item(i).getParentNode().equals(doc.getDocumentElement()))
			{
				titleEle = (Element)titleList.item(i);
				break;
			}
		}
		
		if (titleEle != null)
		{
			// Attempt to get the previous topic and process it
			final List<TopicRelationship> prevList = topic.getPrevTopicRelationships();
			// Create the paragraph/itemizedlist and list of previous relationships.
			final Element rootEle = doc.createElement("itemizedlist");
			rootEle.setAttribute("role", "process-previous-itemizedlist");
			
			// Create the title
			final Element linkTitleEle = doc.createElement("title");
			linkTitleEle.setAttribute("role", "process-previous-title");
			if (prevList.size() > 1)
			{
				linkTitleEle.setTextContent("Previous Steps in ");
			} else
			{
				linkTitleEle.setTextContent("Previous Step in ");
			}
			
			// Create the title link
			final Element titleXrefItem = doc.createElement("link");
			titleXrefItem.setTextContent(topic.getParent().getTitle());
			titleXrefItem.setAttribute("linkend", topic.getParent().getUniqueLinkId(useFixedUrls));
			titleXrefItem.setAttribute("xrefstyle", "process-previous-title-link");
			linkTitleEle.appendChild(titleXrefItem);
			rootEle.appendChild(linkTitleEle);
			
			for (final TopicRelationship prev: prevList)
			{
				final Element prevEle = doc.createElement("para");
				final SpecTopic prevTopic = prev.getSecondaryRelationship();
				
				// Add the previous element to either the list or paragraph
				// Create the link element
				final Element xrefItem = doc.createElement("xref");
				xrefItem.setAttribute("linkend", prevTopic.getUniqueLinkId(useFixedUrls));
				xrefItem.setAttribute("xrefstyle", "process-previous-link");
				prevEle.appendChild(xrefItem);
				
				final Element listitemEle = doc.createElement("listitem");
				listitemEle.setAttribute("role", "process-previous-listitem");
				listitemEle.appendChild(prevEle);
				rootEle.appendChild(listitemEle);
			}
			
			// Insert the node after the title node
			Node nextNode = titleEle.getNextSibling();
			while (nextNode != null && nextNode.getNodeType() != Node.ELEMENT_NODE && nextNode.getNodeType() != Node.COMMENT_NODE)
			{
				nextNode = nextNode.getNextSibling();
			}
			doc.getDocumentElement().insertBefore(rootEle, nextNode);
		}
	}
	
	/**
	 * Insert a itemized list into the end of the topic with any NEXT
	 * relationships that exists for the Spec Topic. The title for the
	 * list is set to "Next Step(s) in <TOPIC_PARENT_NAME>".
	 * 
	 * @param topic The topic to process the injection for.
	 * @param doc The DOM Document object that represents the topics XML.
	 * @param useFixedUrls Whether fixed URL's should be used in the injected links.
	 */
	public void processNextRelationshipInjections(final SpecTopic topic, final Document doc, final boolean useFixedUrls)
	{
		if (topic.getNextTopicRelationships().isEmpty()) return;
		
		// Attempt to get the previous topic and process it
		final List<TopicRelationship> nextList = topic.getNextTopicRelationships();
		// Create the paragraph/itemizedlist and list of next relationships.
		final Element rootEle = doc.createElement("itemizedlist");
		rootEle.setAttribute("role", "process-next-itemizedlist");
		
		// Create the title
		final Element linkTitleEle = doc.createElement("title");
		linkTitleEle.setAttribute("role", "process-next-title");
		if (nextList.size() > 1)
		{
			linkTitleEle.setTextContent("Next Steps in ");
		}
		else
		{
			linkTitleEle.setTextContent("Next Step in ");
		}
		
		// Create the title link
		final Element titleXrefItem = doc.createElement("link");
		titleXrefItem.setTextContent(topic.getParent().getTitle());
		titleXrefItem.setAttribute("linkend", topic.getParent().getUniqueLinkId(useFixedUrls));
		titleXrefItem.setAttribute("xrefstyle", "process-next-title-link");
		linkTitleEle.appendChild(titleXrefItem);
		rootEle.appendChild(linkTitleEle);

		for (final TopicRelationship next: nextList)
		{
			final Element nextEle = doc.createElement("para");
			final SpecTopic nextTopic = next.getSecondaryRelationship();
			
			// Add the next element to either the list or paragraph
			// Create the link element
			final Element xrefItem = doc.createElement("xref");
			xrefItem.setAttribute("linkend", nextTopic.getUniqueLinkId(useFixedUrls));
			xrefItem.setAttribute("xrefstyle", "process-next-link");
			nextEle.appendChild(xrefItem);
			
			final Element listitemEle = doc.createElement("listitem");
			listitemEle.setAttribute("role", "process-next-listitem");
			listitemEle.appendChild(nextEle);
			rootEle.appendChild(listitemEle);
		}
		
		// Add the node to the end of the XML data
		doc.getDocumentElement().appendChild(rootEle);
	}
	
	/**
	 * Insert a itemized list into the start of the topic, below the title with any
	 * PREREQUISITE relationships that exists for the Spec Topic. The title for
	 * the list is set to "Prerequisites:".
	 * 
	 * @param topic The topic to process the injection for.
	 * @param doc The DOM Document object that represents the topics XML.
	 * @param useFixedUrls Whether fixed URL's should be used in the injected links.
	 */
	public void processPrerequisiteInjections(final SpecTopic topic, final Document doc, final boolean useFixedUrls)
	{
		if (topic.getPrerequisiteRelationships().isEmpty()) return;
		
		// Get the title element so that it can be used later to add the prerequisite topic nodes
		Element titleEle = null;
		final NodeList titleList = doc.getDocumentElement().getElementsByTagName("title");
		for (int i = 0; i < titleList.getLength(); i++)
		{
			if (titleList.item(i).getParentNode().equals(doc.getDocumentElement()))
			{
				titleEle = (Element)titleList.item(i);
				break;
			}
		}
		
		if (titleEle != null)
		{
			// Create the paragraph and list of prerequisites.
			final Element formalParaEle = doc.createElement("formalpara");
			formalParaEle.setAttribute("role", "prereqs-list");
			final Element formalParaTitleEle = doc.createElement("title");
			formalParaTitleEle.setTextContent("Prerequisites:");
			formalParaEle.appendChild(formalParaTitleEle);
			final List<List<Element>> list = new LinkedList<List<Element>>();
			
			// Add the Relationships
			for (final Relationship prereq: topic.getPrerequisiteRelationships())
			{
				if (prereq instanceof TopicRelationship)
				{
					final SpecTopic relatedTopic = ((TopicRelationship) prereq).getSecondaryRelationship();
					
					list.add(DocBookUtilities.buildXRef(doc, relatedTopic.getUniqueLinkId(useFixedUrls), "prereq"));
				}
				else
				{
					final SpecNode specNode = ((TargetRelationship) prereq).getSecondaryElement();
					
					list.add(DocBookUtilities.buildXRef(doc, specNode.getUniqueLinkId(useFixedUrls), "prereq"));
				}
			}
			
			// Wrap the items into an itemized list
			final List<Element> items = DocBookUtilities.wrapItemizedListItemsInPara(doc, list);
			for (final Element ele: items)
			{
				formalParaEle.appendChild(ele);
			}

			// Add the paragraph and list after the title node
			Node nextNode = titleEle.getNextSibling();
			while (nextNode != null && nextNode.getNodeType() != Node.ELEMENT_NODE && nextNode.getNodeType() != Node.COMMENT_NODE)
			{
				nextNode = nextNode.getNextSibling();
			}

			doc.getDocumentElement().insertBefore(formalParaEle, nextNode);
		}
	}
	
	/**
	 * Insert a itemized list into the end of the topic with any RELATED
	 * relationships that exists for the Spec Topic. The title for the
	 * list is set to "See Also:".
	 * 
	 * @param topic The topic to process the injection for.
	 * @param doc The DOM Document object that represents the topics XML.
	 * @param useFixedUrls Whether fixed URL's should be used in the injected links.
	 */
	public void processSeeAlsoInjections(final SpecTopic topic, final Document doc, final boolean useFixedUrls)
	{
		// Create the paragraph and list of prerequisites.
		if (topic.getRelatedRelationships().isEmpty()) return;
		final Element formalParaEle = doc.createElement("formalpara");
		formalParaEle.setAttribute("role", "see-also-list");
		final Element formalParaTitleEle = doc.createElement("title");
		formalParaTitleEle.setTextContent("See Also:");
		formalParaEle.appendChild(formalParaTitleEle);
		final List<List<Element>> list = new LinkedList<List<Element>>();
		
		// Add the Relationships
		for (final Relationship seeAlso: topic.getRelatedRelationships())
		{
			if (seeAlso instanceof TopicRelationship)
			{
				final SpecTopic relatedTopic = ((TopicRelationship) seeAlso).getSecondaryRelationship();
				
				list.add(DocBookUtilities.buildXRef(doc, relatedTopic.getUniqueLinkId(useFixedUrls), "see-also"));
			}
			else
			{
				final SpecNode specNode = ((TargetRelationship) seeAlso).getSecondaryElement();
				
				list.add(DocBookUtilities.buildXRef(doc, specNode.getUniqueLinkId(useFixedUrls), "see-also"));
			}
		}
		
		// Wrap the items into an itemized list
		final List<Element> items = DocBookUtilities.wrapItemizedListItemsInPara(doc, list);
		for (final Element ele: items)
		{
			formalParaEle.appendChild(ele);
		}
		
		// Add the paragraph and list after at the end of the xml data
		doc.getDocumentElement().appendChild(formalParaEle);
	}
	
	/**
	 * Insert a itemized list into the end of the topic with the any
	 * LINKLIST relationships that exists for the Spec Topic.
	 * 
	 * @param topic The topic to process the injection for.
	 * @param doc The DOM Document object that represents the topics XML.
	 * @param useFixedUrls Whether fixed URL's should be used in the injected links.
	 */
	public void processLinkListRelationshipInjections(final SpecTopic topic, final Document doc, final boolean useFixedUrls)
	{
		// Create the paragraph and list of prerequisites.
		if (topic.getLinkListRelationships().isEmpty()) return;
		final Element formalParaEle = doc.createElement("formalpara");
		formalParaEle.setAttribute("role", "link-list");
		final Element formalParaTitleEle = doc.createElement("title");
		formalParaTitleEle.setTextContent("");
		formalParaEle.appendChild(formalParaTitleEle);
		final List<List<Element>> list = new LinkedList<List<Element>>();
		
		// Add the Relationships
		for (final Relationship linkList: topic.getLinkListRelationships())
		{
			if (linkList instanceof TopicRelationship)
			{
				final SpecTopic relatedTopic = ((TopicRelationship) linkList).getSecondaryRelationship();
				
				list.add(DocBookUtilities.buildXRef(doc, relatedTopic.getUniqueLinkId(useFixedUrls), "link-list"));
			}
			else
			{
				final SpecNode specNode = ((TargetRelationship) linkList).getSecondaryElement();
				
				list.add(DocBookUtilities.buildXRef(doc, specNode.getUniqueLinkId(useFixedUrls), "link-list"));
			}
		}
		
		// Wrap the items into an itemized list
		final List<Element> items = DocBookUtilities.wrapItemizedListItemsInPara(doc, list);
		for (final Element ele: items)
		{
			formalParaEle.appendChild(ele);
		}
		
		// Add the paragraph and list after at the end of the xml data
		doc.getDocumentElement().appendChild(formalParaEle);
	}

	public static String processDocumentType(final String xml)
	{
		assert xml != null : "The xml parameter can not be null";

		if (XMLUtilities.findDocumentType(xml) == null)
		{
			final String preamble = XMLUtilities.findPreamble(xml);
			final String fixedPreamble = preamble == null ? "" : preamble + "\n";
			final String fixedXML = preamble == null ? xml : xml.replace(preamble, "");

			return fixedPreamble + "<!DOCTYPE section PUBLIC \"-//OASIS//DTD DocBook XML V4.5//EN\" \"http://www.oasis-open.org/docbook/xml/4.5/docbookx.dtd\" []>\n" + fixedXML;
		}

		return xml;
	}
}