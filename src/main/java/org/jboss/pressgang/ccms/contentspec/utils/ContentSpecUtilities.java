package org.jboss.pressgang.ccms.contentspec.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;
import org.jboss.pressgang.ccms.contentspec.ContentSpec;
import org.jboss.pressgang.ccms.contentspec.KeyValueNode;
import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.Node;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;
import org.jboss.pressgang.ccms.contentspec.entities.Revision;
import org.jboss.pressgang.ccms.contentspec.entities.RevisionList;
import org.jboss.pressgang.ccms.contentspec.sort.EnversRevisionSort;
import org.jboss.pressgang.ccms.contentspec.structures.StringToCSNodeCollection;
import org.jboss.pressgang.ccms.provider.ContentSpecProvider;
import org.jboss.pressgang.ccms.utils.common.CollectionUtilities;
import org.jboss.pressgang.ccms.utils.common.StringUtilities;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.ContentSpecWrapper;
import org.jboss.pressgang.ccms.wrapper.collection.CollectionWrapper;


public class ContentSpecUtilities {
    public static final Pattern CS_CHECKSUM_PATTERN = Pattern.compile("CHECKSUM[ ]*=[ ]*(?<Checksum>[A-Za-z0-9]+)");
    private static final List<String> translatableMetaData = CollectionUtilities.toArrayList(
            new String[]{CSConstants.TITLE_TITLE, CSConstants.PRODUCT_TITLE, CSConstants.SUBTITLE_TITLE, CSConstants.ABSTRACT_TITLE,
                    CSConstants.COPYRIGHT_HOLDER_TITLE, CSConstants.VERSION_TITLE, CSConstants.EDITION_TITLE});

    private ContentSpecUtilities() {
    }

    /**
     * Get the checksum of a Content Specification object.
     *
     * @param contentSpec The content spec object to calculate the checksum for.
     * @return The MD5 hash representing the content spec contents.
     */
    public static String getContentSpecChecksum(final ContentSpec contentSpec) {
        final String contentSpecString = contentSpec.toString();
        return getContentSpecChecksum(contentSpecString);
    }

    /**
     * Get the checksum of a Content Specification object.
     *
     * @param contentSpecString The content spec string to calculate the checksum for.
     * @return The MD5 hash representing the content spec contents.
     */
    public static String getContentSpecChecksum(final String contentSpecString) {
        final Matcher matcher = CS_CHECKSUM_PATTERN.matcher(contentSpecString);
        if (matcher.find()) {
            return matcher.group("Checksum");
        }

        return null;
    }

    /**
     * Generates a random target it in the form of T<Line Number>0<Random Number><count>.
     * I.e. The topic is on line 50 and the target to be created for is topic 4 in a process, the
     * output would be T500494
     *
     * @param uniqueId The unique id for a topic.
     * @param count    The count of topics in the process.
     * @return The partially random target id.
     */
    public static String generateRandomTargetId(final String uniqueId, final int count) {
        return generateRandomTargetId(uniqueId) + count;
    }

    /**
     * Generates a random target it in the form of T-<UniqueId>0<Random Number>.
     * The random number is between 0-49.
     *
     * @param uniqueId The unique id for a topic.
     * @return The partially random target id.
     */
    public static String generateRandomTargetId(final String uniqueId) {
        int randomNum = (int) (Math.random() * 50);
        return "T-" + uniqueId + "0" + randomNum;
    }

    public static List<StringToCSNodeCollection> getTranslatableStrings(final ContentSpecWrapper contentSpec,
            final boolean allowDuplicates) {
        if (contentSpec == null) return null;

        final List<StringToCSNodeCollection> retValue = new ArrayList<StringToCSNodeCollection>();

        final CollectionWrapper<CSNodeWrapper> contentSpecNodes = contentSpec.getChildren();
        for (final CSNodeWrapper node : contentSpecNodes.getItems()) {
            if (node.getNodeType() == CommonConstants.CS_NODE_META_DATA) {
                if (translatableMetaData.contains(node.getTitle())) {
                    addTranslationToNodeDetailsToCollection(node.getAdditionalText().toString(), node, allowDuplicates, retValue);
                }
            }
        }

        // Find the level nodes translations
        for (final CSNodeWrapper childNode : contentSpec.getChildren().getItems()) {
            if (childNode.getNodeType() != CommonConstants.CS_NODE_META_DATA && childNode.getNodeType() != CommonConstants.CS_NODE_TOPIC) {
                getTranslatableStringsFromLevel(childNode, retValue, allowDuplicates);
            }
        }

        return CollectionUtilities.toArrayList(retValue);
    }

    private static void getTranslatableStringsFromLevel(final CSNodeWrapper level, final List<StringToCSNodeCollection> translationStrings,
            final boolean allowDuplicates) {
        if (level == null || translationStrings == null) return;

        addTranslationToNodeDetailsToCollection(level.getTitle(), level, allowDuplicates, translationStrings);

        for (final CSNodeWrapper childNode : level.getChildren().getItems()) {
            if (childNode.getNodeType() != CommonConstants.CS_NODE_META_DATA && childNode.getNodeType() != CommonConstants.CS_NODE_TOPIC) {
                getTranslatableStringsFromLevel(childNode, translationStrings, allowDuplicates);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void replaceTranslatedStrings(final ContentSpecWrapper contentSpecEntity, final ContentSpec contentSpec,
            final Map<String, String> translations) {
        if (contentSpecEntity == null || translations == null || translations.size() == 0) return;

        /*
         * Get the translation strings and the nodes that the string maps to. We
         * assume that the text being provided here is an exact match for the
         * text that was supplied to getTranslatableStrings originally, which we
         * then assume matches the strings supplied as the keys in the
         * translations parameter.
         */
        final List<StringToCSNodeCollection> stringToNodeCollections = getTranslatableStrings(contentSpecEntity, false);

        if (stringToNodeCollections == null || stringToNodeCollections.size() == 0) return;

        for (final StringToCSNodeCollection stringToNodeCollection : stringToNodeCollections) {
            final String originalString = stringToNodeCollection.getTranslationString();
            final ArrayList<CSNodeWrapper> nodeCollections = stringToNodeCollection.getNodeCollections();

            if (nodeCollections != null && nodeCollections.size() != 0) {
                // Zanata will change the format of the strings that it returns. Here we account for any trimming that was done.
                final ZanataStringDetails fixedStringDetails = new ZanataStringDetails(translations, originalString);
                if (fixedStringDetails.getFixedString() != null) {
                    final String translation = translations.get(fixedStringDetails.getFixedString());

                    if (translation != null && !translation.isEmpty()) {
                        // Build up the padding that Zanata removed
                        final StringBuilder leftTrimPadding = new StringBuilder();
                        final StringBuilder rightTrimPadding = new StringBuilder();

                        for (int i = 0; i < fixedStringDetails.getLeftTrimCount(); ++i)
                            leftTrimPadding.append(" ");

                        for (int i = 0; i < fixedStringDetails.getRightTrimCount(); ++i)
                            rightTrimPadding.append(" ");

                        final String fixedTranslation = leftTrimPadding.toString() + translation + rightTrimPadding.toString();

                        for (final CSNodeWrapper node : nodeCollections) {
                            final Node contentSpecNode = findMatchingContentSpecNode(contentSpec, node.getId());
                            if (contentSpecNode != null) {
                                if (contentSpecNode instanceof KeyValueNode) {
                                    ((KeyValueNode) contentSpecNode).setValue(fixedTranslation);
                                } else if (contentSpecNode instanceof Level) {
                                    ((Level) contentSpecNode).setTranslatedTitle(fixedTranslation);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static Node findMatchingContentSpecNode(final ContentSpec contentSpec, final Integer csNodeId) {
        for (final Node node : contentSpec.getNodes()) {
            if (node.getUniqueId() != null && node.getUniqueId().equals(csNodeId)) {
                return node;
            }
        }

        return findMatchingContentSpecNode(contentSpec.getBaseLevel(), csNodeId);
    }

    public static Node findMatchingContentSpecNode(final Level level, final Integer csNodeId) {
        for (final Node node : level.getChildNodes()) {
            if (node instanceof Level) {
                return findMatchingContentSpecNode((Level) node, csNodeId);
            } else if (node.getUniqueId() != null && node.getUniqueId().equals(csNodeId)) {
                return node;
            }
        }

        return null;
    }

    private static StringToCSNodeCollection findExistingText(final String text, final List<StringToCSNodeCollection> translationStrings) {
        for (final StringToCSNodeCollection stringToNodeCollection : translationStrings) {
            if (stringToNodeCollection.getTranslationString().equals(text)) return stringToNodeCollection;
        }

        return null;
    }

    private static void addTranslationToNodeDetailsToCollection(final String text, final CSNodeWrapper node, final boolean allowDuplicates,
            final List<StringToCSNodeCollection> translationStrings) {
        final ArrayList<CSNodeWrapper> nodes = new ArrayList<CSNodeWrapper>();
        nodes.add(node);
        addTranslationToNodeDetailsToCollection(text, nodes, allowDuplicates, translationStrings);
    }

    private static void addTranslationToNodeDetailsToCollection(final String text, final ArrayList<CSNodeWrapper> nodes,
            final boolean allowDuplicates, final List<StringToCSNodeCollection> translationStrings) {

        if (allowDuplicates) {
            translationStrings.add(new StringToCSNodeCollection(text).addNodeCollection(nodes));
        } else {
            final StringToCSNodeCollection stringToNodeCollection = findExistingText(text, translationStrings);

            if (stringToNodeCollection == null) translationStrings.add(new StringToCSNodeCollection(text).addNodeCollection(nodes));
            else stringToNodeCollection.addNodeCollection(nodes);
        }
    }

    /*
     * Gets a list of Revision's from the CSProcessor database for a specific content spec
     */
    public static RevisionList getContentSpecRevisionsById(final ContentSpecProvider contentSpecProvider, final Integer csId) {
        final List<Revision> results = new ArrayList<Revision>();
        final CollectionWrapper<ContentSpecWrapper> contentSpecRevisions = contentSpecProvider.getContentSpec(csId).getRevisions();

        // Create the unique array from the revisions
        if (contentSpecRevisions != null && contentSpecRevisions.getItems() != null) {
            final List<ContentSpecWrapper> contentSpecRevs = contentSpecRevisions.getItems();
            for (final ContentSpecWrapper contentSpecRev : contentSpecRevs) {
                Revision revision = new Revision();
                revision.setRevision(contentSpecRev.getRevision());
                revision.setDate(contentSpecRev.getLastModified());
                results.add(revision);
            }

            Collections.sort(results, new EnversRevisionSort());

            return new RevisionList(csId, "Content Specification", results);
        } else {
            return null;
        }
    }

    public static Map<String, SpecTopic> getUniqueIdSpecTopicMap(ContentSpec contentSpec) {
        // Create the map of unique ids to spec topics
        final Map<String, SpecTopic> specTopicMap = new HashMap<String, SpecTopic>();
        final List<SpecTopic> specTopics = contentSpec.getSpecTopics();
        for (final SpecTopic specTopic : specTopics) {
            specTopicMap.put(specTopic.getUniqueId(), specTopic);
        }
        return specTopicMap;
    }

    /**
     * Check to see if a Meta Data line is a Spec Topic Meta Data, based on the key value.
     *
     * @param key The Meta Data key.
     * @return True if the Meta Data is a Spec Topic Meta Data, otherwise false.
     */
    public static boolean isSpecTopicMetaData(final String key) {
        return key.equalsIgnoreCase(CSConstants.LEGAL_NOTICE_TITLE) || key.equalsIgnoreCase(
                CSConstants.REV_HISTORY_TITLE) || key.equalsIgnoreCase(CSConstants.FEEDBACK_TITLE);
    }
}

/**
 * Zanata will modify strings sent to it for translation. This class contains the info necessary to take a string from Zanata and match
 * it to the source XML.
 */
class ZanataStringDetails {
    /**
     * The number of spaces that Zanata removed from the left
     */
    private final int leftTrimCount;
    /**
     * The number of spaces that Zanata removed from the right
     */
    private final int rightTrimCount;
    /**
     * The string that was matched to the one returned by Zanata. This will be null if there was no match.
     */
    private final String fixedString;

    ZanataStringDetails(final Map<String, String> translations, final String originalString) {
        /*
         * Here we account for any trimming that is done by Zanata.
         */
        final String lTrimtString = StringUtilities.ltrim(originalString);
        final String rTrimString = StringUtilities.rtrim(originalString);
        final String trimString = originalString.trim();

        final boolean containsExactMacth = translations.containsKey(originalString);
        final boolean lTrimMatch = translations.containsKey(lTrimtString);
        final boolean rTrimMatch = translations.containsKey(rTrimString);
        final boolean trimMatch = translations.containsKey(trimString);

        /* remember the details of the trimming, so we can add the padding back */
        if (containsExactMacth) {
            leftTrimCount = 0;
            rightTrimCount = 0;
            fixedString = originalString;
        } else if (lTrimMatch) {
            leftTrimCount = originalString.length() - lTrimtString.length();
            rightTrimCount = 0;
            fixedString = lTrimtString;
        } else if (rTrimMatch) {
            leftTrimCount = 0;
            rightTrimCount = originalString.length() - rTrimString.length();
            fixedString = rTrimString;
        } else if (trimMatch) {
            leftTrimCount = StringUtilities.ltrimCount(originalString);
            rightTrimCount = StringUtilities.rtrimCount(originalString);
            fixedString = trimString;
        } else {
            leftTrimCount = 0;
            rightTrimCount = 0;
            fixedString = null;
        }
    }

    public int getLeftTrimCount() {
        return leftTrimCount;
    }

    public int getRightTrimCount() {
        return rightTrimCount;
    }

    public String getFixedString() {
        return fixedString;
    }
}
