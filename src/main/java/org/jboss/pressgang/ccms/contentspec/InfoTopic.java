package org.jboss.pressgang.ccms.contentspec;

import java.util.ArrayList;
import java.util.List;

import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;
import org.jboss.pressgang.ccms.contentspec.enums.TopicType;
import org.jboss.pressgang.ccms.utils.common.StringUtilities;
import org.jboss.pressgang.ccms.wrapper.base.BaseTopicWrapper;
import org.w3c.dom.Document;

public class InfoTopic implements ITopicNode {
    protected String id;
    protected Integer DBId = null;
    protected Integer revision = null;
    protected List<String> tags = new ArrayList<String>();
    protected List<String> removeTags = new ArrayList<String>();
    protected Level level;
    protected String uniqueId = null;
    protected String translationUniqueId = null;
    protected String condition;
    protected String description;
    protected String assignedWriter;
    protected String duplicateId;

    protected BaseTopicWrapper<?> topic = null;
    protected Document xmlDocument = null;

    /**
     * Constructor
     *
     * @param level The level this info topic contains information for.
     */
    public InfoTopic(final Level level) {
        this.level = level;
    }

    /**
     * Constructor
     *
     * @param DBId  The Database ID of the info topic.
     * @param level The level this info topic contains information for.
     */
    public InfoTopic(int DBId, final Level level) {
        id = Integer.toString(DBId);
        this.DBId = DBId;
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    protected void setLevel(final Level level) {
        this.level = level;
    }

    @Override
    public void setUniqueId(final String uniqueId) {
        this.uniqueId = uniqueId == null ? null : uniqueId.replaceAll("[^\\w\\d\\-]", "");
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public void setTranslationUniqueId(final String translationUniqueId) {
        this.translationUniqueId = translationUniqueId == null ? null : translationUniqueId.replaceAll("[^\\w\\d\\-]", "");
    }

    @Override
    public String getTranslationUniqueId() {
        return translationUniqueId;
    }

    @Override
    public String getDuplicateId() {
        return duplicateId;
    }

    @Override
    public void setDuplicateId(final String duplicateId) {
        this.duplicateId = duplicateId;
    }

    @Override
    public void setDescription(final String desc) {
        description = desc;
    }

    @Override
    public String getDescription(final boolean useInherited) {
        if (description == null && level != null && useInherited) {
            return level.getDescription(true);
        } else {
            return description;
        }
    }

    @Override
    public void setAssignedWriter(final String writer) {
        assignedWriter = writer;
    }

    @Override
    public String getAssignedWriter(final boolean useInherited) {
        if (assignedWriter == null && level != null && useInherited) {
            return level.getAssignedWriter(true);
        }
        return assignedWriter;
    }

    @Override
    public void setTags(final List<String> tags) {
        this.tags = tags;
    }

    @Override
    public List<String> getTags(final boolean useInherited) {
        List<String> temp = new ArrayList<String>();
        // Get the inherited tags
        if (useInherited && level != null) {
            temp = level.getTags(true);
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

    @Override
    public void setRemoveTags(final List<String> tags) {
        removeTags = tags;
    }

    @Override
    public List<String> getRemoveTags(final boolean useInherited) {
        List<String> temp = new ArrayList<String>();
        // Get the parent remove tags if requested
        if (useInherited && level != null) {
            temp = level.getRemoveTags(true);
        }

        // Add any local remove tags
        if (removeTags != null) {
            temp.addAll(removeTags);
        }

        return temp;
    }

    @Override
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

    @Override
    public boolean addTags(final List<String> tagArray) {
        boolean error = false;
        for (final String tag : tagArray) {
            if (!addTag(tag)) {
                error = true;
            }
        }
        return !error;
    }

    @Override
    public void setConditionStatement(final String condition) {
        this.condition = condition;
    }

    @Override
    public String getConditionStatement() {
        return getConditionStatement(false);
    }

    @Override
    public String getConditionStatement(final boolean useInherited) {
        if (condition == null && useInherited && level != null) {
            return level.getConditionStatement(true);
        } else {
            return condition;
        }
    }

    /**
     * Get the underlying topic that this Spec Topic represents.
     *
     * @return The underlying topic if it has been set otherwise null.
     */
    public BaseTopicWrapper<?> getTopic() {
        return topic;
    }

    /**
     * Set the underlying topic that this spec topic represents.
     *
     * @param topic The underlying topic.
     */
    public void setTopic(final BaseTopicWrapper<?> topic) {
        this.topic = topic;
    }

    @Override
    public void setId(final String id) {
        // Set the DBId as well if it isn't a new id
        if (id.matches(CSConstants.EXISTING_TOPIC_ID_REGEX)) {
            DBId = Integer.parseInt(id);
        }
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setDBId(Integer id) {
        DBId = id;
    }

    @Override
    public Integer getDBId() {
        return DBId;
    }

    @Override
    public Integer getRevision() {
        return revision;
    }

    @Override
    public void setRevision(final Integer revision) {
        this.revision = revision;
    }

    @Override
    public boolean isTopicANewTopic() {
        return CSConstants.NEW_TOPIC_ID_PATTERN.matcher(id).matches();
    }

    @Override
    public boolean isTopicAnExistingTopic() {
        return id.matches(CSConstants.EXISTING_TOPIC_ID_REGEX);
    }

    @Override
    public boolean isTopicAClonedTopic() {
        return id.matches(CSConstants.CLONED_TOPIC_ID_REGEX);
    }

    @Override
    public boolean isTopicADuplicateTopic() {
        return id.matches(CSConstants.DUPLICATE_TOPIC_ID_REGEX);
    }

    @Override
    public boolean isTopicAClonedDuplicateTopic() {
        return id.matches(CSConstants.CLONED_DUPLICATE_TOPIC_ID_REGEX);
    }

    @Override
    public Document getXMLDocument() {
        return xmlDocument;
    }

    @Override
    public void setXMLDocument(final Document xmlDocument) {
        this.xmlDocument = xmlDocument;
    }

    public SpecTopic getClosestTopicByDBId(final Integer DBId, final boolean checkParentNode) {
        if (getLevel() != null) {
            return getLevel().getClosestTopicByDBId(DBId, checkParentNode);
        }

        return null;
    }

    public SpecNode getClosestSpecNodeByTargetId(final String targetId, final boolean checkParentNode) {
        if (getLevel() != null) {
            return getLevel().getClosestSpecNodeByTargetId(targetId, checkParentNode);
        }

        return null;
    }

    public String getText() {
        final StringBuilder output = new StringBuilder("[Info: ");
        final String idAndOptions = getIdAndOptionsString();
        output.append(idAndOptions).append("]");
        return output.toString();
    }

    @Override
    public int getLineNumber() {
        return level.getLineNumber();
    }

    @Override
    public TopicType getTopicType() {
        return TopicType.INFO;
    }

    @Override
    public Integer getStep() {
        return level.getStep();
    }

    /**
     * Get the ID and Options string for the topic.
     *
     * @return
     */
    protected String getIdAndOptionsString() {
        final String options = getOptionsString();
        if (isTopicANewTopic()) {
            return id + (options.equals("") ? "" : (", " + options));
        } else {
            return id + (revision == null ? "" : (", rev: " + revision)) + (options.equals("") ? "" : (", " + options));
        }
    }

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

        if (assignedWriter != null && !assignedWriter.trim().isEmpty()) {
            vars.add("writer = " + assignedWriter);
        }

        if (description != null && !description.trim().isEmpty()) {
            vars.add("description = " + description);
        }

        if (condition != null && !condition.trim().isEmpty()) {
            vars.add("condition = " + condition);
        }
        return StringUtilities.buildString(vars.toArray(new String[vars.size()]), ", ");
    }

    @Override
    public String toString() {
        return getText();
    }
}
