package com.petra.lib.z_user_package.source_handlers.params;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration

//@EnableTransactionManagement
//@PropertySource("classpath:application.properties")
public class ConfigurationTest {
//    @Autowired
//    private Environment env;

//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
//
//        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        vendorAdapter.setDatabase(Database.POSTGRESQL);
//        vendorAdapter.setGenerateDdl(true);
//
//        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
//        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
//        entityManagerFactoryBean.setDataSource(dataSource);
//        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
//        entityManagerFactoryBean.setPackagesToScan("com.petra.lib");
//
//        return entityManagerFactoryBean;
//    }

//    @Bean
//    public JpaVendorAdapter jpaVendorAdapter() {
//        System.out.println("hello");
//        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
//        System.out.println(adapter);
//        adapter.setDatabase(Database.POSTGRESQL);
//        adapter.setShowSql(true);
//        adapter.setGenerateDdl(false);
//        adapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
//        return  adapter;
//    }

    @Value("${spring.datasource.url:jdbc:postgresql://192.168.0.10:5432/postgres}")
    private String dbUrl;

    @Value("${spring.datasource.username:your_user}")
    private String username;

    @Value("${spring.datasource.password:your_password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl(dbUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(20);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        System.out.println("DataSource configured: " + dbUrl + " | User: " + username);

        return new HikariDataSource(config);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
        System.out.println("DATASOURCE " + dataSource);
        LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
        emfb.setDataSource(dataSource);
        //emfb.setPersistenceUnitName("test");
        emfb.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emfb.setPackagesToScan("com.petra.lib");
        emfb.setJpaProperties(additionalProperties());
//        EntityManagerFactory emf = emfb.getObject();
        System.out.println("EntityManagerFactory " + emfb);
        return emfb;
    }

    @Bean
    public JpaTransactionManager annotationDrivenTransactionManager(EntityManagerFactory emf) {
        //System.out.println(emf);
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

//    @Bean
//    public PetraController petraController(JpaTransactionManager transactionManager, PetraProperties petraProperties, Map<String, UserActionHandler> userActionHandlerMap,
//                                           Map<String, SourceUserHandler> sourceUserHandlerMap){
//        System.out.println("PetraTestAware");
//        Constructor constructor = new Constructor();
//        ObjectMapper objectMapper = new ObjectMapper();
//        ConstructorModel constructorModel = null;
//        try {
//            constructorModel = objectMapper.readValue(new File("src/main/resources/petra_config.json"), ConstructorModel.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
////        JpaTransactionManager transactionManager = applicationContext.getBean(JpaTransactionManager.class);
////        PetraProperties petraProperties = applicationContext.getBean(PetraProperties.class);
////        Map<String, UserActionHandler> userActionHandlerMap = applicationContext.getBeansOfType(UserActionHandler.class);
////        Map<String, SourceUserHandler> sourceUserHandlerMap = applicationContext.getBeansOfType(SourceUserHandler.class);
//        return constructor.construct(constructorModel, transactionManager, petraProperties,
//                userActionHandlerMap, sourceUserHandlerMap);
//    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "none");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");

        return properties;
    }


}
