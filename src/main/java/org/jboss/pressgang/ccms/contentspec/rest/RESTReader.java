package org.jboss.pressgang.ccms.contentspec.rest;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.core.PathSegment;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;
import org.jboss.pressgang.ccms.contentspec.entities.AuthorInformation;
import org.jboss.pressgang.ccms.contentspec.rest.utils.RESTCollectionCache;
import org.jboss.pressgang.ccms.contentspec.rest.utils.RESTEntityCache;
import org.jboss.pressgang.ccms.rest.v1.collections.RESTCategoryCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.collections.RESTTagCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.collections.RESTTopicCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.collections.RESTTranslatedTopicCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.collections.RESTUserCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.components.ComponentBaseRESTEntityWithPropertiesV1;
import org.jboss.pressgang.ccms.rest.v1.components.ComponentBaseTopicV1;
import org.jboss.pressgang.ccms.rest.v1.components.ComponentTagV1;
import org.jboss.pressgang.ccms.rest.v1.components.ComponentTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTCategoryV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTImageV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTagV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTopicSourceUrlV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTranslatedTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTUserV1;
import org.jboss.pressgang.ccms.rest.v1.entities.base.RESTBaseTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.join.RESTAssignedPropertyTagV1;
import org.jboss.pressgang.ccms.rest.v1.entities.join.RESTCategoryTagV1;
import org.jboss.pressgang.ccms.rest.v1.expansion.ExpandDataDetails;
import org.jboss.pressgang.ccms.rest.v1.expansion.ExpandDataTrunk;
import org.jboss.pressgang.ccms.rest.v1.jaxrsinterfaces.RESTInterfaceV1;
import org.jboss.pressgang.ccms.utils.common.CollectionUtilities;
import org.jboss.pressgang.ccms.utils.common.ExceptionUtilities;
import org.jboss.resteasy.specimpl.PathSegmentImpl;

public class RESTReader
{
	private final Logger log = Logger.getLogger(RESTReader.class);

	private final RESTInterfaceV1 client;
	private final ObjectMapper mapper = new ObjectMapper();
	private final RESTEntityCache entityCache;
	private final RESTCollectionCache collectionsCache;

	public RESTReader(final RESTInterfaceV1 client, final RESTEntityCache entityCache, final RESTCollectionCache collectionsCache)
	{
		this.client = client;
		this.entityCache = entityCache;
		this.collectionsCache = collectionsCache;
	}

	// CATEGORY QUERIES

	/*
	 * Gets a specific category tuple from the database as specified by the
	 * categories ID.
	 */
	public RESTCategoryV1 getCategoryById(final int id)
	{
		try
		{
			if (entityCache.containsKeyValue(RESTCategoryV1.class, id))
			{
				return entityCache.get(RESTCategoryV1.class, id);
			}
			else
			{
				final RESTCategoryV1 category = client.getJSONCategory(id, null);
				entityCache.add(category);
				return category;
			}
		}
		catch (Exception e)
		{
			log.debug(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Gets a List of all categories tuples for a specified name.
	 */
	public List<RESTCategoryV1> getCategoriesByName(final String name)
	{
		final List<RESTCategoryV1> output = new ArrayList<RESTCategoryV1>();

		try
		{
		    RESTCategoryCollectionV1 categories = collectionsCache.get(RESTCategoryV1.class, RESTCategoryCollectionV1.class);
			if (categories.getItems() == null)
			{
				/* We need to expand the Categories collection */
				final ExpandDataTrunk expand = new ExpandDataTrunk();
				expand.setBranches(CollectionUtilities.toArrayList(new ExpandDataTrunk(new ExpandDataDetails("categories"))));

				final String expandString = mapper.writeValueAsString(expand);
				//final String expandEncodedString = URLEncoder.encode(expandString, "UTF-8");

				categories = client.getJSONCategories(expandString);
				collectionsCache.add(RESTCategoryV1.class, categories);
			}

			if (categories != null)
			{
			    final List<RESTCategoryV1> cats = categories.returnItems();
				for (final RESTCategoryV1 cat : cats)
				{
					if (cat.getName().equals(name))
					{
						output.add(cat);
					}
				}
			}

			return output;
		}
		catch (Exception e)
		{
			log.error(ExceptionUtilities.getStackTrace(e));
		}
		return null;
	}

	/*
	 * Gets a Category item assuming that tags can only have one category
	 */
	public RESTCategoryTagV1 getCategoryByTagId(final int tagId)
	{
		final RESTTagV1 tag = getTagById(tagId);
		if (tag == null)
			return null;

		final List<RESTCategoryTagV1> categories = tag.getCategories().returnItems();
		return categories.size() > 0 ? categories.get(0) : null;
	}

	// TAG QUERIES

	/*
	 * Gets a specific tag tuple from the database as specified by the tags ID.
	 */
	public RESTTagV1 getTagById(final int id)
	{
		try
		{
			if (entityCache.containsKeyValue(RESTTagV1.class, id))
			{
				return entityCache.get(RESTTagV1.class, id);
			}
			else
			{
				/*
				 * We need to expand the Categories collection in most cases so
				 * expand it anyway
				 */
				final ExpandDataTrunk expand = new ExpandDataTrunk();
                final ExpandDataTrunk expandCategories = new ExpandDataTrunk(new ExpandDataDetails(RESTTagV1.CATEGORIES_NAME));
                final ExpandDataTrunk expandProperties = new ExpandDataTrunk(new ExpandDataDetails(RESTTagV1.PROPERTIES_NAME));
				expand.setBranches(CollectionUtilities.toArrayList(expandCategories, expandProperties));

				final String expandString = mapper.writeValueAsString(expand);
				//final String expandEncodedString = URLEncoder.encode(expandString, "UTF-8");
				
				final RESTTagV1 tag = client.getJSONTag(id, expandString);
				entityCache.add(tag);
				return tag;
			}
		}
		catch (Exception e)
		{
			log.error(ExceptionUtilities.getStackTrace(e));
		}
		return null;
	}

	/*
	 * Gets a List of all tag tuples for a specified name.
	 */
	public List<RESTTagV1> getTagsByName(final String name)
	{
		final List<RESTTagV1> output = new ArrayList<RESTTagV1>();

		try
		{
		    RESTTagCollectionV1 tags = collectionsCache.get(RESTTagV1.class, RESTTagCollectionV1.class);
			if (tags.getItems() == null)
			{
				/* We need to expand the Tags & Categories collection */
				final ExpandDataTrunk expand = new ExpandDataTrunk();
				final ExpandDataTrunk expandTags = new ExpandDataTrunk(new ExpandDataDetails("tags"));
                final ExpandDataTrunk expandCategories = new ExpandDataTrunk(new ExpandDataDetails(RESTTagV1.CATEGORIES_NAME));
                final ExpandDataTrunk expandProperties = new ExpandDataTrunk(new ExpandDataDetails(RESTTagV1.PROPERTIES_NAME));
				expandTags.setBranches(CollectionUtilities.toArrayList(expandCategories, expandProperties));
				expand.setBranches(CollectionUtilities.toArrayList(expandTags));

				final String expandString = mapper.writeValueAsString(expand);

				tags = client.getJSONTags(expandString);
				collectionsCache.add(RESTTagV1.class, tags);
			}

			// Iterate through the list of tags and check if the tag is a Type
			// and matches the name.
			if (tags != null)
			{
			    final List<RESTTagV1> tagItems = tags.returnItems();
				for (final RESTTagV1 tag : tagItems)
				{
					if (tag.getName().equals(name))
					{
						output.add(tag);
					}
				}
			}

			return output;
		}
		catch (Exception e)
		{
			log.error(ExceptionUtilities.getStackTrace(e));
		}
		return null;
	}

	/*
	 * Gets a List of Tag tuples for a specified its TopicID relationship
	 * through TopicToTag.
	 */
	public List<RESTTagV1> getTagsByTopicId(final int topicId)
	{
		final RESTTopicV1 topic;
		if (entityCache.containsKeyValue(RESTTopicV1.class, topicId))
		{
			topic = entityCache.get(RESTTopicV1.class, topicId);
		}
		else
		{
			topic = getTopicById(topicId, null);
		}

		return topic == null ? null : topic.getTags().returnItems();
	}

	// TOPIC QUERIES

	/*
	 * Gets a specific tag tuple from the database as specified by the tags ID.
	 */
	public RESTTopicV1 getTopicById(final int id, final Integer rev)
	{
		return getTopicById(id, rev, false);
	}
	
	/*
	 * Gets a specific tag tuple from the database as specified by the tags ID.
	 */
	public RESTTopicV1 getTopicById(final int id, final Integer rev, final boolean expandTranslations)
	{
		try
		{
			final RESTTopicV1 topic;
			if (entityCache.containsKeyValue(RESTTopicV1.class, id, rev))
			{
				topic = entityCache.get(RESTTopicV1.class, id, rev);
			}
			else
			{
				/* We need to expand the all the items in the topic collection */
				final ExpandDataTrunk expand = new ExpandDataTrunk();
				final ExpandDataTrunk expandTags = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.TAGS_NAME));
                final ExpandDataTrunk expandSourceUrls = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.SOURCE_URLS_NAME));
                final ExpandDataTrunk expandCategories = new ExpandDataTrunk(new ExpandDataDetails(RESTTagV1.CATEGORIES_NAME));
                final ExpandDataTrunk expandProperties = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.PROPERTIES_NAME));
                //final ExpandDataTrunk expandOutgoing = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.OUTGOING_NAME));
                //final ExpandDataTrunk expandIncoming = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.INCOMING_NAME));
				final ExpandDataTrunk expandTopicTranslations = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.TRANSLATEDTOPICS_NAME));
				
				expandTags.setBranches(CollectionUtilities.toArrayList(expandCategories, expandProperties));
				expand.setBranches(CollectionUtilities.toArrayList(expandTags, expandSourceUrls, expandProperties/*,
						expandOutgoing, expandIncoming*/));

				if (expandTranslations)
				{
					expand.getBranches().add(expandTopicTranslations);
				}
				
				final String expandString = mapper.writeValueAsString(expand);
				
				if (rev == null)
				{
					topic = client.getJSONTopic(id, expandString);
					entityCache.add(topic);
				}
				else
				{
					topic = client.getJSONTopicRevision(id, rev, expandString);
					entityCache.add(topic, rev);
				}
			}
			return topic;
		}
		catch (Exception e)
		{
			log.error(ExceptionUtilities.getStackTrace(e));
		}
		return null;
	}

	/*
	 * Gets a collection of topics based on the list of ids passed.
	 */
	public RESTTopicCollectionV1 getTopicsByIds(final List<Integer> ids, final boolean expandTranslations)
	{
		if (ids.isEmpty())
			return null;

		try
		{
			final RESTTopicCollectionV1 topics = new RESTTopicCollectionV1();
			final StringBuffer urlVars = new StringBuffer("query;topicIds=");
			//final String encodedComma = URLEncoder.encode(",", "UTF-8");

			for (Integer id : ids)
			{
				if (!entityCache.containsKeyValue(RESTTopicV1.class, id))
				{
					urlVars.append(id + ",");
				}
				else
				{
					topics.addItem(entityCache.get(RESTTopicV1.class, id));
				}
			}

			String query = urlVars.toString();

			/* Get the missing topics from the REST interface */
			if (query.length() != "query;topicIds=".length())
			{
				query = query.substring(0, query.length() - 1);

				final PathSegment path = new PathSegmentImpl(query, false);
				
				final RESTTopicCollectionV1 downloadedTopicsSize = client.getJSONTopicsWithQuery(path, "");
				
				/* Load the topics in groups to save memory when unmarshalling */
				final int numTopics = downloadedTopicsSize.getSize();
				for (int i = 0; i <= numTopics; i = i + 100)
				{
					/* We need to expand the all the items in the topic collection */
					final ExpandDataTrunk expand = new ExpandDataTrunk();
					final ExpandDataDetails expandTopicDetails = new ExpandDataDetails("topics");
					expandTopicDetails.setStart(i);
					expandTopicDetails.setEnd(i + 100);
					final ExpandDataTrunk topicsExpand = new ExpandDataTrunk(expandTopicDetails);
					final ExpandDataTrunk tags = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.TAGS_NAME));
					final ExpandDataTrunk properties = new ExpandDataTrunk(new ExpandDataDetails(RESTBaseTopicV1.PROPERTIES_NAME));
					final ExpandDataTrunk categories = new ExpandDataTrunk(new ExpandDataDetails(RESTTagV1.CATEGORIES_NAME));
					//final ExpandDataTrunk outgoingRelationships = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.OUTGOING_NAME));
					final ExpandDataTrunk expandTranslatedTopics = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.TRANSLATEDTOPICS_NAME));
					final ExpandDataTrunk expandSourceUrls = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.SOURCE_URLS_NAME));

					/* We need to expand the categories collection on the topic tags */
					tags.setBranches(CollectionUtilities.toArrayList(categories, properties));
					if (expandTranslations)
					{
						//outgoingRelationships.setBranches(CollectionUtilities.toArrayList(tags, properties, expandTranslatedTopics));
						topicsExpand.setBranches(CollectionUtilities.toArrayList(tags, /*outgoingRelationships,*/ properties, expandSourceUrls, expandTranslatedTopics));
					}
					else
					{
						//outgoingRelationships.setBranches(CollectionUtilities.toArrayList(tags, properties));
						topicsExpand.setBranches(CollectionUtilities.toArrayList(tags, /*outgoingRelationships,*/ properties, expandSourceUrls));
					}
					
					expand.setBranches(CollectionUtilities.toArrayList(topicsExpand));

					final String expandString = mapper.writeValueAsString(expand);
					//final String expandEncodedString = URLEncoder.encode(expandString, "UTF-8");
					
					final RESTTopicCollectionV1 downloadedTopics = client.getJSONTopicsWithQuery(path, expandString);
					entityCache.add(downloadedTopics);

					/* Transfer the downloaded data to the current topic list */
					if (downloadedTopics != null && downloadedTopics.getItems() != null)
					{
					    final List<RESTTopicV1> items = downloadedTopics.returnItems();
						for (final RESTTopicV1 item : items)
						{
							topics.addItem(item);
						}
					}
				}
			}

			return topics;
		}
		catch (Exception e)
		{
			log.error(ExceptionUtilities.getStackTrace(e));
		}
		return null;
	}

	/*
	 * Gets a list of Revision's from the TopicIndex database for a specific
	 * topic
	 */
	public List<Object[]> getTopicRevisionsById(final Integer topicId)
	{
		final List<Object[]> results = new ArrayList<Object[]>();
		try
		{
			final List<String> additionalKeys = CollectionUtilities.toArrayList("revisions", "topic" + topicId);
			final RESTTopicCollectionV1 topicRevisions;
			if (collectionsCache.containsKey(RESTTopicV1.class, additionalKeys))
			{
				topicRevisions = collectionsCache.get(RESTTopicV1.class, RESTTopicCollectionV1.class, additionalKeys);
			}
			else
			{
				/* We need to expand the Revisions collection */
				final ExpandDataTrunk expand = new ExpandDataTrunk();
				final ExpandDataTrunk expandTags = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.TAGS_NAME));
				final ExpandDataTrunk expandRevs = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.REVISIONS_NAME));
				final ExpandDataTrunk expandSourceUrls = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.SOURCE_URLS_NAME));
				final ExpandDataTrunk expandCategories = new ExpandDataTrunk(new ExpandDataDetails(RESTTagV1.CATEGORIES_NAME));
				final ExpandDataTrunk expandProperties = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.PROPERTIES_NAME));
				//final ExpandDataTrunk expandOutgoing = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.OUTGOING_NAME));
				//final ExpandDataTrunk expandIncoming = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.INCOMING_NAME));
				expandTags.setBranches(CollectionUtilities.toArrayList(expandCategories));
				expandRevs.setBranches(CollectionUtilities.toArrayList(expandTags, expandSourceUrls, expandProperties/*,
						expandOutgoing, expandIncoming*/));
				expand.setBranches(CollectionUtilities.toArrayList(expandRevs));

				final String expandString = mapper.writeValueAsString(expand);

				final RESTTopicV1 topic = client.getJSONTopic(topicId, expandString);
				collectionsCache.add(RESTTopicV1.class, topic.getRevisions(), additionalKeys, true);
				topicRevisions = topic.getRevisions();
			}

			// Create the custom revisions list
			if (topicRevisions != null && topicRevisions.getItems() != null)
			{
			    final List<RESTTopicV1> topicRevs = topicRevisions.returnItems();
				for (final RESTTopicV1 topicRev : topicRevs)
				{
					Object[] revision = new Object[3];
					revision[0] = topicRev.getRevision();
					revision[1] = topicRev.getLastModified();
					revision[2] = "";
					results.add(revision);
				}
			}
			return results;
		}
		catch (Exception e)
		{
			log.debug(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Gets a List of TopicSourceUrl tuples for a specified its TopicID
	 * relationship through TopicToTopicSourceUrl.
	 */
	public List<RESTTopicSourceUrlV1> getSourceUrlsByTopicId(final int topicId)
	{
		final RESTTopicV1 topic;
		if (entityCache.containsKeyValue(RESTTopicV1.class, topicId))
		{
			topic = entityCache.get(RESTTopicV1.class, topicId);
		}
		else
		{
			topic = getTopicById(topicId, null);
		}
		return topic == null ? null : topic.getSourceUrls_OTM().returnItems();
	}

	// TRANSLATED TOPICS QUERIES

	/*
	 * Gets a collection of translated topics based on the list of topic ids
	 * passed.
	 */
	public RESTTranslatedTopicCollectionV1 getTranslatedTopicsByTopicIds(final List<Integer> ids, final String locale)
	{
		if (ids.isEmpty())
			return null;

		try
		{
			final RESTTranslatedTopicCollectionV1 topics = new RESTTranslatedTopicCollectionV1();
			final StringBuffer urlVars = new StringBuffer("query;latestTranslations=true;topicIds=");
			//final String encodedComma = URLEncoder.encode(",", "UTF-8");

			for (final Integer id : ids)
			{
				if (!entityCache.containsKeyValue(RESTTranslatedTopicV1.class, id) && !entityCache.containsKeyValue(RESTTranslatedTopicV1.class, (id * -1)))
				{
					urlVars.append(id + ",");
				}
				else if (entityCache.containsKeyValue(RESTTranslatedTopicV1.class, (id * -1)))
				{
					topics.addItem(entityCache.get(RESTTranslatedTopicV1.class, (id * -1) + "-" + locale));
				}
				else
				{
					topics.addItem(entityCache.get(RESTTranslatedTopicV1.class, id + "-" + locale));
				}
			}

			String query = urlVars.toString();

			if (query.length() != "query;latestTranslations=true;topicIds=".length())
			{
				query = query.substring(0, query.length() - 1);

				/* Add the locale to the query if one was passed */
				if (locale != null && !locale.isEmpty())
					query += ";locale1=" + locale + "1";

				PathSegment path = new PathSegmentImpl(query, false);

				/*
				 * We need to expand the all the items in the translatedtopic
				 * collection
				 */
				final ExpandDataTrunk expand = new ExpandDataTrunk();

				final ExpandDataTrunk translatedTopicsExpand = new ExpandDataTrunk(new ExpandDataDetails("translatedTopics"));
				final ExpandDataTrunk topicExpandTranslatedTopics = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.TRANSLATEDTOPICS_NAME));
				final ExpandDataTrunk tags = new ExpandDataTrunk(new ExpandDataDetails(RESTTranslatedTopicV1.TAGS_NAME));
				final ExpandDataTrunk properties = new ExpandDataTrunk(new ExpandDataDetails(RESTBaseTopicV1.PROPERTIES_NAME));
				final ExpandDataTrunk categories = new ExpandDataTrunk(new ExpandDataDetails(RESTTagV1.CATEGORIES_NAME));
				//final ExpandDataTrunk outgoingRelationships = new ExpandDataTrunk(new ExpandDataDetails(RESTTranslatedTopicV1.ALL_LATEST_OUTGOING_NAME));
				final ExpandDataTrunk topicsExpand = new ExpandDataTrunk(new ExpandDataDetails(RESTTranslatedTopicV1.TOPIC_NAME));

				/* We need to expand the categories collection on the topic tags */
				tags.setBranches(CollectionUtilities.toArrayList(categories, properties));
				//outgoingRelationships.setBranches(CollectionUtilities.toArrayList(tags, properties, topicsExpand));

				topicsExpand.setBranches(CollectionUtilities.toArrayList(topicExpandTranslatedTopics));

				translatedTopicsExpand.setBranches(CollectionUtilities.toArrayList(tags, /*outgoingRelationships,*/ properties, topicsExpand));

				expand.setBranches(CollectionUtilities.toArrayList(translatedTopicsExpand));

				final String expandString = mapper.writeValueAsString(expand);
				//final String expandEncodedString = URLEncoder.encode(expandString, "UTF-8");
				
				final RESTTranslatedTopicCollectionV1 downloadedTopics = client.getJSONTranslatedTopicsWithQuery(path, expandString);
				entityCache.add(downloadedTopics);

				/* Transfer the downloaded data to the current topic list */
				if (downloadedTopics != null && downloadedTopics.getItems() != null)
				{
				    final List<RESTTranslatedTopicV1> items = downloadedTopics.returnItems();
					for (final RESTTranslatedTopicV1 item : items)
					{
						entityCache.add(item);
						topics.addItem(item);
					}
				}
			}

			return topics;
		}
		catch (Exception e)
		{
			log.error(ExceptionUtilities.getStackTrace(e));
		}
		return null;
	}
	
	/*
	 * Gets a collection of translated topics based on the list of topic ids
	 * passed.
	 */
	public RESTTranslatedTopicCollectionV1 getTranslatedTopicsByZanataIds(final List<Integer> ids, final String locale)
	{
		if (ids.isEmpty())
			return null;

		try
		{
			final RESTTranslatedTopicCollectionV1 topics = new RESTTranslatedTopicCollectionV1();
			final StringBuffer urlVars = new StringBuffer("query;latestTranslations=true;zanataIds=");
			final String encodedComma = URLEncoder.encode(",", "UTF-8");

			for (final Integer id : ids)
			{
				if (!entityCache.containsKeyValue(RESTTranslatedTopicV1.class, id + "-" + locale))
				{
					urlVars.append(id + encodedComma);
				}
				else
				{
					topics.addItem(entityCache.get(RESTTranslatedTopicV1.class, id + "-" + locale));
				}
			}

			String query = urlVars.toString();

			if (query.length() != "query;latestTranslations=true;zanataIds=".length())
			{
				query = query.substring(0, query.length() - encodedComma.length());

				/* Add the locale to the query if one was passed */
				if (locale != null && !locale.isEmpty())
					query += ";locale1=" + locale + "1";

				PathSegment path = new PathSegmentImpl(query, false);

				/*
				 * We need to expand the all the items in the translatedtopic
				 * collection
				 */
				final ExpandDataTrunk expand = new ExpandDataTrunk();

				final ExpandDataTrunk translatedTopicsExpand = new ExpandDataTrunk(new ExpandDataDetails("translatedTopics"));
				final ExpandDataTrunk topicExpandTranslatedTopics = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.TRANSLATEDTOPICS_NAME));
				final ExpandDataTrunk tags = new ExpandDataTrunk(new ExpandDataDetails(RESTTranslatedTopicV1.TAGS_NAME));
				final ExpandDataTrunk properties = new ExpandDataTrunk(new ExpandDataDetails(RESTBaseTopicV1.PROPERTIES_NAME));
				final ExpandDataTrunk categories = new ExpandDataTrunk(new ExpandDataDetails(RESTTagV1.CATEGORIES_NAME));
				//final ExpandDataTrunk outgoingRelationships = new ExpandDataTrunk(new ExpandDataDetails(RESTTranslatedTopicV1.ALL_LATEST_OUTGOING_NAME));
				final ExpandDataTrunk topicsExpand = new ExpandDataTrunk(new ExpandDataDetails(RESTTranslatedTopicV1.TOPIC_NAME));

				/* We need to expand the categories collection on the topic tags */
				tags.setBranches(CollectionUtilities.toArrayList(categories, properties));
				//outgoingRelationships.setBranches(CollectionUtilities.toArrayList(tags, properties, topicsExpand));

				topicsExpand.setBranches(CollectionUtilities.toArrayList(topicExpandTranslatedTopics));

				translatedTopicsExpand.setBranches(CollectionUtilities.toArrayList(tags, /*outgoingRelationships,*/ properties, topicsExpand));

				expand.setBranches(CollectionUtilities.toArrayList(translatedTopicsExpand));

				final String expandString = mapper.writeValueAsString(expand);
				//final String expandEncodedString = URLEncoder.encode(expandString, "UTF-8");
				
				final RESTTranslatedTopicCollectionV1 downloadedTopics = client.getJSONTranslatedTopicsWithQuery(path, expandString);
				entityCache.add(downloadedTopics);

				/* Transfer the downloaded data to the current topic list */
				if (downloadedTopics != null && downloadedTopics.getItems() != null)
				{
				    final List<RESTTranslatedTopicV1> items = downloadedTopics.returnItems();
					for (final RESTTranslatedTopicV1 item : items)
					{
						entityCache.add(item);
						topics.addItem(item);
					}
				}
			}

			return topics;
		}
		catch (Exception e)
		{
			log.error(ExceptionUtilities.getStackTrace(e));
		}
		return null;
	}

	/**
	 * Gets a translated topic based on a topic id, revision and locale.
	 */
	public RESTTranslatedTopicV1 getTranslatedTopicByTopicId(final Integer id, final Integer rev, final String locale)
	{
		if (locale == null) return null;
		final RESTTopicV1 topic = getTopicById(id, rev, true);
		if (topic == null)
			return null;
		
		if (topic.getTranslatedTopics_OTM() != null && topic.getTranslatedTopics_OTM().getItems() != null)
		{
		    final List<RESTTranslatedTopicV1> translatedTopics = topic.getTranslatedTopics_OTM().returnItems();
			for (final RESTTranslatedTopicV1 translatedTopic : translatedTopics)
			{
				if (rev != null && translatedTopic.getTopicRevision().equals(rev) && translatedTopic.getLocale().equals(locale))
				{
					return translatedTopic;
				}
				else if (rev == null)
				{
					return translatedTopic;
				}
			}
		}

		return null;
	}

	/**
	 * Gets a translated topic based on a topic id, revision and locale. The translated topic
	 * that is returned will be less then or equal to the revision that is passed. If the revision
	 * is null then the latest translated topic will be passed.
	 * 
	 * @param id The TopicID to find the translation for.
	 * @param rev The Topic Revision to find the translation for.
	 * @param locale The locale of the translation to find.
	 * @param expand If the content of the translated topic should be expanded.
	 * @return The closest matching translated topic otherwise null if none exist.
	 */
	public RESTTranslatedTopicV1 getClosestTranslatedTopicByTopicId(final Integer id, final Integer rev, final String locale, final boolean expand)
	{
		if (locale == null)
		{
			return null;
		}
		
		final RESTTopicV1 topic = getTopicById(id, rev, true);
		if (topic == null)
		{
			return null;
		}
		
		RESTTranslatedTopicV1 closestTranslation = null;
		if (topic.getTranslatedTopics_OTM() != null && topic.getTranslatedTopics_OTM().getItems() != null)
		{
		    final List<RESTTranslatedTopicV1> translatedTopics = topic.getTranslatedTopics_OTM().returnItems();
			for (final RESTTranslatedTopicV1 translatedTopic : translatedTopics)
			{
				if (translatedTopic.getLocale().equals(locale)
						/* Ensure that the translation is the newest translation possible */
						&& (closestTranslation == null || closestTranslation.getTopicRevision() < translatedTopic.getTopicRevision())
						/* Ensure that the translation revision is less than or equal to the revision specified */
						&& (rev == null || translatedTopic.getTopicRevision() <= rev))
				{
					
					closestTranslation = translatedTopic;
				}
			}
		}

		if (!expand)
		{
			return closestTranslation;
		}
		else if (closestTranslation != null)
		{
			return getTranslatedTopicById(closestTranslation.getId());
		}
		else
		{
			return null;
		}
	}

	/**
	 * Get a Translated Topic by it's id.
	 * 
	 * @param id
	 * @return
	 */
	public RESTTranslatedTopicV1 getTranslatedTopicById(final Integer id)
	{
		try
		{
			if (entityCache.containsKeyValue(RESTTranslatedTopicV1.class, id))
			{
				return entityCache.get(RESTTranslatedTopicV1.class, id);
			}
			else
			{
				/*
				 * We need to expand the all the items in the translatedtopic
				 * collection
				 */
				final ExpandDataTrunk expand = new ExpandDataTrunk();
		
				final ExpandDataTrunk topicExpandTranslatedTopics = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.TRANSLATEDTOPICS_NAME));
				final ExpandDataTrunk tags = new ExpandDataTrunk(new ExpandDataDetails(RESTTranslatedTopicV1.TAGS_NAME));
				final ExpandDataTrunk properties = new ExpandDataTrunk(new ExpandDataDetails(RESTBaseTopicV1.PROPERTIES_NAME));
				final ExpandDataTrunk categories = new ExpandDataTrunk(new ExpandDataDetails(RESTTagV1.CATEGORIES_NAME));
				//final ExpandDataTrunk outgoingRelationships = new ExpandDataTrunk(new ExpandDataDetails(RESTTranslatedTopicV1.ALL_LATEST_OUTGOING_NAME));
				final ExpandDataTrunk topicsExpand = new ExpandDataTrunk(new ExpandDataDetails(RESTTranslatedTopicV1.TOPIC_NAME));
		
				/* We need to expand the categories collection on the topic tags */
				tags.setBranches(CollectionUtilities.toArrayList(categories, properties));
				//outgoingRelationships.setBranches(CollectionUtilities.toArrayList(tags, properties, topicsExpand));

				topicsExpand.setBranches(CollectionUtilities.toArrayList(topicExpandTranslatedTopics));

				expand.setBranches(CollectionUtilities.toArrayList(tags, /*outgoingRelationships,*/ properties, topicsExpand));
		
				final String expandString = mapper.writeValueAsString(expand);
				
				final RESTTranslatedTopicV1 translatedTopic = client.getJSONTranslatedTopic(id, expandString);
				entityCache.add(translatedTopic);
				
				return translatedTopic;
			}
		}
		catch (Exception e)
		{
			log.error(ExceptionUtilities.getStackTrace(e));
		}
		return null;
	}

	// USER QUERIES

	/*
	 * Gets a List of all User tuples for a specified name.
	 */
	public List<RESTUserV1> getUsersByName(final String userName)
	{
		final List<RESTUserV1> output = new ArrayList<RESTUserV1>();

		try
		{
			final RESTUserCollectionV1 users;
			if (collectionsCache.containsKey(RESTUserV1.class))
			{
				users = collectionsCache.get(RESTUserV1.class, RESTUserCollectionV1.class);
			}
			else
			{
				/* We need to expand the Users collection */
				final ExpandDataTrunk expand = new ExpandDataTrunk();
				expand.setBranches(CollectionUtilities.toArrayList(new ExpandDataTrunk(new ExpandDataDetails("users"))));

				final String expandString = mapper.writeValueAsString(expand);
				//final String expandEncodedString = URLEncoder.encode(expandString, "UTF-8");
				
				users = client.getJSONUsers(expandString);
				collectionsCache.add(RESTUserV1.class, users);
			}

			if (users != null)
			{
			    final List<RESTUserV1> userItems = users.returnItems();
				for (final RESTUserV1 user : userItems)
				{
					if (user.getName().equals(userName))
					{
						output.add(user);
					}
				}
			}

			return output;
		}
		catch (Exception e)
		{
			log.error(ExceptionUtilities.getStackTrace(e));
		}
		return null;
	}

	/*
	 * Gets a specific User tuple from the database as specified by the tags ID.
	 */
	public RESTUserV1 getUserById(final int id)
	{
		try
		{
			if (entityCache.containsKeyValue(RESTUserV1.class, id))
			{
				return entityCache.get(RESTUserV1.class, id);
			}
			else
			{
				final RESTUserV1 user = client.getJSONUser(id, null);
				entityCache.add(user);
				return user;
			}
		}
		catch (Exception e)
		{
			log.error(ExceptionUtilities.getStackTrace(e));
		}
		return null;
	}

	// CONTENT SPEC QUERIES

	/*
	 * Gets a ContentSpec tuple for a specified id.
	 */
	public RESTTopicV1 getContentSpecById(final int id, final Integer rev)
	{
		return getContentSpecById(id, rev, false);
	}
	
	/*
	 * Gets a ContentSpec tuple for a specified id.
	 */
	public RESTTopicV1 getContentSpecById(final int id, final Integer rev, final boolean expandTranslations)
	{
		final RESTTopicV1 cs = getTopicById(id, rev, expandTranslations);
		if (cs == null)
			return null;
		
		final List<RESTTagV1> topicTypes = ComponentBaseTopicV1.returnTagsInCategoriesByID(cs, CollectionUtilities.toArrayList(CSConstants.TYPE_CATEGORY_ID));
		for (final RESTTagV1 type : topicTypes)
		{
			if (type.getId().equals(CSConstants.CONTENT_SPEC_TAG_ID))
			{
				return cs;
			}
		}
		return null;
	}
	
	/*
	 * Gets a ContentSpec tuple for a specified id.
	 */
	public RESTTranslatedTopicV1 getTranslatedContentSpecById(final int id, final Integer rev, final String locale)
	{
		if (locale == null) return null;
		final RESTTopicV1 cs = getTopicById(id, rev, true);
		if (cs == null)
			return null;
		
		final List<RESTTagV1> topicTypes = ComponentBaseTopicV1.returnTagsInCategoriesByID(cs, CollectionUtilities.toArrayList(CSConstants.TYPE_CATEGORY_ID));
		if (cs.getTranslatedTopics_OTM() != null && cs.getTranslatedTopics_OTM().getItems() != null)
		{
			for (final RESTTagV1 type : topicTypes)
			{
				if (type.getId().equals(CSConstants.CONTENT_SPEC_TAG_ID))
				{
				    final List<RESTTranslatedTopicV1> topics = cs.getTranslatedTopics_OTM().returnItems();
				    
				    RESTTranslatedTopicV1 latestTranslatedTopic = null;
					for (final RESTTranslatedTopicV1 topic : topics)
					{
					    if (topic.getLocale().equals(locale) && (latestTranslatedTopic == null || latestTranslatedTopic.getTopicRevision() < topic.getTopicRevision()))
					    {
    						if (rev != null && topic.getTopicRevision() <= cs.getRevision())
    						{
    						    latestTranslatedTopic = topic;
    						}
    						else if (rev == null)
    						{
    						    latestTranslatedTopic = topic;
    						}
					    }
					}
					
					return latestTranslatedTopic;
				}
			}
		}
		return null;
	}

	/*
	 * Gets a list of Revision's from the CSProcessor database for a specific
	 * content spec
	 */
	public List<Object[]> getContentSpecRevisionsById(final Integer csId, final Integer startLimit, final Integer endLimit)
	{
		final List<Object[]> results = new ArrayList<Object[]>();
		try
		{
			final List<String> additionalKeys = CollectionUtilities.toArrayList("revision", "topic" + csId);
			final RESTTopicCollectionV1 topicRevisions;
			if (collectionsCache.containsKey(RESTTopicV1.class, additionalKeys))
			{
				topicRevisions = collectionsCache.get(RESTTopicV1.class, RESTTopicCollectionV1.class, additionalKeys);
			}
			else
			{
			    final ExpandDataDetails revisionDetails = new ExpandDataDetails(RESTTopicV1.REVISIONS_NAME);
			    
			    if (startLimit != null || endLimit!= null)
			    {
			        revisionDetails.setEnd(endLimit);
			        revisionDetails.setStart(startLimit);
			    }
			    
				/* We need to expand the Revisions collection */
				final ExpandDataTrunk expand = new ExpandDataTrunk();
				final ExpandDataTrunk expandTags = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.TAGS_NAME));
				final ExpandDataTrunk expandRevs = new ExpandDataTrunk(revisionDetails);
				expandRevs.setBranches(CollectionUtilities.toArrayList(new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.PROPERTIES_NAME))));
				expand.setBranches(CollectionUtilities.toArrayList(expandTags, expandRevs));

				final String expandString = mapper.writeValueAsString(expand);
				//final String expandEncodedString = URLEncoder.encode(expandString, "UTF-8");

				final RESTTopicV1 topic = client.getJSONTopic(csId, expandString);
				// Check that the topic is a content spec
				if (!ComponentBaseTopicV1.hasTag(topic, CSConstants.CONTENT_SPEC_TAG_ID))
					return null;

				// Add the content spec revisions to the cache
				//collectionsCache.add(RESTTopicV1.class, topic.getRevisions(), additionalKeys, true);
				topicRevisions = topic.getRevisions();
			}

			// Create the unique array from the revisions
			if (topicRevisions != null && topicRevisions.getItems() != null)
			{
			    final List<RESTTopicV1> topicRevs = topicRevisions.returnItems();
				for (final RESTTopicV1 topicRev : topicRevs)
				{
					Object[] revision = new Object[3];
					revision[0] = topicRev.getRevision();
					revision[1] = topicRev.getLastModified();
					final RESTAssignedPropertyTagV1 type = ComponentTopicV1.returnProperty(topicRev, CSConstants.CSP_TYPE_PROPERTY_TAG_ID);
					if (type != null)
					{
						revision[2] = type.getValue();
					}
					else
					{
						revision[2] = "";
					}
					results.add(revision);
				}
			}
			return results;
		}
		catch (Exception e)
		{
			log.error(ExceptionUtilities.getStackTrace(e));
		}
		return null;
	}

	/*
	 * Gets a list of all content specifications in the database or the first 50
	 * if limit is set
	 */
	public List<RESTTopicV1> getContentSpecs(Integer startPos, Integer limit)
	{
		final List<RESTTopicV1> results = new ArrayList<RESTTopicV1>();

		try
		{
			final RESTTopicCollectionV1 topics;

			// Set the startPos and limit to zero if they are null
			startPos = startPos == null ? 0 : startPos;
			limit = limit == null ? 0 : limit;

			final List<String> additionalKeys = CollectionUtilities.toArrayList("start-" + startPos, "end-" + (startPos + limit));
			if (collectionsCache.containsKey(RESTTopicV1.class, additionalKeys))
			{
				topics = collectionsCache.get(RESTTopicV1.class, RESTTopicCollectionV1.class, additionalKeys);
			}
			else
			{
				/* We need to expand the topics collection */
				final ExpandDataTrunk expand = new ExpandDataTrunk();
				ExpandDataDetails expandDataDetails = new ExpandDataDetails("topics");
				if (startPos != 0 && startPos != null)
				{
					expandDataDetails.setStart(startPos);
				}
				if (limit != 0 && limit != null)
				{
					expandDataDetails.setEnd(startPos + limit);
				}

				final ExpandDataTrunk expandTopics = new ExpandDataTrunk(expandDataDetails);
				final ExpandDataTrunk expandTags = new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.TAGS_NAME));
				expandTags.setBranches(CollectionUtilities.toArrayList(new ExpandDataTrunk(new ExpandDataDetails(RESTTagV1.CATEGORIES_NAME))));
				expandTopics.setBranches(CollectionUtilities.toArrayList(expandTags, new ExpandDataTrunk(new ExpandDataDetails(RESTTopicV1.PROPERTIES_NAME))));

				expand.setBranches(CollectionUtilities.toArrayList(expandTopics));

				final String expandString = mapper.writeValueAsString(expand);
				//final String expandEncodedString = URLEncoder.encode(expandString, "UTF-8");

				final PathSegment path = new PathSegmentImpl("query;tag" + CSConstants.CONTENT_SPEC_TAG_ID + "=1;", false);
				topics = client.getJSONTopicsWithQuery(path, expandString);
				collectionsCache.add(RESTTopicV1.class, topics, additionalKeys);
			}

			return topics.returnItems();
		}
		catch (Exception e)
		{
			log.error(ExceptionUtilities.getStackTrace(e));
		}
		return results;
	}

	/*
	 * Gets a list of Revision's from the CSProcessor database for a specific
	 * content spec
	 */
	public Integer getLatestCSRevById(final Integer csId)
	{
		final RESTTopicV1 cs = getTopicById(csId, null, false);
		if (cs != null)
		{
			return cs.getRevision();
		}
		return null;
	}

	/*
     * Get the Pre Processed Content Specification for a ID and Revision
     */
    public RESTTopicV1 getPreContentSpecById(final Integer id, final Integer revision)
    {
        return getPreContentSpecById(id, revision, null, 5, true, 5);
    }
	
	/*
	 * Get the Pre Processed Content Specification for a ID and Revision
	 */
	public RESTTopicV1 getPreContentSpecById(final Integer id, final Integer revision, final Integer startLimit, final Integer endLimit, final boolean recursive, final int increment)
	{
		final RESTTopicV1 cs = getContentSpecById(id, revision);
		final List<Object[]> specRevisions = getContentSpecRevisionsById(id, startLimit, endLimit);

		if (specRevisions == null || specRevisions.isEmpty())
			return null;

		// Create a sorted set of revision ids that are less the the current
		// revision
		final SortedSet<Integer> sortedSpecRevisions = new TreeSet<Integer>();
		for (final Object[] specRev : specRevisions)
		{
			if ((Integer) specRev[0] <= cs.getRevision())
			{
				sortedSpecRevisions.add((Integer) specRev[0]);
			}
		}

		// Find the Pre Content Spec from the revisions
		RESTTopicV1 preContentSpec = null;
		if (sortedSpecRevisions.size() > 0)
        {
    		Integer specRev = sortedSpecRevisions.last();
    		while (specRev != null)
    		{
    			final RESTTopicV1 contentSpecRev = getContentSpecById(id, specRev);
    			if (ComponentBaseRESTEntityWithPropertiesV1.returnProperty(contentSpecRev, CSConstants.CSP_TYPE_PROPERTY_TAG_ID) != null && ComponentBaseRESTEntityWithPropertiesV1.returnProperty(contentSpecRev, CSConstants.CSP_TYPE_PROPERTY_TAG_ID).getValue().equals(CSConstants.CSP_PRE_PROCESSED_STRING))
    			{
    				preContentSpec = contentSpecRev;
    				break;
    			}
    			specRev = sortedSpecRevisions.headSet(specRev).isEmpty() ? null : sortedSpecRevisions.headSet(specRev).last();
    		}
        }
		
		/* If the defaults were used then try to go back 5 more */
        if (preContentSpec == null && recursive)
        {
            final Integer start = startLimit == null ? increment : startLimit + increment;
            final Integer end = endLimit == null ? null : endLimit + increment;
            
            return this.getPreContentSpecById(id, revision, start, end, recursive, increment);
        }
		return preContentSpec;
	}
	
	/*
     * Get the Pre Processed Content Specification for a ID and Revision
     */
    public RESTTopicV1 getPostContentSpecById(final Integer id, final Integer revision)
    {
        return getPostContentSpecById(id, revision, null, 5, true, 5);
    }

	/*
	 * Get the Pre Processed Content Specification for a ID and Revision
	 */
	public RESTTopicV1 getPostContentSpecById(final Integer id, final Integer revision, final Integer startLimit, final Integer endLimit, final boolean recursive, final int increment)
	{
		final RESTTopicV1 cs = getContentSpecById(id, revision);
		final List<Object[]> specRevisions = getContentSpecRevisionsById(id, startLimit, endLimit);

		if (specRevisions == null || specRevisions.isEmpty())
			return null;

		// Create a sorted set of revision ids that are less the the current
		// revision
		final SortedSet<Integer> sortedSpecRevisions = new TreeSet<Integer>();
		for (final Object[] specRev : specRevisions)
		{
			if ((Integer) specRev[0] <= cs.getRevision())
			{
				sortedSpecRevisions.add((Integer) specRev[0]);
			}
		}

		// Find the Post Content Spec from the revisions
		RESTTopicV1 postContentSpec = null;
		if (sortedSpecRevisions.size() > 0)
		{
    		Integer specRev = sortedSpecRevisions.last();
    		while (specRev != null)
    		{
    			final RESTTopicV1 contentSpecRev = getContentSpecById(id, specRev);
    			if (ComponentBaseRESTEntityWithPropertiesV1.returnProperty(contentSpecRev, CSConstants.CSP_TYPE_PROPERTY_TAG_ID) != null && ComponentBaseRESTEntityWithPropertiesV1.returnProperty(contentSpecRev, CSConstants.CSP_TYPE_PROPERTY_TAG_ID).getValue().equals(CSConstants.CSP_POST_PROCESSED_STRING))
    			{
    				postContentSpec = contentSpecRev;
    				break;
    			}
    			specRev = sortedSpecRevisions.headSet(specRev).isEmpty() ? null : sortedSpecRevisions.headSet(specRev).last();
    		}
		}
		
		/* If the defaults were used then try to go back 5 more */
		if (postContentSpec == null && recursive)
		{
		    final Integer start = startLimit == null ? increment : startLimit + increment;
		    final Integer end = endLimit == null ? null : endLimit + increment;
		    
		    return this.getPostContentSpecById(id, revision, start, end, recursive, increment);
		}
		return postContentSpec;
	}

	// MISC QUERIES

	/*
	 * Gets a List of all type tuples for a specified name.
	 */
	public RESTTagV1 getTypeByName(final String name)
	{
		final List<RESTTagV1> tags = getTagsByName(name);

		// Iterate through the list of tags and check if the tag is a Type and
		// matches the name.
		if (tags != null)
		{
			for (final RESTTagV1 tag : tags)
			{
				if (ComponentTagV1.containedInCategory(tag, CSConstants.TYPE_CATEGORY_ID) && tag.getName().equals(name))
				{
					return tag;
				}
			}
		}
		return null;
	}

	/*
	 * Gets an Image File for a specific ID
	 */
	public RESTImageV1 getImageById(final int id)
	{
		try
		{
			return client.getJSONImage(id, null);
		}
		catch (Exception e)
		{
			log.error(ExceptionUtilities.getStackTrace(e));
		}
		return null;
	}

	// AUTHOR INFORMATION QUERIES

	/*
	 * Gets the Author Tag for a specific topic
	 */
	public RESTTagV1 getAuthorForTopic(final int topicId, final Integer rev)
	{
		if (rev == null)
		{
			final List<RESTTagV1> tags = this.getTagsByTopicId(topicId);

			if (tags != null)
			{
				for (RESTTagV1 tag : tags)
				{
					if (ComponentTagV1.containedInCategory(tag, CSConstants.WRITER_CATEGORY_ID))
						return tag;
				}
			}
		}
		else
		{
			final RESTTopicV1 topic = this.getTopicById(topicId, rev);
			if (topic != null)
			{
			    final List<RESTTopicV1> topicRevisions = topic.getRevisions().returnItems();
				for (final RESTTopicV1 topicRevision : topicRevisions)
				{
					if (topicRevision.getRevision().equals(rev))
					{
						List<RESTTagV1> writerTags = ComponentBaseTopicV1.returnTagsInCategoriesByID(topicRevision, CollectionUtilities.toArrayList(CSConstants.WRITER_CATEGORY_ID));
						if (writerTags.size() == 1)
							return writerTags.get(0);
						break;
					}
				}
			}
		}
		return null;
	}

	/*
	 * Gets the Author Information for a specific author
	 */
	public AuthorInformation getAuthorInformation(final Integer authorId)
	{
		final AuthorInformation authInfo = new AuthorInformation();
		authInfo.setAuthorId(authorId);
		final RESTTagV1 tag = getTagById(authorId);
		if (tag != null && ComponentBaseRESTEntityWithPropertiesV1.returnProperty(tag, CSConstants.FIRST_NAME_PROPERTY_TAG_ID) != null && ComponentBaseRESTEntityWithPropertiesV1.returnProperty(tag, CSConstants.LAST_NAME_PROPERTY_TAG_ID) != null)
		{
			authInfo.setFirstName(ComponentBaseRESTEntityWithPropertiesV1.returnProperty(tag, CSConstants.FIRST_NAME_PROPERTY_TAG_ID).getValue());
			authInfo.setLastName(ComponentBaseRESTEntityWithPropertiesV1.returnProperty(tag, CSConstants.LAST_NAME_PROPERTY_TAG_ID).getValue());
			if (ComponentBaseRESTEntityWithPropertiesV1.returnProperty(tag, CSConstants.EMAIL_PROPERTY_TAG_ID) != null)
			{
				authInfo.setEmail(ComponentBaseRESTEntityWithPropertiesV1.returnProperty(tag, CSConstants.EMAIL_PROPERTY_TAG_ID).getValue());
			}
			if (ComponentBaseRESTEntityWithPropertiesV1.returnProperty(tag, CSConstants.ORGANIZATION_PROPERTY_TAG_ID) != null)
			{
				authInfo.setOrganization(ComponentBaseRESTEntityWithPropertiesV1.returnProperty(tag, CSConstants.ORGANIZATION_PROPERTY_TAG_ID).getValue());
			}
			if (ComponentBaseRESTEntityWithPropertiesV1.returnProperty(tag, CSConstants.ORG_DIVISION_PROPERTY_TAG_ID) != null)
			{
				authInfo.setOrgDivision(ComponentBaseRESTEntityWithPropertiesV1.returnProperty(tag, CSConstants.ORG_DIVISION_PROPERTY_TAG_ID).getValue());
			}
			return authInfo;
		}
		return null;
	}

	/*
	 * Gets a list of all content specifications in the database
	 */
	public int getNumberOfContentSpecs()
	{
		final List<RESTTopicV1> contentSpecs = getContentSpecs(0, 0);
		return contentSpecs.size();
	}
}
