/*
  Copyright 2011-2014 Red Hat, Inc

  This file is part of PressGang CCMS.

  PressGang CCMS is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  PressGang CCMS is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with PressGang CCMS.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.jboss.pressgang.ccms.contentspec.entities;

import java.util.ArrayList;
import java.util.List;

public class SpecList {
    private List<Spec> specs;
    private long count = 0;

    public SpecList() {
        specs = new ArrayList<Spec>();
    }

    public SpecList(List<Spec> specs) {
        this.specs = specs;
        count = specs.size();
    }

    public SpecList(long count) {
        specs = new ArrayList<Spec>();
        this.count = count;
    }

    public SpecList(List<Spec> specs, long count) {
        this.specs = specs;
        this.count = count;
    }

    public List<Spec> getSpecs() {
        return specs;
    }

    public void addSpec(Spec spec) {
        specs.add(spec);
        count++;
    }

    public long getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String toString() {
        final StringBuilder output = new StringBuilder("Number of Content Specifications: " + count + "\n");
        for (final Spec spec : specs) {
            output.append(spec.toString() + "\n");
        }
        return output.toString();
    }
}
