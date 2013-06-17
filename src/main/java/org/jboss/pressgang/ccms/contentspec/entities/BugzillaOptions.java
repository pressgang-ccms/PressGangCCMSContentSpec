package org.jboss.pressgang.ccms.contentspec.entities;

public class BugzillaOptions {

    private String product = null;
    private String component = null;
    private String version = null;
    private String urlComponent = null;
    private boolean injectLinks = true;
    private boolean injectAssignee = true;

    /**
     * @return the product
     */
    public String getProduct() {
        return product;
    }

    /**
     * @param product the product to set
     */
    public void setProduct(final String product) {
        this.product = product;
    }

    /**
     * @return the component
     */
    public String getComponent() {
        return component;
    }

    /**
     * @param component the component to set
     */
    public void setComponent(final String component) {
        this.component = component;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    public boolean isBugzillaLinksEnabled() {
        return injectLinks;
    }

    /**
     * @param enabled Whether bug links should be injected
     */
    public void setBugzillaLinksEnabled(final boolean enabled) {
        this.injectLinks = enabled;
    }

    /**
     * Get the URL component that is used in the .ent file when
     * building the Docbook files.
     *
     * @return The BZURL component for the content specification.
     */
    public String getUrlComponent() {
        return urlComponent;
    }

    /**
     * Set the URL component that is used in the .ent file when
     * building the Docbook files.
     *
     * @param urlComponent The BZURL component to be used when building.
     */
    public void setUrlComponent(final String urlComponent) {
        this.urlComponent = urlComponent;
    }

    public boolean isInjectAssignee() {
        return injectAssignee;
    }

    public void setInjectAssignee(boolean injectAssignee) {
        this.injectAssignee = injectAssignee;
    }
}
