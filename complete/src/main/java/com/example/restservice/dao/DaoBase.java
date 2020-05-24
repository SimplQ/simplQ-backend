package com.example.restservice.dao;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public abstract class DaoBase {

  protected final EntityManagerFactory entityManagerFactory;

  public DaoBase() {
    Map<String, Object> configOverrides = new HashMap<>();
    if (System.getenv("RDS_HOSTNAME") != null) {
      String dbName = System.getenv("RDS_DB_NAME");
      String userName = System.getenv("RDS_USERNAME");
      String password = System.getenv("RDS_PASSWORD");
      String hostname = System.getenv("RDS_HOSTNAME");
      String port = System.getenv("RDS_PORT");

      configOverrides.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
      configOverrides.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
      configOverrides.put(
          "javax.persistence.jdbc.url",
          "jdbc:postgresql://" + hostname + ":" + port + "/" + dbName);
      configOverrides.put("javax.persistence.jdbc.user", userName);
      configOverrides.put("javax.persistence.jdbc.password", password);
    }

    entityManagerFactory =
        Persistence.createEntityManagerFactory("com.example.jpa", configOverrides);
  }
}
