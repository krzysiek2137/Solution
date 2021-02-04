package se.visionmate.solution.exposure.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class PasswordResetRepresentation{
    @JsonProperty(value = "userName", required = true)
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
