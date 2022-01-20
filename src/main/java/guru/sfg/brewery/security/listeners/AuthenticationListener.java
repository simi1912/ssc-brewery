package guru.sfg.brewery.security.listeners;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.domain.security.LoginSuccess;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.LoginFailureRepository;
import guru.sfg.brewery.repositories.security.LoginSuccessRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationListener{

    private final LoginSuccessRepository loginSuccessRepository;
    private final LoginFailureRepository loginFailureRepository;
    private final UserRepository userRepository;

    @EventListener
    public void listenOnSuccess(AuthenticationSuccessEvent event) {
        if(event.getSource() instanceof UsernamePasswordAuthenticationToken){
            LoginSuccess.LoginSuccessBuilder builder = LoginSuccess.builder();

            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

            if(token.getPrincipal() instanceof User){
                User user = (User) token.getPrincipal();
                builder.user(user);

                log.debug("User Logged In: " + user.getUsername());
            }

            if(token.getDetails() instanceof WebAuthenticationDetails){
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();
                builder.sourceIp(details.getRemoteAddress());
                log.debug("Source IP: " + details.getRemoteAddress());
            }

            LoginSuccess loginSuccess = loginSuccessRepository.save(builder.build());

            log.debug("Login Success saved. Id: " + loginSuccess.getId());
        }
    }

    @EventListener
    public void listenOnFailure(AuthenticationFailureBadCredentialsEvent event) {
        if(event.getSource() instanceof UsernamePasswordAuthenticationToken){
            LoginFailure.LoginFailureBuilder builder = LoginFailure.builder();

            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

            log.debug("LoggingIn fail for: " + token);
            builder.username(token.getName());

            if(token.getDetails() instanceof WebAuthenticationDetails){
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();

                builder.sourceIp(details.getRemoteAddress());
                log.debug("Source IP: " + details.getRemoteAddress());
            }

            LoginFailure loginFailure = builder.build();

            loginFailureRepository.save(loginFailure);

            log.debug("Login Failed saved. Id: " + loginFailure.getId());

            User user = userRepository.findByUsername(loginFailure.getUsername())
                    .orElse(null);

            if(user != null) {
                log.debug("Username match with registered user.");
                lockUser(user);
            }
        }
    }

    private void lockUser(User user) {
        List<LoginFailure> failures = loginFailureRepository.findAllByUsernameAndCreatedDateIsAfter(
                user.getUsername(), Timestamp.valueOf(LocalDateTime.now().minusDays(1)));

        if(failures.size() > 3) {
            log.debug("Locking User Account: " + user.getUsername());
            user.setAccountNotLocked(false);
            userRepository.save(user);
        }
    }

}
