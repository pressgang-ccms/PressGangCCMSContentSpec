package org.jboss.pressgang.ccms.contentspec.sort;

import java.util.Comparator;

import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;

public class TagWrapperNameComparator implements Comparator<TagWrapper> {
    public int compare(final TagWrapper o1, final TagWrapper o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        if (o1.getName() == null && o2.getName() == null) return 0;
        if (o1.getName() == null) return -1;
        if (o2.getName() == null) return 1;

        return o1.getName().compareTo(o2.getName());
    }

}