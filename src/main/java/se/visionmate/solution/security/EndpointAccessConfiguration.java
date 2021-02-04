package se.visionmate.solution.security;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpMethod;

public class EndpointAccessConfiguration {

    private static List<EndpointAccessRule> endpointAccessRules = Arrays.asList(
        new EndpointAccessRule("/user", HttpMethod.POST, Arrays.asList("Create user")),
        new EndpointAccessRule("/user", HttpMethod.PATCH, Arrays.asList("Update user")),
        new EndpointAccessRule("/user/.*", HttpMethod.DELETE, Arrays.asList("Delete user")),
        new EndpointAccessRule("/user", HttpMethod.GET, Arrays.asList("List users"))
    );

    public static Optional<EndpointAccessRule> findAccessRule(String method, String endpointPath) {
        for (EndpointAccessRule rule: endpointAccessRules) {
            if (endpointPath.matches(rule.getPath()) && rule.getMethod().equals(HttpMethod.valueOf(method))) {
                return Optional.of(rule);
            }
        }
        return Optional.empty();
    }
}
