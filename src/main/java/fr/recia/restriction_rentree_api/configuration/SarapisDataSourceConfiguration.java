package fr.recia.restriction_rentree_api.configuration;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class SarapisDataSourceConfiguration {

    @Bean(name = "sarapisDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.sarapis")
    public DataSource sarapisDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "sarapisJdbcTemplate")
    public JdbcTemplate sarapisJdbcTemplate(@Qualifier("sarapisDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
