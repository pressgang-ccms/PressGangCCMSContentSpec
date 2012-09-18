package org.jboss.pressgang.ccms.contentspec.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.pressgang.ccms.contentspec.Chapter;
import org.jboss.pressgang.ccms.contentspec.ContentSpec;
import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.Section;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.structures.TagRequirements;
import org.jboss.pressgang.ccms.rest.v1.collections.base.RESTBaseCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.components.ComponentBaseTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTCategoryV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTagV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTranslatedTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.base.RESTBaseTagV1;
import org.jboss.pressgang.ccms.rest.v1.entities.base.RESTBaseTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.join.RESTCategoryTagV1;
import org.jboss.pressgang.ccms.rest.v1.entities.join.RESTTagCategoryV1;
import org.jboss.pressgang.ccms.rest.v1.expansion.ExpandDataDetails;
import org.jboss.pressgang.ccms.rest.v1.expansion.ExpandDataTrunk;
import org.jboss.pressgang.ccms.rest.v1.jaxrsinterfaces.RESTInterfaceV1;
import org.jboss.pressgang.ccms.utils.common.CollectionUtilities;
import org.jboss.pressgang.ccms.utils.common.ExceptionUtilities;
import org.jboss.pressgangccms.docbook.compiling.DocbookBuildingOptions;
import org.jboss.pressgangccms.docbook.constants.DocbookBuilderConstants;


public class ContentSpecGenerator<T extends RESTBaseTopicV1<T, U, ?>, U extends RESTBaseCollectionV1<T, U, ?>>
{
	/** The REST client */
	private final RESTInterfaceV1 restClient;

	/** Jackson object mapper */
	private final ObjectMapper mapper = new ObjectMapper();
	
	public ContentSpecGenerator(final RESTInterfaceV1 restClient)
	{
		this.restClient = restClient;
	}
	

	/**
	 * Generates a Content Specification and fills it in using a set of topics. Once the content
	 * specification is assembled it then removes any empty sections.
	 * 
	 * Note: All topics should be of the same locale.
	 * 
	 * @param clazz The Class of the list of topics. This should be either TopicV1 or TranslatedTopicV1.
	 * @param topics The collection of topics to be used in the generate of the Content Specification.
	 * @param locale The locale of the topics.
	 * @return A ContentSpec object that represents the Content Specification. The toString() method can be used to get the text based version.
	 */
	public ContentSpec generateContentSpecFromTopics(final Class<T> clazz, final U topics, final String locale)
	{
		return this.generateContentSpecFromTopics(clazz, topics, locale, new DocbookBuildingOptions());
	}
	

	/**
	 * Generates a Content Specification and fills it in using a set of topics. Once the content
	 * specification is assembled it then removes any empty sections.
	 * 
	 * Note: All topics should be of the same locale.
	 * 
	 * @param clazz The Class of the list of topics. This should be either TopicV1 or TranslatedTopicV1.
	 * @param topics The collection of topics to be used in the generate of the Content Specification.
	 * @param locale The locale of the topics.
	 * @param docbookBuildingOptions The options that are to be used from a docbook build to generate the content spec.
	 * @return A ContentSpec object that represents the Content Specification. The toString() method can be used to get the text based version.
	 */
	public ContentSpec generateContentSpecFromTopics(final Class<T> clazz, final U topics, final String locale, final DocbookBuildingOptions docbookBuildingOptions)
	{
		final ContentSpec contentSpec = doFormattedTocPass(clazz, topics, locale, docbookBuildingOptions);
		if (contentSpec != null)
		{
			trimEmptySectionsFromContentSpecLevel(contentSpec.getBaseLevel());
		}
		return contentSpec;
	}
	
	/**
	 * Removes any levels from a Content Specification level that contain no content.
	 * 
	 * @param level The level to remove empty sections from.
	 */
	private void trimEmptySectionsFromContentSpecLevel(final Level level)
	{
		if (level == null) return;
		
		final List<Level> childLevels = new LinkedList<Level>(level.getChildLevels());
		for (final Level childLevel : childLevels)
		{
			if (!childLevel.hasSpecTopics())
				level.removeChild(childLevel);
			
			trimEmptySectionsFromContentSpecLevel(childLevel);
		}
	}
	
	/**
	 * Populates a content specifications level with all topics that match the
	 * criteria required by the TagRequirements.
	 * 
	 * @param topics The list of topics that can be matched to the level requirements.
	 * @param level The level to populate with topics.
	 * @param childRequirements The TagRequirements for this level based on the child requirements from the levels parent.
	 * @param displayRequirements The TagRequirements to display topics at this level.
	 */
	@SuppressWarnings("rawtypes")
    private void populateContentSpecLevel(final U topics, final Level level, final TagRequirements childRequirements, final TagRequirements displayRequirements)
	{
		/*
		 * If this branch has no parent, then it is the top level and we don't
		 * add topics to it
		 */
		if (level.getParent() != null && childRequirements != null && displayRequirements != null && displayRequirements.hasRequirements())
		{
			final TagRequirements requirements = new TagRequirements();
			/* get the tags required to be a child of the parent toc levels */
			requirements.merge(childRequirements);
			/* and add the tags required to be displayed at this level */
			requirements.merge(displayRequirements);

			for (final T topic : topics.returnItems())
			{
				boolean doesMatch = true;
				for (final RESTBaseTagV1 andTag : requirements.getMatchAllOf())
				{
					if (!ComponentBaseTopicV1.hasTag(topic, andTag.getId()))
					{
						doesMatch = false;
						break;
					}
				}

				if (doesMatch && requirements.getMatchOneOf().size() != 0)
				{
					for (final ArrayList<RESTBaseTagV1> orBlock : requirements.getMatchOneOf())
					{
						if (orBlock.size() != 0)
						{
							boolean matchesOrBlock = false;
							for (final RESTBaseTagV1 orTag : orBlock)
							{
								if (ComponentBaseTopicV1.hasTag(topic, orTag.getId()))
								{
									matchesOrBlock = true;
									break;
								}
							}

							if (!matchesOrBlock)
							{
								doesMatch = false;
								break;
							}
						}
					}
				}

				if (doesMatch)
				{
					final Integer topicId;
					final String topicTitle;
					if (topic instanceof RESTTranslatedTopicV1)
					{
						topicId = ((RESTTranslatedTopicV1) topic).getTopicId();
						topicTitle = ((RESTTranslatedTopicV1) topic).getTopic().getTitle();
					}
					else
					{
						topicId = topic.getId();
						topicTitle = topic.getTitle();
					}
					
					final SpecTopic specTopic = new SpecTopic(topicId, topicTitle);
					specTopic.setTopic(topic.clone(false));
					level.appendSpecTopic(specTopic);
				}
			}
		}
	}

	/**
	 * Uses the technology, common names and concerns to build a basic content specification and then
	 * adds topics that match each levels criteria into the content specification.
	 * 
	 * @param clazz The Class of the list of topics. This should be either TopicV1 or TranslatedTopicV1.
	 * @param topics The collection of topics to be used in the generate of the Content Specification.
	 * @param locale The locale of the topics.
	 * @param docbookBuildingOptions The options that are to be used from a docbook build to generate the content spec.
	 * @return A ContentSpec object that represents the assembled Content Specification. The toString() method can be used to get the text based version.
	 */
	@SuppressWarnings("rawtypes")
    private ContentSpec doFormattedTocPass(final Class<T> clazz, final U topics, final String locale, final DocbookBuildingOptions docbookBuildingOptions)
	{
		try
		{
			/* The return value is a content specification. The 
			 * content specification defines the structure and 
			 * contents of the TOC.
			 */
			final ContentSpec retValue = new ContentSpec();
			
			/* Setup the basic content specification data */
			retValue.setTitle(docbookBuildingOptions.getBookTitle());
			retValue.setBrand("JBoss");
			retValue.setProduct(docbookBuildingOptions.getBookProduct());
			retValue.setVersion(docbookBuildingOptions.getBookProductVersion());
			retValue.setEdition(docbookBuildingOptions.getBookEdition() == null || docbookBuildingOptions.getBookEdition().isEmpty() ? null : docbookBuildingOptions.getBookEdition());
			retValue.setSubtitle(docbookBuildingOptions.getBookSubtitle() == null || docbookBuildingOptions.getBookSubtitle().isEmpty() ? null : docbookBuildingOptions.getBookSubtitle());
			retValue.setDtd("Docbook 4.5");
			retValue.setCopyrightHolder("Red Hat, Inc");
			retValue.setInjectSurveyLinks(docbookBuildingOptions.getInsertSurveyLink() == null ? false : docbookBuildingOptions.getInsertSurveyLink());
			
			if (clazz == RESTTranslatedTopicV1.class)
				retValue.setLocale(locale);

			/* Create an expand block for the tag parent tags */
			final ExpandDataTrunk parentTags = new ExpandDataTrunk(new ExpandDataDetails("parenttags"));
			parentTags.setBranches(CollectionUtilities.toArrayList(new ExpandDataTrunk(new ExpandDataDetails("categories"))));

			final ExpandDataTrunk childTags = new ExpandDataTrunk(new ExpandDataDetails("childtags"));

			final ExpandDataTrunk expandTags = new ExpandDataTrunk(new ExpandDataDetails("tags"));
			expandTags.setBranches(CollectionUtilities.toArrayList(parentTags, childTags));

			final ExpandDataTrunk expand = new ExpandDataTrunk();
			expand.setBranches(CollectionUtilities.toArrayList(expandTags));

			final String expandString = mapper.writeValueAsString(expand);
			//final String expandEncodedString = URLEncoder.encode(expandString, "UTF-8");

			/* Get the technology and common names categories */
			final RESTCategoryV1 technologyCategroy = restClient.getJSONCategory(DocbookBuilderConstants.TECHNOLOGY_CATEGORY_ID, expandString);
			final RESTCategoryV1 commonNamesCategory = restClient.getJSONCategory(DocbookBuilderConstants.COMMON_NAME_CATEGORY_ID, expandString);

			/*
			 * The top level TOC elements are made up of the technology and
			 * common name tags that are not encompassed by another tag. So here
			 * we get the tags out of the tech and common names categories, and
			 * pull outthose that are not encompassed.
			 */
			final List<RESTTagCategoryV1> topLevelTags = new ArrayList<RESTTagCategoryV1>();
			for (final RESTCategoryV1 category : new RESTCategoryV1[]
			{ technologyCategroy, commonNamesCategory })
			{
				if (category.getTags().returnItems() != null)
				{
					for (final RESTTagCategoryV1 tag : category.getTags().returnItems())
					{
						boolean isEmcompassed = false;
						for (final RESTTagV1 parentTag : tag.getParentTags().returnItems())
						{
							for (final RESTCategoryTagV1 parentTagCategory : parentTag.getCategories().returnItems())
							{
								if (parentTagCategory.getId().equals(DocbookBuilderConstants.TECHNOLOGY_CATEGORY_ID) || parentTagCategory.getId().equals(DocbookBuilderConstants.COMMON_NAME_CATEGORY_ID))
								{
									isEmcompassed = true;
									break;
								}
							}
	
							if (isEmcompassed)
								break;
						}
	
						/*
						 * This tag is not encompassed by any other tech or common
						 * name tags, so it is a candidate to appear on the top
						 * level of the TOC
						 */
						if (!isEmcompassed)
						{
							topLevelTags.add(tag);
						}
					}
				}
			}

			/* Create an expand block for the tag parent tags */
			final ExpandDataTrunk concernCategoryExpand = new ExpandDataTrunk();
			final ExpandDataTrunk concernCategoryExpandTags = new ExpandDataTrunk(new ExpandDataDetails("tags"));
			concernCategoryExpand.setBranches(CollectionUtilities.toArrayList(concernCategoryExpandTags));

			final String concernCategoryExpandString = mapper.writeValueAsString(concernCategoryExpand);
			//final String concernCategoryExpandStringEncoded = URLEncoder.encode(concernCategoryExpandString, "UTF-8");

			/* Get the technology and common names categories */
			final RESTCategoryV1 concernCategory = restClient.getJSONCategory(DocbookBuilderConstants.CONCERN_CATEGORY_ID, concernCategoryExpandString);

			/* Get the task reference and concept tag*/
			final RESTTagV1 referenceTag = restClient.getJSONTag(DocbookBuilderConstants.REFERENCE_TAG_ID, "");
			final RESTTagV1 conceptTag = restClient.getJSONTag(DocbookBuilderConstants.CONCEPT_TAG_ID, "");
			final RESTTagV1 conceptualOverviewTag = restClient.getJSONTag(DocbookBuilderConstants.CONCEPTUALOVERVIEW_TAG_ID, "");
			final RESTTagV1 taskTag = restClient.getJSONTag(DocbookBuilderConstants.TASK_TAG_ID, "");

			/* add TocFormatBranch objects for each top level tag */
			for (final RESTTagCategoryV1 tag : topLevelTags)
			{
				/*
				 * Create the top level tag. This level is represented by the
				 * tags that are not encompased, and includes any topic that has
				 * that tag or any tag that is encompassed by this tag.
				 */
				final TagRequirements topLevelBranchTags = new TagRequirements((RESTBaseTagV1) null, new ArrayList<RESTBaseTagV1>()
				{
					private static final long serialVersionUID = 7499166852563779981L;

					{
						add(tag);
						addAll(tag.getChildTags().returnItems());
					}
				});

				final Chapter topLevelTagChapter = new Chapter(tag.getName());
				retValue.appendChapter(topLevelTagChapter);
				
				populateContentSpecLevel(topics, topLevelTagChapter, topLevelBranchTags, null);

				for (final RESTTagCategoryV1 concernTag : concernCategory.getTags().returnItems())
				{
					/*
					 * the second level of the toc are the concerns, which will
					 * display the tasks and conceptual overviews beneath them
					 */
					final TagRequirements concernLevelChildTags = new TagRequirements(concernTag, (RESTTagV1) null);
					concernLevelChildTags.merge(topLevelBranchTags);
					final TagRequirements concernLevelDisplayTags = new TagRequirements((RESTBaseTagV1) null, new ArrayList<RESTBaseTagV1>()
					{
                        private static final long serialVersionUID = -204535050439409584L;

                        {
					        add(conceptualOverviewTag);
					        add(taskTag);
					    }
					});
					
					final Section concernSection = new Section(concernTag.getName());
					topLevelTagChapter.appendChild(concernSection);
					
					populateContentSpecLevel(topics, concernSection, concernLevelChildTags, concernLevelDisplayTags);
					
					/*
					 * the third levels of the TOC are the concept and reference
					 * topics
					 */
					final Section conceptSection = new Section(conceptTag.getName());
					final Section referenceSection = new Section(referenceTag.getName());
					
					if (concernSection.getChildNodes().isEmpty())
						concernSection.appendChild(referenceSection);
					else
						concernSection.insertBefore(referenceSection, concernSection.getFirstSpecNode());
					concernSection.insertBefore(conceptSection, referenceSection);
					
					populateContentSpecLevel(topics, conceptSection, concernLevelChildTags, new TagRequirements(conceptTag, (RESTTagV1) null));
					populateContentSpecLevel(topics, referenceSection, concernLevelChildTags, new TagRequirements(referenceTag, (RESTTagV1) null));

				}
			}

			return retValue;
		}
		catch (final Exception ex)
		{
			ExceptionUtilities.handleException(ex);
			return null;
		}
	}
}
