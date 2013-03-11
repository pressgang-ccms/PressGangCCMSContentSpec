package org.jboss.pressgang.ccms.contentspec.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;
import org.jboss.pressgang.ccms.contentspec.entities.AuthorInformation;
import org.jboss.pressgang.ccms.contentspec.entities.Revision;
import org.jboss.pressgang.ccms.contentspec.entities.RevisionList;
import org.jboss.pressgang.ccms.contentspec.sort.EnversRevisionSort;
import org.jboss.pressgang.ccms.contentspec.sort.TagWrapperNameComparator;
import org.jboss.pressgang.ccms.provider.DataProviderFactory;
import org.jboss.pressgang.ccms.provider.TagProvider;
import org.jboss.pressgang.ccms.provider.TopicProvider;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.utils.structures.NameIDSortMap;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.CategoryInTagWrapper;
import org.jboss.pressgang.ccms.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.wrapper.TopicWrapper;
import org.jboss.pressgang.ccms.wrapper.TranslatedTopicStringWrapper;
import org.jboss.pressgang.ccms.wrapper.TranslatedTopicWrapper;
import org.jboss.pressgang.ccms.wrapper.base.BaseTopicWrapper;
import org.jboss.pressgang.ccms.wrapper.collection.CollectionWrapper;

public class EntityUtilities {

    /**
     * Gets a translated topic based on a topic id, revision and locale. The translated topic that is returned will be less then
     * or equal to the revision that is passed. If the revision is null then the latest translated topic will be passed.
     *
     * @param providerFactory
     * @param id              The TopicID to find the translation for.
     * @param rev             The Topic Revision to find the translation for.
     * @param locale          The locale of the translation to find.
     * @return The closest matching translated topic otherwise null if none exist.
     */
    public static TranslatedTopicWrapper getClosestTranslatedTopicByTopicId(final DataProviderFactory providerFactory, final Integer id,
            final Integer rev, final String locale) {
        if (locale == null) {
            return null;
        }

        final CollectionWrapper<TranslatedTopicWrapper> translatedTopics = providerFactory.getProvider(TopicProvider.class).getTopic(id,
                rev).getTranslatedTopics();

        TranslatedTopicWrapper closestTranslation = null;
        if (translatedTopics != null && translatedTopics.getItems() != null) {
            final List<TranslatedTopicWrapper> entities = translatedTopics.getItems();
            for (final TranslatedTopicWrapper translatedTopic : entities) {
                if (translatedTopic.getLocale().equals(locale)
                /* Ensure that the translation is the newest translation possible */ && (closestTranslation == null || closestTranslation
                        .getTopicRevision() < translatedTopic.getTopicRevision())
                /* Ensure that the translation revision is less than or equal to the revision specified */ && (rev == null ||
                        translatedTopic.getTopicRevision() <= rev)) {

                    closestTranslation = translatedTopic;
                }
            }
        }

        return closestTranslation;
    }

    /**
     * Gets a translated topic based on a topic id, revision and locale.
     */
    public static CollectionWrapper<TranslatedTopicStringWrapper> getTranslatedTopicStringsByTopicId(
            final DataProviderFactory providerFactory, final Integer id, final Integer rev, final String locale) {
        final TranslatedTopicWrapper translatedTopic = getClosestTranslatedTopicByTopicId(providerFactory, id, rev, locale);

        if (translatedTopic == null) return null;

        return translatedTopic.getTranslatedStrings();
    }

    /**
     * Gets a translated topic based on a topic id, revision and locale.
     */
    public static TranslatedTopicWrapper getTranslatedTopicByTopicId(final DataProviderFactory providerFactory, final Integer id,
            final Integer rev, final String locale) {
        if (locale == null) return null;
        final CollectionWrapper<TranslatedTopicWrapper> translatedTopics = providerFactory.getProvider(
                TopicProvider.class).getTopicTranslations(id, rev);

        if (translatedTopics != null) {
            final List<TranslatedTopicWrapper> translatedTopicItems = translatedTopics.getItems();
            for (final TranslatedTopicWrapper translatedTopic : translatedTopicItems) {
                if (rev != null && translatedTopic.getTopicRevision().equals(rev) && translatedTopic.getLocale().equals(locale)) {
                    return translatedTopic;
                } else if (rev == null) {
                    return translatedTopic;
                }
            }
        }

        return null;
    }

    public static TreeMap<NameIDSortMap, ArrayList<TagWrapper>> getCategoriesMappedToTags(final BaseTopicWrapper<?> source) {
        final TreeMap<NameIDSortMap, ArrayList<TagWrapper>> tags = new TreeMap<NameIDSortMap, ArrayList<TagWrapper>>();

        if (source.getTags() != null && source.getTags().getItems() != null) {
            final List<TagWrapper> tagItems = source.getTags().getItems();
            for (final TagWrapper tag : tagItems) {
                if (tag.getCategories() != null && tag.getCategories().getItems() != null) {
                    final List<CategoryInTagWrapper> categories = tag.getCategories().getItems();

                    if (categories.size() == 0) {
                        final NameIDSortMap categoryDetails = new NameIDSortMap("Uncategorised", -1, 0);

                        if (!tags.containsKey(categoryDetails)) tags.put(categoryDetails, new ArrayList<TagWrapper>());

                        tags.get(categoryDetails).add(tag);
                    } else {
                        for (final CategoryInTagWrapper category : categories) {
                            final NameIDSortMap categoryDetails = new NameIDSortMap(category.getName(), category.getId(),
                                    category.getRelationshipSort() == null ? 0 : category.getRelationshipSort());

                            if (!tags.containsKey(categoryDetails)) tags.put(categoryDetails, new ArrayList<TagWrapper>());

                            tags.get(categoryDetails).add(tag);
                        }
                    }
                }
            }
        }

        return tags;
    }

    public static String getCommaSeparatedTagList(final BaseTopicWrapper<?> source) {
        final TreeMap<NameIDSortMap, ArrayList<TagWrapper>> tags = getCategoriesMappedToTags(source);

        String tagsList = "";
        for (final NameIDSortMap key : tags.keySet()) {
            // sort alphabetically
            Collections.sort(tags.get(key), new TagWrapperNameComparator());

            if (tagsList.length() != 0) tagsList += " ";

            tagsList += key.getName() + ": ";

            String thisTagList = "";

            for (final TagWrapper tag : tags.get(key)) {
                if (thisTagList.length() != 0) thisTagList += ", ";

                thisTagList += tag.getName();
            }

            tagsList += thisTagList + " ";
        }

        return tagsList;
    }

    public static boolean isDummyTopic(final BaseTopicWrapper<?> source) {
        return source.getId() == null || source.getId() < 0;
    }

    /*
     * Gets the Author Information for a specific author
     */
    public static AuthorInformation getAuthorInformation(final DataProviderFactory providerFactory, final Integer authorId) {
        final AuthorInformation authInfo = new AuthorInformation();
        authInfo.setAuthorId(authorId);
        final TagWrapper tag = providerFactory.getProvider(TagProvider.class).getTag(authorId);
        if (tag != null && tag.getProperty(CSConstants.FIRST_NAME_PROPERTY_TAG_ID) != null && tag.getProperty(
                CSConstants.LAST_NAME_PROPERTY_TAG_ID) != null) {
            authInfo.setFirstName(tag.getProperty(CSConstants.FIRST_NAME_PROPERTY_TAG_ID).getValue());
            authInfo.setLastName(tag.getProperty(CSConstants.LAST_NAME_PROPERTY_TAG_ID).getValue());
            if (tag.getProperty(CSConstants.EMAIL_PROPERTY_TAG_ID) != null) {
                authInfo.setEmail(tag.getProperty(CSConstants.EMAIL_PROPERTY_TAG_ID).getValue());
            }
            if (tag.getProperty(CSConstants.ORGANIZATION_PROPERTY_TAG_ID) != null) {
                authInfo.setOrganization(tag.getProperty(CSConstants.ORGANIZATION_PROPERTY_TAG_ID).getValue());
            }
            if (tag.getProperty(CSConstants.ORG_DIVISION_PROPERTY_TAG_ID) != null) {
                authInfo.setOrgDivision(tag.getProperty(CSConstants.ORG_DIVISION_PROPERTY_TAG_ID).getValue());
            }
            return authInfo;
        }
        return null;
    }

    public static TranslatedTopicWrapper returnPushedTranslatedTopic(final TranslatedTopicWrapper source) {
        if (!isDummyTopic(source)) return source;

        /* Check that a translation exists that is the same locale as the base topic */
        TranslatedTopicWrapper pushedTranslatedTopic = null;
        if (source.getTopic().getTranslatedTopics() != null && source.getTopic().getTranslatedTopics().getItems() != null) {
            final Integer topicRev = source.getTopicRevision();
            final List<TranslatedTopicWrapper> topics = source.getTopic().getTranslatedTopics().getItems();
            for (final TranslatedTopicWrapper translatedTopic : topics) {
                if (translatedTopic.getLocale().equals(source.getTopic().getLocale()) &&
                        // Ensure that the topic revision is less than or equal to the source revision
                        (topicRev == null || translatedTopic.getTopicRevision() <= topicRev) &&
                        // Check if this translated topic is a higher revision then the current stored translation
                        (pushedTranslatedTopic == null || pushedTranslatedTopic.getTopicRevision() < translatedTopic.getTopicRevision()))
                    pushedTranslatedTopic = translatedTopic;
            }
        }

        return pushedTranslatedTopic;
    }

    public static boolean hasBeenPushedForTranslation(final TranslatedTopicWrapper source) {
        if (!isDummyTopic(source)) return true;

        /* Check that a translation exists that is the same locale as the base topic */
        boolean baseTranslationExists = false;
        if (source.getTopic().getTranslatedTopics() != null && source.getTopic().getTranslatedTopics().getItems() != null) {
            final List<TranslatedTopicWrapper> topics = source.getTopic().getTranslatedTopics().getItems();
            for (final TranslatedTopicWrapper translatedTopic : topics) {
                if (translatedTopic.getLocale().equals(source.getTopic().getLocale())) baseTranslationExists = true;
            }
        }

        return baseTranslationExists;
    }

    /*
     * Gets a list of Revision's from the CSProcessor database for a specific content spec
     */
    public static RevisionList getTopicRevisionsById(final TopicProvider topicProvider, final Integer csId) {
        final List<Revision> results = new ArrayList<Revision>();
        final CollectionWrapper<TopicWrapper> topicRevisions = topicProvider.getTopic(csId).getRevisions();

        // Create the unique array from the revisions
        if (topicRevisions != null && topicRevisions.getItems() != null) {
            final List<TopicWrapper> topicRevs = topicRevisions.getItems();
            for (final TopicWrapper topicRev : topicRevs) {
                Revision revision = new Revision();
                revision.setRevision(topicRev.getRevision());
                revision.setDate(topicRev.getLastModified());
                results.add(revision);
            }

            Collections.sort(results, new EnversRevisionSort());

            return new RevisionList(csId, "Topic", results);
        } else {
            return null;
        }
    }

    /**
     * Converts a list of tags into a mapping of categories to tags. The key is the Category and the value is a List
     * of Tags for that category.
     *
     * @param tags The List of tags to be converted.
     * @return The mapping of Categories to Tags.
     */
    public static Map<CategoryInTagWrapper, List<TagWrapper>> getCategoryMappingFromTagList(final List<TagWrapper> tags) {
        final HashMap<CategoryInTagWrapper, List<TagWrapper>> mapping = new HashMap<CategoryInTagWrapper, List<TagWrapper>>();
        for (final TagWrapper tag : tags) {
            final List<CategoryInTagWrapper> catList = tag.getCategories().getItems();
            if (catList != null) {
                for (final CategoryInTagWrapper cat : catList) {
                    if (!mapping.containsKey(cat)) mapping.put(cat, new ArrayList<TagWrapper>());
                    mapping.get(cat).add(tag);
                }
            }
        }
        return mapping;
    }

    /**
     * Gets the CSNode Topic entity that is represented by the node.
     *
     * @param node          The node that represents a topic entry.
     * @param topicProvider The topic provider to lookup the topic entity from.
     * @return The topic entity represented by the node, or null if there isn't one that matches.
     */
    public static TopicWrapper getCSNodeTopicEntity(final CSNodeWrapper node, final TopicProvider topicProvider) {
        if (node.getNodeType() != CommonConstants.CS_NODE_TOPIC) return null;

        return topicProvider.getTopic(node.getEntityId(), node.getEntityRevision());
    }
}
