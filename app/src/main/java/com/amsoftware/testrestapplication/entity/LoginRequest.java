package com.amsoftware.testrestapplication.entity;


import java.io.Serializable;

public class LoginRequest implements Serializable
{

    private String username;

    private String password;

    public String getUsernameOrEmail()
    {
        return username;
    }

    public void setUsernameOrEmail(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}