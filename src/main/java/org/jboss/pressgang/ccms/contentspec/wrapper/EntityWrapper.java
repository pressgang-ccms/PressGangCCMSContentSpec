package org.jboss.pressgang.ccms.contentspec.wrapper;

public abstract interface EntityWrapper<T extends EntityWrapper<T>> {
    Integer getId();
    Integer getRevision();
    /**
     * Get the underlying Entity instance.
     * 
     * @return
     */
    Object unwrap();
    T clone(boolean deepCopy);
    boolean isRevisionEntity();
}
