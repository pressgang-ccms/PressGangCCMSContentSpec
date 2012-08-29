package org.jboss.pressgangccms.contentspec.rest;

import org.jboss.pressgangccms.contentspec.rest.utils.RESTCollectionCache;
import org.jboss.pressgangccms.contentspec.rest.utils.RESTEntityCache;
import org.jboss.pressgangccms.rest.v1.client.PressGangCCMSProxyFactory;
import org.jboss.pressgangccms.rest.v1.jaxrsinterfaces.RESTInterfaceV1;

/**
 * A class to store and manage database reading and writing via REST Interface
 */
public class RESTManager
{

	private final RESTReader reader;
	private final RESTWriter writer;
	private final PressGangCCMSProxyFactory factory;
	private final RESTInterfaceV1 client;
	private final RESTEntityCache entityCache = new RESTEntityCache();
	private final RESTCollectionCache collectionCache = new RESTCollectionCache(entityCache);
	
	public RESTManager(final String serverUrl)
	{
		factory = PressGangCCMSProxyFactory.create(serverUrl);
		client = factory.getRESTInterfaceV1Client();
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
