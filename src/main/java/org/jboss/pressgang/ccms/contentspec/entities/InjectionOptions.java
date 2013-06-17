package org.jboss.pressgang.ccms.contentspec.entities;

import java.util.ArrayList;

import com.google.code.regexp.Matcher;
import com.google.code.regexp.Pattern;
import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;
import org.jboss.pressgang.ccms.utils.common.StringUtilities;

public class InjectionOptions {
    private UserType clientType = UserType.NONE;
    private UserType contentSpecType = UserType.NONE;
    private final ArrayList<String> strictTopicTypes = new ArrayList<String>();

    public static enum UserType {NONE, OFF, STRICT, ON}

    public InjectionOptions() {
    }

    public InjectionOptions(final String options) {
        String[] types = null;
        if (StringUtilities.indexOf(options, '[') != -1 && StringUtilities.indexOf(options, ']') != -1) {
            final Pattern bracketPattern = Pattern.compile(String.format(CSConstants.BRACKET_NAMED_PATTERN, '[', ']'));
            final Matcher matcher = bracketPattern.matcher(options);

            // Find all of the variables inside of the brackets defined by the regex
            while (matcher.find()) {
                final String topicTypes = matcher.group(CSConstants.BRACKET_CONTENTS);
                types = StringUtilities.split(topicTypes, ',');
                for (final String type : types) {
                    addStrictTopicType(type.trim());
                }
            }
        }

        String injectionSetting = getInjectionSetting(options, '[');
        if (injectionSetting.trim().equalsIgnoreCase("on")) {
            if (types != null) {
                setContentSpecType(InjectionOptions.UserType.STRICT);
            } else {
                setContentSpecType(InjectionOptions.UserType.ON);
            }
        } else if (injectionSetting.trim().equalsIgnoreCase("off")) {
            setContentSpecType(InjectionOptions.UserType.OFF);
        }
    }

    public InjectionOptions(final UserType clientSetting) {
        clientType = clientSetting;
    }

    /**
     * Gets the Injection Setting for these options when using the String Constructor.
     *
     * @param input      The input to be parsed to get the setting.
     * @param startDelim The delimiter that specifies that start of options (ie '[')
     * @return The title as a String or null if the title is blank.
     */
    private String getInjectionSetting(final String input, final char startDelim) {
        return input == null || input.equals("") ? null : StringUtilities.split(input, startDelim)[0].trim();
    }

    public boolean isInjectionAllowed() {
        if ((contentSpecType == UserType.OFF || contentSpecType == UserType.NONE) && (clientType == UserType.OFF || clientType ==
                UserType.NONE))
            return false;
        return true;
    }

    public boolean isInjectionAllowedForType(final String typeName) {
        if (isInjectionAllowed()) {
            if (clientType == UserType.STRICT || contentSpecType == UserType.STRICT) {
                boolean found = false;
                for (String type : strictTopicTypes) {
                    if (type.equals(typeName)) {
                        found = true;
                        break;
                    }
                }
                return found;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public UserType getClientType() {
        return clientType;
    }

    public void setClientType(final UserType clientType) {
        this.clientType = clientType;
    }

    public UserType getContentSpecType() {
        return contentSpecType;
    }

    public void setContentSpecType(final UserType contentSpecType) {
        this.contentSpecType = contentSpecType;
    }

    public void addStrictTopicType(final String typeName) {
        boolean found = false;
        for (String type : strictTopicTypes) {
            if (type.equals(typeName)) {
                found = true;
                break;
            }
        }
        if (!found) strictTopicTypes.add(typeName);
    }

    public void addStrictTopicTypes(final ArrayList<String> types) {
        if (types == null) return;
        for (String type : types) {
            addStrictTopicType(type);
        }
    }

    public ArrayList<String> getStrictTopicTypes() {
        return strictTopicTypes;
    }

    @Override
    public String toString() {
        String output = "";
        if (getContentSpecType() == InjectionOptions.UserType.STRICT) {
            output += "on [" + StringUtilities.buildString(getStrictTopicTypes().toArray(new String[0]), ", ") + "]";
        } else if (getContentSpecType() == InjectionOptions.UserType.ON) {
            output += "on";
        } else if (getContentSpecType() == InjectionOptions.UserType.OFF) {
            output += "off";
        }
        return output;
    }
}
