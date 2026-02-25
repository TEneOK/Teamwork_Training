package org.skypro.teamwork.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "org.skypro.teamwork.repository.jpa",
        entityManagerFactoryRef = "dynamicRuleEntityManagerFactory",
        transactionManagerRef = "dynamicRuleTransactionManager"
)
public class DynamicRuleJpaConfiguration {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.dynamic-rules")
    public DataSourceProperties dynamicRuleDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource dynamicRuleDataSource() {
        return dynamicRuleDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean(name = "dynamicRuleEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean dynamicRuleEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dynamicRuleDataSource())
                .packages("org.skypro.teamwork.models")
                .persistenceUnit("dynamicRule")
                .build();
    }

    @Bean(name = "dynamicRuleTransactionManager")
    public PlatformTransactionManager dynamicRuleTransactionManager(
            @Qualifier("dynamicRuleEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}