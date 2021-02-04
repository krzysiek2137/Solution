package se.visionmate.solution.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.visionmate.solution.exposure.model.UserRequestRepresentation;
import se.visionmate.solution.exposure.model.UserResponseRepresentation;
import se.visionmate.solution.model.Role;
import se.visionmate.solution.model.User;
import se.visionmate.solution.repository.RoleRepository;
import se.visionmate.solution.repository.UserRepository;
import se.visionmate.solution.utils.ResourceException;

@Service
public class UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void saveUser(UserRequestRepresentation userRequestRepresentation) {
        Optional<User> dbUser = userRepository.findById(userRequestRepresentation.getUserName());
        if (dbUser.isPresent()) {
            throw new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR,
                "User " + userRequestRepresentation.getUserName() + " already exists");
        }
        Role role = roleRepository.findRoleByRoleName(userRequestRepresentation.getRole())
            .orElseThrow(() -> new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Role " + userRequestRepresentation.getRole() + " not found"));
        User user = new User();
        user.setUserName(userRequestRepresentation.getUserName());
        user.setPassword(BCrypt.hashpw(userRequestRepresentation.getPassword(), BCrypt.gensalt()));
        user.setRole(role);

        userRepository.save(user);
    }

    public List<UserResponseRepresentation> getUsers() {
        List<UserResponseRepresentation> userRequestRepresentations = new ArrayList();
        userRepository.findAll().forEach(user -> {
            userRequestRepresentations.add(new UserResponseRepresentation(user.getUserName(),
                user.getRole().getRoleName()));
        });
        return userRequestRepresentations;

    }

    @Transactional
    public void updateUser(UserRequestRepresentation userRequestRepresentation) {
        User user = userRepository.findById(userRequestRepresentation.getUserName())
            .orElseThrow(() -> new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR,
            "User " + userRequestRepresentation.getUserName() + " not found"));

        Role role = roleRepository.findRoleByRoleName(userRequestRepresentation.getRole())
            .orElseThrow(() -> new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Role " + userRequestRepresentation.getRole() + " not found"));
        user.setRole(role);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String userName) {
        userRepository.deleteById(userName);
    }
}
