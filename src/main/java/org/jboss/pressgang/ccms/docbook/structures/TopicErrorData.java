package org.jboss.pressgang.ccms.docbook.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.pressgang.ccms.contentspec.wrapper.TopicWrapper;
import org.jboss.pressgang.ccms.docbook.structures.TopicErrorDatabase.ErrorLevel;
import org.jboss.pressgang.ccms.docbook.structures.TopicErrorDatabase.ErrorType;

/**
 * Stores information on the errors and warnings that were detected in a topic.
 */
public class TopicErrorData
{	
	private TopicWrapper topic;
	private Map<ErrorLevel, Set<String>> errors = new HashMap<ErrorLevel, Set<String>>();
	private List<ErrorType> errorTypes = new ArrayList<ErrorType>();

	public TopicWrapper getTopic()
	{
		return topic;
	}

	public void setTopic(final TopicWrapper topic)
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
