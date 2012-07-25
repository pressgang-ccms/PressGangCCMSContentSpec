package org.jboss.pressgangccms.docbook.structures;

import java.util.ArrayList;
import java.util.List;

import org.jboss.pressgangccms.rest.v1.collections.base.BaseRestCollectionV1;
import org.jboss.pressgangccms.rest.v1.entities.base.RESTBaseTopicV1;
import org.jboss.pressgangccms.utils.structures.Pair;

/**
 * Provides a way to manage a collection of GenericInjectionPoint objects.
 */
public class GenericInjectionPointDatabase<T extends RESTBaseTopicV1<T, U>, U extends BaseRestCollectionV1<T, U>>
{
	private List<GenericInjectionPoint<T, U>> injectionPoints = new ArrayList<GenericInjectionPoint<T, U>>();
	
	public GenericInjectionPoint<T, U> getInjectionPoint(final Pair<Integer, String> tagDetails)
	{
		return getInjectionPoint(tagDetails.getFirst());
	}
	
	public GenericInjectionPoint<T, U> getInjectionPoint(final Integer tagId)
	{
		for (final GenericInjectionPoint<T, U> genericInjectionPoint : injectionPoints)
		{
			if (genericInjectionPoint.getCategoryIDAndName().getFirst().equals(tagId))
				return genericInjectionPoint;
		}
		
		return null;
	}
	
	public void addInjectionTopic(final Pair<Integer, String> tagDetails, final T topic)
	{
		GenericInjectionPoint<T, U> genericInjectionPoint = getInjectionPoint(tagDetails);
		if (genericInjectionPoint == null)
		{
			genericInjectionPoint = new GenericInjectionPoint<T, U>(tagDetails);
			injectionPoints.add(genericInjectionPoint);
		}
		genericInjectionPoint.addTopic(topic);
	}

	public List<GenericInjectionPoint<T, U>> getInjectionPoints()
	{
		return injectionPoints;
	}

	public void setInjectionPoints(List<GenericInjectionPoint<T, U>> injectionPoints)
	{
		this.injectionPoints = injectionPoints;
	}
}
