package org.jboss.pressgang.ccms.contentspec.utils;

import java.util.List;

import org.jboss.pressgang.ccms.contentspec.provider.DataProviderFactory;
import org.jboss.pressgang.ccms.contentspec.provider.TopicProvider;
import org.jboss.pressgang.ccms.contentspec.provider.TranslatedTopicProvider;
import org.jboss.pressgang.ccms.contentspec.wrapper.TranslatedTopicStringWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TranslatedTopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public class EntityUtilities {

    /**
     * Gets a translated topic based on a topic id, revision and locale. The translated topic that is returned will be less then
     * or equal to the revision that is passed. If the revision is null then the latest translated topic will be passed.
     * 
     * @param id The TopicID to find the translation for.
     * @param rev The Topic Revision to find the translation for.
     * @param locale The locale of the translation to find.
     * @param expand If the content of the translated topic should be expanded.
     * @return The closest matching translated topic otherwise null if none exist.
     */
    public static TranslatedTopicWrapper getClosestTranslatedTopicByTopicId(final Integer id, final Integer rev, final String locale,
            final boolean expand) {
        if (locale == null) {
            return null;
        }

        final CollectionWrapper<TranslatedTopicWrapper> translatedTopics = DataProviderFactory.getInstance(TopicProvider.class).getTopic(id, rev).getTranslatedTopics();

        TranslatedTopicWrapper closestTranslation = null;
        if (translatedTopics != null && translatedTopics.getItems() != null) {
            final List<TranslatedTopicWrapper> entities = translatedTopics.getItems();
            for (final TranslatedTopicWrapper translatedTopic : entities) {
                if (translatedTopic.getLocale().equals(locale)
                /* Ensure that the translation is the newest translation possible */
                && (closestTranslation == null || closestTranslation.getRevision() < translatedTopic.getRevision())
                /* Ensure that the translation revision is less than or equal to the revision specified */
                && (rev == null || translatedTopic.getRevision() <= rev)) {

                    closestTranslation = translatedTopic;
                }
            }
        }

        if (!expand) {
            return closestTranslation;
        } else if (closestTranslation != null) {
            return DataProviderFactory.getInstance(TranslatedTopicProvider.class).getTranslatedTopic(closestTranslation.getId(), null);
        } else {
            return null;
        }
    }
    
    /**
     * Gets a translated topic based on a topic id, revision and locale.
     */
    public static CollectionWrapper<TranslatedTopicStringWrapper> getTranslatedTopicStringsByTopicId(final Integer id, final Integer rev,
            final String locale) {
        final TranslatedTopicWrapper translatedTopic = getClosestTranslatedTopicByTopicId(id, rev, locale, false);

        if (translatedTopic == null)
            return null;

        return translatedTopic.getTranslatedStrings();
    }
    
    /**
     * Gets a translated topic based on a topic id, revision and locale.
     */
    public static TranslatedTopicWrapper getTranslatedTopicByTopicId(final Integer id, final Integer rev, final String locale) {
        if (locale == null)
            return null;
        final CollectionWrapper<TranslatedTopicWrapper> translatedTopics = DataProviderFactory.getInstance(TopicProvider.class).getTopicTranslations(id, rev);

        if (translatedTopics != null) {
            final List<TranslatedTopicWrapper> translatedTopicItems = translatedTopics.getItems();
            for (final TranslatedTopicWrapper translatedTopic : translatedTopicItems) {
                if (rev != null && translatedTopic.getRevision().equals(rev) && translatedTopic.getLocale().equals(locale)) {
                    return translatedTopic;
                } else if (rev == null) {
                    return translatedTopic;
                }
            }
        }

        return null;
    }
}
