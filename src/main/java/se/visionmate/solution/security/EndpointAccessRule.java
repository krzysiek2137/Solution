package se.visionmate.solution.security;


import java.util.List;

import org.springframework.http.HttpMethod;

public class EndpointAccessRule {
    private String path;
    private HttpMethod method;
    private List<String> permissions;

    public EndpointAccessRule(String path, HttpMethod method, List<String> permissions) {
        this.path = path;
        this.method = method;
        this.permissions = permissions;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
