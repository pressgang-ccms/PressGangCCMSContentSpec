package org.jboss.pressgang.ccms.docbook.structures;

import java.util.ArrayList;
import java.util.List;

import org.jboss.pressgang.ccms.contentspec.wrapper.BaseTopicWrapper;
import org.jboss.pressgang.ccms.utils.structures.Pair;

/**
 * Provides a way to manage a collection of GenericInjectionPoint objects.
 */
public class GenericInjectionPointDatabase
{
	private List<GenericInjectionPoint> injectionPoints = new ArrayList<GenericInjectionPoint>();
	
	public GenericInjectionPoint getInjectionPoint(final Pair<Integer, String> tagDetails)
	{
		return getInjectionPoint(tagDetails.getFirst());
	}
	
	public GenericInjectionPoint getInjectionPoint(final Integer tagId)
	{
		for (final GenericInjectionPoint genericInjectionPoint : injectionPoints)
		{
			if (genericInjectionPoint.getCategoryIDAndName().getFirst().equals(tagId))
				return genericInjectionPoint;
		}
		
		return null;
	}
	
	public void addInjectionTopic(final Pair<Integer, String> tagDetails, final BaseTopicWrapper<?> topic)
	{
		GenericInjectionPoint genericInjectionPoint = getInjectionPoint(tagDetails);
		if (genericInjectionPoint == null)
		{
			genericInjectionPoint = new GenericInjectionPoint(tagDetails);
			injectionPoints.add(genericInjectionPoint);
		}
		genericInjectionPoint.addTopic(topic);
	}

	public List<GenericInjectionPoint> getInjectionPoints()
	{
		return injectionPoints;
	}

	public void setInjectionPoints(List<GenericInjectionPoint> injectionPoints)
	{
		this.injectionPoints = injectionPoints;
	}
}
