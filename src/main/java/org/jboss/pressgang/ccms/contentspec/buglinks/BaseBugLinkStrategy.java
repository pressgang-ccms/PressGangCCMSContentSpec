package org.jboss.pressgang.ccms.contentspec.buglinks;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.jboss.pressgang.ccms.contentspec.InitialContent;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.exceptions.ValidationException;
import org.jboss.pressgang.ccms.wrapper.ContentSpecWrapper;

public abstract class BaseBugLinkStrategy<T extends BugLinkOptions> {
    private String serverUrl;

    protected BaseBugLinkStrategy() {
    }

    public abstract void initialise(final String serverUrl, final Object... args);

    public abstract String generateUrl(T bugOptions, SpecTopic specTopic, String buildName,
            Date buildDate) throws UnsupportedEncodingException;

    public abstract String generateUrl(T bugOptions, InitialContent initialContent, String buildName,
            Date buildDate) throws UnsupportedEncodingException;

    /**
     * Validates the bugzilla options against an external server.
     *
     * @param bugOptions
     * @throws ValidationException
     */
    public abstract void validate(T bugOptions) throws ValidationException;

    public abstract boolean hasValuesChanged(ContentSpecWrapper contentSpecEntity, T bugOptions);

    /**
     * Check that the values in the bug options are valid so that they can be sent to an external server.
     *
     * @param bugOptions
     * @throws ValidationException
     */
    public abstract void checkValidValues(T bugOptions) throws ValidationException;

    protected String getServerUrl() {
        return serverUrl;
    }

    protected void setServerUrl(final String serverUrl) {
        this.serverUrl = serverUrl;
    }

    protected String getFixedServerUrl() {
        return getServerUrl() == null ? null : (getServerUrl().endsWith("/") ? getServerUrl() : (getServerUrl() + "/"));
    }
}
