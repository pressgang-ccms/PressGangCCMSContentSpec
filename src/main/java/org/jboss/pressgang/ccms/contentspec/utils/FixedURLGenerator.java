package org.jboss.pressgang.ccms.contentspec.utils;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.RuleBasedNumberFormat;
import org.jboss.pressgang.ccms.contentspec.CommonContent;
import org.jboss.pressgang.ccms.contentspec.ContentSpec;
import org.jboss.pressgang.ccms.contentspec.ITopicNode;
import org.jboss.pressgang.ccms.contentspec.KeyValueNode;
import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.Node;
import org.jboss.pressgang.ccms.contentspec.SpecNode;
import org.jboss.pressgang.ccms.contentspec.enums.LevelType;
import org.jboss.pressgang.ccms.contentspec.enums.TopicType;
import org.jboss.pressgang.ccms.utils.common.DocBookUtilities;
import org.jboss.pressgang.ccms.wrapper.PropertyTagInTopicWrapper;
import org.jboss.pressgang.ccms.wrapper.TranslatedTopicWrapper;
import org.jboss.pressgang.ccms.wrapper.base.BaseTopicWrapper;

public class FixedURLGenerator {
    private static final Pattern STARTS_WITH_NUMBER_RE = Pattern.compile("^(?<Numbers>\\d+)(?<EverythingElse>.*)$");
    // See http://stackoverflow.com/a/4307261/1330640
    private static String UNICODE_WORD = "\\pL\\pM\\p{Nd}\\p{Nl}\\p{Pc}[\\p{InEnclosedAlphanumerics}&&\\p{So}]";
    private static final Pattern STARTS_WITH_INVALID_SEQUENCE_RE = Pattern.compile("^(?<InvalidSeq>[^" + UNICODE_WORD + "]+)" +
            "(?<EverythingElse>.*)$");
    private static final List<TopicType> STATIC_FIXED_URL_TOPIC_TYPES = Arrays.asList(TopicType.ABSTRACT, TopicType.AUTHOR_GROUP,
            TopicType.LEGAL_NOTICE, TopicType.REVISION_HISTORY);

    /**
     * Generate the fixed urls and sets it where required for a content specification.
     *
     * @param contentSpec           The content spec to generate fixed urls for.
     * @param missingOnly           Generate only the missing fixed urls.
     * @param fixedUrlPropertyTagId The Fixed URL Property Tag ID.
     */
    public static void generateFixedUrls(final ContentSpec contentSpec, boolean missingOnly, final Integer fixedUrlPropertyTagId) {
        final Set<String> existingFixedUrls = new HashSet<String>();
        final Set<SpecNode> nodesWithoutFixedUrls = new HashSet<SpecNode>();
        final List<SpecNode> specNodes = getAllSpecNodes(contentSpec);

        // Collect any current fixed urls or nodes that need configuring
        if (missingOnly) {
            collectFixedUrlInformation(specNodes, nodesWithoutFixedUrls, existingFixedUrls);
        }

        generateFixedUrlForNodes(nodesWithoutFixedUrls, existingFixedUrls, fixedUrlPropertyTagId);
    }

    /**
     * Collect the fixed url information from a list of spec nodes.
     *
     * @param specNodes             The spec nodes to collect the information from.
     * @param nodesWithoutFixedUrls A modifiable collection to add nodes that have no fixed url information.
     * @param existingFixedUrls     A modifiable set to add existing fixed urls to.
     */
    public static void collectFixedUrlInformation(final Collection<SpecNode> specNodes, final Collection<SpecNode> nodesWithoutFixedUrls,
            final Set<String> existingFixedUrls) {
        for (final SpecNode specNode : specNodes) {
            if (isNullOrEmpty(specNode.getFixedUrl())) {
                if (specNode instanceof CommonContent) {
                    // Ignore common content as it can't have a fixed url
                    continue;
                } else if (specNode instanceof Level) {
                    final Level level = (Level) specNode;

                    // Ignore initial content and base levels
                    if (level.getLevelType() != LevelType.INITIAL_CONTENT && level.getLevelType() != LevelType.BASE) {
                        nodesWithoutFixedUrls.add(specNode);
                    }
                } else if (specNode instanceof ITopicNode) {
                    final ITopicNode topicNode = ((ITopicNode) specNode);

                    // Ignore info topics
                    if (topicNode.getTopicType() != TopicType.INFO && topicNode.getTopicType() != TopicType.INITIAL_CONTENT) {
                        nodesWithoutFixedUrls.add(specNode);
                    }
                } else {
                    nodesWithoutFixedUrls.add(specNode);
                }
            } else {
                existingFixedUrls.add(specNode.getFixedUrl());
            }
        }
    }

    /**
     * Collect all of the {@link SpecNode}s in a {@link ContentSpec}.
     *
     * @param contentSpec The content spec to get the spec nodes from.
     * @return A list of all spec nodes in the content spec.
     */
    protected static List<SpecNode> getAllSpecNodes(final ContentSpec contentSpec) {
        final List<SpecNode> specNodes = new ArrayList<SpecNode>();

        for (final Node childNode : contentSpec.getNodes()) {
            if (childNode instanceof SpecNode) {
                specNodes.add((SpecNode) childNode);
            } else if (childNode instanceof KeyValueNode && ((KeyValueNode) childNode).getValue() instanceof SpecNode) {
                specNodes.add((SpecNode) ((KeyValueNode) childNode).getValue());
            }

            if (childNode instanceof Level) {
                specNodes.addAll(getAllSpecNodes((Level) childNode));
            }
        }

        specNodes.addAll(getAllSpecNodes(contentSpec.getBaseLevel()));

        return specNodes;
    }

    /**
     * Collect all of the {@link SpecNode}s in a {@link Level}.
     *
     * @param level The level to get the spec nodes from.
     * @return A list of all spec nodes in the level.
     */
    protected static List<SpecNode> getAllSpecNodes(final Level level) {
        final List<SpecNode> specNodes = new ArrayList<SpecNode>();

        for (final Node childNode : level.getChildNodes()) {
            if (childNode instanceof SpecNode) {
                specNodes.add((SpecNode) childNode);
            }

            if (childNode instanceof Level) {
                specNodes.addAll(getAllSpecNodes((Level) childNode));
            }
        }

        return specNodes;
    }

    public static void generateFixedUrlForNodes(final Collection<SpecNode> specNodes, final Integer fixedUrlPropertyTagId) {
        generateFixedUrlForNodes(specNodes, new HashSet<String>(), fixedUrlPropertyTagId);
    }

    public static void generateFixedUrlForNodes(final Collection<SpecNode> specNodes, final Set<String> existingFixedUrls,
            final Integer fixedUrlPropertyTagId) {
        // Create the fixed urls for any nodes that didn't have urls
        for (final SpecNode specNode : specNodes) {
            // Generate a fixed url
            final String value = generateFixedURLForNode(specNode, existingFixedUrls, fixedUrlPropertyTagId);

            // Set the fixed url on the node
            setFixedURL(specNode, value, existingFixedUrls);
        }
    }

    public static void generateFixedUrlForNode(final SpecNode specNode, final Integer fixedUrlPropertyTagId) {
        final Set<String> processedFileNames = new HashSet<String>();

        // Generate a fixed url
        final String fixedUrl = generateFixedURLForNode(specNode, processedFileNames, fixedUrlPropertyTagId);

        // Set the fixed url on the node
        setFixedURL(specNode, fixedUrl, processedFileNames);
    }

    /**
     * Generate a fixed url for a specific content spec node, making sure that it is valid within the Content Specification.
     *
     * @param specNode              The spec node to generate the fixed url for.
     * @param existingFixedUrls
     * @param fixedUrlPropertyTagId
     * @return A unique generate file name for the specified node.
     */
    public static String generateFixedURLForNode(final SpecNode specNode, final Set<String> existingFixedUrls,
            final Integer fixedUrlPropertyTagId) {
        String value;
        if (specNode instanceof ITopicNode) {
            final ITopicNode topicNode = (ITopicNode) specNode;
            final BaseTopicWrapper<?> topic = topicNode.getTopic();

            if (STATIC_FIXED_URL_TOPIC_TYPES.contains(topicNode.getTopicType())) {
                // Ignore certain topics as those are unique per book and should have a static name
                value = getStaticFixedURLForTopicNode(topicNode);
            } else if (topic != null) {
                // See if a fixed url property exists for the topic and it's valid
                final PropertyTagInTopicWrapper fixedUrl = topic.getProperty(fixedUrlPropertyTagId);
                if (fixedUrl != null && !existingFixedUrls.contains(fixedUrl.getValue())) {
                    value = fixedUrl.getValue();
                } else {
                    // Get the topic title with conditional content removed
                    final String topicTitle;
                    if (topic instanceof TranslatedTopicWrapper) {
                        topicTitle = ContentSpecUtilities.getTopicTitleWithConditions(topicNode, ((TranslatedTopicWrapper) topic).getTopic());
                    } else {
                        topicTitle = ContentSpecUtilities.getTopicTitleWithConditions(topicNode, topic);
                    }
                    value = createURLTitle(topicTitle);
                }
            } else {
                value = createURLTitle(specNode.getTitle());
            }
        } else if (specNode instanceof Level) {
            final String levelPrefix = ContentSpecUtilities.getLevelPrefix((Level) specNode);
            value = levelPrefix + createURLTitle(specNode.getTitle());
        } else {
            value = createURLTitle(specNode.getTitle());
        }

        // If the basic url already exists or couldn't be generated then fix it up
        if (isNullOrEmpty(value) || existingFixedUrls.contains(value)) {
            // If the title has no characters that can be used in a url, then just use a generic one
            String baseUrlName = value;
            if (isNullOrEmpty(baseUrlName) || baseUrlName.matches("^\\d+$")) {
                if (specNode instanceof Level) {
                    final Level level = (Level) specNode;
                    final String levelPrefix = ContentSpecUtilities.getLevelPrefix(level);
                    baseUrlName = levelPrefix + level.getLevelType().getTitle().replace(" ", "_") + "ID" + specNode.getUniqueId();
                } else if (specNode instanceof ITopicNode) {
                    baseUrlName = "TopicID" + ((ITopicNode) specNode).getDBId();
                } else {
                    throw new RuntimeException("Cannot generate a fixed url for an Unknown SpecNode type");
                }
            }

            // Add a numerical prefix until we have something unique
            String postFix = "";
            for (int uniqueCount = 1; ; ++uniqueCount) {
                if (!existingFixedUrls.contains(baseUrlName + postFix)) {
                    value = baseUrlName + postFix;
                    break;
                } else {
                    postFix = uniqueCount + "";
                }
            }
        }

        return value;
    }

    /**
     * Sets the fixed URL property on the node.
     *
     * @param specNode          The spec node to update.
     * @param fixedURL          The fixed url to apply to the node.
     * @param existingFixedUrls A list of file names that already exist in the spec.
     */
    protected static void setFixedURL(final SpecNode specNode, final String fixedURL, final Set<String> existingFixedUrls) {
        specNode.setFixedUrl(fixedURL);

        // Add the fixed url to the processed file names
        existingFixedUrls.add(fixedURL);
    }

    /**
     * Generate the fixed url for a static topic node.
     *
     * @param topicNode The topic node to generate the static fixed url for.
     * @return The fixed url for the node.
     */
    public static String getStaticFixedURLForTopicNode(final ITopicNode topicNode) {
        if (topicNode.getTopicType() == TopicType.REVISION_HISTORY) {
            return "appe-Revision_History";
        } else if (topicNode.getTopicType() == TopicType.LEGAL_NOTICE) {
            return "Legal_Notice";
        } else if (topicNode.getTopicType() == TopicType.AUTHOR_GROUP) {
            return "Author_Group";
        } else if (topicNode.getTopicType() == TopicType.ABSTRACT) {
            return "Abstract";
        } else {
            return null;
        }
    }

    /**
     * Creates the URL specific title for a topic or level.
     *
     * @param title The title that will be used to create the URL Title.
     * @return The URL representation of the title.
     */
    public static String createURLTitle(final String title) {
        String baseTitle = title;
        // Remove XML Elements from the Title.
        baseTitle = baseTitle.replaceAll("</(.*?)>", "").replaceAll("<(.*?)>", "");

        // Check if the title starts with an invalid sequence
        final Matcher invalidSequenceMatcher = STARTS_WITH_INVALID_SEQUENCE_RE.matcher(baseTitle);
        if (invalidSequenceMatcher.find()) {
            baseTitle = invalidSequenceMatcher.group("EverythingElse");
        }

        // Start by removing any prefixed numbers (you can't start an xref id with numbers)
        final Matcher matcher = STARTS_WITH_NUMBER_RE.matcher(baseTitle);
        if (matcher.find()) {
            final String numbers = matcher.group("Numbers");
            final String everythingElse = matcher.group("EverythingElse");

            if (numbers != null && everythingElse != null) {
                final NumberFormat formatter = new RuleBasedNumberFormat(RuleBasedNumberFormat.SPELLOUT);
                final String numbersSpeltOut = formatter.format(Integer.parseInt(numbers));
                baseTitle = numbersSpeltOut + everythingElse;

                // Capitalize the first character
                if (baseTitle.length() > 0) {
                    baseTitle = baseTitle.substring(0, 1).toUpperCase() + baseTitle.substring(1, baseTitle.length());
                }
            }
        }

        // Escape the title
        final String escapedTitle = DocBookUtilities.escapeTitle(baseTitle);

        // We don't want only numeric fixed urls, as that is completely meaningless.
        if (escapedTitle.matches("^\\d+$")) {
            return "";
        } else {
            return escapedTitle;
        }
    }
}
