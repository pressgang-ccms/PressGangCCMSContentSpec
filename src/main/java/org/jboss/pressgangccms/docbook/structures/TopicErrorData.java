package org.jboss.pressgangccms.docbook.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.pressgangccms.docbook.structures.TopicErrorDatabase.ErrorLevel;
import org.jboss.pressgangccms.docbook.structures.TopicErrorDatabase.ErrorType;
import org.jboss.pressgangccms.rest.v1.collections.base.BaseRestCollectionV1;
import org.jboss.pressgangccms.rest.v1.entities.base.RESTBaseTopicV1;

/**
 * Stores information on the errors and warnings that were detected in a topic.
 */
public class TopicErrorData<T extends RESTBaseTopicV1<T, U>, U extends BaseRestCollectionV1<T, U>>
{	
	private T topic;
	private Map<ErrorLevel, Set<String>> errors = new HashMap<ErrorLevel, Set<String>>();
	private List<ErrorType> errorTypes = new ArrayList<ErrorType>();

	public T getTopic()
	{
		return topic;
	}

	public void setTopic(T topic)
	{
		this.topic = topic;
	}

	public Map<ErrorLevel, Set<String>> getErrors()
	{
		return errors;
	}

	public void setErrors(final Map<ErrorLevel, Set<String>> errors)
	{
		this.errors = errors;
	}
	
	public void addError(final String item, final ErrorLevel level, final ErrorType errorType)
	{
		if (!errors.containsKey(level))
			errors.put(level, new HashSet<String>());
		errors.get(level).add(item);
		
		if (errorType != null)
		{
		if (!errorTypes.contains(errorType))
			errorTypes.add(errorType);
		}
	}
	
	public boolean hasItemsOfType(final ErrorLevel level)
	{
		return errors.containsKey(level);
	}
	
	public Set<String> getItemsOfType(final ErrorLevel level)
	{
		if (hasItemsOfType(level))
			return errors.get(level);
		return new HashSet<String>();
	}
	
	public boolean hasErrorType(final ErrorType errorType)
	{
		return errorTypes.contains(errorType);
	}
}
