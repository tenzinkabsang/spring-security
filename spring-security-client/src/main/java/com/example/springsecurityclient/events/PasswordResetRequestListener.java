package com.example.springsecurityclient.events;

import com.example.springsecurityclient.entities.PasswordResetToken;
import com.example.springsecurityclient.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class PasswordResetRequestListener implements ApplicationListener<PasswordResetRequestEvent> {

    private static final String PATH = "/savePassword";
    private final UserService userService;

    @Autowired
    public PasswordResetRequestListener(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(PasswordResetRequestEvent event) {

        // Check if there is an existing password reset token that is not expired.
        Optional<PasswordResetToken> existingActiveToken =
                userService.getUserPasswordResetToken(event.getUser().getId());

        // If found: simply resend the email.
        if(existingActiveToken.isPresent()) {
            PasswordResetToken token = existingActiveToken.get();

            // If expired, make it active again.
            if(token.isExpired())
                userService.refreshPasswordResetToken(token);

            // Email
            String resetUrl = resetPasswordPath(event.getRequest(), token.getToken());
            log.info("Resend existing Password reset token: {}", resetUrl);
        } else {
            // If no token exists, then create a new one.
            String token = UUID.randomUUID().toString();
            PasswordResetToken passwordResetToken = new PasswordResetToken(event.getUser(), token);

            userService.createPasswordResetToken(passwordResetToken);

            // Email
            String resetUrl = resetPasswordPath(event.getRequest(), token);
            log.info("New Password reset token: {}", resetUrl);
        }

    }

    // Simplify this mess of %s :(
    private static String resetPasswordPath(HttpServletRequest request, String token) {
        return String.format("http://%s:%s%s?token=%s",
                request.getServerName(),
                request.getServerPort(),
                PATH,
                token);
    }
}
