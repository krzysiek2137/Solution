package se.visionmate.solution.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionRepository {
    private static Map<String, String> sessionStorage = new HashMap<>();
    private static Map<String, String> resetStorage = new HashMap<>();

    public static void addSession(String sessionId, String userName){
        sessionStorage.put(sessionId, userName);
    }

    public static Optional<String> getUserNameFromSession(String sessionId) {
        return Optional.ofNullable(sessionStorage.get(sessionId));
    }

    public static void addResetOperation(String resetId, String userName) {
        resetStorage.put(resetId, userName);
    }

    public static Optional<String> getUserNameFromResetOperation(String resetId) {
        return Optional.ofNullable(resetStorage.get(resetId));
    }

    public static void removeResetOperationById(String resetId) {
        resetStorage.remove(resetId);
    }
}
