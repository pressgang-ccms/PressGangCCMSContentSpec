package org.jboss.pressgang.ccms.contentspec.utils;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;
import org.jboss.pressgang.ccms.contentspec.ContentSpec;
import org.jboss.pressgang.ccms.contentspec.Level;
import org.jboss.pressgang.ccms.contentspec.Node;
import org.jboss.pressgang.ccms.contentspec.SpecTopic;
import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;
import org.jboss.pressgang.ccms.contentspec.entities.Revision;
import org.jboss.pressgang.ccms.contentspec.entities.RevisionList;
import org.jboss.pressgang.ccms.contentspec.sort.EnversRevisionSort;
import org.jboss.pressgang.ccms.provider.ContentSpecProvider;
import org.jboss.pressgang.ccms.utils.common.DocBookUtilities;
import org.jboss.pressgang.ccms.utils.common.StringUtilities;
import org.jboss.pressgang.ccms.utils.common.XMLUtilities;
import org.jboss.pressgang.ccms.utils.constants.CommonConstants;
import org.jboss.pressgang.ccms.utils.structures.DocBookVersion;
import org.jboss.pressgang.ccms.wrapper.CSNodeWrapper;
import org.jboss.pressgang.ccms.wrapper.ContentSpecWrapper;
import org.jboss.pressgang.ccms.wrapper.collection.CollectionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;


public class ContentSpecUtilities {
    private static final Logger log = LoggerFactory.getLogger(ContentSpecUtilities.class);
    public static final Pattern CS_CHECKSUM_PATTERN = Pattern.compile("CHECKSUM[ ]*=[ ]*(?<Checksum>[A-Za-z0-9]+)(\r)?\n");
    public static final Pattern CS_ID_PATTERN = Pattern.compile("ID[ ]*=[ ]*(?<ID>[0-9]+)(\r)?\n");
    private static final String ENCODING = "UTF-8";

    protected ContentSpecUtilities() {
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
     * Get the ID of a Content Specification object.
     *
     * @param contentSpecString The content spec string to calculate the checksum for.
     * @return The ID of the Content Spec, or null if it isn't set.
     */
    public static Integer getContentSpecID(final String contentSpecString) {
        final Matcher matcher = CS_ID_PATTERN.matcher(contentSpecString);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group("ID"));
        }

        return null;
    }

    public static String removeChecksum(final String contentSpecString) {
        String retValue = contentSpecString;

        Matcher matcher = CS_CHECKSUM_PATTERN.matcher(retValue);
        if (matcher.find()) {
            retValue = matcher.replaceFirst("");
        }

        return retValue;
    }

    public static String removeChecksumAndId(final String contentSpecString) {
        String retValue = contentSpecString;

        Matcher matcher = CS_CHECKSUM_PATTERN.matcher(retValue);
        if (matcher.find()) {
            retValue = matcher.replaceFirst("");
        }

        matcher = CS_ID_PATTERN.matcher(retValue);
        if (matcher.find()) {
            retValue = matcher.replaceFirst("");
        }

        return retValue;
    }

    /**
     * Replaces the checksum of a Content Spec with a new checksum value
     *
     * @param contentSpecString The content spec to replace the checksum for.
     * @param checksum          The new checksum to be set in the Content Spec.
     * @return The fixed content spec string.
     */
    public static String replaceChecksum(final String contentSpecString, final String checksum) {
        Matcher matcher = CS_CHECKSUM_PATTERN.matcher(contentSpecString);
        if (matcher.find()) {
            return matcher.replaceFirst("CHECKSUM=" + checksum + "\n");
        }

        return contentSpecString;
    }

    /**
     * Fixes a failed Content Spec so that the ID and CHECKSUM are included. This is primarily an issue when creating new content specs
     * and they are initially invalid.
     *
     * @param contentSpec     The content spec to fix.
     * @param validText       The content specs latest valid text.
     * @param includeChecksum If the checksum should be included in the output.
     * @return The fixed failed content spec string.
     */
    public static String fixFailedContentSpec(final ContentSpecWrapper contentSpec, final String validText, final boolean includeChecksum) {
        return fixFailedContentSpec(contentSpec.getId(), contentSpec.getFailed(), validText, includeChecksum);
    }

    /**
     * Fixes a failed Content Spec so that the ID and CHECKSUM are included. This is primarily an issue when creating new content specs
     * and they are initially invalid.
     *
     * @param id                The id of the content spec tha tis being fixed.
     * @param failedContentSpec The failed content spec to fix.
     * @param validText         The content specs latest valid text.
     * @param includeChecksum   If the checksum should be included in the output.
     * @return The fixed failed content spec string.
     */
    public static String fixFailedContentSpec(final Integer id, final String failedContentSpec, final String validText,
            final boolean includeChecksum) {
        if (failedContentSpec == null || failedContentSpec.isEmpty()) {
            return null;
        } else if (includeChecksum) {
            if (id == null || getContentSpecChecksum(failedContentSpec) != null) {
                return failedContentSpec;
            } else {
                final String cleanContentSpec = removeChecksumAndId(failedContentSpec);
                final String checksum = getContentSpecChecksum(validText);
                return CommonConstants.CS_CHECKSUM_TITLE + " = " + checksum + "\n" + CommonConstants.CS_ID_TITLE + " = " +
                        id + "\n" + cleanContentSpec;
            }
        } else {
            final String cleanContentSpec = removeChecksum(failedContentSpec);
            if (id == null || getContentSpecID(cleanContentSpec) != null) {
                return cleanContentSpec;
            } else {
                return CommonConstants.CS_ID_TITLE + " = " + id + "\n" + cleanContentSpec;
            }
        }
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

    public static Node findMatchingContentSpecNode(final ContentSpec contentSpec, final Integer csNodeId) {
        for (final Node node : contentSpec.getNodes()) {
            if (node.getUniqueId() != null && node.getUniqueId().equals(csNodeId + "")) {
                return node;
            }
        }

        return findMatchingContentSpecNode(contentSpec.getBaseLevel(), csNodeId);
    }

    public static Node findMatchingContentSpecNode(final Level level, final Integer csNodeId) {
        // Check the node
        if (level.getUniqueId() != null && level.getUniqueId().equals(csNodeId + "")) {
            return level;
        }

        for (final Node node : level.getChildNodes()) {
            // Check the children of the node if it is a level
            if (node instanceof Level) {
                final Node foundNode = findMatchingContentSpecNode((Level) node, csNodeId);
                if (foundNode != null) {
                    return foundNode;
                }
            } else {
                if (node.getUniqueId() != null && node.getUniqueId().equals(csNodeId + "")) {
                    return node;
                }
            }
        }

        return null;
    }

    /**
     * Recursively find all of a Content Specs child nodes.
     *
     * @param contentSpec The content spec to get all the children nodes from.
     * @return A list of children nodes that exist for the content spec.
     */
    public static List<CSNodeWrapper> getAllNodes(final ContentSpecWrapper contentSpec) {
        final List<CSNodeWrapper> nodes = new LinkedList<CSNodeWrapper>();
        if (contentSpec.getChildren() != null) {
            final List<CSNodeWrapper> childrenNodes = contentSpec.getChildren().getItems();
            for (final CSNodeWrapper childNode : childrenNodes) {
                nodes.add(childNode);
                nodes.addAll(getAllChildrenNodes(childNode));
            }
        }

        return nodes;
    }

    /**
     * Recursively find all of a Content Spec Nodes children.
     *
     * @param csNode The node to get all the children nodes from.
     * @return A list of children nodes that exist for the node.
     */
    public static List<CSNodeWrapper> getAllChildrenNodes(final CSNodeWrapper csNode) {
        final List<CSNodeWrapper> nodes = new LinkedList<CSNodeWrapper>();
        if (csNode.getChildren() != null) {
            final List<CSNodeWrapper> childrenNodes = csNode.getChildren().getItems();
            for (final CSNodeWrapper childNode : childrenNodes) {
                nodes.add(childNode);
                nodes.addAll(getAllChildrenNodes(childNode));
            }
        }

        return nodes;
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

    public static Map<String, SpecTopic> getTargetIdSpecTopicMap(ContentSpec contentSpec) {
        // Create the map of unique ids to spec topics
        final Map<String, SpecTopic> specTopicMap = new HashMap<String, SpecTopic>();
        final List<SpecTopic> specTopics = contentSpec.getSpecTopics();
        for (final SpecTopic specTopic : specTopics) {
            if (specTopic.getTargetId() != null) {
                specTopicMap.put(specTopic.getTargetId(), specTopic);
            }
        }
        return specTopicMap;
    }

    public static Map<String, List<SpecTopic>> getIdSpecTopicMap(ContentSpec contentSpec) {
        // Create the map of unique ids to spec topics
        final Map<String, List<SpecTopic>> specTopicMap = new HashMap<String, List<SpecTopic>>();
        final List<SpecTopic> specTopics = contentSpec.getSpecTopics();
        for (final SpecTopic specTopic : specTopics) {
            if (specTopic.getId() != null) {
                if (!specTopicMap.containsKey(specTopic.getId())) {
                    specTopicMap.put(specTopic.getId(), new ArrayList<SpecTopic>());
                }
                specTopicMap.get(specTopic.getId()).add(specTopic);
            }
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
        return key.equalsIgnoreCase(CommonConstants.CS_LEGAL_NOTICE_TITLE) || key.equalsIgnoreCase(
                CommonConstants.CS_REV_HISTORY_TITLE) || key.equalsIgnoreCase(CommonConstants.CS_FEEDBACK_TITLE) || key.equalsIgnoreCase(
                CommonConstants.CS_AUTHOR_GROUP_TITLE) || key.equalsIgnoreCase(CommonConstants.CS_ABSTRACT_TITLE) || key.equalsIgnoreCase(
                CommonConstants.CS_ABSTRACT_ALTERNATE_TITLE);
    }

    /**
     * Checks if a line is a multiple line metadata node. These are the elements that are classed as multiple line metadata elements:
     * <ul>
     * <li>publican.cfg</li>
     * <li>&lt;NAME&gt;-publican.cfg</li>
     * <li>Entities</li>
     * <li>Abstract</li>
     * <li>Description</li>
     * </ul>
     *
     * @param key The key of the metadata line.
     * @return True if it should be parsed as a multiple line node, otherwise false.
     */
    public static boolean isMetaDataMultiLine(final String key) {
        return isNullOrEmpty(key) ? false : key.equalsIgnoreCase(CommonConstants.CS_PUBLICAN_CFG_TITLE) || key.equalsIgnoreCase(
                CommonConstants.CS_ENTITIES_TITLE) || CSConstants.CUSTOM_PUBLICAN_CFG_PATTERN.matcher(key).matches();
    }

    /**
     * Escapes a title so that it can be used in a Content Specification.
     *
     * @param title
     * @return
     */
    public static String escapeTitle(final String title) {
        if (title == null) {
            return null;
        } else {
            return title.replace("[", "\\[").replace("]", "\\]");
        }
    }

    /**
     * Escapes a title so that it can be used in a relationship in a Content Specification.
     *
     * @param title
     * @return
     */
    public static String escapeRelationshipTitle(final String title) {
        if (title == null) {
            return null;
        } else {
            return escapeTitle(title).replace(",", "\\,");
        }
    }

    public static String generateEntitiesForContentSpec(final ContentSpec contentSpec, final DocBookVersion docBookVersion,
            final String escapedTitle, final String originalTitle, final String originalProduct) {
        Document doc = null;
        if (!isNullOrEmpty(contentSpec.getEntities())) {
            try {
                final String wrappedEntities = "<!DOCTYPE section [" + contentSpec.getEntities() + "]><section></section>";
                doc = XMLUtilities.convertStringToDocument(wrappedEntities);
            } catch (Exception e) {
                log.debug("Invalid Content Specification entities", e);
            }
        }

        // Find what entities have already been defined
        final StringBuilder retValue = new StringBuilder(100);
        final List<String> definedEntities = new ArrayList<String>();
        if (doc != null) {
            final NamedNodeMap entityNodes = doc.getDoctype().getEntities();
            for (int i = 0; i < entityNodes.getLength(); i++) {
                final org.w3c.dom.Node entityNode = entityNodes.item(i);
                definedEntities.add(entityNode.getNodeName());
            }
        }

        // Add the default entities
        // BOOKID
        if (!definedEntities.contains("BOOKID")) {
            retValue.append("<!ENTITY BOOKID \"").append(escapedTitle).append("\">\n");
        }

        // PRODUCT
        if (!definedEntities.contains("PRODUCT")) {
            final String escapedProduct = escapeForXMLEntity(contentSpec.getProduct());
            retValue.append("<!ENTITY PRODUCT \"").append(escapedProduct).append("\">\n");
        }

        // TITLE
        if (!definedEntities.contains("TITLE")) {
            final String title = escapeTitleForXMLEntity(originalTitle);
            retValue.append("<!ENTITY TITLE \"").append(title).append("\">\n");
        }

        // YEAR
        if (!definedEntities.contains("YEAR")) {
            final String year = contentSpec.getCopyrightYear() == null ? Integer.toString(
                    Calendar.getInstance().get(Calendar.YEAR)) : contentSpec.getCopyrightYear();
            retValue.append("<!ENTITY YEAR \"").append(year).append("\">\n");
        }

        // HOLDER
        if (!definedEntities.contains("HOLDER")) {
            final String escapedHolder = escapeForXMLEntity(contentSpec.getCopyrightHolder());
            retValue.append("<!ENTITY HOLDER \"").append(escapedHolder).append("\">\n");
        }

        // BZPRODUCT
        if (!definedEntities.contains("BZPRODUCT")) {
            final String escapedBZProduct = escapeForXMLEntity(
                    contentSpec.getBugzillaProduct() == null ? originalProduct : contentSpec.getBugzillaProduct());
            retValue.append("<!ENTITY BZPRODUCT \"").append(escapedBZProduct).append("\">\n");
        }

        // BZCOMPONENT
        if (!definedEntities.contains("BZCOMPONENT")) {
            final String escapedBZComponent = escapeForXMLEntity(
                    contentSpec.getBugzillaComponent() == null ? CSConstants.DEFAULT_BZCOMPONENT : contentSpec.getBugzillaComponent());
            retValue.append("<!ENTITY BZCOMPONENT \"").append(escapedBZComponent).append("\">\n");
        }

        // BZURL
        if (!definedEntities.contains("BZURL")) {
            final String host = isNullOrEmpty(contentSpec.getBugzillaServer()) ? CSConstants.DEFAULT_BUGZILLA_URL : contentSpec
                    .getBugzillaServer();
            try {
                final StringBuilder fixedBZURL = new StringBuilder();
                if (contentSpec.getBugzillaURL() == null) {
                    if (docBookVersion == DocBookVersion.DOCBOOK_50) {
                        final String linkEle = DocBookUtilities.addDocBook50Namespace("<link>", "link").replace("\"", "'").replace(">", "");
                        fixedBZURL.append(linkEle).append(" xlink:href='");
                    } else {
                        fixedBZURL.append("<ulink url='");
                    }
                    fixedBZURL.append(host);
                    fixedBZURL.append("enter_bug.cgi");
                    // Add in the product specific link details
                    if (contentSpec.getBugzillaProduct() != null) {
                        final String encodedProduct = URLEncoder.encode(contentSpec.getBugzillaProduct(), ENCODING);
                        fixedBZURL.append("?product=").append(escapeForXMLEntity(encodedProduct));
                        if (contentSpec.getBugzillaComponent() != null) {
                            final String encodedComponent = URLEncoder.encode(contentSpec.getBugzillaComponent(), ENCODING);
                            fixedBZURL.append("&amp;component=").append(escapeForXMLEntity(encodedComponent));
                        }
                        if (contentSpec.getBugzillaVersion() != null) {
                            final String encodedVersion = URLEncoder.encode(contentSpec.getBugzillaVersion(), ENCODING);
                            fixedBZURL.append("&amp;version=").append(escapeForXMLEntity(encodedVersion));
                        }
                    }
                    fixedBZURL.append("'>").append(host);
                    if (docBookVersion == DocBookVersion.DOCBOOK_50) {
                        fixedBZURL.append("</link>");
                    } else {
                        fixedBZURL.append("</ulink>");
                    }
                } else {
                    fixedBZURL.append(escapeForXMLEntity(contentSpec.getBugzillaURL()));
                }

                retValue.append("<!ENTITY BZURL \"").append(fixedBZURL).append("\">\n");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        // Add the custom entities if any exist
        if (doc != null) {
            retValue.append(contentSpec.getEntities().trim());
        }

        return retValue.toString();
    }

    protected static String escapeForXMLEntity(final String input) {
        return StringUtilities.escapeForXML(input).replace("%", "&percnt;");
    }

    protected static String escapeTitleForXMLEntity(final String input) {
        return DocBookUtilities.escapeForXML(input).replace("%", "&percnt;");
    }

    public static DocBookVersion getDocBookVersion(final ContentSpec contentSpec) {
        if (CommonConstants.DOCBOOK_50_TITLE.equals(contentSpec.getFormat())) {
            return DocBookVersion.DOCBOOK_50;
        } else {
            return DocBookVersion.DOCBOOK_45;
        }
    }

    /**
     * Gets the entities that are allowed to be used in a Content Specification.
     *
     * @param contentSpec The content spec object to get the entities from.
     * @return A {@link List} of {@link org.w3c.dom.Entity} objects created from the content spec entities.
     */
    public static List<Entity> getContentSpecEntities(final ContentSpec contentSpec) {
        final DocBookVersion docBookVersion = getDocBookVersion(contentSpec);
        final String escapedTitle = DocBookUtilities.escapeTitle(contentSpec.getTitle());
        final String entitiesString = ContentSpecUtilities.generateEntitiesForContentSpec(contentSpec, docBookVersion, escapedTitle,
                contentSpec.getTitle(), contentSpec.getProduct());
        return XMLUtilities.parseEntitiesFromString(entitiesString);
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
