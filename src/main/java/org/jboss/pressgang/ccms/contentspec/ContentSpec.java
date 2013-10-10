package org.jboss.pressgang.ccms.contentspec;

/**
 * A class that is used to hold the data for a Content Specification. It holds a basic data(Title, Product, Version, etc...) and
 * the original pre processed text of the Content Specification and all of the levels (Appendixes, Chapters, Sections,
 * etc...) inside the Content Spec.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;
import org.jboss.pressgang.ccms.contentspec.buglinks.BugzillaBugLinkOptions;
import org.jboss.pressgang.ccms.contentspec.entities.InjectionOptions;
import org.jboss.pressgang.ccms.contentspec.buglinks.JIRABugLinkOptions;
import org.jboss.pressgang.ccms.contentspec.entities.Relationship;
import org.jboss.pressgang.ccms.contentspec.enums.BookType;
import org.jboss.pressgang.ccms.contentspec.enums.BugLinkType;
import org.jboss.pressgang.ccms.contentspec.enums.LevelType;
import org.jboss.pressgang.ccms.contentspec.enums.TopicType;
import org.jboss.pressgang.ccms.utils.common.DocBookUtilities;
import org.jboss.pressgang.ccms.utils.common.HashUtilities;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;

public class ContentSpec extends Node {
    private KeyValueNode<Integer> id = null;
    private KeyValueNode<String> title = null;
    private KeyValueNode<String> product = null;
    private KeyValueNode<String> version = null;
    private KeyValueNode<String> brand = null;
    private KeyValueNode<String> subtitle = null;
    private KeyValueNode<String> edition = null;
    private KeyValueNode<String> bookVersion = null;
    private KeyValueNode<Integer> pubsNumber = null;
    private KeyValueNode<String> publicanCfg = null;
    private KeyValueNode<String> dtd = null;
    private KeyValueNode<String> checksum = null;
    private KeyValueNode<String> copyrightHolder = null;
    private KeyValueNode<String> copyrightYear = null;
    private KeyValueNode<String> description = null;
    private KeyValueNode<InjectionOptions> injectionOptions = null;
    private KeyValueNode<String> bugzillaProduct = null;
    private KeyValueNode<String> bugzillaComponent = null;
    private KeyValueNode<String> bugzillaVersion = null;
    private KeyValueNode<String> bugzillaKeywords = null;
    private KeyValueNode<String> bugzillaServer = null;
    private KeyValueNode<String> bugzillaURL = null;
    private KeyValueNode<BugLinkType> bugLinks = null;
    private KeyValueNode<Boolean> injectBugzillaAssignee = null;
    private KeyValueNode<Boolean> injectSurveyLinks = null;
    private KeyValueNode<String> outputStyle = null;
    private KeyValueNode<Boolean> allowDuplicateTopics = null;
    private KeyValueNode<Boolean> allowEmptyLevels = null;
    private KeyValueNode<BookType> bookType = null;
    private KeyValueNode<String> brandLogo = null;
    private KeyValueNode<SpecTopic> revisionHistory = null;
    private KeyValueNode<SpecTopic> feedback = null;
    private KeyValueNode<SpecTopic> legalNotice = null;
    private KeyValueNode<SpecTopic> authorGroup = null;
    private KeyValueNode<String> groupId = null;
    private KeyValueNode<String> artifactId = null;
    private KeyValueNode<String> jiraProject = null;
    private KeyValueNode<String> jiraComponent = null;
    private KeyValueNode<String> jiraVersion = null;
    private KeyValueNode<String> jiraLabels = null;
    private KeyValueNode<String> jiraServer = null;
    private FileList files = null;
    private KeyValueNode<String> entities = null;
    private Integer revision = null;
    private String locale = null;

    private final LinkedList<Node> nodes = new LinkedList<Node>();
    private final Level level = new Level("Initial Level", 0, null, LevelType.BASE);

    /**
     * Constructor
     *
     * @param title           The Title of the Content Specification.
     * @param product         The Product that the Content Specification documents.
     * @param version         The Version of the Product that the Content Specification documents.
     * @param copyrightHolder The Copyright Holder of the Content Specification and the book it creates.
     */
    public ContentSpec(final String title, final String product, final String version, final String copyrightHolder) {
        setTitle(title);
        setProduct(product);
        setVersion(version);
        setCopyrightHolder(copyrightHolder);
    }

    /**
     * Constructor
     *
     * @param title The title of the Content Specification.
     */
    public ContentSpec(final String title) {
        setTitle(title);
    }

    /**
     * Constructor
     *
     * @param id    The Database ID of the Content Specification.
     * @param title The title of the Content Specification.
     */
    public ContentSpec(final int id, final String title) {
        this(title);
        setId(id);
    }

    public ContentSpec() {
    }

    // Start of the basic getter/setter methods for this Scope.

    /**
     * Get the base Level of the Content Specification. This level will contain all the other levels in the content
     * specification.
     *
     * @return The Base Level object of a Content Specification.
     */
    public Level getBaseLevel() {
        return level;
    }

    /**
     * Gets the Product that the Content Specification documents.
     *
     * @return The name of the product.
     */
    public String getProduct() {
        return product == null ? "" : product.getValue();
    }

    /**
     * Sets the name of the product that the Content Specification documents.
     *
     * @param product The name of the Product.
     */
    public void setProduct(final String product) {
        if (product == null && this.product == null) {
            return;
        } else if (product == null) {
            removeChild(this.product);
            this.product = null;
        } else if (this.product == null) {
            this.product = new KeyValueNode<String>(CommonConstants.CS_PRODUCT_TITLE, product);
            appendChild(this.product, false);
        } else {
            this.product.setValue(product);
        }
    }

    /**
     * Get the version of the product that the Content Specification documents.
     *
     * @return The version of the product or an empty string if the version is null.
     */
    public String getVersion() {
        return version == null ? null : version.getValue();
    }

    /**
     * Set the version of the product that the Content Specification documents.
     *
     * @param version The product version.
     */
    public void setVersion(final String version) {
        if (version == null && this.version == null) {
            return;
        } else if (version == null) {
            removeChild(this.version);
            this.version = null;
        } else if (this.version == null) {
            this.version = new KeyValueNode<String>(CommonConstants.CS_VERSION_TITLE, version);
            appendChild(this.version, false);
        } else {
            this.version.setValue(version);
        }
    }

    /**
     * Gets the brand of the product that the Content Specification documents.
     *
     * @return The brand of the product or null if one doesn't exist.
     */
    public String getBrand() {
        return brand == null ? null : brand.getValue();
    }

    /**
     * Set the brand of the product that the Content Specification documents.
     *
     * @param brand The brand of the product.
     */
    public void setBrand(final String brand) {
        if (brand == null && this.brand == null) {
            return;
        } else if (brand == null) {
            removeChild(this.brand);
            this.brand = null;
        } else if (this.brand == null) {
            this.brand = new KeyValueNode<String>(CommonConstants.CS_BRAND_TITLE, brand);
            appendChild(this.brand, false);
        } else {
            this.brand.setValue(brand);
        }
    }

    /**
     * Sets the ID of the Content Specification.
     *
     * @param id The database ID of the content specification.
     */
    public void setId(final Integer id) {
        if (id == null && this.id == null) {
            return;
        } else if (id == null) {
            removeChild(this.id);
            this.id = null;
        } else if (this.id == null) {
            this.id = new KeyValueNode<Integer>(CommonConstants.CS_ID_TITLE, id);
            nodes.addFirst(this.id);
            if (this.id.getParent() != null) {
                this.id.removeParent();
            }
            this.id.setParent(this);
        } else {
            this.id.setValue(id);
        }
    }

    /**
     * Gets the ID of the Content Specification
     *
     * @return The Content Specification database ID or 0 if one hasn't been set.
     */
    public Integer getId() {
        return (Integer) (id == null ? null : id.getValue());
    }

    /**
     * The revision number for this Content Spec as it exists in the database. This property is not exposible in a Content
     * Specification and is used internally only.
     *
     * @return The Content Specifications Database revision, or null for the latest version.
     */
    public Integer getRevision() {
        return revision;
    }

    /**
     * Sets the revision number for this Content Spec as it exists in the database. This property is not exposible in a Content
     * Specification and is used internally only.
     *
     * @param revision The Content Specifications Database revision, or null for the latest version.
     */
    public void setRevision(final Integer revision) {
        this.revision = revision;
    }

    /**
     * Gets the title of the Content Specification.
     *
     * @return The Content Specification title or an empty string if it is null.
     */
    public String getTitle() {
        return title == null ? "" : title.getValue();
    }

    /**
     * Gets the escaped version of the title for the Content Specification.
     *
     * @return The Content Specification title.
     */
    public String getEscapedTitle() {
        return DocBookUtilities.escapeTitle(getTitle());
    }

    /**
     * Sets the Content Specifications title.
     *
     * @param title The title for the content specification
     */
    public void setTitle(final String title) {
        if (title == null && this.title == null) {
            return;
        } else if (title == null) {
            removeChild(this.title);
            this.title = null;
        } else if (this.title == null) {
            this.title = new KeyValueNode<String>(CommonConstants.CS_TITLE_TITLE, title);
            appendChild(this.title, false);
        } else {
            this.title.setValue(title);
        }
    }

    /**
     * Gets the Subtitle for the Content Specification.
     *
     * @return The Subtitle of the Content Specification or null if one doesn't exist.
     */
    public String getSubtitle() {
        return subtitle == null ? null : subtitle.getValue();
    }

    /**
     * Sets the Subtitle for the Content Specification
     *
     * @param subtitle The subtitle for the Content Specification
     */
    public void setSubtitle(final String subtitle) {
        if (subtitle == null && this.subtitle == null) {
            return;
        } else if (subtitle == null) {
            removeChild(this.subtitle);
            this.subtitle = null;
        } else if (this.subtitle == null) {
            this.subtitle = new KeyValueNode<String>(CommonConstants.CS_SUBTITLE_TITLE, subtitle);
            appendChild(this.subtitle, false);
        } else {
            this.subtitle.setValue(subtitle);
        }
    }

    /**
     * Gets the Book Version of the book the Content Specification will create.
     *
     * @return The Content Specifications Book Edition or null if one doesn't exist.
     */
    public String getBookVersion() {
        return bookVersion == null ? null : bookVersion.getValue();
    }

    /**
     * Set the BookVersion of the Book the Content Specification represents.
     *
     * @param bookVersion The Book Version.
     */
    public void setBookVersion(final String bookVersion) {
        if (bookVersion == null && this.bookVersion == null) {
            return;
        } else if (bookVersion == null) {
            removeChild(this.bookVersion);
            this.bookVersion = null;
        } else if (this.bookVersion == null) {
            this.bookVersion = new KeyValueNode<String>(CommonConstants.CS_BOOK_VERSION_TITLE, bookVersion);
            appendChild(this.bookVersion, false);
        } else {
            this.bookVersion.setValue(bookVersion);
        }
    }

    /**
     * Gets the Edition of the book the Content Specification will create.
     *
     * @return The Content Specifications Book Edition or null if one doesn't exist.
     */
    public String getEdition() {
        return edition == null ? null : edition.getValue();
    }

    /**
     * Set the Edition of the Book the Content Specification represents.
     *
     * @param edition The Book Edition.
     */
    public void setEdition(final String edition) {
        if (edition == null && this.edition == null) {
            return;
        } else if (edition == null) {
            removeChild(this.edition);
            this.edition = null;
        } else if (this.edition == null) {
            this.edition = new KeyValueNode<String>(CommonConstants.CS_EDITION_TITLE, edition);
            appendChild(this.edition, false);
        } else {
            this.edition.setValue(edition);
        }
    }

    /**
     * Get the publication number used by publican
     *
     * @return The publication number or null if one doesn't exist.
     */
    public Integer getPubsNumber() {
        return (Integer) (pubsNumber == null ? null : pubsNumber.getValue());
    }

    /**
     * Set the publication number for the Content Specification.
     *
     * @param pubsNumber The publication number.
     */
    public void setPubsNumber(final Integer pubsNumber) {
        if (pubsNumber == null && this.pubsNumber == null) {
            return;
        } else if (pubsNumber == null) {
            removeChild(this.pubsNumber);
            this.pubsNumber = null;
        } else if (this.pubsNumber == null) {
            this.pubsNumber = new KeyValueNode<Integer>(CommonConstants.CS_PUBSNUMBER_TITLE, pubsNumber);
            appendChild(this.pubsNumber, false);
        } else {
            this.pubsNumber.setValue(pubsNumber);
        }
    }

    /**
     * Gets the data what will be appended to the publican.cfg file when built.
     *
     * @return The data to be appended or null if none exist.
     */
    public String getPublicanCfg() {
        return publicanCfg == null ? null : publicanCfg.getValue();
    }

    /**
     * Set the data that will be appended to the publican.cfg file when built.
     *
     * @param publicanCfg The data to be appended.
     */
    public void setPublicanCfg(final String publicanCfg) {
        if (publicanCfg == null && this.publicanCfg == null) {
            return;
        } else if (publicanCfg == null) {
            removeChild(this.publicanCfg);
            this.publicanCfg = null;
        } else if (this.publicanCfg == null) {
            this.publicanCfg = new KeyValueNode<String>(CommonConstants.CS_PUBLICAN_CFG_TITLE, publicanCfg);
            appendChild(this.publicanCfg, false);
        } else {
            this.publicanCfg.setValue(publicanCfg);
        }
    }

    /**
     * Get the DTD of the Content Specification. The default value is "Docbook 4.5".
     *
     * @return The DTD of the Content Specification or the default value if one isn't set.
     */
    public String getDtd() {
        return dtd == null ? "Docbook 4.5" : dtd.getValue();
    }

    /**
     * Sets the DTD for a Content Specification.
     *
     * @param dtd The DTD of the Content Specification.
     */
    public void setDtd(final String dtd) {
        if (dtd == null && this.dtd == null) {
            return;
        } else if (dtd == null) {
            removeChild(this.dtd);
            this.dtd = null;
        } else if (this.dtd == null) {
            this.dtd = new KeyValueNode<String>(CommonConstants.CS_DTD_TITLE, dtd);
            appendChild(this.dtd, false);
        } else {
            this.dtd.setValue(dtd);
        }
    }

    /**
     * Get the set Checksum value for a Content Specification.
     * <p/>
     * Note: This function doesn't calculate the Checksum of a Content Specification.
     *
     * @return The set value of the Content Specifications Checksum or null if one doesn't exist.
     */
    public String getChecksum() {
        return checksum == null ? null : checksum.getValue();
    }

    /**
     * Set the Checksum value for a Content Specification.
     * <p/>
     * Note: This value isn't used by the toString() function as it re calculates the Checksum.
     *
     * @param checksum The Checksum of the Content Specification.
     */
    public void setChecksum(final String checksum) {
        if (checksum == null && this.checksum == null) {
            return;
        } else if (checksum == null) {
            removeChild(this.checksum);
            this.checksum = null;
        } else if (this.checksum == null) {
            this.checksum = new KeyValueNode<String>(CommonConstants.CS_CHECKSUM_TITLE, checksum);
            appendChild(this.checksum, false);
        } else {
            this.checksum.setValue(checksum);
        }
    }

    /**
     * Gets the Abstract (or Description) for a Content Specification that will be added to a book when built.
     *
     * @return The abstract for the Content Specification or null if one doesn't exist.
     */
    public String getAbstract() {
        return description == null ? null : description.getValue();
    }

    /**
     * Sets the Abstract (or Description) for a Content Specification.
     *
     * @param description The Abstract for the Content Specifications book.
     */
    public void setAbstract(final String description) {
        if (description == null && this.description == null) {
            return;
        } else if (description == null) {
            removeChild(this.description);
            this.description = null;
        } else if (this.description == null) {
            this.description = new KeyValueNode<String>(CommonConstants.CS_ABSTRACT_TITLE, description);
            appendChild(this.description, false);
        } else {
            this.description.setValue(description);
        }
    }

    /**
     * Get the Copyright Holder of the Content Specification and the book it creates.
     *
     * @return The name of the Copyright Holder.
     */
    public String getCopyrightHolder() {
        return copyrightHolder == null ? "" : copyrightHolder.getValue();
    }

    /**
     * Set the Copyright Holder of the Content Specification and the book it creates.
     *
     * @param copyrightHolder The name of the Copyright Holder.
     */
    public void setCopyrightHolder(final String copyrightHolder) {
        if (copyrightHolder == null && this.copyrightHolder == null) {
            return;
        } else if (copyrightHolder == null) {
            removeChild(this.copyrightHolder);
            this.copyrightHolder = null;
        } else if (this.copyrightHolder == null) {
            this.copyrightHolder = new KeyValueNode<String>(CommonConstants.CS_COPYRIGHT_HOLDER_TITLE, copyrightHolder);
            appendChild(this.copyrightHolder, false);
        } else {
            this.copyrightHolder.setValue(copyrightHolder);
        }
    }

    /**
     * Get the Copyright Year(s) of the Content Specification and the book it creates.
     *
     * @return The year(s) for the Copyright.
     */
    public String getCopyrightYear() {
        return copyrightYear == null ? null : copyrightYear.getValue();
    }

    /**
     * Set the Copyright Year(s) of the Content Specification and the book it creates.
     *
     * @param copyrightYear The year(s) for the Copyright.
     */
    public void setCopyrightYear(final String copyrightYear) {
        if (copyrightYear == null && this.copyrightYear == null) {
            return;
        } else if (copyrightYear == null) {
            removeChild(this.copyrightYear);
            this.copyrightYear = null;
        } else if (this.copyrightYear == null) {
            this.copyrightYear = new KeyValueNode<String>(CommonConstants.CS_COPYRIGHT_YEAR_TITLE, copyrightYear);
            appendChild(this.copyrightYear, false);
        } else {
            this.copyrightYear.setValue(copyrightYear);
        }
    }

    /**
     * Get the Type of Book the Content Specification should be created for. The current values that are supported are "Book"
     * and "Article".
     *
     * @return The type of book the Content Specification should be transformed into or BOOK if the type isn't set.
     */
    public BookType getBookType() {
        return bookType == null ? BookType.BOOK : bookType.getValue();
    }

    /**
     * Set the Type of Book the Content Specification should be created for. The current values that are supported are "Book"
     * and "Article".
     *
     * @param bookType The type the book should be built as.
     */
    public void setBookType(final BookType bookType) {
        if (bookType == null && this.bookType == null) {
            return;
        } else if (bookType == null) {
            removeChild(this.bookType);
            this.bookType = null;
        } else if (this.bookType == null) {
            this.bookType = new KeyValueNode<BookType>(CommonConstants.CS_BOOK_TYPE_TITLE, bookType);
            appendChild(this.bookType, false);
        } else {
            this.bookType.setValue(bookType);
        }
    }

    /**
     * Gets the path of the Brand Logo for the Content Specification.
     *
     * @return The path to the Brand Logo.
     */
    public String getBrandLogo() {
        return brandLogo == null ? "" : brandLogo.getValue();
    }

    /**
     * Sets the path of the Brand Logo for the Content Specification.
     *
     * @param brandLogo The path to the Brand Logo.
     */
    public void setBrandLogo(final String brandLogo) {
        if (brandLogo == null && this.brandLogo == null) {
            return;
        } else if (brandLogo == null) {
            removeChild(this.brandLogo);
            this.brandLogo = null;
        } else if (this.brandLogo == null) {
            this.brandLogo = new KeyValueNode<String>(CommonConstants.CS_BRAND_LOGO_TITLE, brandLogo);
            appendChild(this.brandLogo, false);
        } else {
            this.brandLogo.setValue(brandLogo);
        }
    }

    /**
     * Gets the injection Options for the Content Specification that will be used when building a book.
     *
     * @return A InjectionOptions object containing the Injection Options to be used when building.
     */
    public InjectionOptions getInjectionOptions() {
        return injectionOptions == null ? null : injectionOptions.getValue();
    }

    /**
     * Sets the InjectionOptions that will be used by the Builder when building a book.
     *
     * @param injectionOptions The InjectionOptions to be used when building a book.
     */
    public void setInjectionOptions(final InjectionOptions injectionOptions) {
        if (injectionOptions == null && this.injectionOptions == null) {
            return;
        } else if (injectionOptions == null) {
            removeChild(this.injectionOptions);
            this.injectionOptions = null;
        } else if (this.injectionOptions == null) {
            this.injectionOptions = new KeyValueNode<InjectionOptions>(CommonConstants.CS_INLINE_INJECTION_TITLE, injectionOptions);
            appendChild(this.injectionOptions, false);
        } else {
            this.injectionOptions.setValue(injectionOptions);
        }
    }

    /**
     * Sets the description for the global tags.
     *
     * @param desc The description.
     */
    public void setDescription(final String desc) {
        level.setDescription(desc);
    }

    /**
     * Get the description that will be applied globally.
     *
     * @return The description as a String
     */
    public String getDescription() {
        return level.getDescription(false);
    }

    /**
     * Gets the locale of the Content Specification.
     *
     * @return The Content Specification locale.
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Sets the Content Specifications locale.
     *
     * @param locale The locale for the content specification
     */
    public void setLocale(final String locale) {
        this.locale = locale;
    }

    /**
     * Gets the output style for the Content Specification. The default is CSP.
     *
     * @return The Content Specification output style.
     */
    public String getOutputStyle() {
        return outputStyle == null ? CSConstants.CSP_OUTPUT_FORMAT : outputStyle.getValue();
    }

    /**
     * Sets the Content Specifications output style.
     *
     * @param outputStyle The output style for the content specification
     */
    public void setOutputStyle(final String outputStyle) {
        if (outputStyle == null && this.outputStyle == null) {
            return;
        } else if (outputStyle == null) {
            removeChild(this.outputStyle);
            this.outputStyle = null;
        } else if (this.outputStyle == null) {
            this.outputStyle = new KeyValueNode<String>(CSConstants.OUTPUT_STYLE_TITLE, outputStyle);
            appendChild(this.outputStyle, false);
        } else {
            this.outputStyle.setValue(outputStyle);
        }
    }

    /**
     * Sets the Assigned Writer for the global tags.
     *
     * @param writer The writers name that matches to the assigned writer tag in the database
     */
    public void setAssignedWriter(final String writer) {
        level.setAssignedWriter(writer);
    }

    /**
     * Gets the Assigned Writer that will be applied globally.
     *
     * @return The Assigned Writers name as a String
     */
    public String getAssignedWriter() {
        return level.getAssignedWriter(false);
    }

    /**
     * Sets the set of tags for the global tags
     *
     * @param tags A List of tags by their name.
     */
    public void setTags(final List<String> tags) {
        level.setTags(tags);
    }

    /**
     * Gets the set of tags for the global tags.
     *
     * @return A list of tag names
     */
    public List<String> getTags() {
        return level.getTags(false);
    }

    /**
     * Sets the list of tags that are to be removed in the global options.
     *
     * @param tags An ArrayList of tags to be removed
     */
    public void setRemoveTags(List<String> tags) {
        level.setRemoveTags(tags);
    }

    /**
     * Gets an ArrayList of tags that are to be removed globally.
     *
     * @return An ArrayList of tags
     */
    public List<String> getRemoveTags() {
        return level.getRemoveTags(false);
    }

    /**
     * Sets the list of source urls to be applied globally
     *
     * @param sourceUrls A List of urls
     */
    public void setSourceUrls(final List<String> sourceUrls) {
        level.setSourceUrls(sourceUrls);
    }

    /**
     * Get the Source Urls that are to be applied globally.
     *
     * @return A List of Strings that represent the source urls
     */
    public List<String> getSourceUrls() {
        return level.getSourceUrls(true);
    }

    public Boolean getAllowDuplicateTopics() {
        return (Boolean) (allowDuplicateTopics == null ? false : allowDuplicateTopics.getValue());
    }

    public void setAllowDuplicateTopics(final Boolean allowDuplicateTopics) {
        if (allowDuplicateTopics == null && this.allowDuplicateTopics == null) {
            return;
        } else if (allowDuplicateTopics == null) {
            removeChild(this.allowDuplicateTopics);
            this.allowDuplicateTopics = null;
        } else if (this.allowDuplicateTopics == null) {
            this.allowDuplicateTopics = new KeyValueNode<Boolean>("Duplicate Topics", allowDuplicateTopics);
            appendChild(this.allowDuplicateTopics, false);
        } else {
            this.allowDuplicateTopics.setValue(allowDuplicateTopics);
        }
    }

    public Boolean getAllowEmptyLevels() {
        return (Boolean) (allowEmptyLevels == null ? false : allowEmptyLevels.getValue());
    }

    public void setAllowEmptyLevels(final Boolean allowEmptyLevels) {
        if (allowEmptyLevels == null && this.allowEmptyLevels == null) {
            return;
        } else if (allowEmptyLevels == null) {
            removeChild(this.allowEmptyLevels);
            this.allowEmptyLevels = null;
        } else if (this.allowEmptyLevels == null) {
            this.allowEmptyLevels = new KeyValueNode<Boolean>("Allow Empty Levels", allowEmptyLevels);
            appendChild(this.allowEmptyLevels, false);
        } else {
            this.allowEmptyLevels.setValue(allowEmptyLevels);
        }
    }

    /**
     * Adds a tag to the global list of tags. If the tag starts with a - then its added to the remove tag list otherwise its
     * added to the normal tag mapping. Also strips off + & - from the start of tags.
     *
     * @param tagName The name of the Tag to be added.
     * @return True if the tag was added successfully otherwise false.
     */
    public boolean addTag(final String tagName) {
        return level.addTag(tagName);
    }

    /**
     * Adds a list of tags to the global list of tags
     *
     * @param tagArray A list of tags by name that are to be added.
     * @return True if all the tags were added successfully otherwise false.
     */
    public boolean addTags(final List<String> tagArray) {
        return level.addTags(tagArray);
    }

    /**
     * Adds a source URL to the list of global URL's
     *
     * @param url The URL to be added
     */
    public void addSourceUrl(final String url) {
        level.addSourceUrl(url);
    }

    /**
     * Removes a specific Source URL from the list of global URL's
     *
     * @param url The URL to be removed.
     */
    public void removeSourceUrl(final String url) {
        level.removeSourceUrl(url);
    }

    /**
     * Adds a Chapter to the Content Specification. If the Chapter already has a parent, then it is removed from that parent and
     * added to this level.
     *
     * @param chapter A Chapter to be added to the Content Specification.
     */
    public void appendChapter(final Chapter chapter) {
        level.appendChild(chapter);
    }

    /**
     * Adds a Part to the Content Specification. If the Part already has a parent, then it is removed from that parent and added
     * to this level.
     *
     * @param part The Part to be added to the Content Specification.
     */
    public void appendPart(final Part part) {
        level.appendChild(part);
    }

    /**
     * Removes a Chapter from the Content Specification and removes the Content Specification as the Chapters parent.
     *
     * @param chapter The Chapter to be removed from the Content Specification.
     */
    public void removeChapter(final Chapter chapter) {
        level.appendChild(chapter);
    }

    /**
     * Gets a ordered linked list of the child nodes within the Content Specification. This includes comments and chapters.
     *
     * @return The ordered list of nodes for the Content Specification.
     */
    public LinkedList<Node> getChildNodes() {
        return level.getChildNodes();
    }

    /**
     * Gets the number of Chapters in the Content Specification.
     *
     * @return The number of Child Levels
     */
    public int getNumberOfChapters() {
        return level.getNumberOfChildLevels();
    }

    /**
     * Gets a List of all the chapters in this level.
     * <p/>
     * Note: The Chapters may not be in order.
     *
     * @return A List of Chapters.
     */
    public List<Level> getChapters() {
        return level.getChildLevels();
    }

    /**
     * Appends a Comment to the Content Specification.
     *
     * @param comment The comment node to be appended to the Content Specification.
     */
    public void appendComment(final Comment comment) {
        appendChild(comment);
    }

    /**
     * Creates and appends a Comment node to the Content Specification.
     *
     * @param comment The Comment to be appended.
     */
    public void appendComment(final String comment) {
        appendComment(new Comment(comment));
    }

    /**
     * Removes a Comment from the Content Specification.
     *
     * @param comment The Comment node to be removed.
     */
    public void removeComment(final Comment comment) {
        removeChild(comment);
    }

    // End of the basic getter/setter methods for this ContentSpec.

    public List<SpecTopic> getSpecTopics() {
        final List<SpecTopic> specTopics = getLevelSpecTopics(level);

        // Add the Revision History Spec Topic
        if (getRevisionHistory() != null) {
            specTopics.add(getRevisionHistory());
        }

        // Add the Feedback Spec Topic
        if (getFeedback() != null) {
            specTopics.add(getFeedback());
        }

        // Add the Legal Notice Spec Topic
        if (getLegalNotice() != null) {
            specTopics.add(getLegalNotice());
        }

        // Add the Author Group Spec Topic
        if (getAuthorGroup() != null) {
            specTopics.add(getAuthorGroup());
        }

        return specTopics;
    }

    private List<SpecTopic> getLevelSpecTopics(final Level level) {
        final List<SpecTopic> specTopics = new ArrayList<SpecTopic>(level.getSpecTopics());
        for (final Level childLevel : level.getChildLevels()) {
            specTopics.addAll(getLevelSpecTopics(childLevel));
        }
        return specTopics;
    }

    public Map<SpecTopic, List<Relationship>> getRelationships() {
        final List<SpecTopic> specTopics = getSpecTopics();
        final Map<SpecTopic, List<Relationship>> relationships = new HashMap<SpecTopic, List<Relationship>>();
        for (final SpecTopic specTopic : specTopics) {
            relationships.put(specTopic, specTopic.getRelationships());
        }
        return relationships;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public String getBugzillaProduct() {
        return bugzillaProduct == null ? null : bugzillaProduct.getValue().toString();
    }

    public void setBugzillaProduct(final String bugzillaProduct) {
        if (bugzillaProduct == null && this.bugzillaProduct == null) {
            return;
        } else if (bugzillaProduct == null) {
            removeChild(this.bugzillaProduct);
            this.bugzillaProduct = null;
        } else if (this.bugzillaProduct == null) {
            this.bugzillaProduct = new KeyValueNode<String>(CommonConstants.CS_BUGZILLA_PRODUCT_TITLE, bugzillaProduct);
            appendChild(this.bugzillaProduct, false);
        } else {
            this.bugzillaProduct.setValue(bugzillaProduct);
        }
    }

    public String getBugzillaComponent() {
        return bugzillaComponent == null ? null : bugzillaComponent.getValue().toString();
    }

    public void setBugzillaComponent(final String bugzillaComponent) {
        if (bugzillaComponent == null && this.bugzillaComponent == null) {
            return;
        } else if (bugzillaComponent == null) {
            removeChild(this.bugzillaComponent);
            this.bugzillaComponent = null;
        } else if (this.bugzillaComponent == null) {
            this.bugzillaComponent = new KeyValueNode<String>(CommonConstants.CS_BUGZILLA_COMPONENT_TITLE, bugzillaComponent);
            appendChild(this.bugzillaComponent, false);
        } else {
            this.bugzillaComponent.setValue(bugzillaComponent);
        }
    }

    /**
     * Get the Bugzilla Version to be applied during building.
     *
     * @return The version of the product in bugzilla.
     */
    public String getBugzillaVersion() {
        return bugzillaVersion == null ? null : bugzillaVersion.getValue().toString();
    }

    /**
     * Set the Bugzilla Version to be applied during building.
     *
     * @param bugzillaVersion The version of the product in bugzilla.
     */
    public void setBugzillaVersion(final String bugzillaVersion) {
        if (bugzillaVersion == null && this.bugzillaVersion == null) {
            return;
        } else if (bugzillaVersion == null) {
            removeChild(this.bugzillaVersion);
            this.bugzillaVersion = null;
        } else if (this.bugzillaVersion == null) {
            this.bugzillaVersion = new KeyValueNode<String>(CommonConstants.CS_BUGZILLA_VERSION_TITLE, bugzillaVersion);
            appendChild(this.bugzillaVersion, false);
        } else {
            this.bugzillaVersion.setValue(bugzillaVersion);
        }
    }

    /**
     * Get the Bugzilla Keywords to be applied during building.
     *
     * @return The keywords to be set in bugzilla.
     */
    public String getBugzillaKeywords() {
        return bugzillaKeywords == null ? null : bugzillaKeywords.getValue().toString();
    }

    /**
     * Set the Bugzilla Keywords to be applied during building.
     *
     * @param bugzillaKeywords The keywords to be set in bugzilla.
     */
    public void setBugzillaKeywords(final String bugzillaKeywords) {
        if (bugzillaKeywords == null && this.bugzillaKeywords == null) {
            return;
        } else if (bugzillaKeywords == null) {
            removeChild(this.bugzillaKeywords);
            this.bugzillaKeywords = null;
        } else if (this.bugzillaKeywords == null) {
            this.bugzillaKeywords = new KeyValueNode<String>(CommonConstants.CS_BUGZILLA_KEYWORDS_TITLE, bugzillaKeywords);
            appendChild(this.bugzillaKeywords, false);
        } else {
            this.bugzillaVersion.setValue(bugzillaKeywords);
        }
    }

    /**
     * @return
     */
    public String getBugzillaServer() {
        return bugzillaServer == null ? null : bugzillaServer.getValue().toString();
    }

    /**
     * @param bugzillaServer
     */
    public void setBugzillaServer(final String bugzillaServer) {
        if (bugzillaServer == null && this.bugzillaServer == null) {
            return;
        } else if (bugzillaServer == null) {
            removeChild(this.bugzillaServer);
            this.bugzillaServer = null;
        } else if (this.bugzillaServer == null) {
            this.bugzillaServer = new KeyValueNode<String>(CommonConstants.CS_BUGZILLA_SERVER_TITLE, bugzillaServer);
            appendChild(this.bugzillaServer, false);
        } else {
            this.bugzillaServer.setValue(bugzillaServer);
        }
    }

    /**
     * Get the URL component that is used in the .ent file when building the Docbook files.
     *
     * @return The BZURL component for the content specification.
     */
    public String getBugzillaURL() {
        return bugzillaURL == null ? null : bugzillaURL.getValue().toString();
    }

    /**
     * Set the URL component that is used in the .ent file when building the Docbook files.
     *
     * @param bugzillaURL The BZURL component to be used when building.
     */
    public void setBugzillaURL(final String bugzillaURL) {
        if (bugzillaURL == null && this.bugzillaURL == null) {
            return;
        } else if (bugzillaURL == null) {
            removeChild(this.bugzillaURL);
            this.bugzillaURL = null;
        } else if (this.bugzillaURL == null) {
            this.bugzillaURL = new KeyValueNode<String>(CommonConstants.CS_BUGZILLA_URL_TITLE, bugzillaURL);
            appendChild(this.bugzillaURL, false);
        } else {
            this.bugzillaURL.setValue(bugzillaURL);
        }
    }

    public boolean isInjectBugLinks() {
        return !getBugLinks().equals(BugLinkType.NONE);
    }

    public BugLinkType getBugLinks() {
        return (bugLinks == null || bugLinks.getValue() == null) ? BugLinkType.BUGZILLA : bugLinks.getValue();
    }

    public BugLinkType getBugLinksActualValue() {
        return bugLinks == null ? null : bugLinks.getValue();
    }

    public void setBugLinks(final BugLinkType bugLinks) {
        if (bugLinks == null && this.bugLinks == null) {
            return;
        } else if (bugLinks == null) {
            removeChild(this.bugLinks);
            this.bugLinks = null;
        } else if (this.bugLinks == null) {
            this.bugLinks = new KeyValueNode<BugLinkType>(CommonConstants.CS_BUG_LINKS_TITLE, bugLinks);
            appendChild(this.bugLinks, false);
        } else {
            this.bugLinks.setValue(bugLinks);
        }
    }

    public boolean isInjectSurveyLinks() {
        return (Boolean) (injectSurveyLinks == null ? false : injectSurveyLinks.getValue());
    }

    public void setInjectSurveyLinks(final Boolean injectSurveyLinks) {
        if (injectSurveyLinks == null && this.injectSurveyLinks == null) {
            return;
        } else if (injectSurveyLinks == null) {
            removeChild(this.injectSurveyLinks);
            this.injectSurveyLinks = null;
        } else if (this.injectSurveyLinks == null) {
            this.injectSurveyLinks = new KeyValueNode<Boolean>(CSConstants.SURVEY_LINK_TITLE, injectSurveyLinks);
            appendChild(this.injectSurveyLinks, false);
        } else {
            this.injectSurveyLinks.setValue(injectSurveyLinks);
        }
    }

    public boolean isInjectBugzillaAssignee() {
        return (Boolean) (injectBugzillaAssignee == null ? true : injectBugzillaAssignee.getValue());
    }

    public void setInjectBugzillaAssignee(final Boolean injectBugzillaAssignee) {
        if (injectBugzillaAssignee == null && this.injectBugzillaAssignee == null) {
            return;
        } else if (injectBugzillaAssignee == null) {
            removeChild(this.injectBugzillaAssignee);
            this.injectBugzillaAssignee = null;
        } else if (this.injectBugzillaAssignee == null) {
            this.injectBugzillaAssignee = new KeyValueNode<Boolean>(CommonConstants.CS_BUGZILLA_ASSIGNEE_TITLE, injectBugzillaAssignee);
            appendChild(this.injectBugzillaAssignee, false);
        } else {
            this.injectBugzillaAssignee.setValue(injectBugzillaAssignee);
        }
    }

    public BugzillaBugLinkOptions getBugzillaBugLinkOptions() {
        final BugzillaBugLinkOptions bzOption = new BugzillaBugLinkOptions();
        bzOption.setProduct(getBugzillaProduct());
        bzOption.setComponent(getBugzillaComponent());
        bzOption.setVersion(getBugzillaVersion());
        bzOption.setBaseUrl(getBugzillaServer() == null ? "https://bugzilla.redhat.com/" : getBugzillaServer());
        bzOption.setBugLinksEnabled(isInjectBugLinks());
        bzOption.setInjectAssignee(isInjectBugzillaAssignee());
        bzOption.setKeywords(getBugzillaKeywords());
        return bzOption;
    }

    public String getJIRAProject() {
        return jiraProject == null ? null : jiraProject.getValue().toString();
    }

    public void setJIRAProject(final String jiraProject) {
        if (jiraProject == null && this.jiraProject == null) {
            return;
        } else if (jiraProject == null) {
            removeChild(this.jiraProject);
            this.jiraProject = null;
        } else if (this.jiraProject == null) {
            this.jiraProject = new KeyValueNode<String>(CommonConstants.CS_JIRA_PROJECT_TITLE, jiraProject);
            appendChild(this.jiraProject, false);
        } else {
            this.jiraProject.setValue(jiraProject);
        }
    }

    public String getJIRAComponent() {
        return jiraComponent == null ? null : jiraComponent.getValue().toString();
    }

    public void setJIRAComponent(final String jiraComponent) {
        if (jiraComponent == null && this.jiraComponent == null) {
            return;
        } else if (jiraComponent == null) {
            removeChild(this.jiraComponent);
            this.jiraComponent = null;
        } else if (this.jiraComponent == null) {
            this.jiraComponent = new KeyValueNode<String>(CommonConstants.CS_JIRA_COMPONENT_TITLE, jiraComponent);
            appendChild(this.jiraComponent, false);
        } else {
            this.jiraComponent.setValue(jiraComponent);
        }
    }

    /**
     * Get the JIRA Version to be applied during building.
     *
     * @return The version of the project in jira.
     */
    public String getJIRAVersion() {
        return jiraVersion == null ? null : jiraVersion.getValue().toString();
    }

    /**
     * Set the JIRA Version to be applied during building.
     *
     * @param jiraVersion The version of the project in jira.
     */
    public void setJIRAVersion(final String jiraVersion) {
        if (jiraVersion == null && this.jiraVersion == null) {
            return;
        } else if (jiraVersion == null) {
            removeChild(this.jiraVersion);
            this.jiraVersion = null;
        } else if (this.jiraVersion == null) {
            this.jiraVersion = new KeyValueNode<String>(CommonConstants.CS_JIRA_VERSION_TITLE, jiraVersion);
            appendChild(this.jiraVersion, false);
        } else {
            this.jiraVersion.setValue(jiraVersion);
        }
    }

    /**
     * Get the JIRA Labels to be applied during building.
     *
     * @return The labels to be set in jira.
     */
    public String getJIRALabels() {
        return jiraLabels == null ? null : jiraLabels.getValue().toString();
    }

    /**
     * Set the JIRA Labels to be applied during building.
     *
     * @param jiraLabels The keywords to be set in jira.
     */
    public void setJIRALabels(final String jiraLabels) {
        if (jiraLabels == null && this.jiraLabels == null) {
            return;
        } else if (jiraLabels == null) {
            removeChild(this.jiraLabels);
            this.jiraLabels = null;
        } else if (this.jiraLabels == null) {
            this.jiraLabels = new KeyValueNode<String>(CommonConstants.CS_JIRA_LABELS_TITLE, jiraLabels);
            appendChild(this.jiraLabels, false);
        } else {
            this.jiraLabels.setValue(jiraLabels);
        }
    }

    /**
     * @return
     */
    public String getJIRAServer() {
        return jiraServer == null ? null : jiraServer.getValue().toString();
    }

    /**
     * @param jiraServer
     */
    public void setJIRAServer(final String jiraServer) {
        if (jiraServer == null && this.jiraServer == null) {
            return;
        } else if (jiraServer == null) {
            removeChild(this.jiraServer);
            this.jiraServer = null;
        } else if (this.jiraServer == null) {
            this.jiraServer = new KeyValueNode<String>(CommonConstants.CS_JIRA_SERVER_TITLE, jiraServer);
            appendChild(this.jiraServer, false);
        } else {
            this.jiraServer.setValue(jiraServer);
        }
    }

    public JIRABugLinkOptions getJIRABugLinkOptions() {
        final JIRABugLinkOptions bzOption = new JIRABugLinkOptions();
        bzOption.setProject(getJIRAProject());
        bzOption.setComponent(getJIRAComponent());
        bzOption.setVersion(getJIRAVersion());
        bzOption.setBaseUrl(getJIRAServer());
        bzOption.setBugLinksEnabled(isInjectBugLinks());
        bzOption.setLabels(getJIRALabels());
        return bzOption;
    }

    /**
     * Gets the Revision History SpecTopic of the Content Specification.
     *
     * @return The SpecTopic for the Revision History.
     */
    public SpecTopic getRevisionHistory() {
        return revisionHistory == null ? null : revisionHistory.getValue();
    }

    /**
     * Sets the SpecTopic of the Revision History for the Content Specification.
     *
     * @param revisionHistory The SpecTopic for the Revision History
     */
    public void setRevisionHistory(final SpecTopic revisionHistory) {
        if (revisionHistory == null && this.revisionHistory == null) {
            return;
        } else if (revisionHistory == null) {
            removeChild(this.revisionHistory);
            this.revisionHistory = null;
        } else if (this.revisionHistory == null) {
            revisionHistory.setTopicType(TopicType.REVISION_HISTORY);
            this.revisionHistory = new KeyValueNode<SpecTopic>(CommonConstants.CS_REV_HISTORY_TITLE, revisionHistory);
            appendChild(this.revisionHistory, false);
        } else {
            revisionHistory.setTopicType(TopicType.REVISION_HISTORY);
            this.revisionHistory.setValue(revisionHistory);
        }
    }

    /**
     * Gets the Feedback SpecTopic of the Content Specification.
     *
     * @return The SpecTopic for the Feedback.
     */
    public SpecTopic getFeedback() {
        return feedback == null ? null : feedback.getValue();
    }

    /**
     * Sets the SpecTopic of the Feedback for the Content Specification.
     *
     * @param feedback The SpecTopic for the Feedback content.
     */
    public void setFeedback(final SpecTopic feedback) {
        if (feedback == null && this.feedback == null) {
            return;
        } else if (feedback == null) {
            removeChild(this.feedback);
            this.feedback = null;
        } else if (this.feedback == null) {
            feedback.setTopicType(TopicType.FEEDBACK);
            this.feedback = new KeyValueNode<SpecTopic>(CommonConstants.CS_FEEDBACK_TITLE, feedback);
            appendChild(this.feedback, false);
        } else {
            feedback.setTopicType(TopicType.FEEDBACK);
            this.feedback.setValue(feedback);
        }
    }

    /**
     * Gets the Legal Notice SpecTopic of the Content Specification.
     *
     * @return The SpecTopic for the Legal Notice.
     */
    public SpecTopic getLegalNotice() {
        return legalNotice == null ? null : legalNotice.getValue();
    }

    /**
     * Sets the SpecTopic of the Legal Notice for the Content Specification.
     *
     * @param legalNotice The SpecTopic for the Legal Notice.
     */
    public void setLegalNotice(final SpecTopic legalNotice) {
        if (legalNotice == null && this.legalNotice == null) {
            return;
        } else if (legalNotice == null) {
            removeChild(this.legalNotice);
            this.legalNotice = null;
        } else if (this.legalNotice == null) {
            legalNotice.setTopicType(TopicType.LEGAL_NOTICE);
            this.legalNotice = new KeyValueNode<SpecTopic>(CommonConstants.CS_LEGAL_NOTICE_TITLE, legalNotice);
            appendChild(this.legalNotice, false);
        } else {
            legalNotice.setTopicType(TopicType.LEGAL_NOTICE);
            this.legalNotice.setValue(legalNotice);
        }
    }

    /**
     * Gets the Author Group SpecTopic of the Content Specification.
     *
     * @return The SpecTopic for the Author Group.
     */
    public SpecTopic getAuthorGroup() {
        return authorGroup == null ? null : authorGroup.getValue();
    }

    /**
     * Sets the SpecTopic of the Author Group for the Content Specification.
     *
     * @param authorGroup The SpecTopic for the Author Group.
     */
    public void setAuthorGroup(final SpecTopic authorGroup) {
        if (authorGroup == null && this.authorGroup == null) {
            return;
        } else if (authorGroup == null) {
            removeChild(this.authorGroup);
            this.authorGroup = null;
        } else if (this.authorGroup == null) {
            authorGroup.setTopicType(TopicType.AUTHOR_GROUP);
            this.authorGroup = new KeyValueNode<SpecTopic>(CommonConstants.CS_AUTHOR_GROUP_TITLE, authorGroup);
            appendChild(this.authorGroup, false);
        } else {
            authorGroup.setTopicType(TopicType.AUTHOR_GROUP);
            this.authorGroup.setValue(authorGroup);
        }
    }

    /**
     * Get the Maven groupId that is used in the pom.xml file when building the jDocbook files.
     *
     * @return The Maven groupId for the content specification.
     */
    public String getGroupId() {
        return groupId == null ? null : groupId.getValue().toString();
    }

    /**
     * Set the Maven groupId that is used in the pom.xml file when building the jDocbook files.
     *
     * @param groupId The Maven groupId to be used when building.
     */
    public void setGroupId(final String groupId) {
        if (groupId == null && this.groupId == null) {
            return;
        } else if (groupId == null) {
            removeChild(this.groupId);
            this.groupId = null;
        } else if (this.groupId == null) {
            this.groupId = new KeyValueNode<String>(CommonConstants.CS_MAVEN_GROUP_ID_TITLE, groupId);
            appendChild(this.groupId, false);
        } else {
            this.groupId.setValue(groupId);
        }
    }

    /**
     * Get the Maven artifactId that is used in the pom.xml file when building the jDocbook files.
     *
     * @return The Maven artifactId for the content specification.
     */
    public String getArtifactId() {
        return artifactId == null ? null : artifactId.getValue().toString();
    }

    /**
     * Set the Maven artifactId that is used in the pom.xml file when building the jDocbook files.
     *
     * @param artifactId The Maven artifactId to be used when building.
     */
    public void setArtifactId(final String artifactId) {
        if (artifactId == null && this.artifactId == null) {
            return;
        } else if (artifactId == null) {
            removeChild(this.artifactId);
            this.artifactId = null;
        } else if (this.artifactId == null) {
            this.artifactId = new KeyValueNode<String>(CommonConstants.CS_MAVEN_ARTIFACT_ID_TITLE, artifactId);
            appendChild(this.artifactId, false);
        } else {
            this.artifactId.setValue(artifactId);
        }
    }

    /**
     * Gets the list of additional files needed by the book.
     *
     * @return The list of additional Files.
     */
    public List<File> getFiles() {
        return files == null ? null : files.getValue();
    }

    /**
     * Gets the list of additional files needed by the book.
     *
     * @return The list of additional Files.
     */
    public FileList getFileList() {
        return files;
    }

    /**
     * Sets the list of additional files needed by the book.
     *
     * @param files The list of additional Files.
     */
    public void setFiles(final List<File> files) {
        if (files == null && this.files == null) {
            return;
        } else if (files == null) {
            removeChild(this.files);
            this.files = null;
        } else if (this.files == null) {
            this.files = new FileList(CommonConstants.CS_FILE_TITLE, files);
            appendChild(this.files, false);
        } else {
            this.files.setValue(files);
        }
    }

    /**
     * Gets the data what will be appended to the &lt;book&gt;.ent file when built.
     *
     * @return The data to be appended or null if none exist.
     */
    public String getEntities() {
        return entities == null ? null : entities.getValue();
    }

    /**
     * Set the data that will be appended to the &lt;book&gt;.ent file when built.
     *
     * @param entities The data to be appended.
     */
    public void setEntities(final String entities) {
        if (entities == null && this.entities == null) {
            return;
        } else if (entities == null) {
            removeChild(this.entities);
            this.entities = null;
        } else if (this.entities == null) {
            this.entities = new KeyValueNode<String>(CommonConstants.CS_ENTITIES_TITLE, entities);
            appendChild(this.entities, false);
        } else {
            this.entities.setValue(entities);
        }
    }

    /**
     * Adds a Child node to the Content Spec. If the Child node already has a parent, then it is removed from that parent and added
     * to this content spec.
     *
     * @param child A Child Node to be added to the ContentSpec.
     */
    public void appendChild(final Node child) {
        appendChild(child, true);
    }

    /**
     * Adds a Child node to the Content Spec. If the Child node already has a parent, then it is removed from that parent and added
     * to this content spec.
     *
     * @param child        A Child Node to be added to the ContentSpec.
     * @param checkForType If the method should check the type of the child, and use a type specific method instead.
     */
    protected void appendChild(final Node child, boolean checkForType) {
        if (checkForType && child instanceof KeyValueNode) {
            appendKeyValueNode((KeyValueNode<?>) child);
        } else if (checkForType && child instanceof Level) {
            getBaseLevel().appendChild(child);
        } else if (checkForType && child instanceof SpecTopic) {
            getBaseLevel().appendChild(child);
        } else {
            nodes.add(child);
            if (child.getParent() != null) {
                child.removeParent();
            }
            child.setParent(this);
        }
    }

    /**
     * Removes a child node from the content spec and removes the content as the childs parent.
     *
     * @param child The Child Node to be removed from the Content Spec.
     */
    public void removeChild(final Node child) {
        nodes.remove(child);
        child.setParent(null);
    }

    /**
     * Appends a KeyValueNode to the content specification. This method will also set the appropriate getter/setter for all metadata
     * values.
     *
     * @param node The KeyValue node to be added.
     * @throws NumberFormatException Throw if the node needs an Integer but the value is not a valid Number string.
     */
    public void appendKeyValueNode(final KeyValueNode<?> node) throws NumberFormatException {
        final String key = node.getKey();
        final Object value = node.getValue();

        KeyValueNode<?> fixedNode = node;

        if (key.equalsIgnoreCase(CommonConstants.CS_TITLE_TITLE) && value instanceof String) {
            title = (KeyValueNode<String>) node;
            setKeyValueNodeKey(title, CommonConstants.CS_TITLE_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_ID_TITLE) && (value instanceof String || value instanceof Integer)) {
            final KeyValueNode<Integer> idNode;
            if (value instanceof String) {
                idNode = new KeyValueNode<Integer>(CommonConstants.CS_ID_TITLE, Integer.parseInt((String) value));
                cloneKeyValueNode(node, idNode);
                fixedNode = idNode;
            } else {
                idNode = (KeyValueNode<Integer>) node;
            }
            id = idNode;
            setKeyValueNodeKey(id, CommonConstants.CS_ID_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_CHECKSUM_TITLE) && value instanceof String) {
            checksum = (KeyValueNode<String>) node;
            setKeyValueNodeKey(checksum, CommonConstants.CS_CHECKSUM_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_PRODUCT_TITLE) && value instanceof String) {
            product = (KeyValueNode<String>) node;
            setKeyValueNodeKey(product, CommonConstants.CS_PRODUCT_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_VERSION_TITLE) && value instanceof String) {
            version = (KeyValueNode<String>) node;
            setKeyValueNodeKey(version, CommonConstants.CS_VERSION_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_BOOK_TYPE_TITLE) && (value instanceof String || value instanceof BookType)) {
            final KeyValueNode<BookType> bookTypeNode;
            if (value instanceof String) {
                bookTypeNode = new KeyValueNode<BookType>(key, BookType.getBookType((String) value));
                cloneKeyValueNode(node, bookTypeNode);
                fixedNode = bookTypeNode;
            } else {
                bookTypeNode = (KeyValueNode<BookType>) node;
            }
            bookType = bookTypeNode;
            setKeyValueNodeKey(bookType, CommonConstants.CS_BOOK_TYPE_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_EDITION_TITLE) && value instanceof String) {
            edition = (KeyValueNode<String>) node;
            setKeyValueNodeKey(edition, CommonConstants.CS_EDITION_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_BOOK_VERSION_TITLE) && value instanceof String) {
            bookVersion = (KeyValueNode<String>) node;
            setKeyValueNodeKey(bookVersion, CommonConstants.CS_BOOK_VERSION_TITLE);
        } else if (key.equalsIgnoreCase(
                CommonConstants.CS_BUG_LINKS_TITLE) && (value instanceof String || value instanceof BugLinkType)) {
            final KeyValueNode<BugLinkType> bugLinkNode;
            if (value instanceof String) {
                bugLinkNode = new KeyValueNode<BugLinkType>(CommonConstants.CS_BUG_LINKS_TITLE, BugLinkType.getType((String) value));
                cloneKeyValueNode(node, bugLinkNode);
                fixedNode = bugLinkNode;
            } else {
                bugLinkNode = (KeyValueNode<BugLinkType>) value;
            }
            bugLinks = bugLinkNode;
            setKeyValueNodeKey(bugLinks, CommonConstants.CS_BUG_LINKS_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_BUGZILLA_COMPONENT_TITLE) && value instanceof String) {
            bugzillaComponent = (KeyValueNode<String>) node;
            setKeyValueNodeKey(bugzillaComponent, CommonConstants.CS_BUGZILLA_COMPONENT_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_BUGZILLA_PRODUCT_TITLE) && value instanceof String) {
            bugzillaProduct = (KeyValueNode<String>) node;
            setKeyValueNodeKey(bugzillaProduct, CommonConstants.CS_BUGZILLA_PRODUCT_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_BUGZILLA_VERSION_TITLE) && value instanceof String) {
            bugzillaVersion = (KeyValueNode<String>) node;
            setKeyValueNodeKey(bugzillaVersion, CommonConstants.CS_BUGZILLA_VERSION_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_BUGZILLA_KEYWORDS_TITLE) && value instanceof String) {
            bugzillaKeywords = (KeyValueNode<String>) node;
            setKeyValueNodeKey(bugzillaKeywords, CommonConstants.CS_BUGZILLA_KEYWORDS_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_BUGZILLA_SERVER_TITLE) && value instanceof String) {
            bugzillaServer = (KeyValueNode<String>) node;
            setKeyValueNodeKey(bugzillaServer, CommonConstants.CS_BUGZILLA_SERVER_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_BUGZILLA_URL_TITLE) && value instanceof String) {
            bugzillaURL = (KeyValueNode<String>) node;
            setKeyValueNodeKey(bugzillaURL, CommonConstants.CS_BUGZILLA_URL_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_BUGZILLA_ASSIGNEE_TITLE) && (value instanceof String || value instanceof Boolean)) {
            final KeyValueNode<Boolean> injectBugzillaAssigneeNode;
            if (value instanceof String) {
                final Boolean fixedValue;
                if (((String) value).equalsIgnoreCase("ON")) {
                    fixedValue = true;
                } else {
                    fixedValue = Boolean.parseBoolean((String) value);
                }
                injectBugzillaAssigneeNode = new KeyValueNode<Boolean>(CommonConstants.CS_BUGZILLA_ASSIGNEE_TITLE, fixedValue);
                cloneKeyValueNode(node, injectBugzillaAssigneeNode);
                fixedNode = injectBugzillaAssigneeNode;
            } else {
                injectBugzillaAssigneeNode = (KeyValueNode<Boolean>) node;
            }
            injectBugzillaAssignee = injectBugzillaAssigneeNode;
            setKeyValueNodeKey(injectBugzillaAssignee, CommonConstants.CS_BUGZILLA_ASSIGNEE_TITLE);
        } else if (key.equalsIgnoreCase(
                CommonConstants.CS_INLINE_INJECTION_TITLE) && (value instanceof String || value instanceof InjectionOptions)) {
            final KeyValueNode<InjectionOptions> injectionOptionsNode;
            if (value instanceof String) {
                injectionOptionsNode = new KeyValueNode<InjectionOptions>(CommonConstants.CS_INLINE_INJECTION_TITLE,
                        new InjectionOptions((String) value));
                cloneKeyValueNode(node, injectionOptionsNode);
                fixedNode = injectionOptionsNode;
            } else {
                injectionOptionsNode = (KeyValueNode<InjectionOptions>) node;
            }
            injectionOptions = injectionOptionsNode;
            setKeyValueNodeKey(injectionOptions, CommonConstants.CS_INLINE_INJECTION_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_DTD_TITLE) && value instanceof String) {
            dtd = (KeyValueNode<String>) node;
            setKeyValueNodeKey(dtd, CommonConstants.CS_DTD_TITLE);
        } else if (key.equalsIgnoreCase(CSConstants.OUTPUT_STYLE_TITLE) && value instanceof String) {
            outputStyle = (KeyValueNode<String>) node;
            setKeyValueNodeKey(outputStyle, CSConstants.OUTPUT_STYLE_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_PUBSNUMBER_TITLE) && (value instanceof String || value instanceof Integer)) {
            final KeyValueNode<Integer> pubsNumberNode;
            if (value instanceof String) {
                pubsNumberNode = new KeyValueNode<Integer>(key, Integer.parseInt((String) value));
                cloneKeyValueNode(node, pubsNumberNode);
                fixedNode = pubsNumberNode;
            } else {
                pubsNumberNode = (KeyValueNode<Integer>) node;
            }
            pubsNumber = pubsNumberNode;
            setKeyValueNodeKey(pubsNumber, CommonConstants.CS_PUBSNUMBER_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_PUBLICAN_CFG_TITLE) && value instanceof String) {
            publicanCfg = (KeyValueNode<String>) node;
            setKeyValueNodeKey(publicanCfg, CommonConstants.CS_PUBLICAN_CFG_TITLE);
        } else if (key.equalsIgnoreCase(CSConstants.SURVEY_LINK_TITLE) && (value instanceof String || value instanceof Boolean)) {
            final KeyValueNode<Boolean> injectSurveyLinkNode;
            if (value instanceof String) {
                final Boolean fixedValue;
                if (((String) value).equalsIgnoreCase("ON")) {
                    fixedValue = true;
                } else {
                    fixedValue = Boolean.parseBoolean((String) value);
                }
                injectSurveyLinkNode = new KeyValueNode<Boolean>(CSConstants.SURVEY_LINK_TITLE, fixedValue);
                cloneKeyValueNode(node, injectSurveyLinkNode);
                fixedNode = injectSurveyLinkNode;
            } else {
                injectSurveyLinkNode = (KeyValueNode<Boolean>) node;
            }
            injectSurveyLinks = injectSurveyLinkNode;
            setKeyValueNodeKey(injectSurveyLinks, CSConstants.SURVEY_LINK_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_BRAND_TITLE) && value instanceof String) {
            brand = (KeyValueNode<String>) node;
            setKeyValueNodeKey(brand, CommonConstants.CS_BRAND_TITLE);
        } else if ((key.equalsIgnoreCase(CommonConstants.CS_ABSTRACT_TITLE) || key.equalsIgnoreCase(
                CommonConstants.CS_ABSTRACT_ALTERNATE_TITLE)) && value instanceof String) {
            description = (KeyValueNode<String>) node;
            setKeyValueNodeKey(description, CommonConstants.CS_ABSTRACT_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_COPYRIGHT_HOLDER_TITLE) && value instanceof String) {
            copyrightHolder = (KeyValueNode<String>) node;
            setKeyValueNodeKey(copyrightHolder, CommonConstants.CS_COPYRIGHT_HOLDER_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_COPYRIGHT_YEAR_TITLE) && value instanceof String) {
            copyrightYear = (KeyValueNode<String>) node;
            setKeyValueNodeKey(copyrightYear, CommonConstants.CS_COPYRIGHT_YEAR_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_SUBTITLE_TITLE) && value instanceof String) {
            subtitle = (KeyValueNode<String>) node;
            setKeyValueNodeKey(subtitle, CommonConstants.CS_SUBTITLE_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_REV_HISTORY_TITLE) && value instanceof SpecTopic) {
            revisionHistory = (KeyValueNode<SpecTopic>) node;
            if (value != null) {
                revisionHistory.getValue().setTopicType(TopicType.REVISION_HISTORY);
            }
            setKeyValueNodeKey(revisionHistory, CommonConstants.CS_REV_HISTORY_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_FEEDBACK_TITLE) && value instanceof SpecTopic) {
            feedback = (KeyValueNode<SpecTopic>) node;
            if (value != null) {
                feedback.getValue().setTopicType(TopicType.FEEDBACK);
            }
            setKeyValueNodeKey(feedback, CommonConstants.CS_FEEDBACK_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_LEGAL_NOTICE_TITLE) && value instanceof SpecTopic) {
            legalNotice = (KeyValueNode<SpecTopic>) node;
            if (value != null) {
                legalNotice.getValue().setTopicType(TopicType.LEGAL_NOTICE);
            }
            setKeyValueNodeKey(legalNotice, CommonConstants.CS_LEGAL_NOTICE_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_AUTHOR_GROUP_TITLE) && value instanceof SpecTopic) {
            authorGroup = (KeyValueNode<SpecTopic>) node;
            if (value != null) {
                authorGroup.getValue().setTopicType(TopicType.AUTHOR_GROUP);
            }
            setKeyValueNodeKey(authorGroup, CommonConstants.CS_AUTHOR_GROUP_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_MAVEN_ARTIFACT_ID_TITLE) && value instanceof String) {
            artifactId = (KeyValueNode<String>) node;
            setKeyValueNodeKey(artifactId, CommonConstants.CS_MAVEN_ARTIFACT_ID_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_MAVEN_GROUP_ID_TITLE) && value instanceof String) {
            groupId = (KeyValueNode<String>) node;
            setKeyValueNodeKey(groupId, CommonConstants.CS_MAVEN_GROUP_ID_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_BRAND_LOGO_TITLE) && value instanceof String) {
            brandLogo = (KeyValueNode<String>) node;
            setKeyValueNodeKey(brandLogo, CommonConstants.CS_BRAND_LOGO_TITLE);
        } else if ((key.equalsIgnoreCase(CommonConstants.CS_FILE_TITLE) || key.equalsIgnoreCase(
                CommonConstants.CS_FILE_SHORT_TITLE)) && node instanceof FileList) {
            files = (FileList) node;
            setKeyValueNodeKey(files, CommonConstants.CS_FILE_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_JIRA_COMPONENT_TITLE) && value instanceof String) {
            jiraComponent = (KeyValueNode<String>) node;
            setKeyValueNodeKey(jiraComponent, CommonConstants.CS_JIRA_COMPONENT_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_JIRA_PROJECT_TITLE) && value instanceof String) {
            jiraProject = (KeyValueNode<String>) node;
            setKeyValueNodeKey(jiraProject, CommonConstants.CS_JIRA_PROJECT_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_JIRA_VERSION_TITLE) && value instanceof String) {
            jiraVersion = (KeyValueNode<String>) node;
            setKeyValueNodeKey(jiraVersion, CommonConstants.CS_JIRA_VERSION_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_JIRA_LABELS_TITLE) && value instanceof String) {
            jiraLabels = (KeyValueNode<String>) node;
            setKeyValueNodeKey(jiraLabels, CommonConstants.CS_JIRA_LABELS_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_JIRA_SERVER_TITLE) && value instanceof String) {
            jiraServer = (KeyValueNode<String>) node;
            setKeyValueNodeKey(jiraServer, CommonConstants.CS_JIRA_SERVER_TITLE);
        } else if (key.equalsIgnoreCase(CommonConstants.CS_ENTITIES_TITLE) && value instanceof String) {
            entities = (KeyValueNode<String>) node;
            setKeyValueNodeKey(entities, CommonConstants.CS_ENTITIES_TITLE);
        }

        // Add the node to the list of nodes
        appendChild(fixedNode, false);
    }

    private void setKeyValueNodeKey(final KeyValueNode<?> node, final String key) {
        if (node == null) {
            return;
        } else {
            node.setKey(key);
        }
    }

    private void cloneKeyValueNode(final KeyValueNode<?> in, final KeyValueNode<?> out) {
        out.setTranslationUniqueId(in.getTranslationUniqueId());
        out.setUniqueId(in.getUniqueId());
        out.setParent(in.getParent());
        out.setText(in.getText());
    }

    /**
     * Returns a String representation of the Content Specification.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean includeChecksum) {
        final StringBuilder output = new StringBuilder();
        for (final Node node : nodes) {
            if (node instanceof KeyValueNode) {
                final KeyValueNode keyValueNode = (KeyValueNode) node;
                if (!keyValueNode.getKey().equals(CommonConstants.CS_CHECKSUM_TITLE) && !keyValueNode.getKey().equals(CommonConstants.CS_ID_TITLE)) {
                    output.append(node.toString());
                }
            } else {
                output.append(node.toString());
            }
        }

        // Add any global options
        String options = level.getOptionsString();
        if (!options.equals("")) {
            output.append("[" + options + "]\n");
        }

        // Append the String representation of each level
        output.append(level.toString());

        // If the id isn't null then add the id and checksum
        if (getId() != null) {
            if (includeChecksum) {
                output.insert(0, CommonConstants.CS_CHECKSUM_TITLE + " = " + HashUtilities.generateMD5(
                    CommonConstants.CS_ID_TITLE + " = " + id.getValue() + "\n" + output) + "\n" + CommonConstants.CS_ID_TITLE + " = " + id.getValue() +
                    "\n");
            } else {
                output.insert(0, CommonConstants.CS_ID_TITLE + " = " + id.getValue() + "\n");
            }
        }
        return output.toString();
    }

    @Override
    public Integer getStep() {
        return null;
    }

    @Override
    protected void removeParent() {
        return;
    }
}
