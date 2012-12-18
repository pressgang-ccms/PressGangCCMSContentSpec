package org.jboss.pressgang.ccms.docbook.sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jboss.pressgang.ccms.contentspec.wrapper.BaseTopicWrapper;
import org.jboss.pressgang.ccms.docbook.structures.InjectionTopicData;
import org.jboss.pressgang.ccms.utils.sort.ExternalListSort;

public class TopicTitleSorter implements ExternalListSort<Integer, BaseTopicWrapper<?>, InjectionTopicData> {
    public void sort(final List<BaseTopicWrapper<?>> topics, final List<InjectionTopicData> list) {
        if (topics == null || list == null) return;

        Collections.sort(list, new Comparator<InjectionTopicData>() {
            public int compare(final InjectionTopicData o1, final InjectionTopicData o2) {
                BaseTopicWrapper<?> topic1 = null;
                BaseTopicWrapper<?> topic2 = null;

                for (final BaseTopicWrapper<?> topic : topics) {
                    if (topic.getTopicId().equals(o1.topicId)) topic1 = topic;
                    if (topic.getTopicId().equals(o2.topicId)) topic2 = topic;

                    if (topic1 != null && topic2 != null) break;
                }

                final boolean v1Exists = topic1 != null;
                final boolean v2Exists = topic2 != null;

                if (!v1Exists && !v2Exists) return 0;
                if (!v1Exists) return -1;
                if (!v2Exists) return 1;

                final BaseTopicWrapper<?> v1 = topic1;
                final BaseTopicWrapper<?> v2 = topic2;

                if (v1.getTitle() == null && v2.getTitle() == null) return 0;

                if (v1.getTitle() == null) return -1;

                if (v2.getTitle() == null) return 1;

                return v1.getTitle().toLowerCase().compareTo(v2.getTitle().toLowerCase());
            }
        });
    }
}

