package org.jboss.pressgang.ccms.contentspec.buglinks;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import com.j2bugzilla.base.BugField;
import com.j2bugzilla.base.BugFieldValue;
import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.base.BugzillaException;
import com.j2bugzilla.base.ConnectionException;
import com.j2bugzilla.base.Product;
import com.j2bugzilla.base.ProductComponent;
import com.j2bugzilla.base.ProductVersion;
import com.j2bugzilla.rpc.GetBugField;
import com.j2bugzilla.rpc.GetProduct;
import org.jboss.pressgang.ccms.contentspec.InitialContent;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.exceptions.ValidationException;
import org.jboss.pressgang.ccms.contentspec.utils.EntityUtilities;
import org.jboss.pressgang.ccms.utils.common.StringUtilities;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.wrapper.ContentSpecWrapper;
import org.jboss.pressgang.ccms.wrapper.base.BaseTopicWrapper;

public class BugzillaBugLinkStrategy extends BaseBugLinkStrategy<BugzillaBugLinkOptions> {
    protected static final String BUGZILLA_DESCRIPTION_TEMPLATE = "Title: %s\n\n" + "Describe the issue:\n\n\nSuggestions for " +
            "improvement:\n\n\nAdditional information:";

    private BugzillaConnector connector;
    private boolean connected = false;

    public BugzillaBugLinkStrategy() {
    }

    @Override
    public void initialise(final String bugzillaUrl, final Object... args) {
        connector = new BugzillaConnector();
        setServerUrl(bugzillaUrl);
    }

    protected void connect() throws ConnectionException {
        connected = true;
        connector.connectTo(getFixedBugzillaUrl());
    }

    protected String getFixedBugzillaUrl() {
        return getServerUrl() == null ? "https://bugzilla.redhat.com/" : getFixedServerUrl();
    }

    @Override
    public String generateUrl(final BugzillaBugLinkOptions bzOptions, final SpecTopic specTopic, String buildName,
            final Date buildDate) throws UnsupportedEncodingException {
        final BaseTopicWrapper<?> topic = specTopic.getTopic();

        final String bugzillaDescription = URLEncoder.encode(String.format(BUGZILLA_DESCRIPTION_TEMPLATE, topic.getTitle()), ENCODING);
        final StringBuilder bugzillaEnvironment = buildBaseEnvironment(bzOptions, buildName, buildDate);
        bugzillaEnvironment.append("\nTopic ID: ").append(topic.getTopicId()).append("-").append(topic.getTopicRevision());
        final StringBuilder bugzillaBuildID = new StringBuilder();
        bugzillaBuildID.append(topic.getBugzillaBuildId());

        if (specTopic.getRevision() == null) {
            bugzillaBuildID.append(" [Latest]");
            bugzillaEnvironment.append(" [Latest]");
        } else {
            bugzillaBuildID.append(" [Specified]");
            bugzillaEnvironment.append(" [Specified]");
        }

        // Encode the URL and add in the build name/date entities
        final String encodedBugzillaEnvironment;
        if (bzOptions.isUseEntities()) {
            encodedBugzillaEnvironment = addBuildNameAndDateEntities(URLEncoder.encode(bugzillaEnvironment.toString(), ENCODING));
        } else {
            encodedBugzillaEnvironment = URLEncoder.encode(bugzillaEnvironment.toString(), ENCODING);
        }

        return generateUrl(bzOptions, bugzillaBuildID.toString(), bugzillaDescription, encodedBugzillaEnvironment, null);
    }

    @Override
    public String generateUrl(final BugzillaBugLinkOptions bzOptions, final InitialContent initialContent, String buildName,
            final Date buildDate) throws UnsupportedEncodingException {
        final String bugzillaDescription = URLEncoder.encode(
                String.format(BUGZILLA_DESCRIPTION_TEMPLATE, initialContent.getParent().getTitle()), ENCODING);
        final StringBuilder bugzillaEnvironment = buildBaseEnvironment(bzOptions, buildName, buildDate);
        bugzillaEnvironment.append("\nTopic IDs:");

        for (final SpecTopic initialContentTopic : initialContent.getSpecTopics()) {
            final BaseTopicWrapper<?> topic = initialContentTopic.getTopic();

            bugzillaEnvironment.append("\n");
            bugzillaEnvironment.append(topic.getTopicId()).append("-").append(topic.getTopicRevision());
            if (initialContentTopic.getRevision() == null) {
                bugzillaEnvironment.append(" [Latest]");
            } else {
                bugzillaEnvironment.append(" [Specified]");
            }
        }

        // Encode the URL and add in the build name/date entities
        final String encodedBugzillaEnvironment;
        if (bzOptions.isUseEntities()) {
            encodedBugzillaEnvironment = addBuildNameAndDateEntities(URLEncoder.encode(bugzillaEnvironment.toString(), ENCODING));
        } else {
            encodedBugzillaEnvironment = URLEncoder.encode(bugzillaEnvironment.toString(), ENCODING);
        }

        return generateUrl(bzOptions, null, bugzillaDescription, encodedBugzillaEnvironment, null);
    }

    @Override
    public String generateEntities(final BugzillaBugLinkOptions bzOptions, final String buildName,
            final Date buildDate) throws UnsupportedEncodingException {
        final StringBuilder retValue = new StringBuilder(super.generateEntities(bzOptions, buildName, buildDate));

        if (bzOptions != null && bzOptions.getProduct() != null) {
            final String encodedBZProduct = URLEncoder.encode(bzOptions.getProduct(), ENCODING);
            retValue.append("<!ENTITY BUILD_BZPRODUCT \"").append(StringUtilities.escapeForXMLEntity(encodedBZProduct)).append("\">\n");

            if (bzOptions.getComponent() != null) {
                final String encodedBZComponent = URLEncoder.encode(bzOptions.getComponent(), ENCODING);
                retValue.append("<!ENTITY BUILD_BZCOMPONENT \"").append(StringUtilities.escapeForXMLEntity(encodedBZComponent))
                        .append("\">\n");
            }

            if (bzOptions.getVersion() != null) {
                final String encodedBZVersion = URLEncoder.encode(bzOptions.getVersion(), ENCODING);
                retValue.append("<!ENTITY BUILD_BZVERSION \"").append(StringUtilities.escapeForXMLEntity(encodedBZVersion))
                        .append("\">\n");
            }

            if (bzOptions.getKeywords() != null) {
                final String encodedBZKeywords = URLEncoder.encode(bzOptions.getKeywords(), ENCODING);
                retValue.append("<!ENTITY BUILD_BZKEYWORDS \"").append(StringUtilities.escapeForXMLEntity(encodedBZKeywords))
                        .append("\">\n");
            }
        }

        return retValue.toString();
    }

    protected String generateUrl(final BugzillaBugLinkOptions bzOptions, final String buildId, final String encodedDescription,
            final String encodedEnvironment, final String bugzillaAssignedTo) throws UnsupportedEncodingException {
        // build the bugzilla url options
        final StringBuilder bugzillaURLComponents = new StringBuilder("?");
        bugzillaURLComponents.append("cf_environment=").append(encodedEnvironment);
        bugzillaURLComponents.append("&amp;");
        bugzillaURLComponents.append("comment=").append(encodedDescription);

        if (!isNullOrEmpty(buildId)) {
            bugzillaURLComponents.append("&amp;");
            bugzillaURLComponents.append("cf_build_id=").append(URLEncoder.encode(buildId, ENCODING));
        }

        if (bzOptions.isInjectAssignee() && !isNullOrEmpty(bugzillaAssignedTo)) {
            bugzillaURLComponents.append("&amp;");
            bugzillaURLComponents.append("assigned_to=").append(bugzillaAssignedTo);
        }

        // check the content spec options first
        if (bzOptions != null && bzOptions.getProduct() != null) {
            bugzillaURLComponents.append("&amp;");
            bugzillaURLComponents.append("product=");
            if (bzOptions.isUseEntities()) {
                bugzillaURLComponents.append("&BUILD_BZPRODUCT;");
            } else {
                bugzillaURLComponents.append(URLEncoder.encode(bzOptions.getProduct(), ENCODING));
            }

            if (bzOptions.getComponent() != null) {
                bugzillaURLComponents.append("&amp;");
                bugzillaURLComponents.append("component=");
                if (bzOptions.isUseEntities()) {
                    bugzillaURLComponents.append("&BUILD_BZCOMPONENT;");
                } else {
                    bugzillaURLComponents.append(URLEncoder.encode(bzOptions.getComponent(), ENCODING));
                }
            }

            if (bzOptions.getVersion() != null) {
                bugzillaURLComponents.append("&amp;");
                bugzillaURLComponents.append("version=");
                if (bzOptions.isUseEntities()) {
                    bugzillaURLComponents.append("&BUILD_BZVERSION;");
                } else {
                    bugzillaURLComponents.append(URLEncoder.encode(bzOptions.getVersion(), ENCODING));
                }
            }

            if (bzOptions.getKeywords() != null) {
                bugzillaURLComponents.append("&amp;");
                bugzillaURLComponents.append("keywords=");
                if (bzOptions.isUseEntities()) {
                    bugzillaURLComponents.append("&BUILD_BZKEYWORDS;");
                } else {
                    bugzillaURLComponents.append(URLEncoder.encode(bzOptions.getKeywords(), ENCODING));
                }
            }
        }

        // build the bugzilla url with the base components
        return getFixedBugzillaUrl() + "enter_bug.cgi" + bugzillaURLComponents.toString();
    }

    @Override
    public void validate(final BugzillaBugLinkOptions bugzillaOptions) throws ValidationException {
        if (!connected) {
            try {
                connect();
            } catch (ConnectionException e) {
                throw new RuntimeException(e);
            }
        }
        final GetProduct getProduct = new GetProduct(bugzillaOptions.getProduct());
        final GetBugField getBugField = new GetBugField("keywords");
        try {
            if (!isNullOrEmpty(bugzillaOptions.getProduct())) {
                connector.executeMethod(getProduct);
                final Product product = getProduct.getProduct();
                if (product == null) {
                    throw new ValidationException("No Bugzilla Product exists for product \"" + bugzillaOptions.getProduct() + "\", " +
                            "or it is a private Product.");
                } else {
                    // Validate the Bugzilla Component
                    if (bugzillaOptions.getComponent() != null) {
                        final ProductComponent component = getBugzillaComponent(bugzillaOptions.getComponent(), product);
                        if (component == null) {
                            throw new ValidationException(
                                    "No Bugzilla Component exists for component \"" + bugzillaOptions.getComponent() + "\".");
                        } else if (!component.getIsActive()) {
                            throw new ValidationException(
                                    "The Bugzilla Component \"" + bugzillaOptions.getComponent() + "\" is not active.");
                        }
                    }

                    // Validate the Bugzilla Version
                    if (bugzillaOptions.getVersion() != null) {
                        final ProductVersion version = getBugzillaVersion(bugzillaOptions.getVersion(), product);
                        if (version == null) {
                            throw new ValidationException(
                                    "No Bugzilla Version exists for version \"" + bugzillaOptions.getVersion() + "\".");
                        } else if (!version.getIsActive()) {
                            throw new ValidationException("The Bugzilla Version \"" + bugzillaOptions.getVersion() + "\" is not active.");
                        }
                    }
                }
            }

            // Validate the keywords
            if (!isNullOrEmpty(bugzillaOptions.getKeywords())) {
                connector.executeMethod(getBugField);
                final BugField bugField = getBugField.getBugField();
                final List<BugFieldValue> values = bugField.getValues();

                final String[] keywords = bugzillaOptions.getKeywords().split("\\s*,\\s*");
                for (final String keyword : keywords) {
                    boolean found = false;
                    for (final BugFieldValue value : values) {
                        if (value.getName() != null && value.getName().equals(keyword)) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        throw new ValidationException("No Bugzilla Keyword exists for keyword \"" + keyword + "\".");
                    }
                }
            }
        } catch (ValidationException e) {
            throw e;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (BugzillaException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasValuesChanged(ContentSpecWrapper contentSpecEntity, BugzillaBugLinkOptions bugOptions) {
        boolean changed = false;
        // Server
        if (EntityUtilities.hasContentSpecMetaDataChanged(CommonConstants.CS_BUGZILLA_SERVER_TITLE, getServerUrl(), contentSpecEntity)) {
            changed = true;
        }

        // Product
        if (EntityUtilities.hasContentSpecMetaDataChanged(CommonConstants.CS_BUGZILLA_PRODUCT_TITLE, bugOptions.getProduct(),
                contentSpecEntity)) {
            changed = true;
        }

        // Version
        if (EntityUtilities.hasContentSpecMetaDataChanged(CommonConstants.CS_BUGZILLA_VERSION_TITLE, bugOptions.getVersion(),
                contentSpecEntity)) {
            changed = true;
        }

        // Component
        if (EntityUtilities.hasContentSpecMetaDataChanged(CommonConstants.CS_BUGZILLA_COMPONENT_TITLE, bugOptions.getComponent(),
                contentSpecEntity)) {
            changed = true;
        }

        // Keywords
        if (EntityUtilities.hasContentSpecMetaDataChanged(CommonConstants.CS_BUGZILLA_KEYWORDS_TITLE, bugOptions.getKeywords(),
                contentSpecEntity)) {
            changed = true;
        }

        return changed;
    }

    @Override
    public void checkValidValues(BugzillaBugLinkOptions bugOptions) throws ValidationException {
        if (isNullOrEmpty(bugOptions.getProduct())) {
            if (!isNullOrEmpty(bugOptions.getComponent()) || !isNullOrEmpty(bugOptions.getVersion())) {
                throw new ValidationException("A Bugzilla Product must be specified to set additional fields.");
            }
//        } else if (isNullOrEmpty(bugOptions.getComponent())) {
//            if (!isNullOrEmpty(bugOptions.getVersion())) {
//                throw new ValidationException("A Bugzilla Component must be specified to set a Version.");
//            }
        }
    }

    protected ProductComponent getBugzillaComponent(final String component, final Product product) {
        if (product.getComponents() != null) {
            for (final ProductComponent componentEntity : product.getComponents()) {
                if (componentEntity.getName() != null && componentEntity.getName().equals(component)) {
                    return componentEntity;
                }
            }
        }

        return null;
    }

    protected ProductVersion getBugzillaVersion(final String version, final Product product) {
        if (product.getVersions() != null) {
            for (final ProductVersion versionEntity : product.getVersions()) {
                if (versionEntity.getName() != null && versionEntity.getName().equals(version)) {
                    return versionEntity;
                }
            }
        }

        return null;
    }
}
