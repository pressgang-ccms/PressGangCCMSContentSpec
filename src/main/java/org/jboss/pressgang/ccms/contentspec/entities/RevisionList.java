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

package org.jboss.pressgang.ccms.contentspec.entities;

import java.util.ArrayList;
import java.util.List;

public class RevisionList {
    private List<Revision> revisions;
    private int count = 0;
    private Integer id;
    private String modeName;

    public RevisionList() {
        revisions = new ArrayList<Revision>();
    }

    public RevisionList(int id, final String modeName) {
        this.setId(id);
        this.setModeName(modeName);
        revisions = new ArrayList<Revision>();
    }

    public RevisionList(int id, final String modeName, final List<Revision> revisions) {
        this.setId(id);
        this.setModeName(modeName);
        this.revisions = revisions;
        count = revisions.size();
    }

    public RevisionList(int id, final String modeName, int count) {
        this.setId(id);
        this.setModeName(modeName);
        revisions = new ArrayList<Revision>();
        this.count = count;
    }

    public RevisionList(int id, final String modeName, final List<Revision> revisions, int count) {
        this.setId(id);
        this.setModeName(modeName);
        this.revisions = revisions;
        this.count = count;
    }

    public List<Revision> getRevisions() {
        return revisions;
    }

    public void addRevision(final Revision rev) {
        revisions.add(rev);
        count++;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String toString() {
        final StringBuilder output = new StringBuilder("INFO: Revisions for " + modeName + " ID: " + id + "\n");
        for (final Revision rev : revisions) {
            output.append(rev.toString() + "\n");
        }
        return output.toString();
    }

    public String getModeName() {
        return modeName;
    }

    public void setModeName(String modeName) {
        this.modeName = modeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
