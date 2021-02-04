package se.visionmate.solution.exposure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import se.visionmate.solution.exposure.model.PasswordResetRepresentation;
import se.visionmate.solution.exposure.model.UserCredencialRepresentation;
import se.visionmate.solution.service.SessionService;

@RestController
@RequestMapping("/")
public class AuthController {

    private SessionService sessionService;

    @Autowired
    public AuthController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<String> login(@RequestBody UserCredencialRepresentation userCredential) {
        return ResponseEntity.status(HttpStatus.OK).body(sessionService.login(userCredential));
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public String resetPassword(@RequestBody PasswordResetRepresentation passwordReset) {
        return sessionService.resetPasswordLink(passwordReset.getUserName());
    }

    @RequestMapping(value = "/remind/{resetId}", method = RequestMethod.GET)
    public String remindPassword(@PathVariable String resetId) {
        return sessionService.resetPassword(resetId);
    }
}
