package se.visionmate.solution.exposure;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import se.visionmate.solution.exposure.model.UserRequestRepresentation;
import se.visionmate.solution.exposure.model.UserResponseRepresentation;
import se.visionmate.solution.service.UserService;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createUser(@RequestBody UserRequestRepresentation userRequestRepresentation) {
        userService.saveUser(userRequestRepresentation);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<UserResponseRepresentation>> getUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsers());
    }

    @RequestMapping(value = "/{userName}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteUser(@PathVariable String userName) {
        userService.deleteUser(userName);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    @ResponseBody
    public void updateUser(@RequestBody UserRequestRepresentation userRequestRepresentation) {
        userService.updateUser(userRequestRepresentation);
    }
}
