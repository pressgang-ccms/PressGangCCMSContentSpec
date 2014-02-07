package org.jboss.pressgang.ccms.contentspec.constants;

public class TemplateConstants {
    public static final String FULLY_COMMENTED_TEMPLATE = "# Content spec written by\n" +
            "# Generated on <date>\n" +
            "\n" +
            "# Lines that commence with the # symbol are comments\n" +
            "\n" +
            "# The fields below are mandatory\n" +
            "Title =\n" +
            "Product =\n" +
            "Version =\n" +
            "Copyright Holder =\n" +
            "\n" +
            "# The commented fields below are optional\n" +
            "# Format = DocBook 4.5\n" +
            "# Copyright Year = \n" +
            "# Subtitle =\n" +
            "# Abstract=\n" +
            "# Edition =\n" +
            "# Brand =\n" +
            "# Type = \n" +
            "# publican.cfg = [ custom content to append to publican.cfg can be\n" +
            "# multiline ]\n" +
            "# Pubsnumber =\n" +
            "\n" +
            "# Uncomment this to enable the index in the book.\n" +
            "# Index = ON\n" +
            "\n" +
            "# Bug Links = Off | JIRA | Bugzilla\n" +
            "# BZServer will default to https://bugzilla.redhat.com/ is its not specified.\n" +
            "# BZServer = \n" +
            "# BZProduct = \n" +
            "# BZComponent = \n" +
            "# BZVersion = \n" +
            "# BZKeywords = \n" +
            "# BZURL = \n" +
            "# Uncomment this to disable automatic assignment of bugs to the\n" +
            "# topic's assigned author\n" +
            "# BZ Assignee = Off\n" +
            "\n" +
            "# JIRAServer = \n" +
            "# JIRAProject = \n" +
            "# JIRAComponent = \n" +
            "# JIRAVersion = \n" +
            "# JIRALabels = \n" +
            "\n" +
            "# Uncomment the lines below to customise jDocbook output\n" +
            "# ArtifactId = \n" +
            "# GroupId = \n" +
            "# POM Version = \n" +
            "\n" +
            "# Uncomment these lines and change the XXXX to a valid topic to set\n" +
            "# a custom Revision History, Author Group, Feedback, or Legal Notice.\n" +
            "# Author Group = [XXXX]\n" +
            "# Feedback = [XXXX]\n" +
            "# Legal Notice = [XXXX]\n" +
            "# Revision History = [XXXX]\n" +
            "# (or this format, to create a new one, this works for all types)\n" +
            "# Revision History = [N, Revision History]\n" +
            "\n" +
            "# Indentation is meaningful. Use 2 spaces.\n" +
            "\n" +
            "# These are global tags. Anything declared at this level is inherited by the entire content spec. They will be applied to " +
            "any new topics created by this content spec, and will be assigned to any existing topics included in this spec. They will " +
            "also not be applied to existing topics that are referenced by the spec after the global tag was defined.\n" +
            "\n" +
            "# The \"Writer=jwulf\" directive will tag all the new topics with the Assigned Writer \"jwulf\". It relies on \"jwulf\" " +
            "existing as a tag in PressGang in the Assigned Writer category.\n" +
            "\n" +
            "[Nu-Tea, 2.0, Tea, Writer=jwulf]\n" +
            "\n" +
            "# Conditions are used to selectively include XML elements from topics. This allows the same topic to be included in multiple" +
            " content specifications with slight differences.\n" +
            "# Conditions defined at the global level are used by all topics in the content specification.\n" +
            "\n" +
            "[condition = foo]\n" +
            "\n" +
            "# Chapters are specified with the \"Chapter\" keyword. They cannot be nested. They must be indented with 0 spaces.\n" +
            "# This Chapter has the title \"Tea\"\n" +
            "\n" +
            "Chapter: Tea\n" +
            "\n" +
            "# A new topic is specified with \"N\", and the topic type. Any number of URL arguments can be specified. These are source " +
            "material URL references for the author.\n" +
            "# Use a comma to separate multiple URL= declarations.\n" +
            "# This specifies a new Concept topic with the title \"Tea\"\n" +
            "   Tea [N, Concept, URL=http://www.nu-tea.com/tea]\n" +
            "\n" +
            "# Existing topics are specified by Topic title and Topic ID\n" +
            "\n" +
            "   Beverages [3456]\n" +
            "\n" +
            "# If Topic 3456 has a title different from \"Beverages\", the content spec processor will display a warning when pushing or " +
            "building this spec. These warnings should be reviewed to ensure that the topic that was defined in the spec is actually the " +
            "topic that was intended to be included, and not a mistyped topic ID.\n" +
            "\n" +
            "\n" +
            "# Sometimes the Topic title has been edited since the Content Spec was written. For example, " +
            "capitalisation may have changed or a title may have been edited. In this case, when you try to build or push the spec it " +
            "will display a warning with a list of mismatching topics and IDs, and then automatically update the title. If you prefer " +
            "that an error be thrown in this case, preventing the spec from being built with inconsistent titles, " +
            "use the --strict-titles option.\n" +
            "\n" +
            "   Biscuits (Content spec Topic title doesn't match Topic title in PressGang) [234]\n" +
            "\n" +
            "# Existing topics can be reused simply by specifying them more than once in the content spec. They have a unique identifier," +
            " the Topic ID.\n" +
            "\n" +
            "# To create a new topic that will be reused in this content spec, give it a unique ID as part of the N directive:\n" +
            "\n" +
            "   Tea-making tools [N1, Reference]\n" +
            "\n" +
            "# Sections are specified by the keyword \"Section\". They must be within Chapters or Appendices . Sections can be nested.\n" +
            "# This Section is in the \"Tea\" chapter, and has the title \"Simple Tea\"\n" +
            "\n" +
            "   Section: Simple Tea\n" +
            "\n" +
            "# Tags can be added to new topics by placing them in the topic specification block. In this case, " +
            "the tag \"Tea-making\" must exist in PressGang for the content spec to validate and process. The \"Tea-making\" tag will " +
            "applied to the new Task topic \"Make a cup of tea (basic)\"\n" +
            "\n" +
            "# The Refer-to Relationship block [R: <target>] is used to inject a \"See also:\" reference into a topic. In this case a " +
            "\"See Also:\" will be injected for the element with target ID T10. In this content spec, " +
            "that is the Chapter \"Advanced Tea Making\"\n" +
            "\n" +
            "     Make a cup of tea (basic) [N, Task, Tea-making] [R: T10]\n" +
            "\n" +
            "# Tags declared at the Chapter or Section level are inherited by that Chapter or Section. They will be applied to *new " +
            "topics only*.\n" +
            "# All new topics in the \"History of Tea\" chapter will have the \"History\" tag applied to them.\n" +
            "# This book has a globally assigned writer \"jwulf\". However, the new topics in the \"History of Tea\" chapter will be " +
            "assigned to \"lnewson\".\n" +
            "\n" +
            "Chapter: History of Tea [History, Writer=lnewson]\n" +
            "\n" +
            "# The Description directive will add a description to a new topic. This is used to provide direction to the author as to the" +
            " contents of the topic.\n" +
            "\n" +
            "   Types of Tea [N2, Reference, Description=A list of different types of tea]\n" +
            "\n" +
            "# Existing topics may be cloned. In this example Topic 345 \"Nu-tea (tm)\" is cloned to a new topic. When a topic is cloned," +
            " all its tag information are also cloned. To remove a tag on the newly cloned topic, " +
            "specify it in the topic specification block preceded by a minus sign. To add a tag, specify it as normal.\n" +
            "# If you have a tag that starts with a minus sign, escape it with \\\n" +
            "# The following directive clones topic 345 \"Nu-Tea (tm)\" to a new topic with the same title, " +
            "removes the tag \"1.0\" from the new topic, and adds the two tags \"2.0\" and \"-takeaway\" to the new topic.\n" +
            "\n" +
            "   Nu-Tea (tm) [C345, -1.0, 2.0, \\-takeaway]\n" +
            "\n" +
            "   Find more information on our website [560]\n" +
            "\n" +
            "# Chapters and Sections may be specified as link targets using the link target directive [T<unique ID>]\n" +
            "# The Chapter \"Advanced Tea Making\" is the target of a link in this content specification, " +
            "so it has the link target T10 assigned.\n" +
            "# All new topics in this chapter will inherit the tag \"Tea-making\"\n" +
            "\n" +
            "Chapter: Advanced Tea Making [T10] [Tea-making]\n" +
            "\n" +
            "# To reuse a new topic, the new topic must be specified in the content spec with a unique ID [N<ID>, <topic type>]\n" +
            "# Here we reuse the topic declared as N2 \"Types of Tea\". The topic title must match, " +
            "even in permissive mode. A reused new topic will not inherit the tags specified in the containing Chapter or Section where " +
            "it is reused.\n" +
            "\n" +
            "   Types of Tea [X2]\n" +
            "\n" +
            "# This is a new Concept topic with a link target ID \"T1\"\n" +
            "\n" +
            "   Tea pot [N, Concept] [T1]\n" +
            "\n" +
            "# This is the reference topic \"Tea-making tools\" reused. It is declared elsewhere in the content spec as \"N1\". This " +
            "specific instance of the topic will be the target of links using target ID \"T2\"\n" +
            "\n" +
            "   Tea-making tools [X1] [T2]\n" +
            "\n" +
            "# Refer-to relationships are declared in a Refer-to relationship block using the \"R\" directive.\n" +
            "# They are rendered as an itemizedlist of links inside a formalpara with a role of \"refer-to-list\".\n" +
            "# All Refer-to relationship target topics must be included in the content spec.\n" +
            "# Here a \"Refer to\" link to the \"Tea pot\" concept is injected into the existing topic \"Brew a pot of white tea\"\n" +
            "\n" +
            "   Brew a pot of white tea [567] [R: T1]\n" +
            "\n" +
            "# Here \"Refer to\" links to the \"Tea-making tools\" reference and the \"Brew a pot of white tea\" are injected into the " +
            "existing topic \"Brew a pot of green tea\"\n" +
            "# When a existing topic is included in a content spec only once, its topic ID is an unambiguous target ID within the content" +
            " spec. When an existing topic is included more than once in the content spec, the particular instance of the topic that is " +
            "the target of a refer-to link must be specified using the Target directive \"T\".\n" +
            "\n" +
            "   Brew a pot of green tea [23] [R: T2, 567]\n" +
            "\n" +
            "# A \"Prerequisite\" relationship is declared in a Prerequisites block using the directive \"P\"\n" +
            "# This will inject a Prerequisites: formalpara containing an itemizedlist of links to the specified Prerequisite topics.\n" +
            "# The Prerequisites formalpara has the role \"prereqs-list\", so you can style that in the css\n" +
            "# All Refer-to relationship target topics must be included in the content spec.\n" +
            "\n" +
            "   Make Jasmine green tea [N, Task] [P: 23]\n" +
            "\n" +
            "# Here the existing topic 56, \"Make a fruit tea\" is included\n" +
            "\n" +
            "   Make a fruit tea [56]\n" +
            "\n" +
            "# We specify a new Task topic \"Brew a pot of fruit tea\", and declare that topic 56 \"Make a fruit tea\" is a prerequisite" +
            ".\n" +
            "\n" +
            "   Brew a pot of fruit tea [N3, Task] [P: 56]\n" +
            "\n" +
            "# A contents of a single topic can be included directly under a Chapter, Appendix, Part or Section by listing the topic ID " +
            "after the title. These topics supply the front matter for the level, and usually contain introductory information.\n" +
            "# A warning will be displayed if the title of the level does not match the title of the topic. However, " +
            "unlike inconsistencies between topic titles for the children of a level, inconsistent titles for front matter topics will " +
            "not be automatically updated when the content specification is pushed or built.\n" +
            "\n" +
            "Chapter: The future of tea [123]\n" +
            "\n" +
            "# Conditions can be overriden at any level in the content specification.\n" +
            "# The topic Tea appreciation societies [125] will use the condition bar as defined on the chapter.\n" +
            "# The topic Useful tea websites [124] will use the condition baz as defined on the topic itself.\n" +
            "\n" +
            "Chapter: Getting more information on Tea [condition = bar]\n" +
            "  Tea appreciation societies [125]\n" +
            "  Useful tea websites [124, condition = baz]\n" +
            "\n" +
            "Appendix: Nu-Tea Distributors\n" +
            "   List of Nu-Tea Distributors by Country [N, Reference, URL=http://www.nu-tea.com/distributors, Description=Make it a table and segment by geo]";

    public static final String EMPTY_TEMPLATE = "# Content spec written by\n" +
            "# Generated on <date>\n" +
            "\n" +
            "# Lines that commence with the # symbol are comments\n" +
            "\n" +
            "# The fields below are mandatory\n" +
            "Title = \n" +
            "Subtitle = \n" +
            "Abstract= \n" +
            "Product = \n" +
            "Version = \n" +
            "Format = DocBook 4.5\n" +
            "Copyright Holder= \n" +
            "\n" +
            "# The commented fields below are optional\n" +
            "\n" +
            "# Copyright Year = \n" +
            "# Edition = \n" +
            "# Brand = \n" +
            "# Type = \n" +
            "# publican.cfg = [ custom content to append to publican.cfg can be\n" +
            "multiline ]\n" +
            "# Pubsnumber = \n" +
            "\n" +
            "# Uncomment this to enable the index in the book.\n" +
            "# Index = ON\n" +
            "\n" +
            "# Bug Links = Off | JIRA | Bugzilla\n" +
            "# BZServer will default to https://bugzilla.redhat.com/ is its not specified.\n" +
            "# BZServer = \n" +
            "# BZProduct = \n" +
            "# BZComponent = \n" +
            "# BZVersion = \n" +
            "# BZKeywords = \n" +
            "# BZURL = \n" +
            "# Uncomment this to disable automatic assignment of bugs to the\n" +
            "# topic's assigned author\n" +
            "# BZ Assignee = Off\n" +
            "\n" +
            "# JIRAServer = \n" +
            "# JIRAProject = \n" +
            "# JIRAComponent = \n" +
            "# JIRAVersion = \n" +
            "# JIRALabels = \n" +
            "\n" +
            "# Uncomment the lines below to customise jDocbook output\n" +
            "# ArtifactId = \n" +
            "# GroupId = \n" +
            "# POM Version = \n" +
            "\n" +
            "# Uncomment these lines and change the XXXX to a valid topic to set\n" +
            "# a custom Revision History, Author Group, Feedback, or Legal Notice.\n" +
            "# Author Group = [XXXX]\n" +
            "# Feedback = [XXXX]\n" +
            "# Legal Notice = [XXXX]\n" +
            "# Revision History = [XXXX]\n" +
            "# (or this format, to create a new one, this works for all types)\n" +
            "# Revision History = [N, Revision History]\n" +
            "\n" +
            "# Indentation is meaningful. Use 2 spaces.\n";

    private TemplateConstants() {
    }
}