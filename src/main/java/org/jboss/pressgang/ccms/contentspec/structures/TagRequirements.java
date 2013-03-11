package org.jboss.pressgang.ccms.contentspec.structures;

import java.util.ArrayList;
import java.util.List;

import org.jboss.pressgang.ccms.wrapper.TagWrapper;
import org.jboss.pressgang.ccms.wrapper.base.BaseTagWrapper;

/**
 * This class defines the tags that a topic needs to have in order to be
 * displayed in a particular TOC level
 */
public class TagRequirements {
    /**
     * One of these tags needs to be present
     */
    private final List<ArrayList<BaseTagWrapper>> matchOneOf = new ArrayList<ArrayList<BaseTagWrapper>>();
    /**
     * All of these tags needs to be present
     */
    private final List<BaseTagWrapper> matchAllOf = new ArrayList<BaseTagWrapper>();

    public List<ArrayList<BaseTagWrapper>> getMatchOneOf() {
        return matchOneOf;
    }

    public List<BaseTagWrapper> getMatchAllOf() {
        return matchAllOf;
    }

    public TagRequirements(final ArrayList<BaseTagWrapper> matchAllOf, final ArrayList<BaseTagWrapper> matchOneOf) {
        if (matchOneOf != null) this.matchOneOf.add(matchOneOf);

        if (matchAllOf != null) this.matchAllOf.addAll(matchAllOf);
    }

    public TagRequirements(final ArrayList<BaseTagWrapper> matchAllOf, final TagWrapper matchOneOf) {
        if (matchOneOf != null) {
            final ArrayList<BaseTagWrapper> newArray = new ArrayList<BaseTagWrapper>();
            newArray.add(matchOneOf);
            this.matchOneOf.add(newArray);
        }
        if (matchAllOf != null) this.matchAllOf.addAll(matchAllOf);
    }

    public TagRequirements(final BaseTagWrapper matchAllOf, final ArrayList<BaseTagWrapper> matchOneOf) {
        if (matchOneOf != null) this.matchOneOf.add(matchOneOf);
        if (matchAllOf != null) this.matchAllOf.add(matchAllOf);
    }

    public TagRequirements(final BaseTagWrapper matchAllOf, final BaseTagWrapper matchOneOf) {
        if (matchOneOf != null) {
            final ArrayList<BaseTagWrapper> newArray = new ArrayList<BaseTagWrapper>();
            newArray.add(matchOneOf);
            this.matchOneOf.add(newArray);
        }
        if (matchAllOf != null) this.matchAllOf.add(matchAllOf);
    }

    public TagRequirements() {

    }

    /**
     * This method will merge the tag information stored in another
     * TagRequirements object with the tag information stored in this object.
     *
     * @param other the other TagRequirements object to merge with
     */
    public void merge(final TagRequirements other) {
        if (other != null) {
            this.matchAllOf.addAll(other.matchAllOf);
            this.matchOneOf.addAll(other.matchOneOf);
        }
    }

    public boolean hasRequirements() {
        return this.matchAllOf.size() != 0 || this.matchOneOf.size() != 0;
    }
}
