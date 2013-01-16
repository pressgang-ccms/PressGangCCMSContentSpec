package org.jboss.pressgang.ccms.contentspec.wrapper;

public interface TopicSourceURLWrapper extends EntityWrapper<TopicSourceURLWrapper> {
    String getTitle();

    void setTitle(String title);

    String getUrl();

    void setUrl(String url);

    String getDescription();

    void setDescription(String description);
}
