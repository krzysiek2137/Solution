package se.visionmate.solution.service;

import java.net.InetAddress;
import java.util.Optional;
import java.util.UUID;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;
import org.apache.commons.text.RandomStringGenerator;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.visionmate.solution.exposure.model.UserCredencialRepresentation;
import se.visionmate.solution.model.User;
import se.visionmate.solution.repository.SessionRepository;
import se.visionmate.solution.repository.UserRepository;
import se.visionmate.solution.utils.ResourceException;

@Service
public class SessionService {

    @Value("${server.port}")
    private String serverPort;

    private UserRepository userRepository;

    @Autowired
    public SessionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String login(UserCredencialRepresentation userCredential) {
        User user = findUser(userCredential.getUserName());
        matchesPassword(userCredential.getPassword(), user.getPassword());
        return createSession(userCredential.getUserName());
    }

    public String resetPasswordLink(String userName) {
        User user = findUser(userName);
        String resetOperationId = UUID.randomUUID().toString();
        SessionRepository.addResetOperation(resetOperationId, user.getUserName());
        return "http://" + InetAddress.getLoopbackAddress().getHostAddress() + ":" + serverPort + "/solution/remind/"+resetOperationId;

    }

    @Transactional
    public String resetPassword(String resetId) {
        String userName = SessionRepository.getUserNameFromResetOperation(resetId)
            .orElseThrow(() -> new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR,
            "Reset link not active"));
        String newPassword = generateNewPassword();
        String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        userRepository.updatePassword(userName, hashed);
        return newPassword;
    }

    private User findUser(String userName) {
        Optional<User> user = userRepository.findById(userName);
        if (!user.isPresent()) {
            throw new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR, "User " + userName + " not found");
        }
        return user.get();
    }

    private void matchesPassword(String loginPassword, String existingPassword) {
        if (!BCrypt.checkpw(loginPassword, existingPassword)) {
            throw new ResourceException(HttpStatus.FORBIDDEN, "Invalid password");
        }
    }

    private String generateNewPassword() {
        String newPassword = new RandomStringGenerator.Builder()
            .withinRange('0', 'z')
            .filteredBy(LETTERS, DIGITS)
            .build().generate(10);
        return newPassword;
    }

    private String createSession(String userName) {
        String sessionId = UUID.randomUUID().toString();
        SessionRepository.addSession(sessionId, userName);
        return sessionId;
    }
}
