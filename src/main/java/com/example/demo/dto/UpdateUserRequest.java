package com.example.demo.dto;

public class UpdateUserRequest {

    private String username;
    private String email;            // only username and email can be updated
                                     // role and password are not changed here

    public UpdateUserRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
