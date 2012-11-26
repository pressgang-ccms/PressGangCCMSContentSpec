package org.jboss.pressgang.ccms.contentspec.structures;

import java.util.ArrayList;
import java.util.List;

import org.jboss.pressgang.ccms.contentspec.wrapper.TagWrapper;

/**
 * This class defines the tags that a topic needs to have in order to be
 * displayed in a particular TOC level
 */
public class TagRequirements
{
	/** One of these tags needs to be present */
	private final List<ArrayList<TagWrapper>> matchOneOf = new ArrayList<ArrayList<TagWrapper>>();
	/** All of these tags needs to be present */
	private final List<TagWrapper> matchAllOf = new ArrayList<TagWrapper>();

    public List<ArrayList<TagWrapper>> getMatchOneOf()
	{
		return matchOneOf;
	}

	public List<TagWrapper> getMatchAllOf()
	{
		return matchAllOf;
	}

	public TagRequirements(final ArrayList<TagWrapper> matchAllOf, final ArrayList<TagWrapper> matchOneOf)
	{
		if (matchOneOf != null)
			this.matchOneOf.add(matchOneOf);
		
		if (matchAllOf != null)
			this.matchAllOf.addAll(matchAllOf);
	}

	public TagRequirements(final ArrayList<TagWrapper> matchAllOf, final TagWrapper matchOneOf)
	{
		if (matchOneOf != null)
		{
			final ArrayList<TagWrapper> newArray = new ArrayList<TagWrapper>();
			newArray.add(matchOneOf);
		    this.matchOneOf.add(newArray);
		}
		if (matchAllOf != null)
			this.matchAllOf.addAll(matchAllOf);
	}

	public TagRequirements(final TagWrapper matchAllOf, final ArrayList<TagWrapper> matchOneOf)
	{
		if (matchOneOf != null)
			this.matchOneOf.add(matchOneOf);
		if (matchAllOf != null)
			this.matchAllOf.add(matchAllOf);
	}

	public TagRequirements(final TagWrapper matchAllOf, final TagWrapper matchOneOf)
	{
		if (matchOneOf != null)
		{
            final ArrayList<TagWrapper> newArray = new ArrayList<TagWrapper>();
            newArray.add(matchOneOf);
            this.matchOneOf.add(newArray);
        }
		if (matchAllOf != null)
			this.matchAllOf.add(matchAllOf);
	}

	public TagRequirements()
	{

	}

	/**
	 * This method will merge the tag information stored in another
	 * TagRequirements object with the tag information stored in this object.
	 * 
	 * @param other
	 *            the other TagRequirements object to merge with
	 */
	public void merge(final TagRequirements other)
	{
		if (other != null)
		{
			this.matchAllOf.addAll(other.matchAllOf);
			this.matchOneOf.addAll(other.matchOneOf);
		}
	}
	
	public boolean hasRequirements()
	{
		return this.matchAllOf.size() != 0 || this.matchOneOf.size() != 0;
	}
}
