package org.jboss.pressgang.ccms.contentspec.sort;

import java.util.Comparator;

import org.jboss.pressgang.ccms.contentspec.SpecTopic;

public class SpecTopicLineNumberComparator implements Comparator<SpecTopic> {

    @Override
    public int compare(final SpecTopic o1, final SpecTopic o2) {
        // Check for null values
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        final Integer line1 = o1.getLineNumber();
        final Integer line2 = o2.getLineNumber();

        if (line1 == null && line2 == null) return 0;
        if (line1 == null) return -1;
        if (line2 == null) return 1;

        return line1.compareTo(line2);
    }
}
