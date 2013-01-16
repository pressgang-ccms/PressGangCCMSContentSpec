package org.jboss.pressgang.ccms.contentspec.wrapper;

import org.jboss.pressgang.ccms.contentspec.wrapper.collection.CollectionWrapper;

public abstract interface EntityWrapper<T extends EntityWrapper<T>> {
    /**
     * Get the Unique ID for the entity.
     *
     * @return The ID value.
     */
    Integer getId();

    /**
     * Set the ID for the Entity.
     *
     * @param id The unique id value.
     */
    void setId(Integer id);

    /**
     * Get the revision of the entity.
     *
     * @return The revision number for the entity.
     */
    Integer getRevision();

    /**
     * Get the revisions for the entity.
     *
     * @return A collection of revision entities for the entity.
     */
    CollectionWrapper<T> getRevisions();

    /**
     * Get the underlying Entity instance.
     *
     * @return
     */
    Object unwrap();

    /**
     * Clone the entity and wrapper.
     *
     * @param deepCopy If the collections in the entity should be cloned as well.
     * @return The cloned entity/wrapper.
     */
    T clone(boolean deepCopy);

    /**
     * Check if the entity is a revision entity or the latest entity.
     *
     * @return True if the entity represents a revision otherwise false.
     */
    boolean isRevisionEntity();
}
