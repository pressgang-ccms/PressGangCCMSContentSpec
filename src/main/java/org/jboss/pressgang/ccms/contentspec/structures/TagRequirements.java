package org.jboss.pressgang.ccms.contentspec.structures;

import java.util.ArrayList;
import java.util.List;

import org.jboss.pressgang.ccms.rest.v1.entities.base.RESTBaseTagV1;
import org.jboss.pressgang.ccms.utils.common.CollectionUtilities;

/**
 * This class defines the tags that a topic needs to have in order to be
 * displayed in a particular TOC level
 */
@SuppressWarnings("rawtypes")
public class TagRequirements
{
	/** One of these tags needs to be present */
	private final List<ArrayList<RESTBaseTagV1<?, ?, ?>>> matchOneOf = new ArrayList<ArrayList<RESTBaseTagV1<?, ?, ?>>>();
	/** All of these tags needs to be present */
	private final List<RESTBaseTagV1<?, ?, ?>> matchAllOf = new ArrayList<RESTBaseTagV1<?, ?, ?>>();

    public List<ArrayList<RESTBaseTagV1<?, ?, ?>>> getMatchOneOf()
	{
		return matchOneOf;
	}

	public List<RESTBaseTagV1<?, ?, ?>> getMatchAllOf()
	{
		return matchAllOf;
	}

	public TagRequirements(final ArrayList<RESTBaseTagV1<?, ?, ?>> matchAllOf, final ArrayList<RESTBaseTagV1<?, ?, ?>> matchOneOf)
	{
		if (matchOneOf != null)
			this.matchOneOf.add(matchOneOf);
		
		if (matchAllOf != null)
			this.matchAllOf.addAll(matchAllOf);
	}

	public TagRequirements(final ArrayList<RESTBaseTagV1<?, ?, ?>> matchAllOf, final RESTBaseTagV1<?, ?, ?> matchOneOf)
	{
		if (matchOneOf != null)
		{
			final ArrayList<RESTBaseTagV1<?, ?, ?>> newArray = new ArrayList<RESTBaseTagV1<?, ?, ?>>();
			newArray.add(matchOneOf);
		    this.matchOneOf.add(newArray);
		}
		if (matchAllOf != null)
			this.matchAllOf.addAll(matchAllOf);
	}

	public TagRequirements(final RESTBaseTagV1 matchAllOf, final ArrayList<RESTBaseTagV1<?, ?, ?>> matchOneOf)
	{
		if (matchOneOf != null)
			this.matchOneOf.add(matchOneOf);
		if (matchAllOf != null)
			this.matchAllOf.add(matchAllOf);
	}

	public TagRequirements(final RESTBaseTagV1<?, ?, ?> matchAllOf, final RESTBaseTagV1<?, ?, ?> matchOneOf)
	{
		if (matchOneOf != null)
		{
            final ArrayList<RESTBaseTagV1<?, ?, ?>> newArray = new ArrayList<RESTBaseTagV1<?, ?, ?>>();
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
