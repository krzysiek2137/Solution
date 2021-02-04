package se.visionmate.solution.exposure.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserRequestRepresentation {
    private String userName;
    private String password;
    private String role;

    public UserRequestRepresentation(@JsonProperty(value = "userName", required = true) String userName,
                                     @JsonProperty(value = "password") String password,
                                     @JsonProperty(value = "role", required = true) String role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    public UserRequestRepresentation(String userName, String role) {
        this.userName = userName;
        this.role = role;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
