package gov.dc.surdocs.config;

import javax.sql.DataSource;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jdbc.DataSourceBuilder;

@Configuration
public class DataSourceConfig {

    @Bean
   // @ConfigurationProperties("spring.datasource")
   // public DataSourceProperties dataSourceProperties() {
   //     return new DataSourceProperties();
   // }

   // @Bean
   // @ConfigurationProperties("spring.datasource.hikari")
   // public DataSource dataSource(DataSourceProperties properties) {
   //     return properties.initializeDataSourceBuilder().build();
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

}

