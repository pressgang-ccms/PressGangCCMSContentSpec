package org.jboss.pressgangccms.contentspec.rest.utils;

import java.util.HashMap;

import org.jboss.pressgangccms.rest.v1.collections.base.RESTBaseCollectionItemV1;
import org.jboss.pressgangccms.rest.v1.collections.base.RESTBaseCollectionV1;
import org.jboss.pressgangccms.rest.v1.components.ComponentTranslatedTopicV1;
import org.jboss.pressgangccms.rest.v1.entities.RESTTranslatedTopicV1;
import org.jboss.pressgangccms.rest.v1.entities.base.RESTBaseEntityV1;

public class RESTEntityCache
{

	private HashMap<Class<?>, HashMap<String, RESTBaseEntityV1<?, ?, ?>>> singleEntities = new HashMap<Class<?>, HashMap<String, RESTBaseEntityV1<?, ?, ?>>>();

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        void add(final U value)
	{
		add(value, false);
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        void add(final U value, final boolean isRevisions)
	{
		if (value != null && value.getItems() != null)
		{
			for (final V item : value.getItems())
			{
			    if (item.getItem() != null)
			    {
			        add(item.getItem(), isRevisions);
			    }
			}
		}
	}
	
	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        boolean containsKeyValue(final Class<T> clazz, final Number id, final Number revision)
	{
		return containsKeyValue(clazz, id.toString(), revision);
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        boolean containsKeyValue(final Class<T> clazz, final String id, final Number revision)
	{
		if (singleEntities.containsKey(clazz))
			return revision == null ? singleEntities.get(clazz).containsKey(clazz.getSimpleName() + "-" + id) : singleEntities.get(clazz).containsKey(clazz.getSimpleName() + "-" + id + "-" + revision);
		else
			return false;
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	    boolean containsKeyValue(final Class<T> clazz, final Number id)
	{
		return containsKeyValue(clazz, id.toString(), null);
	}
	
	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	    boolean containsKeyValue(final Class<T> clazz, final String id)
	{
		return containsKeyValue(clazz, id, null);
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        void add(final T value)
	{
		add(value, false);
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        void add(final T value, final Number id, final boolean isRevision)
	{
		add(value, id.toString(), (isRevision ? value.getRevision() : null));
	}
	
	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        void add(final T value, final String id, final boolean isRevision)
    {
        add(value, id, (isRevision ? value.getRevision() : null));
    }
	
	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        void add(final T value, final Number id, final Number revision)
    {
        add(value, id.toString(), revision);
    }

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        void add(final T value, final String id, final Number revision)
	{
		// Add the map if one doesn't exist for the current class
		if (!singleEntities.containsKey(value.getClass()))
		{
			singleEntities.put(value.getClass(), new HashMap<String, RESTBaseEntityV1<?, ?, ?>>());
		}

		// Add the entity
		if (revision != null)
			singleEntities.get(value.getClass()).put(value.getClass().getSimpleName() + "-" + id + "-" + revision, value);
		else
			singleEntities.get(value.getClass()).put(value.getClass().getSimpleName() + "-" + id, value);

		// Add any revisions to the cache
		add(value.getRevisions(), true);
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        void add(final T value, final boolean isRevision)
	{
		add(value, isRevision ? value.getRevision() : null);
	}
	
	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        void add(final T value, final Number revision)
    {
        add(value, value.getId(), revision);
        if (value instanceof RESTTranslatedTopicV1)
        {
            final RESTTranslatedTopicV1 translatedTopic = (RESTTranslatedTopicV1) value;
            add(value, (translatedTopic.getTopicId() + "-" + translatedTopic.getLocale()), revision);
            add(value, (ComponentTranslatedTopicV1.returnZanataId(translatedTopic) + "-" + translatedTopic.getLocale()), revision);
        }
    }

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        U get(final Class<T> clazz, final Class<U> collectionClass)
	{
		try
		{
			final U values = collectionClass.newInstance();
			if (singleEntities.containsKey(clazz))
			{
				for (final String key : singleEntities.get(clazz).keySet())
				{
					values.addItem(clazz.cast(singleEntities.get(clazz).get(key)));
				}
			}
			return values;
		}
		catch (final Exception ex)
		{
			return null;
		}
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        T get(final Class<T> clazz, final Number id)
	{
		return get(clazz, id.toString(), null);
	}
	
	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        T get(final Class<T> clazz, final String id)
	{
		return get(clazz, id, null);
	}
	
	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        T get(final Class<T> clazz, final Number id, final Number revision)
	{
		return get(clazz, id.toString(), revision);
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        T get(final Class<T> clazz, final String id, final Number revision)
	{
		if (!containsKeyValue(clazz, id, revision))
			return null;
		return clazz.cast(revision == null ? singleEntities.get(clazz).get(clazz.getSimpleName() + "-" + id) : singleEntities.get(clazz).get(clazz.getSimpleName() + "-" + id + "-" + revision));
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        void expire(final Class<T> clazz, final Integer id)
	{
		expire(clazz, id, null);
	}

	public <T extends RESTBaseEntityV1<T, U, V>, U extends RESTBaseCollectionV1<T, U, V>, V extends RESTBaseCollectionItemV1<T, U, V>>
	        void expire(final Class<T> clazz, final Integer id, final Number revision)
	{
		final String keyValue = revision == null ? (clazz.getSimpleName() + "-" + id) : (clazz.getSimpleName() + "-" + id + "-" + revision);
		if (singleEntities.containsKey(clazz))
		{
			if (singleEntities.get(clazz).containsKey(keyValue))
			{
				singleEntities.get(clazz).remove(keyValue);
			}
		}
	}

	public void expireByRegex(final String regex)
	{
		for (final Class<?> clazz : singleEntities.keySet())
		{
			for (final String key : singleEntities.get(clazz).keySet())
			{
				if (key.matches(regex))
					singleEntities.remove(key);
			}
		}
	}
}
