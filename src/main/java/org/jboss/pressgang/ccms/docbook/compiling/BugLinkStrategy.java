package org.jboss.pressgang.ccms.docbook.compiling;

import java.io.UnsupportedEncodingException;

import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.entities.BaseBugLinkOptions;
import org.jboss.pressgang.ccms.contentspec.exceptions.ValidationException;

public interface BugLinkStrategy<T extends BaseBugLinkOptions> {
    String generateUrl(T bugOptions, SpecTopic specTopic, String buildName) throws UnsupportedEncodingException;

    void validate(T bugOptions) throws ValidationException;
}
