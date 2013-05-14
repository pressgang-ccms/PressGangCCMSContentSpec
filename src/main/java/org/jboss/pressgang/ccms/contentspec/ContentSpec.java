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
import java.util.Locale;
import java.util.Map;

import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;
import org.jboss.pressgang.ccms.contentspec.entities.BugzillaOptions;
import org.jboss.pressgang.ccms.contentspec.entities.InjectionOptions;
import org.jboss.pressgang.ccms.contentspec.entities.Relationship;
import org.jboss.pressgang.ccms.contentspec.enums.BookType;
import org.jboss.pressgang.ccms.contentspec.enums.LevelType;
import org.jboss.pressgang.ccms.utils.common.DocBookUtilities;
import org.jboss.pressgang.ccms.utils.common.HashUtilities;

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
    private KeyValueNode<String> bugzillaURL = null;
    private KeyValueNode<Boolean> injectBugLinks = null;
    private KeyValueNode<Boolean> injectSurveyLinks = null;
    private KeyValueNode<String> outputStyle = null;
    private KeyValueNode<Boolean> allowDuplicateTopics = null;
    private KeyValueNode<Boolean> allowEmptyLevels = null;
    private KeyValueNode<BookType> bookType = null;
    private KeyValueNode<SpecTopic> revisionHistory = null;
    private KeyValueNode<SpecTopic> feedback = null;
    private KeyValueNode<SpecTopic> legalNotice = null;
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
            this.product = new KeyValueNode<String>(CSConstants.PRODUCT_TITLE, product);
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
        return version == null ? "" : version.getValue();
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
            this.version = new KeyValueNode<String>(CSConstants.VERSION_TITLE, version);
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
            this.brand = new KeyValueNode<String>(CSConstants.BRAND_TITLE, brand);
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
            this.id = new KeyValueNode<Integer>(CSConstants.ID_TITLE, id);
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
            this.title = new KeyValueNode<String>(CSConstants.TITLE_TITLE, title);
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
            this.subtitle = new KeyValueNode<String>(CSConstants.SUBTITLE_TITLE, subtitle);
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
            this.bookVersion = new KeyValueNode<String>(CSConstants.BOOK_VERSION_TITLE, bookVersion);
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
            this.edition = new KeyValueNode<String>(CSConstants.EDITION_TITLE, edition);
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
            this.pubsNumber = new KeyValueNode<Integer>(CSConstants.PUBSNUMBER_TITLE, pubsNumber);
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
            this.publicanCfg = new KeyValueNode<String>(CSConstants.PUBLICAN_CFG_TITLE, publicanCfg);
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
            this.dtd = new KeyValueNode<String>(CSConstants.DTD_TITLE, dtd);
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
            this.checksum = new KeyValueNode<String>(CSConstants.CHECKSUM_TITLE, checksum);
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
            this.description = new KeyValueNode<String>(CSConstants.ABSTRACT_TITLE, description);
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
            this.copyrightHolder = new KeyValueNode<String>(CSConstants.COPYRIGHT_HOLDER_TITLE, copyrightHolder);
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
            this.copyrightYear = new KeyValueNode<String>(CSConstants.COPYRIGHT_YEAR_TITLE, copyrightYear);
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
            this.bookType = new KeyValueNode<BookType>(CSConstants.BOOK_TYPE_TITLE, bookType);
            appendChild(this.bookType, false);
        } else {
            this.bookType.setValue(bookType);
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
            this.injectionOptions = new KeyValueNode<InjectionOptions>(CSConstants.INLINE_INJECTION_TITLE, injectionOptions);
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
        return getLevelSpecTopics(getBaseLevel());
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
            this.bugzillaProduct = new KeyValueNode<String>(CSConstants.BUGZILLA_PRODUCT_TITLE, bugzillaProduct);
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
            this.bugzillaComponent = new KeyValueNode<String>(CSConstants.BUGZILLA_COMPONENT_TITLE, bugzillaComponent);
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
            this.bugzillaVersion = new KeyValueNode<String>(CSConstants.BUGZILLA_VERSION_TITLE, bugzillaVersion);
            appendChild(this.bugzillaVersion, false);
        } else {
            this.bugzillaVersion.setValue(bugzillaVersion);
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
            this.bugzillaURL = new KeyValueNode<String>(CSConstants.BUGZILLA_URL_TITLE, bugzillaURL);
            appendChild(this.bugzillaURL, false);
        } else {
            this.bugzillaURL.setValue(bugzillaURL);
        }
    }

    public boolean isInjectBugLinks() {
        return (Boolean) (injectBugLinks == null ? true : injectBugLinks.getValue());
    }

    public void setInjectBugLinks(final Boolean injectBugLinks) {
        if (injectBugLinks == null && this.injectBugLinks == null) {
            return;
        } else if (injectBugLinks == null) {
            removeChild(this.injectBugLinks);
            this.injectBugLinks = null;
        } else if (this.injectBugLinks == null) {
            this.injectBugLinks = new KeyValueNode<Boolean>(CSConstants.BUG_LINKS_TITLE, injectBugLinks);
            appendChild(this.injectBugLinks, false);
        } else {
            this.injectBugLinks.setValue(injectBugLinks);
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

    public BugzillaOptions getBugzillaOptions() {
        final BugzillaOptions bzOption = new BugzillaOptions();
        bzOption.setProduct(getBugzillaProduct());
        bzOption.setComponent(getBugzillaComponent());
        bzOption.setVersion(getBugzillaVersion());
        bzOption.setUrlComponent(getBugzillaURL());
        bzOption.setBugzillaLinksEnabled(isInjectBugLinks());
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
            this.revisionHistory = new KeyValueNode<SpecTopic>(CSConstants.REV_HISTORY_TITLE, revisionHistory);
            appendChild(this.revisionHistory, false);
        } else {
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
            this.feedback = new KeyValueNode<SpecTopic>(CSConstants.FEEDBACK_TITLE, feedback);
            appendChild(this.feedback, false);
        } else {
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
            this.legalNotice = new KeyValueNode<SpecTopic>(CSConstants.LEGAL_NOTICE, legalNotice);
            appendChild(this.legalNotice, false);
        } else {
            this.legalNotice.setValue(legalNotice);
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
        final String uppercaseKey = node.getKey().toUpperCase(Locale.ENGLISH);
        Object value = node.getValue();
        if (uppercaseKey.equals(CSConstants.TITLE_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setTitle((String) value);
        } else if (uppercaseKey.equals(CSConstants.ID_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setId(Integer.parseInt((String) value));
        } else if (uppercaseKey.equals(CSConstants.CHECKSUM_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setChecksum((String) value);
        } else if (uppercaseKey.equals(CSConstants.PRODUCT_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setProduct((String) value);
        } else if (uppercaseKey.equals(CSConstants.VERSION_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setVersion((String) value);
        } else if (uppercaseKey.equals(CSConstants.BOOK_TYPE_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setBookType(BookType.getBookType((String) value));
        } else if (uppercaseKey.equals(CSConstants.EDITION_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setEdition((String) value);
        } else if (uppercaseKey.equals(CSConstants.BOOK_VERSION_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setBookVersion((String) value);
        } else if (uppercaseKey.equals(CSConstants.BUG_LINKS_TITLE.toUpperCase(Locale.ENGLISH)) && (value instanceof String || value
                instanceof Boolean)) {
            if (value instanceof Boolean) {
                setInjectBugLinks((Boolean) value);
            } else {
                if (((String) value).toUpperCase(Locale.ENGLISH).equals("ON")) {
                    setInjectBugLinks(true);
                } else {
                    setInjectBugLinks(Boolean.parseBoolean((String) value));
                }
            }
        } else if (uppercaseKey.equals(CSConstants.BUGZILLA_COMPONENT_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setBugzillaComponent((String) value);
        } else if (uppercaseKey.equals(CSConstants.BUGZILLA_PRODUCT_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setBugzillaProduct((String) value);
        } else if (uppercaseKey.equals(CSConstants.BUGZILLA_VERSION_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setBugzillaVersion((String) value);
        } else if (uppercaseKey.equals(CSConstants.BUGZILLA_URL_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setBugzillaURL((String) value);
        } else if (uppercaseKey.equals(CSConstants.INLINE_INJECTION_TITLE.toUpperCase(Locale.ENGLISH)) && (value instanceof String || value
                instanceof InjectionOptions)) {
            if (value instanceof String) {
                setInjectionOptions(new InjectionOptions((String) value));
            } else {
                setInjectionOptions((InjectionOptions) value);
            }
        } else if (uppercaseKey.equals(CSConstants.DTD_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setDtd((String) value);
        } else if (uppercaseKey.equals(CSConstants.OUTPUT_STYLE_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setOutputStyle((String) value);
        } else if (uppercaseKey.equals(CSConstants.PUBSNUMBER_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setPubsNumber(Integer.parseInt((String) value));
        } else if (uppercaseKey.equals(CSConstants.PUBLICAN_CFG_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setPublicanCfg((String) value);
        } else if (uppercaseKey.equals(CSConstants.SURVEY_LINK_TITLE.toUpperCase(Locale.ENGLISH)) && (value instanceof String || value
                instanceof Boolean)) {
            if (value instanceof Boolean) {
                setInjectSurveyLinks((Boolean) value);
            } else {
                if (((String) value).toUpperCase(Locale.ENGLISH).equals("ON")) {
                    setInjectSurveyLinks(true);
                } else {
                    setInjectSurveyLinks(Boolean.parseBoolean((String) value));
                }
            }
        } else if (uppercaseKey.equals(CSConstants.BRAND_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setBrand((String) value);
        } else if (uppercaseKey.equals(CSConstants.ABSTRACT_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setAbstract((String) value);
        } else if (uppercaseKey.equals(CSConstants.COPYRIGHT_HOLDER_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setCopyrightHolder((String) value);
        } else if (uppercaseKey.equals(CSConstants.COPYRIGHT_YEAR_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setCopyrightYear((String) value);
        } else if (uppercaseKey.equals(CSConstants.SUBTITLE_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof String) {
            setSubtitle((String) value);
        } else if (uppercaseKey.equals(CSConstants.REV_HISTORY_TITLE.toUpperCase(Locale.ENGLISH)) && value instanceof SpecTopic) {
            setRevisionHistory((SpecTopic) value);
        } else {
            appendChild(node, false);
        }
    }

    /**
     * Returns a String representation of the Content Specification.
     */
    @SuppressWarnings("rawtypes")
    @Override
    public String toString() {
        final StringBuilder output = new StringBuilder();
        for (final Node node : nodes) {
            if (node instanceof KeyValueNode) {
                final KeyValueNode keyValueNode = (KeyValueNode) node;
                if (!keyValueNode.getKey().equals(CSConstants.CHECKSUM_TITLE) && !keyValueNode.getKey().equals(CSConstants.ID_TITLE)) {
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
            output.insert(0, CSConstants.CHECKSUM_TITLE + "=" + HashUtilities.generateMD5(
                    CSConstants.ID_TITLE + " = " + id.getValue() + "\n" + output) + "\n" + CSConstants.ID_TITLE + " = " + id.getValue() +
                    "\n");
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
