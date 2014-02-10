package org.jboss.pressgang.ccms.contentspec;

import java.util.ArrayList;
import java.util.List;

import org.jboss.pressgang.ccms.utils.common.StringUtilities;

/**
 * An abstract class that contains the base objects required for a Content Specification Node.
 *
 * @author lnewson
 */
public abstract class SpecNode extends Node {
    protected List<String> tags = new ArrayList<String>();
    protected List<String> removeTags = new ArrayList<String>();
    protected List<String> sourceUrls = new ArrayList<String>();
    protected String condition;
    protected String description;
    protected String assignedWriter;
    protected String targetId;
    protected String title;
    protected String duplicateId;

    protected SpecNode(final int lineNumber, final String text) {
        super(lineNumber, text);
    }

    protected SpecNode(final String text) {
        super(text);
    }

    protected SpecNode() {
    }

    /**
     * Gets the title of the Level.
     *
     * @return The title of the Level.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title for the Level.
     *
     * @param title The title for the Level.
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDuplicateId() {
        return duplicateId;
    }

    public void setDuplicateId(final String duplicateId) {
        this.duplicateId = duplicateId;
    }

    /**
     * Gets the Target ID for the Content Specification Node if one exists.
     *
     * @return The Target ID or null if none exist.
     */
    public String getTargetId() {
        return targetId;
    }

    /**
     * Set the Target ID for the Content Specification Node.
     *
     * @param targetId The Target ID for the Node.
     */
    public void setTargetId(final String targetId) {
        this.targetId = targetId;
    }

    /**
     * Checks if the target id is only an internally used id, used for processes
     *
     * @return True if the target id is an internal id, otherwise false.
     */
    public boolean isTargetIdAnInternalId() {
        return targetId == null ? false : getTargetId().matches("^T-" + getUniqueId() + "0[0-9]+$");
    }

    /**
     * Sets the description for a node.
     *
     * @param desc The description.
     */
    public void setDescription(final String desc) {
        description = desc;
    }

    /**
     * Get the description for a node. If useInherited is true then it will check for an inherited description as well.
     *
     * @param useInherited If the function should check for an inherited description
     * @return The description as a String
     */
    public String getDescription(final boolean useInherited) {
        if (description == null && parent != null && useInherited) {
            if (parent instanceof ContentSpec) {
                return ((ContentSpec) parent).getBaseLevel().getDescription(true);
            } else if (parent instanceof KeyValueNode) {
                return ((KeyValueNode) parent).getParent().getBaseLevel().getDescription(true);
            } else {
                return ((SpecNode) parent).getDescription(true);
            }
        } else {
            return description;
        }
    }

    /**
     * Sets the Assigned Writer for this set of options
     *
     * @param writer The writers name that matches to the assigned writer tag in the database
     */
    public void setAssignedWriter(final String writer) {
        assignedWriter = writer;
    }

    /**
     * Get the Assigned Writer for a topic. If useInherited is true then it will check for an inherited writer as well.
     *
     * @param useInherited If the function should check for an inherited writer
     * @return The Assigned Writers name as a String
     */
    public String getAssignedWriter(final boolean useInherited) {
        if (assignedWriter == null && parent != null && useInherited) {
            if (parent instanceof ContentSpec) {
                return ((ContentSpec) parent).getBaseLevel().getAssignedWriter(true);
            } else if (parent instanceof KeyValueNode) {
                return ((KeyValueNode) parent).getParent().getBaseLevel().getAssignedWriter(true);
            } else {
                return ((SpecNode) parent).getAssignedWriter(true);
            }
        }
        return assignedWriter;
    }

    /**
     * Sets the set of tags for this set of options
     *
     * @param tags A HashMap of tags. The key in the map is the tags category name and the value is an ArrayList of tags for
     *             each category.
     */
    public void setTags(final List<String> tags) {
        this.tags = tags;
    }

    /**
     * Gets the set of tags for this set of options. If useInherited is true then it will check for inherited options as well.
     * <p/>
     * This function also removes the tags from the HashMap for any tag that has a - in front of its name.
     *
     * @param useInherited If the function should check for inherited tags
     */
    public List<String> getTags(final boolean useInherited) {
        List<String> temp = new ArrayList<String>();
        // Get the inherited tags
        if (useInherited && parent != null) {
            if (parent instanceof ContentSpec) {
                temp = ((ContentSpec) super.getParent()).getBaseLevel().getTags(true);
            } else if (parent instanceof KeyValueNode) {
                temp = ((KeyValueNode) super.getParent()).getParent().getBaseLevel().getTags(true);
            } else {
                temp = ((SpecNode) getParent()).getTags(true);
            }
        }

        // If the local tags are null then just return the temp collection
        if (tags == null) {
            return temp;
        } else {
            temp.addAll(tags);

            // Remove the tags that are set to be removed
            final List<String> newTags = new ArrayList<String>();
            for (final String tagName : temp) {
                final List<String> tempTags = getRemoveTags(useInherited);
                boolean found = false;
                for (final String removeTagName : tempTags) {
                    if (removeTagName.equals(tagName)) {
                        found = true;
                    }
                }
                if (!found) {
                    newTags.add(tagName);
                }
            }
            temp = newTags;
            return temp;
        }
    }

    /**
     * Sets the list of tags that are to be removed in this set of options
     *
     * @param tags An ArrayList of tags to be removed
     */
    public void setRemoveTags(final List<String> tags) {
        removeTags = tags;
    }

    /**
     * Gets an ArrayList of tags that are to be removed for these options. If useInherited is true then it will also add all
     * inherited removeable tags.
     *
     * @param useInherited If the function should check for inherited removable tags
     * @return An ArrayList of tags
     */
    public List<String> getRemoveTags(final boolean useInherited) {
        List<String> temp = new ArrayList<String>();
        // Get the parent remove tags if requested
        if (useInherited && parent != null) {
            if (parent instanceof ContentSpec) {
                temp = ((ContentSpec) parent).getBaseLevel().getRemoveTags(true);
            } else if (parent instanceof KeyValueNode) {
                temp = ((KeyValueNode) parent).getParent().getBaseLevel().getRemoveTags(true);
            } else {
                temp = ((SpecNode) parent).getRemoveTags(true);
            }
        }

        // Add any local remove tags
        if (removeTags != null) {
            temp.addAll(removeTags);
        }

        return temp;
    }

    /**
     * Sets the list of source urls in this node
     *
     * @param sourceUrls An ArrayList of urls
     */
    public void setSourceUrls(final List<String> sourceUrls) {
        this.sourceUrls = sourceUrls;
    }

    /**
     * Get the Source Urls for a node and also checks to make sure the url hasn't already been inherited
     *
     * @param useInherited If the function should check for inherited source urls
     * @return A List of Strings that represent the source urls
     */
    public List<String> getSourceUrls(boolean useInherited) {
        List<String> temp = new ArrayList<String>();
        // Get the parent source urls if requested
        if (useInherited && parent != null) {
            if (parent instanceof ContentSpec) {
                temp = ((ContentSpec) parent).getBaseLevel().getSourceUrls(true);
            } else if (parent instanceof KeyValueNode) {
                temp = ((KeyValueNode) parent).getParent().getBaseLevel().getSourceUrls(true);
            } else {
                temp = ((SpecNode) parent).getSourceUrls(true);
            }
        }

        // Add any local source urls
        if (sourceUrls != null) {
            temp.addAll(sourceUrls);
        }

        return temp;
    }

    /**
     * Adds a tag to the list of tags. If the tag starts with a - then its added to the remove tag list otherwise its added to
     * the normal tag mapping. Also strips off + & - from the start of tags.
     *
     * @param tagName The name of the Tag to be added.
     * @return True if the tag was added successfully otherwise false.
     */
    public boolean addTag(final String tagName) {
        String name = tagName;
        // Remove the + or - from the tag temporarily to get the tag from the database
        if (tagName.startsWith("-") || tagName.startsWith("+")) {
            name = name.substring(1).trim();
        }

        // Check to see which set of tags to add to. The removeTags or additional tags.
        if (tagName.startsWith("-")) {
            if (removeTags.contains(name)) {
                return false;
            } else {
                removeTags.add(name);
            }
        } else {
            if (tags.contains(name)) {
                return false;
            } else {
                tags.add(name);
            }
        }
        return true;
    }

    /**
     * Adds an array of tags to the list of tags for this node
     *
     * @param tagArray A list of tags by name that are to be added.
     * @return True if all the tags were added successfully otherwise false.
     */
    public boolean addTags(final List<String> tagArray) {
        boolean error = false;
        for (final String tag : tagArray) {
            if (!addTag(tag)) {
                error = true;
            }
        }
        return !error;
    }

    /**
     * Adds a source URL to the list of URL's for this set of node
     *
     * @param url The URL to be added
     */
    public void addSourceUrl(final String url) {
        if (sourceUrls.contains(url)) return;
        sourceUrls.add(url);
    }

    /**
     * Removes a specific Source URL from the list of URL's
     *
     * @param url The URL to be removed.
     */
    public void removeSourceUrl(final String url) {
        sourceUrls.remove(url);
    }

    /**
     * Sets the conditional statement to be used when building
     *
     * @param condition The conditional statement for this node and it's sub nodes.
     */
    public void setConditionStatement(final String condition) {
        this.condition = condition;
    }

    /**
     * Gets the conditional statement to be used when building
     *
     * @return The conditional statement for this node and it's sub nodes.
     */
    public String getConditionStatement() {
        return getConditionStatement(false);
    }

    /**
     * Gets the conditional statement to be used when building
     *
     * @param useInherited If the conditional statement should be pulled from its parent nodes.
     * @return The conditional statement for this node and it's sub nodes.
     */
    public String getConditionStatement(final boolean useInherited) {
        if (condition == null && useInherited && parent != null) {
            if (parent instanceof ContentSpec) {
                return ((ContentSpec) parent).getBaseLevel().getConditionStatement(true);
            } else if (parent instanceof KeyValueNode) {
                final KeyValueNode<?> keyValueNode = ((KeyValueNode) parent);
                if (keyValueNode.getParent() != null) {
                    return keyValueNode.getParent().getBaseLevel().getConditionStatement(true);
                } else {
                    return null;
                }
            } else {
                return ((SpecNode) parent).getConditionStatement(true);
            }
        } else {
            return condition;
        }
    }

    /**
     * Gets a string representation of the options in this node. (Tags, Source URL's, Description and Writer)
     *
     * @return The String representation of the options.
     */
    protected String getOptionsString() {
        final ArrayList<String> vars = new ArrayList<String>();
        if (!tags.isEmpty()) {
            vars.addAll(tags);
        }

        if (!removeTags.isEmpty()) {
            for (final String removeTag : removeTags) {
                vars.add("-" + removeTag);
            }
        }

        if (!sourceUrls.isEmpty()) {
            for (final String url : sourceUrls) {
                vars.add("URL = " + url);
            }
        }

        if (assignedWriter != null && !assignedWriter.trim().isEmpty()) {
            vars.add("Writer = " + assignedWriter);
        }

        if (description != null && !description.trim().isEmpty()) {
            vars.add("Description = " + description);
        }

        if (condition != null && !condition.trim().isEmpty()) {
            vars.add("condition = " + condition);
        }
        return StringUtilities.buildString(vars.toArray(new String[vars.size()]), ", ");
    }

    public abstract String getUniqueLinkId(final Integer fixedUrlPropertyTagId, final boolean useFixedUrls);
}
