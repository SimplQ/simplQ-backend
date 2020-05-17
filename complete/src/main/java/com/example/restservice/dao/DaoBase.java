package com.example.restservice.dao;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public abstract class DaoBase {

  protected final EntityManagerFactory entityManagerFactory;

  public DaoBase() {
    entityManagerFactory = Persistence.createEntityManagerFactory("com.example.jpa");
  }
}
