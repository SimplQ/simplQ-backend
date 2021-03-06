package me.simplq.listener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;

@Component
@Profile("dev")
@Slf4j
public class ApplicationReadyListener implements ApplicationListener<ApplicationReadyEvent> {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Value("${spring.datasource.username}")
  private String dbUsername;

  @Value("${spring.datasource.password}")
  private String dbPassword;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    runMigration(); 
  }

  private void runMigration() {
    Liquibase liquibase = null;

    try {
      Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
      Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
      liquibase = new Liquibase("db/changeset.log.xml", new ClassLoaderResourceAccessor(), database);
      liquibase.update(new Contexts(), new liquibase.LabelExpression());

    } catch (SQLException | LiquibaseException e) {
      log.error("Migration failed", e);
      
    } finally {
      if (liquibase != null) {
        try {
          liquibase.close();
        } catch (LiquibaseException e) {
          log.warn("Cannot close liquibase", e);
        }
      }
    }
  }
  
}
