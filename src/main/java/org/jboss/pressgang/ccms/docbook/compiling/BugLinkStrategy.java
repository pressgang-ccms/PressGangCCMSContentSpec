package org.jboss.pressgang.ccms.docbook.compiling;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.entities.BaseBugLinkOptions;
import org.jboss.pressgang.ccms.contentspec.exceptions.ValidationException;
import org.jboss.pressgang.ccms.wrapper.ContentSpecWrapper;

public interface BugLinkStrategy<T extends BaseBugLinkOptions> {
    String generateUrl(T bugOptions, SpecTopic specTopic, String buildName, Date buildDate) throws UnsupportedEncodingException;

    void validate(T bugOptions) throws ValidationException;

    boolean hasValuesChanged(ContentSpecWrapper contentSpecEntity, T bugOptions);
}
