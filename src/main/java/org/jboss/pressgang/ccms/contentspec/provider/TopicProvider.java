package org.jboss.pressgang.ccms.contentspec.provider;

import java.util.List;

import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TranslatedTopicWrapper;

public interface TopicProvider {
    TopicWrapper getTopic(int id);
    TopicWrapper getTopic(int id, final Integer revision);
    List<TopicWrapper> getTopics(final List<Integer> ids);
    List<TagWrapper> getTopicTags(int id, final Integer revision);
    List<TranslatedTopicWrapper> getTopicTranslations(int id, final Integer revision);
}
