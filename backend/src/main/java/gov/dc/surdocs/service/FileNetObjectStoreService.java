package gov.dc.surdocs.service;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;
import gov.dc.surdocs.config.FileNetProperties;
import gov.dc.surdocs.exception.FileNetConnectionException;
import java.util.Locale;
import javax.security.auth.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FileNetObjectStoreService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileNetObjectStoreService.class);

    private final FileNetProperties fileNetProperties;
    private final Object lock = new Object();

    private volatile Connection connection;
    private volatile Domain domain;

    public FileNetObjectStoreService(FileNetProperties fileNetProperties) {
        this.fileNetProperties = fileNetProperties;
    }

    public ObjectStore getObjectStore() {
        validateConfiguration();

        UserContext userContext = null;
        try {
            Connection ceConnection = getOrCreateConnection();
            userContext = pushSubject(ceConnection);
            Domain ceDomain = getOrCreateDomain(ceConnection);

            LOGGER.debug("Fetching FileNet ObjectStore '{}'", fileNetProperties.getObjectStoreName());
            return Factory.ObjectStore.fetchInstance(ceDomain, fileNetProperties.getObjectStoreName(), null);
        } catch (Exception ex) {
            LOGGER.error("Failed to obtain FileNet ObjectStore '{}'", fileNetProperties.getObjectStoreName(), ex);
            throw new FileNetConnectionException("Unable to connect to FileNet ObjectStore", ex);
        } finally {
            popSubjectQuietly(userContext);
        }
    }

    private Connection getOrCreateConnection() {
        Connection currentConnection = this.connection;
        if (currentConnection == null) {
            synchronized (lock) {
                currentConnection = this.connection;
                if (currentConnection == null) {
                    LOGGER.info("Creating FileNet CE connection for URI '{}'", fileNetProperties.getUri());
                    currentConnection = Factory.Connection.getConnection(fileNetProperties.getUri());
                    this.connection = currentConnection;
                }
            }
        }
        return currentConnection;
    }

    private Domain getOrCreateDomain(Connection ceConnection) {
        Domain currentDomain = this.domain;
        if (currentDomain == null) {
            synchronized (lock) {
                currentDomain = this.domain;
                if (currentDomain == null) {
                    LOGGER.info("Fetching FileNet domain using configured CE connection");
                    currentDomain = Factory.Domain.fetchInstance(ceConnection, null, null);
                    this.domain = currentDomain;
                }
            }
        }
        return currentDomain;
    }

    private UserContext pushSubject(Connection ceConnection) {
        Subject subject = UserContext.createSubject(
                ceConnection,
                fileNetProperties.getUsername(),
                fileNetProperties.getPassword(),
                resolveStanza());

        UserContext userContext = UserContext.get();
        userContext.pushSubject(subject);
        userContext.setLocale(Locale.US);
        return userContext;
    }

    private void popSubjectQuietly(UserContext userContext) {
        if (userContext == null) {
            return;
        }
        try {
            userContext.popSubject();
        } catch (Exception ex) {
            LOGGER.warn("Failed to pop FileNet UserContext subject cleanly", ex);
        }
    }

    private String resolveStanza() {
        return StringUtils.hasText(fileNetProperties.getStanza())
                ? fileNetProperties.getStanza()
                : "FileNetP8WSI";
    }

    private void validateConfiguration() {
        if (!StringUtils.hasText(fileNetProperties.getUri())) {
            throw new FileNetConnectionException("Missing FileNet URI configuration");
        }
        if (!StringUtils.hasText(fileNetProperties.getUsername())) {
            throw new FileNetConnectionException("Missing FileNet username configuration");
        }
        if (!StringUtils.hasText(fileNetProperties.getPassword())) {
            throw new FileNetConnectionException("Missing FileNet password configuration");
        }
        if (!StringUtils.hasText(fileNetProperties.getObjectStoreName())) {
            throw new FileNetConnectionException("Missing FileNet ObjectStore configuration");
        }
    }
}
