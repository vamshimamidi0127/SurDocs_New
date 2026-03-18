package gov.dc.surdocs.filenet;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import gov.dc.surdocs.config.FileNetProperties;

public class FileNetObjectStoreProvider {

    private final FileNetConnectionManager connectionManager;
    private final FileNetProperties properties;

    public FileNetObjectStoreProvider(FileNetConnectionManager connectionManager, FileNetProperties properties) {
        this.connectionManager = connectionManager;
        this.properties = properties;
    }

    public ObjectStore getObjectStore() {
        Connection connection = connectionManager.getConnection();
        connectionManager.pushSubject(connection);
        Domain domain = Factory.Domain.fetchInstance(connection, null, null);
        return Factory.ObjectStore.fetchInstance(domain, properties.getObjectStoreName(), null);
    }
}
