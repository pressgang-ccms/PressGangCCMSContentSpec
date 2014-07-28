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

import org.jboss.pressgang.ccms.contentspec.utils.ContentSpecUtilities;

public class File extends Node {
    private String title;
    private Integer id;
    private Integer revision;

    public File(final String title, final Integer id) {
        this.id = id;
        this.title = title;
    }

    public File(final Integer id) {
        this.id = id;
    }

    public String getText() {
        final StringBuilder output = new StringBuilder();
        if (title == null) {
            output.append(getIdAndRevisionText());
        } else {
            output.append(ContentSpecUtilities.escapeRelationshipTitle(title)).append(" ").append("[").append(getIdAndRevisionText()).append
                    ("]");
        }
        return output.toString();
    }

    @Override
    public Integer getStep() {
        return getParent() == null ? null : getParent().getStep();
    }

    @Override
    public FileList getParent() {
        return (FileList) super.getParent();
    }

    public void setParent(final FileList fileList) {
        super.setParent(fileList);
    }

    @Override
    protected void removeParent() {
        getParent().getValue().remove(this);
        setParent(null);
    }

    protected String getIdAndRevisionText() {
        if (revision != null) {
            return id + ", rev: " + revision;
        } else {
            return id.toString();
        }
    }

    public String toString() {
        return getText() + "\n";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
