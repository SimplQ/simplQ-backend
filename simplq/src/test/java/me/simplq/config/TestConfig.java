package me.simplq.config;
import org.springframework.boot.test.context.TestConfiguration;
import me.simplq.dao.QueueRepository;
import me.simplq.service.QueueService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import me.simplq.config.DataSourceConfig;


@TestConfiguration
@Import({QueueRepository.class,DataSourceConfig.class})
public class TestConfig {

    @Autowired
    private QueueRepository queueRepository;

    @Bean
    public QueueService queueService(){
        return new QueueService(queueRepository,null);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean configureEntityManagerFactory() {
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
            entityManagerFactoryBean.setDataSource(configureDataSource());
            entityManagerFactoryBean.setPackagesToScan("${package}");
            entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            Properties jpaProperties = new Properties();
            jpaProperties.put(org.hibernate.cfg.Environment.DIALECT, dialect);
            jpaProperties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, hbm2ddlAuto);
            entityManagerFactoryBean.setJpaProperties(jpaProperties);
            return entityManagerFactoryBean;
        }
}
