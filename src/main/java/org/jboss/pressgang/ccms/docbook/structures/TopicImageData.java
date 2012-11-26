package org.jboss.pressgang.ccms.docbook.structures;

import org.jboss.pressgang.ccms.contentspec.wrapper.TopicWrapper;

/**
 * This class is used to map an image referenced inside a topic to the topic
 * itself. This is mostly for error reporting purposes.
 */
public class TopicImageData
{
	private TopicWrapper topic = null;
	private String imageName = null;
	private Integer revision = null;

	public TopicWrapper getTopic()
	{
		return topic;
	}

	public void setTopic(final TopicWrapper topic)
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

	public TopicImageData(final TopicWrapper topic, final String imageName)
	{
		this.topic = topic;
		this.imageName = imageName;
	}

	public TopicImageData(final TopicWrapper topic, final String imageName, final Integer revision)
	{
		this.topic = topic;
		this.imageName = imageName;
		this.revision = revision;
	}
}
