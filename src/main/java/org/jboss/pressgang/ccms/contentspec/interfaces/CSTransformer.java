package org.jboss.pressgang.ccms.contentspec.interfaces;

import org.jboss.pressgang.ccms.contentspec.Comment;
import org.jboss.pressgang.ccms.contentspec.ContentSpec;
import org.jboss.pressgang.ccms.contentspec.KeyValueNode;
import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;

public interface CSTransformer<T, U, V, W> {
    ContentSpec transformCS(T arg);
    KeyValueNode<?> transformMetaData(final U arg);
    Comment transformComment(final V arg);
    Level transformLevel(final W arg);
    SpecTopic transformSpecTopic(final W arg);
}
