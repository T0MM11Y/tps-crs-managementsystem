package com.twm.mgmt.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.twm.mgmt.persistence.dao.AccountDao;
import com.twm.mgmt.persistence.entity.AccountEntity;
import com.twm.mgmt.persistence.repository.AccountRepository;

@EnableJpaRepositories(entityManagerFactoryRef = MoDbConfig.ENTITY_MANAGER, transactionManagerRef = MoDbConfig.TRANSACTION_MANAGER, basePackageClasses = { AccountRepository.class, AccountDao.class })
@EnableTransactionManagement
@Configuration
public class MoDbConfig {

	public static final String PERSISTENCE_UNIT = "mc";

	public static final String ACCOUNT_SCHEMA = "MOMOAPI";

	public static final String SYSTEM_SCHEMA = "MOMOAPI";

	public static final String REWARD_SCHEMA = "MOMOAPI";

	public static final String CAMPAIGN_SCHEMA = "MOMOAPI";

	public static final String CONTRACT_SCHEMA = "MOMOAPI";

	protected static final String DATA_SOURCE = "moDataSource";

	protected static final String ENTITY_MANAGER = "moEntityManager";

	protected static final String TRANSACTION_MANAGER = "moTransactionManager";

	private static final String DB_PREFIX = "spring.modb";

	@Primary
	@Bean(name = DATA_SOURCE)
	@ConfigurationProperties(prefix = DB_PREFIX)
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Primary
	@Bean(name = ENTITY_MANAGER)
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier(DATA_SOURCE) DataSource datasource, EntityManagerFactoryBuilder builder) {
		return builder.dataSource(datasource).packages(AccountEntity.class).persistenceUnit(PERSISTENCE_UNIT).build();
	}

	@Primary
	@Bean(name = TRANSACTION_MANAGER)
	public PlatformTransactionManager transactionManager(@Qualifier(ENTITY_MANAGER) EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

}
