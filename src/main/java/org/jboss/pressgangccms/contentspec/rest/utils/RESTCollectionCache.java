package org.jboss.pressgangccms.contentspec.rest.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jboss.pressgangccms.rest.v1.collections.base.RESTBaseCollectionItemV1;
import org.jboss.pressgangccms.rest.v1.collections.base.RESTBaseCollectionV1;
import org.jboss.pressgangccms.rest.v1.entities.base.RESTBaseEntityV1;
import org.jboss.pressgangccms.utils.common.StringUtilities;

public class RESTCollectionCache
{
	private final RESTEntityCache entityCache;
	private final HashMap<String, RESTBaseCollectionV1<? extends RESTBaseEntityV1<?, ?, ?>, ? extends RESTBaseCollectionV1<?, ?, ?>, ? extends RESTBaseCollectionItemV1<?, ?, ?>>> collections
	        = new HashMap<String, RESTBaseCollectionV1<? extends RESTBaseEntityV1<?, ?, ?>, ? extends RESTBaseCollectionV1<?, ?, ?>, ? extends RESTBaseCollectionItemV1<?, ?, ?>>>();

	public RESTCollectionCache(final RESTEntityCache entityCache)
	{
		this.entityCache = entityCache;
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        void add(final Class<T> clazz, final U value)
	{
		add(clazz, value, null);
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        void add(final Class<T> clazz, final U value, final List<String> additionalKeys)
	{
		add(clazz, value, additionalKeys, false);
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        void add(final Class<T> clazz, final U value, final List<String> additionalKeys, final boolean isRevisions)
	{
		String key = clazz.getSimpleName();
		if (additionalKeys != null && !additionalKeys.isEmpty())
		{
			key += "-" + StringUtilities.buildString(additionalKeys.toArray(new String[additionalKeys.size()]), "-");
		}
		entityCache.add(value, isRevisions);
		collections.put(key, value);
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        boolean containsKey(final Class<T> clazz)
	{
		return containsKey(clazz, null);
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        boolean containsKey(final Class<T> clazz, final List<String> additionalKeys)
	{
		String key = clazz.getSimpleName();
		if (additionalKeys != null && !additionalKeys.isEmpty())
		{
			key += "-" + StringUtilities.buildString(additionalKeys.toArray(new String[additionalKeys.size()]), "-");
		}
		return collections.containsKey(key);
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        U get(final Class<T> clazz, final Class<U> containerClass)
	{
		return get(clazz, containerClass, new ArrayList<String>());
	}

	@SuppressWarnings("unchecked")
	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        U get(final Class<T> clazz, final Class<U> containerClass, final List<String> additionalKeys)
	{
		try
		{
			String key = clazz.getSimpleName();
			if (additionalKeys != null && !additionalKeys.isEmpty())
			{
				key += "-" + StringUtilities.buildString(additionalKeys.toArray(new String[additionalKeys.size()]), "-");
			}
			return containsKey(clazz, additionalKeys) ? (U) collections.get(key) : containerClass.newInstance();
		}
		catch (final Exception ex)
		{
			return null;
		}
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        void expire(final Class<T> clazz)
	{
		collections.remove(clazz.getSimpleName());
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        void expire(final Class<T> clazz, final List<String> additionalKeys)
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
