package org.jboss.pressgang.ccms.docbook.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.pressgang.ccms.utils.common.CollectionUtilities;
import org.jboss.pressgang.ccms.wrapper.base.BaseTopicWrapper;

/**
 * Provides a central location for storing and adding messages that are
 * generated while compiling to docbook.
 */
public class TopicErrorDatabase {
    public static enum ErrorLevel {ERROR, WARNING}

    ;

    public static enum ErrorType {
        NO_CONTENT, INVALID_INJECTION, INVALID_CONTENT, UNTRANSLATED,
        NOT_PUSHED_FOR_TRANSLATION, INCOMPLETE_TRANSLATION, INVALID_IMAGES, OLD_TRANSLATION, OLD_UNTRANSLATED, FUZZY_TRANSLATION
    }

    private Map<String, List<TopicErrorData>> errors = new HashMap<String, List<TopicErrorData>>();

    public int getErrorCount(final String locale) {
        return errors.containsKey(locale) ? errors.get(locale).size() : 0;
    }

    public boolean hasItems() {
        return errors.size() != 0;
    }

    public boolean hasItems(final String locale) {
        return errors.containsKey(locale) ? errors.get(locale).size() != 0 : false;
    }

    public void addError(final BaseTopicWrapper<?> topic, final ErrorType errorType, final String error) {
        addItem(topic, error, ErrorLevel.ERROR, errorType);
    }

    public void addWarning(final BaseTopicWrapper<?> topic, final ErrorType errorType, final String error) {
        addItem(topic, error, ErrorLevel.WARNING, errorType);
    }

    public void addError(final BaseTopicWrapper<?> topic, final String error) {
        addItem(topic, error, ErrorLevel.ERROR, null);
    }

    public void addWarning(final BaseTopicWrapper<?> topic, final String error) {
        addItem(topic, error, ErrorLevel.WARNING, null);
    }

    /**
     * Add a error for a topic that was included in the TOC
     *
     * @param topic
     * @param error
     */
    public void addTocError(final BaseTopicWrapper<?> topic, final ErrorType errorType, final String error) {
        addItem(topic, error, ErrorLevel.ERROR, errorType);
    }

    public void addTocWarning(final BaseTopicWrapper<?> topic, final ErrorType errorType, final String error) {
        addItem(topic, error, ErrorLevel.WARNING, errorType);
    }

    private void addItem(final BaseTopicWrapper<?> topic, final String item, final ErrorLevel errorLevel, final ErrorType errorType) {
        final TopicErrorData topicErrorData = addOrGetTopicErrorData(topic);        /* don't add duplicates */
        if (!(topicErrorData.getErrors().containsKey(errorLevel) && topicErrorData.getErrors().get(errorLevel).contains(item)))
            topicErrorData.addError(item, errorLevel, errorType);
    }

    private TopicErrorData getErrorData(final BaseTopicWrapper<?> topic) {
        for (final String locale : errors.keySet())
            for (final TopicErrorData topicErrorData : errors.get(locale)) {
                if (topicErrorData.getTopic().getTopicId().equals(topic.getTopicId())) return topicErrorData;
            }
        return null;
    }

    private TopicErrorData addOrGetTopicErrorData(final BaseTopicWrapper<?> topic) {
        TopicErrorData topicErrorData = getErrorData(topic);
        if (topicErrorData == null) {
            topicErrorData = new TopicErrorData();
            topicErrorData.setTopic(topic);
            if (!errors.containsKey(topic.getLocale())) errors.put(topic.getLocale(), new ArrayList<TopicErrorData>());
            errors.get(topic.getLocale()).add(topicErrorData);
        }
        return topicErrorData;
    }

    public List<String> getLocales() {
        return CollectionUtilities.toArrayList(errors.keySet());
    }

    public List<TopicErrorData> getErrors(final String locale) {
        return errors.containsKey(locale) ? errors.get(locale) : null;
    }

    public List<TopicErrorData> getErrorsOfType(final String locale, final ErrorType errorType) {
        final List<TopicErrorData> localeErrors = errors.containsKey(locale) ? errors.get(locale) : new ArrayList<TopicErrorData>();

        final List<TopicErrorData> typeErrorDatas = new ArrayList<TopicErrorData>();
        for (final TopicErrorData errorData : localeErrors) {
            if (errorData.hasErrorType(errorType)) typeErrorDatas.add(errorData);
        }

        return typeErrorDatas;
    }

    public void setErrors(final String locale, final List<TopicErrorData> errors) {
        this.errors.put(locale, errors);
    }
}
