package com.hearst.fbia.frm.config.db;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@PropertySource(value = "classpath:prop/db.properties", ignoreResourceNotFound = false)
@EnableJpaRepositories(basePackages = { "com.hearst.fbia.app.repository" })
@EnableJpaAuditing(auditorAwareRef = "jpaAuditorAware", modifyOnCreate = true)
public class JpaConfig {

	@Autowired
	Environment environment;

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(environment.getRequiredProperty("spring.datasource.driverClassName"));
		dataSource.setUrl(environment.getRequiredProperty("spring.datasource.url"));
		dataSource.setUsername(environment.getRequiredProperty("spring.datasource.username"));
		dataSource.setPassword(environment.getRequiredProperty("spring.datasource.password"));
		return dataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(dataSource);
		entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		entityManagerFactoryBean.setPackagesToScan(environment.getRequiredProperty("spring.jpa.packages-to-scan"));
		entityManagerFactoryBean.setJpaProperties(properties());
		entityManagerFactoryBean.afterPropertiesSet();
		return entityManagerFactoryBean;
	}

	Properties properties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto",
				environment.getRequiredProperty("spring.jpa.hibernate.ddl-auto"));
		properties.setProperty("hibernate.show_sql", environment.getRequiredProperty("spring.jpa.show-sql"));
		properties.setProperty("hibernate.dialect", environment.getRequiredProperty("spring.jpa.database-platform"));
		properties.setProperty("hibernate.temp.use_jdbc_metadata_defaults",
				environment.getRequiredProperty("spring.jpa.metadata.defaults"));
		return properties;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		return new JpaTransactionManager(emf);
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@Bean
	public AuditorAware<String> jpaAuditorAware() {
		return new AuditorAware<String>() {
			@Override
			public String getCurrentAuditor() {
				return "FBIA_ADMIN";
			}
		};
	}
}