/*
  Copyright 2011-2014 Red Hat

  This file is part of PresGang CCMS.

  PresGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PresGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PresGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.contentspec;

import java.util.List;

import org.jboss.pressgang.ccms.utils.constants.CommonConstants;

public class FileList extends KeyValueNode<List<File>> {
    public FileList(String key, List<File> value, char separator, int lineNumber) {
        super(key, value, separator, lineNumber);
    }

    public FileList(String key, List<File> value, char separator) {
        super(key, value, separator);
    }

    public FileList(String key, List<File> value, int lineNumber) {
        super(key, value, '=', lineNumber);
    }

    public FileList(String key, List<File> value) {
        super(key, value, '=');
    }

    @Override
    public String getText() {
        final StringBuilder output = new StringBuilder(CommonConstants.CS_FILE_TITLE);
        output.append(" ").append(getSeparator()).append(" [");
        if (getValue() != null) {
            for (int i = 0; i < getValue().size(); i++) {
                final File file = getValue().get(i);
                if (i > 0) {
                    output.append(",\n").append(SPACER);
                }
                output.append(file.getText());
            }
        }
        output.append("]");
        return output.toString();
    }
}
