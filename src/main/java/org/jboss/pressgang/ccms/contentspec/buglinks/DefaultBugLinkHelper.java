package org.jboss.pressgang.ccms.contentspec.buglinks;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.exceptions.ValidationException;
import org.jboss.pressgang.ccms.wrapper.ContentSpecWrapper;

public class DefaultBugLinkHelper extends BaseBugLinkStrategy<BugLinkOptions> {
    @Override
    public void initialise(String serverUrl, Object... args) {
    }

    @Override
    public String generateUrl(BugLinkOptions bugOptions, SpecTopic specTopic, String buildName,
            Date buildDate) throws UnsupportedEncodingException {
        return bugOptions.getBaseUrl();
    }

    @Override
    public void validate(BugLinkOptions bugOptions) throws ValidationException {
    }

    @Override
    public boolean hasValuesChanged(ContentSpecWrapper contentSpecEntity, BugLinkOptions bugOptions) {
        return false;
    }

    @Override
    public void checkValidValues(BugLinkOptions bugOptions) throws ValidationException {
    }
}