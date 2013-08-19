package org.jboss.pressgang.ccms.contentspec.entities;

public abstract class BaseBugLinkOptions {
    private String baseUrl = null;
    private boolean injectLinks = true;

    public boolean isBugLinksEnabled() {
        return injectLinks;
    }

    /**
     * @param enabled Whether bug links should be injected
     */
    public void setBugLinksEnabled(final boolean enabled) {
        injectLinks = enabled;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
