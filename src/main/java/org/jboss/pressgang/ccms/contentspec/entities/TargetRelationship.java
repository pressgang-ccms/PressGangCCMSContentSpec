package org.jboss.pressgang.ccms.contentspec.entities;

import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.SpecNode;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.enums.RelationshipType;

/**
 * A class to specify a relationship between a topic and a level.
 */
public class TargetRelationship extends Relationship
{
	private final SpecTopic topic;
	private final SpecNode secondaryNode;

	public TargetRelationship(final SpecTopic topic, final Level level, final RelationshipType type)
	{
		super(topic.getId(), level.getTargetId(), type);
		this.topic = topic;
		this.secondaryNode = level;
	}
	
	public TargetRelationship(final SpecTopic topic, final Level level, final RelationshipType type, final String title)
	{
		super(topic.getId(), level.getTargetId(), type, title);
		this.topic = topic;
		this.secondaryNode = level;
	}

	public TargetRelationship(final SpecTopic topic, final SpecTopic secondaryTopic, final RelationshipType type)
	{
		super(topic.getId(), secondaryTopic.getTargetId(), type);
		this.topic = topic;
		this.secondaryNode = secondaryTopic;
	}
	
	public TargetRelationship(final SpecTopic topic, final SpecTopic secondaryTopic, final RelationshipType type, final String title)
	{
		super(topic.getId(), secondaryTopic.getTargetId(), type, title);
		this.topic = topic;
		this.secondaryNode = secondaryTopic;
	}

	public SpecTopic getTopic()
	{
		return topic;
	}

	public SpecNode getSecondaryElement()
	{
		return secondaryNode;
	}
}
