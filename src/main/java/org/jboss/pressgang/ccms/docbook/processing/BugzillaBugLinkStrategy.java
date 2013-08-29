package org.jboss.pressgang.ccms.docbook.processing;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.j2bugzilla.base.BugField;
import com.j2bugzilla.base.BugFieldValue;
import com.j2bugzilla.base.BugzillaConnector;
import com.j2bugzilla.base.ConnectionException;
import com.j2bugzilla.base.Product;
import com.j2bugzilla.base.ProductComponent;
import com.j2bugzilla.base.ProductVersion;
import com.j2bugzilla.rpc.GetBugField;
import com.j2bugzilla.rpc.GetProduct;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.entities.BugzillaBugLinkOptions;
import org.jboss.pressgang.ccms.contentspec.exceptions.ValidationException;
import org.jboss.pressgang.ccms.contentspec.utils.EntityUtilities;
import org.jboss.pressgang.ccms.docbook.compiling.BugLinkStrategy;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.wrapper.ContentSpecWrapper;
import org.jboss.pressgang.ccms.wrapper.PropertyTagInTagWrapper;
import org.jboss.pressgang.ccms.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.wrapper.base.BaseTopicWrapper;

public class BugzillaBugLinkStrategy implements BugLinkStrategy<BugzillaBugLinkOptions> {
    protected static final String ENCODING = "UTF-8";
    protected static final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    protected static final String BUGZILLA_DESCRIPTION_TEMPLATE = "Title: %s\n\n" + "Describe the issue:\n\n\nSuggestions for " +
            "improvement:\n\n\nAdditional information:";

    private final BugzillaConnector connector;
    private final String bugzillaUrl;
    private boolean connected = false;

    public BugzillaBugLinkStrategy(final String bugzillaUrl) {
        connector = new BugzillaConnector();
        this.bugzillaUrl = bugzillaUrl;
    }

    protected void connect() throws ConnectionException {
        connected = true;
        connector.connectTo(getFixedBugzillaUrl());
    }

    protected String getFixedBugzillaUrl() {
        return bugzillaUrl == null ? "https://bugzilla.redhat.com/" : (bugzillaUrl.endsWith("/") ? bugzillaUrl : (bugzillaUrl + "/"));
    }

    @Override
    public String generateUrl(final BugzillaBugLinkOptions bzOptions, final SpecTopic specTopic, String buildName,
            Date buildDate) throws UnsupportedEncodingException {
        final BaseTopicWrapper<?> topic = specTopic.getTopic();

        String bugzillaProduct = null;
        String bugzillaComponent = null;
        String bugzillaVersion = null;
        String bugzillaKeywords = null;
        String bugzillaAssignedTo = null;
        final String bugzillaDescription = URLEncoder.encode(String.format(BUGZILLA_DESCRIPTION_TEMPLATE, topic.getTitle()), ENCODING);
        final StringBuilder bugzillaEnvironment = new StringBuilder("Build Name: ").append(buildName).append("\nBuild Date: ").append(
                DATE_FORMATTER.format(buildDate)).append("\nTopic ID: ").append(topic.getId()).append("-").append(topic.getRevision());
        final StringBuilder bugzillaBuildID = new StringBuilder();
        bugzillaBuildID.append(topic.getBugzillaBuildId());

        if (specTopic.getRevision() == null) {
            bugzillaBuildID.append(" [Latest]");
            bugzillaEnvironment.append(" [Latest]");
        } else {
            bugzillaBuildID.append(" [Specified]");
            bugzillaEnvironment.append(" [Specified]");
        }
        final String encodedBugzillaEnvironment = URLEncoder.encode(bugzillaEnvironment.toString(), ENCODING);

        // look for the bugzilla options
        if (topic.getTags() != null && topic.getTags() != null) {
            final List<TagWrapper> tags = topic.getTags().getItems();
            for (final TagWrapper tag : tags) {
                final PropertyTagInTagWrapper bugzillaProductTag = tag.getProperty(CommonConstants.BUGZILLA_PRODUCT_PROP_TAG_ID);
                final PropertyTagInTagWrapper bugzillaComponentTag = tag.getProperty(CommonConstants.BUGZILLA_COMPONENT_PROP_TAG_ID);
                final PropertyTagInTagWrapper bugzillaKeywordsTag = tag.getProperty(CommonConstants.BUGZILLA_KEYWORDS_PROP_TAG_ID);
                final PropertyTagInTagWrapper bugzillaVersionTag = tag.getProperty(CommonConstants.BUGZILLA_VERSION_PROP_TAG_ID);
                final PropertyTagInTagWrapper bugzillaAssignedToTag = tag.getProperty(CommonConstants.BUGZILLA_PROFILE_PROPERTY);

                if (bugzillaProduct == null && bugzillaProductTag != null)
                    bugzillaProduct = URLEncoder.encode(bugzillaProductTag.getValue(), ENCODING);

                if (bugzillaComponent == null && bugzillaComponentTag != null)
                    bugzillaComponent = URLEncoder.encode(bugzillaComponentTag.getValue(), ENCODING);

                if (bugzillaKeywords == null && bugzillaKeywordsTag != null)
                    bugzillaKeywords = URLEncoder.encode(bugzillaKeywordsTag.getValue(), ENCODING);

                if (bugzillaVersion == null && bugzillaVersionTag != null)
                    bugzillaVersion = URLEncoder.encode(bugzillaVersionTag.getValue(), ENCODING);

                if (bugzillaAssignedTo == null && bugzillaAssignedToTag != null)
                    bugzillaAssignedTo = URLEncoder.encode(bugzillaAssignedToTag.getValue(), ENCODING);
            }
        }

        // build the bugzilla url options
        final StringBuilder bugzillaURLComponents = new StringBuilder("?");
        bugzillaURLComponents.append("cf_environment=").append(encodedBugzillaEnvironment);
        bugzillaURLComponents.append("&amp;");
        bugzillaURLComponents.append("cf_build_id=").append(URLEncoder.encode(bugzillaBuildID.toString(), ENCODING));
        bugzillaURLComponents.append("&amp;");
        bugzillaURLComponents.append("comment=").append(bugzillaDescription);

        if (bzOptions.isInjectAssignee() && bugzillaAssignedTo != null) {
            bugzillaURLComponents.append("&amp;");
            bugzillaURLComponents.append("assigned_to=").append(bugzillaAssignedTo);
        }

        // check the content spec options first
        if (bzOptions != null && bzOptions.getProduct() != null) {
            bugzillaURLComponents.append("&amp;");
            bugzillaURLComponents.append("product=").append(URLEncoder.encode(bzOptions.getProduct(), ENCODING));

            if (bzOptions.getComponent() != null) {
                bugzillaURLComponents.append("&amp;");
                bugzillaURLComponents.append("component=").append(URLEncoder.encode(bzOptions.getComponent(), ENCODING));
            }

            if (bzOptions.getVersion() != null) {
                bugzillaURLComponents.append("&amp;");
                bugzillaURLComponents.append("version=" + URLEncoder.encode(bzOptions.getVersion(), ENCODING));
            }

            if (bzOptions.getKeywords() != null) {
                bugzillaURLComponents.append("&amp;");
                bugzillaURLComponents.append("keywords=").append(URLEncoder.encode(bzOptions.getKeywords(), ENCODING));
            }
        }
        // we need at least a product
        else if (bugzillaProduct != null) {
            bugzillaURLComponents.append("&amp;");
            bugzillaURLComponents.append("product=").append(bugzillaProduct);

            if (bugzillaComponent != null) {
                bugzillaURLComponents.append("&amp;");
                bugzillaURLComponents.append("component=").append(bugzillaComponent);
            }

            if (bugzillaVersion != null) {
                bugzillaURLComponents.append("&amp;");
                bugzillaURLComponents.append("version=").append(bugzillaVersion);
            }

            if (bugzillaKeywords != null) {
                bugzillaURLComponents.append("&amp;");
                bugzillaURLComponents.append("keywords=").append(bugzillaKeywords);
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
                    throw new ValidationException("No Bugzilla Product exists for product \"" + bugzillaOptions.getProduct() + "\".");
                } else {
                    // Validate the Bugzilla Component
                    if (bugzillaOptions.getComponent() != null) {
                        final ProductComponent component = getBugzillaComponent(bugzillaOptions.getComponent(), product);
                        if (component == null) {
                            throw new ValidationException(
                                    "No Bugzilla Component exists for component \"" + bugzillaOptions.getComponent() + "\".");
                        } else if (!component.getIsActive()) {
                            throw new ValidationException("The Bugzilla Component \"" + bugzillaOptions.getComponent() + "\" is not active.");
                        }
                    }

                    // Validate the Bugzilla Version
                    if (bugzillaOptions.getVersion() != null) {
                        final ProductVersion version = getBugzillaVersion(bugzillaOptions.getVersion(), product);
                        if (version == null) {
                            throw new ValidationException("No Bugzilla Version exists for version \"" + bugzillaOptions.getComponent() + "\".");
                        } else if (!version.getIsActive()) {
                            throw new ValidationException("The Bugzilla Version \"" + bugzillaOptions.getComponent() + "\" is not active.");
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
        } catch (Exception e) {
            throw new ValidationException(e);
        }
    }

    @Override
    public boolean hasValuesChanged(ContentSpecWrapper contentSpecEntity, BugzillaBugLinkOptions bugOptions) {
        boolean changed = false;
        // Server
        if (EntityUtilities.hasContentSpecMetaDataChanged(CommonConstants.CS_BUGZILLA_SERVER_TITLE, bugzillaUrl, contentSpecEntity)) {
            changed = true;
        }

        // Product
        if (EntityUtilities.hasContentSpecMetaDataChanged(CommonConstants.CS_BUGZILLA_PRODUCT_TITLE, bugOptions.getProduct(), contentSpecEntity)) {
            changed = true;
        }

        // Version
        if (EntityUtilities.hasContentSpecMetaDataChanged(CommonConstants.CS_BUGZILLA_VERSION_TITLE, bugOptions.getVersion(), contentSpecEntity)) {
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
        } else if (isNullOrEmpty(bugOptions.getComponent())) {
            if (!isNullOrEmpty(bugOptions.getVersion())) {
                throw new ValidationException("A Bugzilla Component must be specified to set a Version.");
            }
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
