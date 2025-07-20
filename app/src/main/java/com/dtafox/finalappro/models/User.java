package com.dtafox.finalappro.models;

public class User {
    private String email;
    private String name;
    private String gender;
    private int age;
    private String userId;

    public User() {} // Required empty constructor for Firebase

    public User(String email, String name, String gender, int age, String userId) {
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.userId = userId;
    }

    public User(String email, String name) {
        this.email = email;
        this.name = name;
        this.gender = "";
        this.age = 0;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
} 