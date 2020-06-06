package com.example.restservice.config;


import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource("classpath:application.properties")
public class DataSourceConfig {


    @Bean
    public DataSource dataSource() {

        Map<String, Object> configOverrides = new HashMap<>();
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        if (System.getenv("RDS_HOSTNAME") != null) {
            String dbName = System.getenv("RDS_DB_NAME");
            String userName = System.getenv("RDS_USERNAME");
            String password = System.getenv("RDS_PASSWORD");
            String hostname = System.getenv("RDS_HOSTNAME");
            String port = System.getenv("RDS_PORT");
            dataSourceBuilder.driverClassName("org.postgresql.Driver");
            dataSourceBuilder.url("jdbc:postgresql://" + hostname + ":" + port + "/" + dbName);
            dataSourceBuilder.username(userName);
            dataSourceBuilder.password(password);
        } else {
            dataSourceBuilder.driverClassName("org.h2.Driver");
            dataSourceBuilder.url("jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;");
            dataSourceBuilder.username("sa");
            dataSourceBuilder.password("");
        }

        return dataSourceBuilder.build();
    }

}