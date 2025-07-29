package com.example.login_signup;

public class User {
    public String id;
    public String email;
    public String password;
    public String mobile;

    public User(String id, String email, String password, String mobile) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
    }
}
