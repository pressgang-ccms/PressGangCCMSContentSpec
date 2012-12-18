package org.jboss.pressgang.ccms.contentspec.wrapper;

public interface TopicSourceURLWrapper extends EntityWrapper<TopicSourceURLWrapper> {
    String getTitle();

    void tempSetTitle(String title);

    void setTitle(String title);

    String getUrl();

    void tempSetUrl(String url);

    void setUrl(String url);

    String getDescription();

    void tempSetDescription(String description);

    void setDescription(String description);
}
