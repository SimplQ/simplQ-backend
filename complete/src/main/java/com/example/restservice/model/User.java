package com.example.restservice.model;


import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    String id;
    String userName;
    String contactNumber;




    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
