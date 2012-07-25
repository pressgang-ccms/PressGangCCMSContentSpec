package org.jboss.pressgangccms.contentspec.rest;

import org.jboss.pressgangccms.contentspec.rest.utils.RESTCollectionCache;
import org.jboss.pressgangccms.contentspec.rest.utils.RESTEntityCache;
import org.jboss.pressgangccms.rest.v1.jaxrsinterfaces.RESTInterfaceV1;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJacksonProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;



/**
 * A class to store and manage database reading and writing via REST Interface
 */
public class RESTManager
{

	private final RESTReader reader;
	private final RESTWriter writer;
	private final RESTInterfaceV1 client;
	private final RESTEntityCache entityCache = new RESTEntityCache();
	private final RESTCollectionCache collectionCache = new RESTCollectionCache(entityCache);
	
	public RESTManager(final String serverUrl)
	{
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		ResteasyProviderFactory.getInstance().registerProvider(ResteasyJacksonProvider.class);
		client = ProxyFactory.create(RESTInterfaceV1.class, serverUrl);
		reader = new RESTReader(client, entityCache, collectionCache);
		writer = new RESTWriter(reader, client, entityCache, collectionCache);
	}
	
	public RESTReader getReader()
	{
		return reader;
	}
	
	public RESTWriter getWriter()
	{
		return writer;
	}
	
	public RESTInterfaceV1 getRESTClient()
	{
		return client;
	}
	
	public RESTEntityCache getRESTEntityCache()
	{
		return entityCache;
	}
}
