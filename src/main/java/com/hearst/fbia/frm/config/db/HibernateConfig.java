package com.hearst.fbia.frm.config.db;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.hearst.fbia.frm.config.audit.AuditInterceptor;

@Configuration
@EnableTransactionManagement
@PropertySource(value = "classpath:prop/db.properties", ignoreResourceNotFound = false)
public class HibernateConfig {

	@Autowired
	Environment environment;

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setPackagesToScan(environment.getRequiredProperty("spring.jpa.packages-to-scan"));
		sessionFactory.setHibernateProperties(hibernateProperties());
		return sessionFactory;
	}

	Properties hibernateProperties() {
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
	public DriverManagerDataSource dataSource() {
		DriverManagerDataSource jdbcDataSource = new DriverManagerDataSource();
		jdbcDataSource.setDriverClassName(environment.getRequiredProperty("spring.datasource.driverClassName"));
		jdbcDataSource.setUrl(environment.getRequiredProperty("spring.datasource.url"));
		jdbcDataSource.setUsername(environment.getRequiredProperty("spring.datasource.username"));
		jdbcDataSource.setPassword(environment.getRequiredProperty("spring.datasource.password"));
		return jdbcDataSource;
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public HibernateTransactionManager transactionManager(SessionFactory sessionFactory,
			AuditInterceptor auditInterceptor) {
		HibernateTransactionManager hibernateTransactionManager = new HibernateTransactionManager(sessionFactory);
		hibernateTransactionManager.setEntityInterceptor(auditInterceptor);
		return hibernateTransactionManager;
	}

	@Bean
	public AuditInterceptor auditInterceptor() {
		return new AuditInterceptor();
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

}