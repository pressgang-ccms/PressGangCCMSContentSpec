package org.jboss.pressgang.ccms.contentspec.constants;

public class CSConstants {

    public static final String BUGZILLA_URL_PROPERTY = "contentSpec.bugzillaUrl";

    // Level Type Constants
    public static final int LEVEL_BASE = -1;

    // Other
    public static final int CSP_PROPERTY_ID = 15;
    public static final String CSP_PRE_PROCESSED_STRING = "Pre Processed";
    public static final String CSP_POST_PROCESSED_STRING = "Post Processed";

    // Output format constants
    public static final String PRESSGANG_OUTPUT_FORMAT = "Cloud";
    public static final String CSP_OUTPUT_FORMAT = "Narrative";

    public static final Integer TYPE_CATEGORY_ID = 4;
    public static final Integer TECHNOLOGY_CATEGORY_ID = 3;
    public static final Integer RELEASE_CATEGORY_ID = 15;
    public static final Integer WRITER_CATEGORY_ID = 12;
    public static final Integer COMMON_NAME_CATEGORY_ID = 17;
    public static final String TECHNOLOGY_CATEGORY_NAME = "Technologies";
    public static final Integer CONCERN_CATEGORY_ID = 2;
    public static final String CONCERN_CATEGORY_NAME = "Concerns";
    public static final Integer LIFECYCLE_CATEGORY_ID = 5;
    public static final Integer INFORMATION_SENSITIVITY_CATEGORY_ID = 19;
    public static final Integer SEO_METADATA_CATEGORY_ID = 24;
    public static final Integer CONTENT_TYPE_CATEGORY_ID = 23;
    public static final Integer PROGRAMMING_LANGUAGE_CATEGORY_ID = 22;

    public static final Integer RH_INTERNAL_TAG_ID = 315;

    public static final Integer UNKNOWN_USER_ID = 89;

    public static final String NEW_TOPIC_ID_REGEX = "^N[0-9]*$";
    public static final String EXISTING_TOPIC_ID_REGEX = "^[0-9]+$";
    public static final String CLONED_TOPIC_ID_REGEX = "^C[0-9]+$";
    public static final String DUPLICATE_TOPIC_ID_REGEX = "^X[0-9]+$";
    public static final String CLONED_DUPLICATE_TOPIC_ID_REGEX = "^XC[0-9]+$";
    public static final String ALL_TOPIC_ID_REGEX = "(^N[0-9]*$)|(^[0-9]+$)|(^X[0-9]+$)|(^C[0-9]+$)|(^XC[0-9]+$)";

    public static final String CHAPTER = "Chapter";
    public static final String APPENDIX = "Appendix";
    public static final String SECTION = "Section";
    public static final String PROCESS = "Process";
    public static final String PART = "Part";
    public static final String PREFACE = "Preface";

    public static final String BOOK_TYPE_BOOK = "Book";
    public static final String BOOK_TYPE_ARTICLE = "Article";
    public static final String BOOK_TYPE_BOOK_DRAFT = "Book-Draft";
    public static final String BOOK_TYPE_ARTICLE_DRAFT = "Article-Draft";

    /**
     * The Concept tag ID
     */
    public static final Integer CONCEPT_TAG_ID = 5;
    /**
     * The Concept tag name
     */
    public static final String CONCEPT_TAG_NAME = "Concept";
    /**
     * The Conceptual Overview tag ID
     */
    public static final Integer CONCEPTUALOVERVIEW_TAG_ID = 93;
    /**
     * The Conceptual Overview tag name
     */
    public static final String CONCEPTUALOVERVIEW_TAG_NAME = "Overview";
    /**
     * The Reference tag ID
     */
    public static final Integer REFERENCE_TAG_ID = 6;
    /**
     * The Reference tag name
     */
    public static final String REFERENCE_TAG_NAME = "Reference";
    /**
     * The Task tag ID
     */
    public static final Integer TASK_TAG_ID = 4;
    /**
     * The Task tag name
     */
    public static final String TASK_TAG_NAME = "Task";
    /**
     * The Written tag ID
     */
    public static final Integer WRITTEN_TAG_ID = 19;
    /**
     * The Tag Description tag ID
     */
    public static final Integer TAG_DESCRIPTION_TAG_ID = 215;
    /**
     * The Home tag ID
     */
    public static final Integer HOME_TAG_ID = 216;
    /**
     * The Content Specification tag ID
     */
    public static final Integer CONTENT_SPEC_TAG_ID = 268;
    /**
     * The Content Specification tag name
     */
    public static final String CONTENT_SPEC_TAG_NAME = "Content Specification";

    /**
     * The Added By Property Tag ID
     */
    public static final Integer ADDED_BY_PROPERTY_TAG_ID = 14;
    /**
     * The First Name Property Tag ID
     */
    public static final Integer FIRST_NAME_PROPERTY_TAG_ID = 1;
    /**
     * The Last Name Property Tag ID
     */
    public static final Integer LAST_NAME_PROPERTY_TAG_ID = 2;
    /**
     * The Email Address Property Tag ID
     */
    public static final Integer EMAIL_PROPERTY_TAG_ID = 3;
    /**
     * The Organization Property Tag ID
     */
    public static final Integer ORGANIZATION_PROPERTY_TAG_ID = 18;
    /**
     * The Organization Division Property Tag ID
     */
    public static final Integer ORG_DIVISION_PROPERTY_TAG_ID = 19;
    /**
     * The DTD Property Tag ID
     */
    public static final Integer DTD_PROPERTY_TAG_ID = 16;
    /**
     * The Content Specification Type Property Tag ID
     */
    public static final Integer CSP_TYPE_PROPERTY_TAG_ID = 17;
    /**
     * The Content Specification Read-Only Property Tag ID
     */
    public static final Integer CSP_READ_ONLY_PROPERTY_TAG_ID = 25;
    /**
     * The Revision History tag ID
     */
    public static final Integer REVISION_HISTORY_TAG_ID = 598;
    /**
     * The Legal Notice tag ID
     */
    public static final Integer LEGAL_NOTICE_TAG_ID = 599;


    /**
     * The initial name for a snapshot revision
     */
    public static final String INITIAL_SNAPSHOT_REVISION_NAME = "Initial Untranslated Revision";

    // Override keys
    public static final String AUTHOR_GROUP_OVERRIDE = "Author_Group.xml";

    public static final String REVISION_HISTORY_OVERRIDE = "Revision_History.xml";

    public static final String FEEDBACK_OVERRIDE = "Feedback.xml";

    public static final String PUBSNUMBER_OVERRIDE = "pubsnumber";

    public static final String REVNUMBER_OVERRIDE = "revnumber";

    public static final String BRAND_OVERRIDE = "brand";

    // Meta Data Constants
    public static final String CHECKSUM_TITLE = "CHECKSUM";
    public static final String ID_TITLE = "ID";
    public static final String TITLE_TITLE = "Title";
    public static final String SUBTITLE_TITLE = "Subtitle";
    public static final String EDITION_TITLE = "Edition";
    public static final String BOOK_VERSION_TITLE = "Book Version";
    public static final String PUBSNUMBER_TITLE = "Pubsnumber";
    public static final String PRODUCT_TITLE = "Product";
    public static final String ABSTRACT_TITLE = "Abstract";
    public static final String COPYRIGHT_HOLDER_TITLE = "Copyright Holder";
    public static final String COPYRIGHT_YEAR_TITLE = "Copyright Year";
    public static final String VERSION_TITLE = "Version";
    public static final String BRAND_TITLE = "Brand";
    public static final String BUG_LINKS_TITLE = "Bug Links";
    public static final String BUGZILLA_PRODUCT_TITLE = "BZProduct";
    public static final String BUGZILLA_COMPONENT_TITLE = "BZComponent";
    public static final String BUGZILLA_VERSION_TITLE = "BZVersion";
    public static final String BUGZILLA_URL_TITLE = "BZURL";
    public static final String SURVEY_LINK_TITLE = "Survey Links";
    public static final String BOOK_TYPE_TITLE = "Type";
    public static final String PUBLICAN_CFG_TITLE = "publican.cfg";
    public static final String INLINE_INJECTION_TITLE = "Inline Injection";
    public static final String DTD_TITLE = "DTD";
    public static final String REV_HISTORY_TITLE = "Revision History";
    public static final String FEEDBACK_TITLE = "Feedback";
    public static final String LEGAL_NOTICE_TITLE = "Legal Notice";

    // Outdated Meta Data Regex Constants
    public static final String OUTPUT_STYLE_TITLE = "Output Style";

    // Other Meta Data Constants
    public static final String SPACES_TITLE = "spaces";
    public static final String DEBUG_TITLE = "Debug";

    public static final String BRACKET_CONTENTS = "Brackets";
    public static final String BRACKET_NAMED_PATTERN = "(?<!\\\\)\\%c(?<" + BRACKET_CONTENTS + ">(.|\n)*?)(?<!\\\\)\\%c";
    public static final String BRACKET_PATTERN = "(?<!\\\\)\\%c((.|\n)*?)(?<!\\\\)\\%c";
}
