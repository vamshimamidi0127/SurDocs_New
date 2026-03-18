package gov.dc.surdocs.config;

import gov.dc.surdocs.filenet.FileNetConnectionManager;
import gov.dc.surdocs.filenet.FileNetDocumentGateway;
import gov.dc.surdocs.filenet.FileNetObjectStoreProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FileNetProperties.class)
public class FileNetConfig {

    @Bean
    public FileNetConnectionManager fileNetConnectionManager(FileNetProperties properties) {
        return new FileNetConnectionManager(properties);
    }

    @Bean
    public FileNetObjectStoreProvider fileNetObjectStoreProvider(FileNetConnectionManager connectionManager,
                                                                 FileNetProperties properties) {
        return new FileNetObjectStoreProvider(connectionManager, properties);
    }

    @Bean
    public FileNetDocumentGateway fileNetDocumentGateway(FileNetObjectStoreProvider objectStoreProvider) {
        return new FileNetDocumentGateway(objectStoreProvider);
    }
}
