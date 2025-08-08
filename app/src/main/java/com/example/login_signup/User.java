package com.example.login_signup;

public class User {
    public String id;
    public String email;
    public String password;
    public String mobile;
    // New fields for Version 2
    public String firstName;
    public String lastName;
    public String address;
    public String role;

    // Constructor with all fields (for Version 2)
    public User(String id, String email, String password, String mobile,
                String firstName, String lastName, String address, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.firstName = firstName != null ? firstName : "";
        this.lastName = lastName != null ? lastName : "";
        this.address = address != null ? address : "";
        this.role = role != null ? role : "user";
    }

    // Constructor for backward compatibility (Version 1 users)
    public User(String id, String email, String password, String mobile) {
        this(id, email, password, mobile, "", "", "", "user");
    }

    // Helper methods for better user experience
    public String getFullName() {
        if (firstName.isEmpty() && lastName.isEmpty()) {
            return "Not provided";
        }
        return (firstName + " " + lastName).trim();
    }

    public boolean isAdmin() {
        return "admin".equals(role);
    }

    public boolean hasCompleteProfile() {
        return !firstName.isEmpty() && !lastName.isEmpty() && !address.isEmpty();
    }

    // Override toString for debugging
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
