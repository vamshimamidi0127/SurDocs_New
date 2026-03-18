package gov.dc.surdocs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "filenet")
public class FileNetProperties {

    private String uri;
    private String username;
    private String password;
    private String objectStoreName;
    private String stanza;
    private boolean connectionCacheEnabled;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getObjectStoreName() {
        return objectStoreName;
    }

    public void setObjectStoreName(String objectStoreName) {
        this.objectStoreName = objectStoreName;
    }

    public String getStanza() {
        return stanza;
    }

    public void setStanza(String stanza) {
        this.stanza = stanza;
    }

    public boolean isConnectionCacheEnabled() {
        return connectionCacheEnabled;
    }

    public void setConnectionCacheEnabled(boolean connectionCacheEnabled) {
        this.connectionCacheEnabled = connectionCacheEnabled;
    }
}
