package org.jboss.pressgang.ccms.contentspec.sort;

import java.util.Comparator;

import org.jboss.pressgang.ccms.wrapper.base.EntityWrapper;

public class EntityWrapperIDComparator implements Comparator<EntityWrapper> {
    public int compare(final EntityWrapper o1, final EntityWrapper o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        if (o1.getId() == null && o2.getId() == null) return 0;
        if (o1.getId() == null) return -1;
        if (o2.getId() == null) return 1;

        return o1.getId().compareTo(o2.getId());
    }

}