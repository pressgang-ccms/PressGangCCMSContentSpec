package org.jboss.pressgang.ccms.contentspec.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jboss.pressgang.ccms.contentspec.entities.AuthorInformation;
import org.jboss.pressgang.ccms.contentspec.entities.Revision;
import org.jboss.pressgang.ccms.contentspec.entities.RevisionList;
import org.jboss.pressgang.ccms.contentspec.sort.EnversRevisionSort;
import org.jboss.pressgang.ccms.contentspec.sort.TagWrapperNameComparator;
import org.jboss.pressgang.ccms.provider.ContentSpecProvider;
import org.jboss.pressgang.ccms.provider.DataProviderFactory;
import org.jboss.pressgang.ccms.provider.TagProvider;
import org.jboss.pressgang.ccms.provider.TopicProvider;
import org.jboss.pressgang.ccms.provider.exception.NotFoundException;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.utils.structures.NameIDSortMap;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.CategoryInTagWrapper;
import org.jboss.pressgang.ccms.wrapper.ContentSpecWrapper;
import org.jboss.pressgang.ccms.wrapper.ServerEntitiesWrapper;
import org.jboss.pressgang.ccms.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.wrapper.TopicWrapper;
import org.jboss.pressgang.ccms.wrapper.TranslatedCSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.TranslatedContentSpecWrapper;
import org.jboss.pressgang.ccms.wrapper.TranslatedTopicWrapper;
import org.jboss.pressgang.ccms.wrapper.base.BaseCSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.base.BaseTopicWrapper;
import org.jboss.pressgang.ccms.wrapper.collection.CollectionWrapper;

public class EntityUtilities {

    /**
     * Gets a translated topic based on a topic id, revision and locale.
     */
    public static TranslatedTopicWrapper getTranslatedTopicByTopicId(final DataProviderFactory providerFactory, final Integer id,
            final Integer rev, final String locale) {
        return getTranslatedTopicByTopicAndNodeId(providerFactory, id, rev, null, locale);
    }

    /**
     * Gets a translated topic based on a topic id, revision and locale.
     */
    public static TranslatedTopicWrapper getTranslatedTopicByTopicAndNodeId(final DataProviderFactory providerFactory, final Integer id,
            final Integer rev, final Integer translatedCSNodeId, final String locale) {
        if (locale == null) return null;
        final CollectionWrapper<TranslatedTopicWrapper> translatedTopics = providerFactory.getProvider(
                TopicProvider.class).getTopicTranslations(id, rev);

        if (translatedTopics != null) {
            final List<TranslatedTopicWrapper> translatedTopicItems = translatedTopics.getItems();
            for (final TranslatedTopicWrapper translatedTopic : translatedTopicItems) {
                // Make sure the locale and topic revision matches
                if (translatedTopic.getLocale().equals(locale) && translatedTopic.getTopicRevision().equals(rev)) {
                    // Make sure the translated cs node id matches
                    if ((translatedCSNodeId == null && translatedTopic.getTranslatedCSNode() == null) || (translatedTopic
                            .getTranslatedCSNode() != null && translatedTopic.getTranslatedCSNode().getId().equals(
                            translatedCSNodeId))) {
                        return translatedTopic;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Gets a translated content spec based on a content spec id and revision
     */
    public static TranslatedContentSpecWrapper getTranslatedContentSpecById(final DataProviderFactory providerFactory, final Integer id,
            final Integer rev) {
        final CollectionWrapper<TranslatedContentSpecWrapper> translatedContentSpecs = providerFactory.getProvider(
                ContentSpecProvider.class).getContentSpecTranslations(id, rev);

        if (translatedContentSpecs != null) {
            final List<TranslatedContentSpecWrapper> translatedContentSpecItems = translatedContentSpecs.getItems();
            for (final TranslatedContentSpecWrapper translatedContentSpec : translatedContentSpecItems) {
                if (rev != null && translatedContentSpec.getContentSpecRevision().equals(rev)) {
                    return translatedContentSpec;
                } else if (rev == null) {
                    return translatedContentSpec;
                }
            }
        }

        return null;
    }

    /**
     * Gets a translated content spec based on a id and revision. The translated content spec that is returned will be less then
     * or equal to the revision that is passed. If the revision is null then the latest translated content spec will be returned.
     *
     * @param providerFactory
     * @param id              The Content Spec ID to find the translation for.
     * @param rev             The Content Spec Revision to find the translation for.
     * @return The closest matching translated content spec otherwise null if none exist.
     */
    public static TranslatedContentSpecWrapper getClosestTranslatedContentSpecById(final DataProviderFactory providerFactory,
            final Integer id, final Integer rev) {

        final CollectionWrapper<TranslatedContentSpecWrapper> translatedContentSpecs = providerFactory.getProvider(ContentSpecProvider
                .class).getContentSpec(id, rev).getTranslatedContentSpecs();

        TranslatedContentSpecWrapper closestTranslation = null;
        if (translatedContentSpecs != null && translatedContentSpecs.getItems() != null) {
            final List<TranslatedContentSpecWrapper> entities = translatedContentSpecs.getItems();
            for (final TranslatedContentSpecWrapper translatedContentSpec : entities) {
                if (
                    // Ensure that the translation is the newest translation possible
                        (closestTranslation == null || closestTranslation.getContentSpecRevision() < translatedContentSpec
                                .getContentSpecRevision())
                                // Ensure that the translation revision is less than or equal to the revision specified
                                && (rev == null || translatedContentSpec.getContentSpecRevision() <= rev)) {
                    closestTranslation = translatedContentSpec;
                }
            }
        }

        return closestTranslation;
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
    public static AuthorInformation getAuthorInformation(final DataProviderFactory providerFactory,
            final ServerEntitiesWrapper serverEntities, final Integer authorId, final Integer revision) {
        final AuthorInformation authInfo = new AuthorInformation();
        authInfo.setAuthorId(authorId);
        TagWrapper tag = null;
        try {
            tag = providerFactory.getProvider(TagProvider.class).getTag(authorId);
        } catch (NotFoundException e) {
            tag = providerFactory.getProvider(TagProvider.class).getTag(authorId, revision);
        }
        if (tag != null && tag.getProperty(serverEntities.getFirstNamePropertyTagId()) != null && tag.getProperty(
                serverEntities.getSurnamePropertyTagId()) != null) {
            authInfo.setFirstName(tag.getProperty(serverEntities.getFirstNamePropertyTagId()).getValue());
            authInfo.setLastName(tag.getProperty(serverEntities.getSurnamePropertyTagId()).getValue());
            if (tag.getProperty(serverEntities.getEmailPropertyTagId()) != null) {
                authInfo.setEmail(tag.getProperty(serverEntities.getEmailPropertyTagId()).getValue());
            }
            if (tag.getProperty(serverEntities.getOrganizationPropertyTagId()) != null) {
                authInfo.setOrganization(tag.getProperty(serverEntities.getOrganizationPropertyTagId()).getValue());
            }
            if (tag.getProperty(serverEntities.getOrganizationDivisionPropertyTagId()) != null) {
                authInfo.setOrgDivision(tag.getProperty(serverEntities.getOrganizationDivisionPropertyTagId()).getValue());
            }
            return authInfo;
        }
        return null;
    }

    public static TranslatedTopicWrapper returnPushedTranslatedTopic(final TopicWrapper source) {
        return returnPushedTranslatedTopic(source, null);
    }

    public static TranslatedTopicWrapper returnPushedTranslatedTopic(final TopicWrapper source,
            final TranslatedCSNodeWrapper translatedCSNode) {
        return returnClosestTranslatedTopic(source, translatedCSNode, source.getLocale());
    }

    public static TranslatedTopicWrapper returnClosestTranslatedTopic(final TopicWrapper source, final String locale) {
        return returnClosestTranslatedTopic(source, null, locale);
    }

    public static TranslatedTopicWrapper returnClosestTranslatedTopic(final TopicWrapper source,
            final TranslatedCSNodeWrapper translatedCSNode, final String locale) {
        // Check that a translation exists that is the same locale as the locale specified
        TranslatedTopicWrapper pushedTranslatedTopic = null;
        if (source.getTranslatedTopics() != null && source.getTranslatedTopics().getItems() != null) {
            final Integer topicRev = source.getTopicRevision();
            final List<TranslatedTopicWrapper> topics = source.getTranslatedTopics().getItems();
            for (final TranslatedTopicWrapper translatedTopic : topics) {
                // Make sure the locale and topic revision matches
                if (translatedTopic.getLocale().equals(locale)) {
                    // Ensure that the topic revision is less than or equal to the source revision
                    if ((topicRev == null || translatedTopic.getTopicRevision() <= topicRev) &&
                            // Check if this translated topic is a higher revision then the current stored translation
                            (pushedTranslatedTopic == null || pushedTranslatedTopic.getTopicRevision() < translatedTopic.getTopicRevision
                                    ())) {
                        // Make sure the translated topic csnode and translatedcsnode match
                        if ((translatedCSNode == null && translatedTopic.getTranslatedCSNode() == null) || (translatedTopic
                                .getTranslatedCSNode() != null && translatedCSNode != null && translatedTopic.getTranslatedCSNode().getId
                                ().equals(
                                translatedCSNode.getId()))) {
                            pushedTranslatedTopic = translatedTopic;
                        }
                    }
                }
            }
        }

        return pushedTranslatedTopic;
    }

    public static TranslatedTopicWrapper returnPushedTranslatedTopic(final TranslatedTopicWrapper source) {
        return returnPushedTranslatedTopic(source, null);
    }

    public static TranslatedTopicWrapper returnPushedTranslatedTopic(final TranslatedTopicWrapper source,
            final TranslatedCSNodeWrapper translatedCSNodeWrapper) {
        if (!isDummyTopic(source)) return source;

        if (source.getTopic() != null) {
            return returnPushedTranslatedTopic(source.getTopic(), translatedCSNodeWrapper);
        } else {
            return null;
        }
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
        CollectionWrapper<TopicWrapper> topicRevisions = topicProvider.getTopic(csId).getRevisions();

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
    public static Map<Integer, List<TagWrapper>> getCategoryMappingFromTagList(final Collection<TagWrapper> tags) {
        final HashMap<Integer, List<TagWrapper>> mapping = new HashMap<Integer, List<TagWrapper>>();
        for (final TagWrapper tag : tags) {
            final List<CategoryInTagWrapper> catList = tag.getCategories().getItems();
            if (catList != null) {
                for (final CategoryInTagWrapper cat : catList) {
                    if (!mapping.containsKey(cat.getId())) {
                        mapping.put(cat.getId(), new ArrayList<TagWrapper>());
                    }
                    mapping.get(cat.getId()).add(tag);
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
        if (!isNodeATopic(node)) return null;

        return topicProvider.getTopic(node.getEntityId(), node.getEntityRevision());
    }

    /**
     * Checks to see if the node is some representation of a Topic entity.
     *
     * @param node The node to be checked.
     * @return
     */
    public static boolean isNodeATopic(final BaseCSNodeWrapper<?> node) {
        switch (node.getNodeType()) {
            case CommonConstants.CS_NODE_TOPIC:
            case CommonConstants.CS_NODE_INNER_TOPIC:
            case CommonConstants.CS_NODE_META_DATA_TOPIC:
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks to see if an entity node is a level representation.
     *
     * @param node
     * @return
     */
    public static boolean isNodeALevel(final BaseCSNodeWrapper<?> node) {
        switch (node.getNodeType()) {
            case CommonConstants.CS_NODE_APPENDIX:
            case CommonConstants.CS_NODE_CHAPTER:
            case CommonConstants.CS_NODE_PART:
            case CommonConstants.CS_NODE_PREFACE:
            case CommonConstants.CS_NODE_PROCESS:
            case CommonConstants.CS_NODE_SECTION:
                return true;
            default:
                return false;
        }
    }

    /**
     * Checks to see if a Content Spec Meta Data element has changed.
     *
     * @param metaDataName      The Content Spec Meta Data name.
     * @param currentValue      The expected current value of the Meta Data node.
     * @param contentSpecEntity The Content Spec Entity to check against.
     * @return True if the meta data node has changed, otherwise false.
     */
    public static boolean hasContentSpecMetaDataChanged(final String metaDataName, final String currentValue,
            final ContentSpecWrapper contentSpecEntity) {
        final CSNodeWrapper metaData = contentSpecEntity.getMetaData(metaDataName);
        if (metaData != null && metaData.getAdditionalText() != null && !metaData.getAdditionalText().equals(currentValue)) {
            // The values no longer match
            return true;
        } else if ((metaData == null || metaData.getAdditionalText() == null) && currentValue != null) {
            // The meta data node doesn't exist but it exists now
            return true;
        } else {
            return false;
        }
    }
}
