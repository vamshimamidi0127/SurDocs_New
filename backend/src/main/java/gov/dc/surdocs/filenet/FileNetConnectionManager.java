package gov.dc.surdocs.filenet;

import com.filenet.api.core.Connection;
import com.filenet.api.core.Factory;
import com.filenet.api.util.UserContext;
import gov.dc.surdocs.config.FileNetProperties;
import java.util.Locale;
import javax.security.auth.Subject;

public class FileNetConnectionManager {

    private final FileNetProperties properties;

    public FileNetConnectionManager(FileNetProperties properties) {
        this.properties = properties;
    }

    public Connection getConnection() {
        return Factory.Connection.getConnection(properties.getUri());
    }

    public UserContext pushSubject(Connection connection) {
        Subject subject = UserContext.createSubject(
                connection,
                properties.getUsername(),
                properties.getPassword(),
                properties.getStanza() == null ? "FileNetP8WSI" : properties.getStanza());
        UserContext userContext = UserContext.get();
        userContext.pushSubject(subject);
        userContext.setLocale(Locale.US);
        return userContext;
    }

    public void popSubject() {
        UserContext.get().popSubject();
    }
}
