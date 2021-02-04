package se.visionmate.solution.exposure.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class UserCredencialRepresentation {
    @JsonProperty(required = true)
    private String userName;
    @JsonProperty(required = true)
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
