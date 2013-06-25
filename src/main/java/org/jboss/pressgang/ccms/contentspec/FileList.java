package org.jboss.pressgang.ccms.contentspec;

import java.util.List;

import org.jboss.pressgang.ccms.contentspec.constants.CSConstants;

public class FileList extends KeyValueNode<List<File>> {
    public FileList(String key, List<File> value, char separator) {
        super(key, value, separator);
    }

    public FileList(String key, List<File> value) {
        super(key, value, '=');
    }

    @Override
    public String getText() {
        final StringBuilder output = new StringBuilder(CSConstants.FILE_TITLE);
        output.append(" ").append(getSeparator()).append(" [");
        int count = 1;
        for (final File file : getValue()) {
            if (count != 1) {
                output.append(",\n").append(SPACER);
            }
            output.append(file.toString());
            count++;
        }
        output.append("]");
        return output.toString();
    }
}
