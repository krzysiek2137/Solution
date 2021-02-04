package se.visionmate.solution.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import se.visionmate.solution.model.User;
import se.visionmate.solution.repository.SessionRepository;
import se.visionmate.solution.repository.UserRepository;
import se.visionmate.solution.utils.ResourceException;

public class CustomBasicAuthenticationFilter implements Filter {
    private final String AUTHORIZATION_HEADER = "Authorization";

    @Value("${admin.role.name}")
    private String adminName;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        String authorizationHeader = ((HttpServletRequest) servletRequest).getHeader(AUTHORIZATION_HEADER);
        isValidateRequest(authorizationHeader);
        if (!isValidateRequest(authorizationHeader)) {
            stopRequestProcessingWithError(servletResponse, "Missing or incorrect basic token");
            return;
        }
        List<String> authorizationHeaderItems = Arrays.asList(authorizationHeader.split(" "));


        Optional<String> userName =  SessionRepository.getUserNameFromSession(authorizationHeaderItems.get(1));
        if (!userName.isPresent()) {
            stopRequestProcessingWithError(servletResponse, "Session does not exist");
            return;
        }
        Optional<User> user = userRepository.findUserWithRoleByUserName(userName.get());
        boolean hasAccess = checkUserEndpointAccess(user.get(), ((HttpServletRequest) servletRequest).getMethod(),
            getPath((HttpServletRequest) servletRequest));
        if (!hasAccess) {
            stopRequestProcessingWithError(servletResponse, "Access not allowed");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isValidateRequest(String authorizationHeader) {
        if (authorizationHeader == null || Arrays.asList(authorizationHeader.split(" ")).size() != 2) {
            return false;
        }
        return true;
    }

    private boolean checkUserEndpointAccess(User user, String method, String endpointPath) {
        AtomicBoolean hasAccess = new AtomicBoolean(true);
        if (!user.getRole().getRoleName().equals(adminName)) {
            Optional<EndpointAccessRule> rule = EndpointAccessConfiguration.findAccessRule(method, endpointPath);
            if (rule.isPresent()) {
                List<String> existingUserPermissions = user.getRole().getPermissions()
                    .stream()
                    .map(permission -> permission.getPermissionName())
                    .collect(Collectors.toList());
                rule.get().getPermissions().forEach(permission -> {
                    if (!existingUserPermissions.contains(permission)) {
                        hasAccess.set(false);
                    }
                });
            } else {
                hasAccess.set(false);
            }
        }
        return hasAccess.get();
    }

    private String getPath(HttpServletRequest servletRequest) {
        if (servletRequest.getPathInfo() == null) {
            return servletRequest.getServletPath();
        } else {
            return servletRequest.getPathInfo();
        }
    }

    private void stopRequestProcessingWithError (ServletResponse response, String errorMessage) {
        HttpServletResponse resp = (HttpServletResponse) response;
        resp.reset();
        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        try {
            resp.getWriter().write(errorMessage);
            resp.getWriter().flush();
            resp.getWriter().close();

        } catch (IOException e) {
             throw new ResourceException(HttpStatus.FORBIDDEN, "Unexpected error");
        }
    }
}

