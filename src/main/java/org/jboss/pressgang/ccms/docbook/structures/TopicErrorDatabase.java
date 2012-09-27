package org.jboss.pressgang.ccms.docbook.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.pressgang.ccms.rest.v1.collections.base.RESTBaseCollectionItemV1;
import org.jboss.pressgang.ccms.rest.v1.collections.base.RESTBaseCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.components.ComponentBaseTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTranslatedTopicV1;
import org.jboss.pressgang.ccms.rest.v1.entities.base.RESTBaseTopicV1;
import org.jboss.pressgang.ccms.utils.common.CollectionUtilities;

/**
 * Provides a central location for storing and adding messages that are
 * generated while compiling to docbook.
 */
public class TopicErrorDatabase<T extends RESTBaseTopicV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
{
	public static enum ErrorLevel {ERROR, WARNING};
	public static enum ErrorType {NO_CONTENT, INVALID_INJECTION, INVALID_CONTENT, UNTRANSLATED, 
		NOT_PUSHED_FOR_TRANSLATION, INCOMPLETE_TRANSLATION, INVALID_IMAGES, OLD_TRANSLATION, OLD_UNTRANSLATED, FUZZY_TRANSLATION}

	private Map<String, List<TopicErrorData<T>>> errors = new HashMap<String, List<TopicErrorData<T>>>();

	public int getErrorCount(final String locale)
	{
		return errors.containsKey(locale) ? errors.get(locale).size() : 0;
	}

	public boolean hasItems()
	{
		return errors.size() != 0;
	}
	
	public boolean hasItems(final String locale)
	{
		return errors.containsKey(locale) ? errors.get(locale).size() != 0 : false;
	}

	public void addError(final T topic, final ErrorType errorType, final String error)
	{
		addItem(topic, error, ErrorLevel.ERROR, errorType);
	}

	public void addWarning(final T topic, final ErrorType errorType, final String error)
	{
		addItem(topic, error, ErrorLevel.WARNING, errorType);
	}
	
	public void addError(final T topic, final String error)
	{
		addItem(topic, error, ErrorLevel.ERROR, null);
	}

	public void addWarning(final T topic, final String error)
	{
		addItem(topic, error, ErrorLevel.WARNING, null);
	}
	
	/**
	 * Add a error for a topic that was included in the TOC
	 * @param topic
	 * @param error
	 */
	public void addTocError(final T topic, final ErrorType errorType, final String error)
	{
		addItem(topic, error, ErrorLevel.ERROR, errorType);
	}

	public void addTocWarning(final T topic, final ErrorType errorType, final String error)
	{
		addItem(topic, error, ErrorLevel.WARNING, errorType);
	}

	private void addItem(final T topic, final String item, final ErrorLevel errorLevel, final ErrorType errorType)
	{
		final TopicErrorData<T> topicErrorData = addOrGetTopicErrorData(topic);
		/* don't add duplicates */
		if (!(topicErrorData.getErrors().containsKey(errorLevel) && topicErrorData.getErrors().get(errorLevel).contains(item)))
			topicErrorData.addError(item, errorLevel, errorType);
	}

	private TopicErrorData<T> getErrorData(final T topic)
	{
		for (final String locale : errors.keySet())
			for (final TopicErrorData<T> topicErrorData : errors.get(locale))
			{
				if (ComponentBaseTopicV1.<T, U, V>returnIsDummyTopic(topic))
				{
					if (topic.getClass() == RESTTranslatedTopicV1.class && topicErrorData.getTopic() instanceof RESTTranslatedTopicV1)
					{
						if (((RESTTranslatedTopicV1) topicErrorData.getTopic()).getTopicId().equals(((RESTTranslatedTopicV1) topic).getTopicId()))
							return topicErrorData;
					}
				}
				else
				{
					if (topicErrorData.getTopic().getId().equals(topic.getId()))
						return topicErrorData;
				}
			}
		return null;
	}

	private TopicErrorData<T> addOrGetTopicErrorData(final T topic)
	{
		TopicErrorData<T> topicErrorData = getErrorData(topic);
		if (topicErrorData == null)
		{
			topicErrorData = new TopicErrorData<T>();
			topicErrorData.setTopic(topic);
			if (!errors.containsKey(topic.getLocale()))
				errors.put(topic.getLocale(), new ArrayList<TopicErrorData<T>>());
			errors.get(topic.getLocale()).add(topicErrorData);
		}
		return topicErrorData;
	}
	
	public List<String> getLocales()
	{
		return CollectionUtilities.toArrayList(errors.keySet());
	}

	public List<TopicErrorData<T>> getErrors(final String locale)
	{
		return errors.containsKey(locale) ? errors.get(locale) : null;
	}
	
	public List<TopicErrorData<T>> getErrorsOfType(final String locale, final ErrorType errorType)
	{
		final List<TopicErrorData<T>> localeErrors = errors.containsKey(locale) ? errors.get(locale) : new ArrayList<TopicErrorData<T>>();
		
		final List<TopicErrorData<T>> typeErrorDatas = new ArrayList<TopicErrorData<T>>();
		for (final TopicErrorData<T> errorData : localeErrors)
		{
			if (errorData.hasErrorType(errorType))
				typeErrorDatas.add(errorData);
		}
		
		return typeErrorDatas;
	}

	public void setErrors(final String locale, final List<TopicErrorData<T>> errors)
	{
		this.errors.put(locale, errors);
	}
}
