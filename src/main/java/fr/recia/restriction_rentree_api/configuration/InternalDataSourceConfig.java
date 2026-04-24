package fr.recia.restriction_rentree_api.configuration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "fr.recia.restriction_rentree_api.repository",
        entityManagerFactoryRef = "internalEntityManagerFactory",
        transactionManagerRef = "internalTransactionManager"
)
public class InternalDataSourceConfig {

    @Bean(name = "internalDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.internal")
    public DataSource internalDataSource() {
        return DataSourceBuilder.create()
                .type(com.zaxxer.hikari.HikariDataSource.class)
                .build();
    }

    @Bean(name = "internalEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean internalEntityManagerFactory(@Qualifier("internalDataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("fr.recia.restriction_rentree_api.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        Map<String, Object> properties = new HashMap<>();
        // TODO : externaliser ces properties
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        properties.put("hibernate.hbm2ddl.auto", "validate");
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean(name = "internalTransactionManager")
    public PlatformTransactionManager internalTransactionManager(@Qualifier("internalEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}