package org.jboss.pressgang.ccms.contentspec.constants;

import java.util.Arrays;
import java.util.List;

import com.google.code.regexp.Pattern;
import org.jboss.pressgang.ccms.utils.common.StringUtilities;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;

public class CSConstants {
    private CSConstants() {
    }

    // Level Type Constants
    public static final int LEVEL_BASE = -1;

    // Output format constants
    public static final String CSP_OUTPUT_FORMAT = "Narrative";

    public static final String DEFAULT_BUGZILLA_URL = "https://bugzilla.redhat.com/";
    public static final String DEFAULT_BZCOMPONENT = "documentation";

    public static final String NEW_TOPIC_ID_REGEX = "^N[0-9]*$";
    public static final Pattern NEW_TOPIC_ID_PATTERN = Pattern.compile(NEW_TOPIC_ID_REGEX);
    public static final String EXISTING_TOPIC_ID_REGEX = "^(-)?[0-9]+$";
    public static final String CLONED_TOPIC_ID_REGEX = "^C[0-9]+$";
    public static final String DUPLICATE_TOPIC_ID_REGEX = "^X[0-9]+$";
    public static final String CLONED_DUPLICATE_TOPIC_ID_REGEX = "^XC[0-9]+$";
    public static final String ALL_TOPIC_ID_REGEX = "(^N[0-9]*$)|(^[0-9]+$)|(^X[0-9]+$)|(^C[0-9]+$)|(^XC[0-9]+$)";
    public static final Pattern CUSTOM_PUBLICAN_CFG_PATTERN = Pattern.compile("([A-Za-z0-9-]+)-" + StringUtilities.convertToRegexString(
            CommonConstants.CS_PUBLICAN_CFG_TITLE), java.util.regex.Pattern.CASE_INSENSITIVE);

    public static final String CHAPTER = "Chapter";
    public static final String APPENDIX = "Appendix";
    public static final String SECTION = "Section";
    public static final String PROCESS = "Process";
    public static final String PART = "Part";
    public static final String PREFACE = "Preface";
    public static final String LEVEL_INITIAL_CONTENT = "Initial Text";

    public static final String BOOK_TYPE_BOOK = "Book";
    public static final String BOOK_TYPE_ARTICLE = "Article";
    public static final String BOOK_TYPE_BOOK_DRAFT = "Book-Draft";
    public static final String BOOK_TYPE_ARTICLE_DRAFT = "Article-Draft";

    // Override keys
    public static final String AUTHOR_GROUP_OVERRIDE = "Author_Group.xml";

    public static final String REVISION_HISTORY_OVERRIDE = "Revision_History.xml";

    public static final String FEEDBACK_OVERRIDE = "Feedback.xml";

    public static final String POM_OVERRIDE = "pom.xml";

    public static final String PUBSNUMBER_OVERRIDE = "pubsnumber";

    public static final String REVNUMBER_OVERRIDE = "revnumber";

    public static final String BRAND_OVERRIDE = "brand";
    public static final String BRAND_ALT_OVERRIDE = "Brand";

    // Outdated Meta Data Regex Constants
    public static final String OUTPUT_STYLE_TITLE = "Output Style";

    public static final String DEBUG_TITLE = "Debug";

    public static final String BRACKET_CONTENTS = "Brackets";
    public static final String BRACKET_NAMED_PATTERN = "(?<!\\\\)\\%c(?<" + BRACKET_CONTENTS + ">(.|\n)*?)(?<!\\\\)\\%c";
    public static final String BRACKET_PATTERN = "(?<!\\\\)\\%c((.|\n)*?)(?<!\\\\)\\%c";

    public static final String DUMMY_CS_NAME_ENT_FILE = "<!ENTITY PRODUCT \"Product\">\n" +
            "<!ENTITY BOOKID \"BOOKID\">\n" +
            "<!ENTITY YEAR \"YYYY\">\n" +
            "<!ENTITY TITLE \"TITLE\">\n" +
            "<!ENTITY HOLDER \"HOLDER\">\n" +
            "<!ENTITY BZURL \"BZURL\">\n" +
            "<!ENTITY BZCOMPONENT \"BZCOMPONENT\">\n" +
            "<!ENTITY BZPRODUCT \"BZPRODUCT\">";

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
