package dive.dev.secondary.dbconfig;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
		entityManagerFactoryRef = "secondaryEntityManagerFactory",
		transactionManagerRef = "secondaryTransactionManager",
		basePackages = {"dive.dev.secondary.repo"}
		)
public class SecondaryDatabaseConnection {
		
	@Value("${spring.secondary.datasource.url}")
    private String url;
	
	@Value("${spring.secondary.datasource.username}")
    private String username;
	
	@Value("${spring.secondary.datasource.password}")
    private String password;
	
	
	@Bean(name = "secondaryDbDataSource")
	public DataSource secondaryDbDataSource(){
        return DataSourceBuilder.create()
        		.url(url)
        		.username(username)
        		.password(password)
        		.build();
    }
	
	@Bean(name = "secondaryEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean 
	secondaryEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("secondaryDbDataSource") DataSource secondaryDataSource) {
		Map<String, String> props = new HashMap<>();
		props.put("hibernate.hbm2ddl.auto", "create-drop");
		return builder
				.dataSource(secondaryDataSource)
				.packages("dive.dev.secondary.models")
				.properties(props)
				.build();
	}
	
	@Bean(name = "secondaryTransactionManager")
	public PlatformTransactionManager secondaryTransactionManager(
			@Qualifier("secondaryEntityManagerFactory") EntityManagerFactory
			secondaryEntityManagerFactory) {
		return new JpaTransactionManager(secondaryEntityManagerFactory);
	}

}
