package org.jboss.pressgang.ccms.contentspec.constants;

import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.List;

import org.jboss.pressgang.ccms.utils.common.StringUtilities;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;

public class CSConstants {
    private CSConstants() {
    }

    public static final String BUGZILLA_URL_PROPERTY = "contentSpec.bugzillaUrl";

    // Level Type Constants
    public static final int LEVEL_BASE = -1;

    // Other
    public static final int CSP_PROPERTY_ID = 15;
    public static final String CSP_PRE_PROCESSED_STRING = "Pre Processed";
    public static final String CSP_POST_PROCESSED_STRING = "Post Processed";

    // Output format constants
    public static final String CSP_OUTPUT_FORMAT = "Narrative";

    public static final Integer TYPE_CATEGORY_ID = 4;
    public static final Integer TECHNOLOGY_CATEGORY_ID = 3;
    public static final Integer RELEASE_CATEGORY_ID = 15;
    public static final Integer WRITER_CATEGORY_ID = 12;
    public static final Integer COMMON_NAME_CATEGORY_ID = 17;
    public static final Integer CONCERN_CATEGORY_ID = 2;
    public static final Integer LIFECYCLE_CATEGORY_ID = 5;
    public static final Integer INFORMATION_SENSITIVITY_CATEGORY_ID = 19;
    public static final Integer SEO_METADATA_CATEGORY_ID = 24;
    public static final Integer CONTENT_TYPE_CATEGORY_ID = 23;
    public static final Integer PROGRAMMING_LANGUAGE_CATEGORY_ID = 22;

    public static final Integer RH_INTERNAL_TAG_ID = 315;

    public static final Integer UNKNOWN_USER_ID = 89;

    public static final String NEW_TOPIC_ID_REGEX = "^N[0-9]*$";
    public static final Pattern NEW_TOPIC_ID_PATTERN = Pattern.compile(NEW_TOPIC_ID_REGEX);
    public static final String EXISTING_TOPIC_ID_REGEX = "^[0-9]+$";
    public static final String CLONED_TOPIC_ID_REGEX = "^C[0-9]+$";
    public static final String DUPLICATE_TOPIC_ID_REGEX = "^X[0-9]+$";
    public static final String CLONED_DUPLICATE_TOPIC_ID_REGEX = "^XC[0-9]+$";
    public static final String ALL_TOPIC_ID_REGEX = "(^N[0-9]*$)|(^[0-9]+$)|(^X[0-9]+$)|(^C[0-9]+$)|(^XC[0-9]+$)";
    public static final Pattern CUSTOM_PUBLICAN_CFG_PATTERN = Pattern.compile("([A-Za-z0-9-]+)-" + StringUtilities.convertToRegexString(
            CommonConstants.CS_PUBLICAN_CFG_TITLE), Pattern.CASE_INSENSITIVE);

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
     * The Task tag ID
     */
    public static final Integer TASK_TAG_ID = 4;
    /**
     * The Content Specification tag ID
     */
    public static final Integer CONTENT_SPEC_TAG_ID = 268;
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
     * The Author Group tag ID
     */
    public static final Integer AUTHOR_GROUP_TAG_ID = 664;
    /**
     * The Valid XML Entities string constant ID
     */
    public static final Integer VALID_ENTITIES_STRING_CONSTANT_ID = 72;

    // Override keys
    public static final String AUTHOR_GROUP_OVERRIDE = "Author_Group.xml";

    public static final String REVISION_HISTORY_OVERRIDE = "Revision_History.xml";

    public static final String FEEDBACK_OVERRIDE = "Feedback.xml";

    public static final String POM_OVERRIDE = "pom.xml";

    public static final String PUBSNUMBER_OVERRIDE = "pubsnumber";

    public static final String REVNUMBER_OVERRIDE = "revnumber";

    public static final String BRAND_OVERRIDE = "brand";
    public static final String BRAND_ALT_OVERRIDE = "Brand";

    public static final String SURVEY_LINK_TITLE = "Survey Links";

    // Outdated Meta Data Regex Constants
    public static final String OUTPUT_STYLE_TITLE = "Output Style";

    public static final String DEBUG_TITLE = "Debug";

    public static final String BRACKET_CONTENTS = "Brackets";
    public static final String BRACKET_NAMED_PATTERN = "(?<!\\\\)\\%c(?<" + BRACKET_CONTENTS + ">(.|\n)*?)(?<!\\\\)\\%c";
    public static final String BRACKET_PATTERN = "(?<!\\\\)\\%c((.|\n)*?)(?<!\\\\)\\%c";

    public static final List<String> PUBLICAN_CFG_PARAMETERS = Arrays.asList(
            "docname",
            "version",
            "xml_lang",
            "edition",
            "type",
            "brand",
            "product",
            "arch",
            "books",
            "brew_dist",
            "chunk_first",
            "chunk_section_depth",
            "classpath",
            "common_config",
            "common_content",
            "condition",
            "confidential",
            "confidential_text",
            "cvs_branch",
            "cvs_pkg",
            "cvs_root",
            "debug",
            "def_lang",
            "doc_url",
            "dt_obsoletes",
            "dtdver",
            "ec_id",
            "ec_name",
            "ec_provider",
            "generate_section_toc_level",
            "git_branch",
            "ignored_translations",
            "license",
            "max_image_width",
            "os_ver",
            "prod_url",
            "release",
            "repo",
            "scm",
            "show_remarks",
            "show_unknown",
            "src_url",
            "strict",
            "tmp_dir",
            "toc_section_depth",
            "web_brew_dist",
            "web_home",
            "web_host",
            "web_name_label",
            "web_obsoletes",
            "web_product_label",
            "web_search",
            "web_version_label"
    );
}
