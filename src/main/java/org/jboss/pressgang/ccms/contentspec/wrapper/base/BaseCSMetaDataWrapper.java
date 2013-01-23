package org.jboss.pressgang.ccms.contentspec.wrapper.base;

public interface BaseCSMetaDataWrapper<T extends BaseCSMetaDataWrapper<T>> extends EntityWrapper<T> {
    String getTitle();

    void setTitle(String title);
}
