package org.jboss.pressgang.ccms.contentspec.rest;

import org.jboss.pressgang.ccms.contentspec.rest.utils.RESTCollectionCache;
import org.jboss.pressgang.ccms.contentspec.rest.utils.RESTEntityCache;
import org.jboss.pressgang.ccms.rest.v1.client.PressGangCCMSProxyFactoryV1;
import org.jboss.pressgang.ccms.rest.v1.jaxrsinterfaces.RESTInterfaceV1;

/**
 * A class to store and manage database reading and writing via REST Interface
 */
public class RESTManager {

    private final RESTReader reader;
    private final RESTWriter writer;
    private final PressGangCCMSProxyFactoryV1 proxyFactory;
    private final RESTInterfaceV1 client;
    private final RESTEntityCache entityCache = new RESTEntityCache();
    private final RESTCollectionCache collectionCache = new RESTCollectionCache(entityCache);

    public RESTManager(final String serverUrl) {
        proxyFactory = PressGangCCMSProxyFactoryV1.create(serverUrl);
        client = proxyFactory.getRESTClient();
        reader = new RESTReader(client, entityCache, collectionCache);
        writer = new RESTWriter(reader, client, entityCache, collectionCache);
    }

    public RESTReader getReader() {
        return reader;
    }

    public RESTWriter getWriter() {
        return writer;
    }

    public RESTInterfaceV1 getRESTClient() {
        return client;
    }

    public RESTEntityCache getRESTEntityCache() {
        return entityCache;
    }

    public PressGangCCMSProxyFactoryV1 getProxyFactory() {
        return proxyFactory;
    }
}
