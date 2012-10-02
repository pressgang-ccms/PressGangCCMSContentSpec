package org.jboss.pressgang.ccms.contentspec.rest.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jboss.pressgang.ccms.rest.v1.collections.base.RESTBaseCollectionItemV1;
import org.jboss.pressgang.ccms.rest.v1.collections.base.RESTBaseCollectionV1;
import org.jboss.pressgang.ccms.rest.v1.entities.base.RESTBaseEntityV1;
import org.jboss.pressgang.ccms.utils.common.StringUtilities;

public class RESTCollectionCache
{
	private final RESTEntityCache entityCache;
	private final HashMap<String, RESTBaseCollectionV1> collections = new HashMap<String, RESTBaseCollectionV1>();

	public RESTCollectionCache(final RESTEntityCache entityCache)
	{
		this.entityCache = entityCache;
	}

	public void add(final Class<? extends RESTBaseEntityV1> clazz, final RESTBaseCollectionV1 value)
	{
		add(clazz, value, null);
	}

	public void add(final Class<? extends RESTBaseEntityV1> clazz, final RESTBaseCollectionV1 value, final List<String> additionalKeys)
	{
		add(clazz, value, additionalKeys, false);
	}

	public void add(final Class<? extends RESTBaseEntityV1> clazz, final RESTBaseCollectionV1 value, final List<String> additionalKeys, final boolean isRevisions)
	{
		String key = clazz.getSimpleName();
		if (additionalKeys != null && !additionalKeys.isEmpty())
		{
			key += "-" + StringUtilities.buildString(additionalKeys.toArray(new String[additionalKeys.size()]), "-");
		}
		entityCache.add(value, isRevisions);
		collections.put(key, value);
	}

	public boolean containsKey(final Class<? extends RESTBaseEntityV1> clazz)
	{
		return containsKey(clazz, null);
	}

	public boolean containsKey(final Class<? extends RESTBaseEntityV1> clazz, final List<String> additionalKeys)
	{
		String key = clazz.getSimpleName();
		if (additionalKeys != null && !additionalKeys.isEmpty())
		{
			key += "-" + StringUtilities.buildString(additionalKeys.toArray(new String[additionalKeys.size()]), "-");
		}
		return collections.containsKey(key);
	}

	public RESTBaseCollectionV1 get(final Class<? extends RESTBaseEntityV1> clazz, final Class<? extends RESTBaseCollectionV1> containerClass)
	{
		return get(clazz, containerClass, new ArrayList<String>());
	}

	public RESTBaseCollectionV1 get(final Class<? extends RESTBaseEntityV1> clazz, final Class<? extends RESTBaseCollectionV1> containerClass, final List<String> additionalKeys)
	{
		try
		{
			String key = clazz.getSimpleName();
			if (additionalKeys != null && !additionalKeys.isEmpty())
			{
				key += "-" + StringUtilities.buildString(additionalKeys.toArray(new String[additionalKeys.size()]), "-");
			}
			return containsKey(clazz, additionalKeys) ? collections.get(key) : containerClass.newInstance();
		}
		catch (final Exception ex)
		{
			return null;
		}
	}

	public void expire(final Class<? extends RESTBaseEntityV1> clazz)
	{
		collections.remove(clazz.getSimpleName());
	}

	public void expire(final Class<? extends RESTBaseEntityV1> clazz, final List<String> additionalKeys)
	{
		collections.remove(clazz.getSimpleName() + "-" + StringUtilities.buildString(additionalKeys.toArray(new String[additionalKeys.size()]), "-"));
		expireByRegex("^" + clazz.getSimpleName() + ".*");
	}

	public void expireByRegex(final String regex)
	{
		for (final String key : collections.keySet())
		{
			if (key.matches(regex))
				collections.remove(key);
		}
	}
}
