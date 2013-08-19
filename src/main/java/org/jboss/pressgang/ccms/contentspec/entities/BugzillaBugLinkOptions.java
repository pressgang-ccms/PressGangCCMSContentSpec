package org.jboss.pressgang.ccms.contentspec.entities;

public class BugzillaBugLinkOptions extends BaseBugLinkOptions {

    private String product = null;
    private String component = null;
    private String version = null;
    private String keywords = null;
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

    public boolean isInjectAssignee() {
        return injectAssignee;
    }

    public void setInjectAssignee(boolean injectAssignee) {
        this.injectAssignee = injectAssignee;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
