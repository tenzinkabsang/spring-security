package com.example.springsecurityclient.controllers;

import com.example.springsecurityclient.entities.User;
import com.example.springsecurityclient.entities.VerificationToken;
import com.example.springsecurityclient.events.PasswordResetRequestEvent;
import com.example.springsecurityclient.events.RegistrationCompleteEvent;
import com.example.springsecurityclient.events.UserTokenRequestEvent;
import com.example.springsecurityclient.models.PasswordModel;
import com.example.springsecurityclient.models.UserModel;
import com.example.springsecurityclient.models.UserNotFoundException;
import com.example.springsecurityclient.models.UserTokenException;
import com.example.springsecurityclient.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.*;

@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {

        User user = userService.registerUser(userModel);

        publisher.publishEvent(new RegistrationCompleteEvent(
                user,
                applicationUrl(request)
        ));

        return "success";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam ("token") String token) {
        boolean success = userService.validateVerificationToken(token);

        if(!success)
            return "Verification failed. Link is expired!";

        return "Validated";
    }

    @GetMapping("/resendVerifyToken")
    public ResponseEntity<String> resendVerificationToken(@RequestParam("token") String oldToken,
                                                         final HttpServletRequest request) throws UserTokenException {
        VerificationToken token = userService.getExistingVerificationToken(oldToken);
        publisher.publishEvent(new UserTokenRequestEvent(
                token,
                applicationUrl(request)
        ));
        String success = "Check your email";

        return ok(success);
    }


    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordModel passwordModel,
    final HttpServletRequest request) throws UserNotFoundException {
        User user = userService.findUserByEmail(passwordModel.getEmail());

        publisher.publishEvent(new PasswordResetRequestEvent(
                user,
                request
        ));

        return ok("Reset password email sent");
    }

    @PostMapping("/savePassword")
    public ResponseEntity<String> savePassword(@RequestParam ("token") String token,
                                               @RequestBody PasswordModel passwordModel){

        var result = userService.isTokenValid(token);

        if(!result.item1()) return ok("Reset link expired.");

        User user = result.item2();
        userService.changePassword(user, passwordModel);

        return ok("Password saved!");
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestBody PasswordModel passwordModel) throws UserNotFoundException {
        User user = userService.findUserByEmail(passwordModel.getEmail());

        if(!userService.checkIfOldPasswordValid(user, passwordModel.getOldPassword())){
            return ok("Invalid old password");
        }
        userService.changePassword(user, passwordModel);
        return ok("Password successfully changed!");
    }

    private String applicationUrl(HttpServletRequest request) {
        return String.format("http://%s:%s/%s",
                request.getServerName(),
                request.getServerPort(),
                request.getContextPath());
    }
}
