package org.jboss.pressgang.ccms.contentspec.provider;

import org.jboss.pressgang.ccms.contentspec.wrapper.TopicSourceURLWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.TopicWrapper;
import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public interface TopicSourceURLProvider {
    TopicSourceURLWrapper newTopicSourceURL(TopicWrapper topic);

    CollectionWrapper<TopicSourceURLWrapper> newTopicSourceURLCollection(TopicWrapper topic);
}
