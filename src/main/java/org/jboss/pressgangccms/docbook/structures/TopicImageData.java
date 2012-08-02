package org.jboss.pressgangccms.docbook.structures;

import org.jboss.pressgangccms.rest.v1.collections.base.BaseRestCollectionV1;
import org.jboss.pressgangccms.rest.v1.entities.base.RESTBaseTopicV1;

/**
 * This class is used to map an image referenced inside a topic to the topic
 * itself. This is mostly for error reporting purposes.
 */
public class TopicImageData<T extends RESTBaseTopicV1<T, U>, U extends BaseRestCollectionV1<T, U>>
{
	private T topic = null;
	private String imageName = null;
	private Integer revision = null;

	public T getTopic()
	{
		return topic;
	}

	public void setTopic(T topic)
	{
		this.topic = topic;
	}

	public String getImageName()
	{
		return imageName;
	}

	public void setImageName(final String imageName)
	{
		this.imageName = imageName;
	}

	public Integer getRevision()
	{
		return revision;
	}

	public void setRevision(final Integer revision)
	{
		this.revision = revision;
	}

	public TopicImageData(final T topic, final String imageName)
	{
		this.topic = topic;
		this.imageName = imageName;
	}

	public TopicImageData(final T topic, final String imageName, final Integer revision)
	{
		this.topic = topic;
		this.imageName = imageName;
		this.revision = revision;
	}
}
