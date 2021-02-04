package se.visionmate.solution.exposure.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class UserResponseRepresentation {
    private String userName;
    private String role;

    public UserResponseRepresentation(@JsonProperty("userName") String userName,
                                      @JsonProperty("role") String role) {
        this.userName = userName;
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
