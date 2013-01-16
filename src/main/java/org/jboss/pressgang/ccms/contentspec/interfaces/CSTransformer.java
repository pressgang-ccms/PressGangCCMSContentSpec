package org.jboss.pressgang.ccms.contentspec.interfaces;

import org.jboss.pressgang.ccms.contentspec.Comment;
import org.jboss.pressgang.ccms.contentspec.ContentSpec;
import org.jboss.pressgang.ccms.contentspec.KeyValueNode;
import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;

public abstract class CSTransformer<T, U, V, W> {
    public abstract ContentSpec transform(T arg);

    protected abstract KeyValueNode<?> transformMetaData(final U arg);

    protected abstract Comment transformComment(final V arg);

    protected abstract Level transformLevel(final W arg);

    protected abstract SpecTopic transformSpecTopic(final W arg);
}
