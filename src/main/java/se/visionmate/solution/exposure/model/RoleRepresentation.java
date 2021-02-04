package se.visionmate.solution.exposure.model;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import se.visionmate.solution.utils.RoleDeserializer;

@JsonDeserialize(using = RoleDeserializer.class)
public class RoleRepresentation {

    private String roleName;
    private List<String> permissions;

    public RoleRepresentation(String roleName, List<String> permissions) {
        this.roleName = roleName;
        this.permissions = permissions;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
