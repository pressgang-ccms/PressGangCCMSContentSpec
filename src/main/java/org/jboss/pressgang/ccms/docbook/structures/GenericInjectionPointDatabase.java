package org.jboss.pressgang.ccms.docbook.structures;

import java.util.ArrayList;
import java.util.List;

import org.jboss.pressgang.ccms.rest.v1.entities.base.RESTBaseTopicV1;
import org.jboss.pressgang.ccms.utils.structures.Pair;

/**
 * Provides a way to manage a collection of GenericInjectionPoint objects.
 */
public class GenericInjectionPointDatabase<T extends RESTBaseTopicV1<T, ?, ?>> {
    private List<GenericInjectionPoint<T>> injectionPoints = new ArrayList<GenericInjectionPoint<T>>();

    public GenericInjectionPoint<T> getInjectionPoint(final Pair<Integer, String> tagDetails) {
        return getInjectionPoint(tagDetails.getFirst());
    }

    public GenericInjectionPoint<T> getInjectionPoint(final Integer tagId) {
        for (final GenericInjectionPoint<T> genericInjectionPoint : injectionPoints) {
            if (genericInjectionPoint.getCategoryIDAndName().getFirst().equals(tagId)) return genericInjectionPoint;
        }

        return null;
    }

    public void addInjectionTopic(final Pair<Integer, String> tagDetails, final T topic) {
        GenericInjectionPoint<T> genericInjectionPoint = getInjectionPoint(tagDetails);
        if (genericInjectionPoint == null) {
            genericInjectionPoint = new GenericInjectionPoint<T>(tagDetails);
            injectionPoints.add(genericInjectionPoint);
        }
        genericInjectionPoint.addTopic(topic);
    }

    public List<GenericInjectionPoint<T>> getInjectionPoints() {
        return injectionPoints;
    }

    public void setInjectionPoints(List<GenericInjectionPoint<T>> injectionPoints) {
        this.injectionPoints = injectionPoints;
    }
}
