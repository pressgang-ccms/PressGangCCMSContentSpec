package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public abstract interface EntityWrapper<T extends EntityWrapper<T>> {
    Integer getId();

    void setId(Integer id);

    Integer getRevision();

    void setRevision(Integer revision);

    CollectionWrapper<T> getRevisions();

    /**
     * Get the underlying Entity instance.
     *
     * @return
     */
    Object unwrap();

    T clone(boolean deepCopy);

    boolean isRevisionEntity();
}
