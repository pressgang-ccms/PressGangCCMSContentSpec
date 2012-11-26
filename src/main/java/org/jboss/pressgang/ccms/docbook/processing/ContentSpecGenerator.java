package org.jboss.pressgang.ccms.docbook.processing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jboss.pressgang.ccms.contentspec.Chapter;
import org.jboss.pressgang.ccms.contentspec.ContentSpec;
import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.Section;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.interfaces.DataProvider;
import org.jboss.pressgang.ccms.contentspec.structures.TagRequirements;
import org.jboss.pressgang.ccms.contentspec.wrapper.CategoryWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TopicWrapper;
import org.jboss.pressgang.ccms.docbook.compiling.DocbookBuildingOptions;
import org.jboss.pressgang.ccms.docbook.constants.DocbookBuilderConstants;
import org.jboss.pressgang.ccms.utils.common.ExceptionUtilities;

public class ContentSpecGenerator
{
	/** The REST client */
	private final DataProvider dataProvider;
	
	public ContentSpecGenerator(final DataProvider dataProvider)
	{
		this.dataProvider = dataProvider;
	}
	

	/**
	 * Generates a Content Specification and fills it in using a set of topics. Once the content
	 * specification is assembled it then removes any empty sections.
	 * 
	 * Note: All topics should be of the same locale.
	 * 
	 * @param topics The collection of topics to be used in the generate of the Content Specification.
	 * @param locale The locale of the topics.
	 * @return A ContentSpec object that represents the Content Specification. The toString() method can be used to get the text based version.
	 */
	public ContentSpec generateContentSpecFromTopics(final List<TopicWrapper> topics, final String locale)
	{
		return this.generateContentSpecFromTopics(topics, locale, new DocbookBuildingOptions());
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
	public ContentSpec generateContentSpecFromTopics(final List<TopicWrapper> topics, final String locale, final DocbookBuildingOptions docbookBuildingOptions)
	{
		final ContentSpec contentSpec = doFormattedTocPass(topics, locale, docbookBuildingOptions);
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
    private void populateContentSpecLevel(final List<TopicWrapper> topics, final Level level, final TagRequirements childRequirements, final TagRequirements displayRequirements)
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

			for (final TopicWrapper topic : topics)
			{
				boolean doesMatch = true;
				for (final TagWrapper andTag : requirements.getMatchAllOf())
				{
					if (!topic.hasTag(andTag.getId()))
					{
						doesMatch = false;
						break;
					}
				}

				if (doesMatch && requirements.getMatchOneOf().size() != 0)
				{
					for (final ArrayList<TagWrapper> orBlock : requirements.getMatchOneOf())
					{
						if (orBlock.size() != 0)
						{
							boolean matchesOrBlock = false;
							for (final TagWrapper orTag : orBlock)
							{
								if (topic.hasTag(orTag.getId()))
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
					final Integer topicId = topic.getId();
					final String topicTitle = topic.getTopicTitle();
					
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
	private ContentSpec doFormattedTocPass(final List<TopicWrapper> topics, final String locale, final DocbookBuildingOptions docbookBuildingOptions)
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

			/* Create an expand block for the tag parent tags */
			/*final ExpandDataTrunk expandParentTags = new ExpandDataTrunk(new ExpandDataDetails("parenttags"));
			expandParentTags.setBranches(CollectionUtilities.toArrayList(new ExpandDataTrunk(new ExpandDataDetails("categories"))));

			final ExpandDataTrunk expandChildTags = new ExpandDataTrunk(new ExpandDataDetails("childtags"));

			final ExpandDataTrunk expandTags = new ExpandDataTrunk(new ExpandDataDetails("tags"));
			expandTags.setBranches(CollectionUtilities.toArrayList(expandParentTags, expandChildTags));

			final ExpandDataTrunk expand = new ExpandDataTrunk();
			expand.setBranches(CollectionUtilities.toArrayList(expandTags));

			final String expandString = mapper.writeValueAsString(expand);
			//final String expandEncodedString = URLEncoder.encode(expandString, "UTF-8");*/

			/* Get the technology and common names categories */
			final CategoryWrapper technologyCategroy = dataProvider.getCategoryById(DocbookBuilderConstants.TECHNOLOGY_CATEGORY_ID);
			final CategoryWrapper commonNamesCategory = dataProvider.getCategoryById(DocbookBuilderConstants.COMMON_NAME_CATEGORY_ID);

			/*
			 * The top level TOC elements are made up of the technology and
			 * common name tags that are not encompassed by another tag. So here
			 * we get the tags out of the tech and common names categories, and
			 * pull outthose that are not encompassed.
			 */
			final List<TagWrapper> topLevelTags = new ArrayList<TagWrapper>();
			for (final CategoryWrapper category : new CategoryWrapper[]
			{ technologyCategroy, commonNamesCategory })
			{
				if (category.getTags() != null)
				{
				    final List<TagWrapper> tags = category.getTags();
					for (final TagWrapper tag : tags)
					{
						boolean isEmcompassed = false;
						final List<TagWrapper> parentTags = tag.getParentTags();
						for (final TagWrapper parentTag : parentTags)
						{
						    final List<CategoryWrapper> parentTagCategories = parentTag.getCategories();
							for (final CategoryWrapper parentTagCategory : parentTagCategories)
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
			/*final ExpandDataTrunk concernCategoryExpand = new ExpandDataTrunk();
			final ExpandDataTrunk concernCategoryExpandTags = new ExpandDataTrunk(new ExpandDataDetails("tags"));
			concernCategoryExpand.setBranches(CollectionUtilities.toArrayList(concernCategoryExpandTags));

			final String concernCategoryExpandString = mapper.writeValueAsString(concernCategoryExpand);
			//final String concernCategoryExpandStringEncoded = URLEncoder.encode(concernCategoryExpandString, "UTF-8");*/

			/* Get the technology and common names categories */
			final CategoryWrapper concernCategory = dataProvider.getCategoryById(DocbookBuilderConstants.CONCERN_CATEGORY_ID);

			/* Get the task reference and concept tag*/
			final TagWrapper referenceTag = dataProvider.getTagById(DocbookBuilderConstants.REFERENCE_TAG_ID);
			final TagWrapper conceptTag = dataProvider.getTagById(DocbookBuilderConstants.CONCEPT_TAG_ID);
			final TagWrapper conceptualOverviewTag = dataProvider.getTagById(DocbookBuilderConstants.CONCEPTUALOVERVIEW_TAG_ID);
			final TagWrapper taskTag = dataProvider.getTagById(DocbookBuilderConstants.TASK_TAG_ID);

			/* add TocFormatBranch objects for each top level tag */
			for (final TagWrapper tag : topLevelTags)
			{
				/*
				 * Create the top level tag. This level is represented by the
				 * tags that are not encompased, and includes any topic that has
				 * that tag or any tag that is encompassed by this tag.
				 */
				final TagRequirements topLevelBranchTags = new TagRequirements((TagWrapper) null, new ArrayList<TagWrapper>()
				{
					private static final long serialVersionUID = 7499166852563779981L;

					{
						add(tag);
						addAll(tag.getChildTags());
					}
				});

				final Chapter topLevelTagChapter = new Chapter(tag.getTagName());
				retValue.appendChapter(topLevelTagChapter);
				
				populateContentSpecLevel(topics, topLevelTagChapter, topLevelBranchTags, null);

				final List<TagWrapper> concernTags = concernCategory.getTags();
				for (final TagWrapper concernTag : concernTags)
				{
					/*
					 * the second level of the toc are the concerns, which will
					 * display the tasks and conceptual overviews beneath them
					 */
					final TagRequirements concernLevelChildTags = new TagRequirements(concernTag, (TagWrapper) null);
					concernLevelChildTags.merge(topLevelBranchTags);
					final TagRequirements concernLevelDisplayTags = new TagRequirements((TagWrapper) null, new ArrayList<TagWrapper>()
					{
                        private static final long serialVersionUID = -204535050439409584L;

                        {
					        add(conceptualOverviewTag);
					        add(taskTag);
					    }
					});
					
					final Section concernSection = new Section(concernTag.getTagName());
					topLevelTagChapter.appendChild(concernSection);
					
					populateContentSpecLevel(topics, concernSection, concernLevelChildTags, concernLevelDisplayTags);
					
					/*
					 * the third levels of the TOC are the concept and reference
					 * topics
					 */
					final Section conceptSection = new Section(conceptTag.getTagName());
					final Section referenceSection = new Section(referenceTag.getTagName());
					
					if (concernSection.getChildNodes().isEmpty())
						concernSection.appendChild(referenceSection);
					else
						concernSection.insertBefore(referenceSection, concernSection.getFirstSpecNode());
					concernSection.insertBefore(conceptSection, referenceSection);
					
					populateContentSpecLevel(topics, conceptSection, concernLevelChildTags, new TagRequirements(conceptTag, (TagWrapper) null));
					populateContentSpecLevel(topics, referenceSection, concernLevelChildTags, new TagRequirements(referenceTag, (TagWrapper) null));

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
