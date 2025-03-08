package org.leverx.ratingapp.services.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.leverx.ratingapp.dtos.auth.AuthenticationRequestDTO;
import org.leverx.ratingapp.dtos.auth.AuthenticationResponseDTO;
import org.leverx.ratingapp.dtos.auth.PasswordResetRequestDTO;
import org.leverx.ratingapp.dtos.auth.registration.RegistrationRequestDTO;
import org.leverx.ratingapp.entities.Comment;
import org.leverx.ratingapp.entities.GameObject;
import org.leverx.ratingapp.entities.User;
import org.leverx.ratingapp.exceptions.AccountNotActivatedException;
import org.leverx.ratingapp.repositories.UserRepository;
import org.leverx.ratingapp.services.auth.jwt.JwtService;
import org.leverx.ratingapp.services.auth.token.ConfirmationTokenService;
import org.leverx.ratingapp.services.user.UserService;
import org.leverx.ratingapp.services.email.interfaces.EmailSender;
import org.leverx.ratingapp.services.email.EmailValidatorService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.leverx.ratingapp.enums.Role;
import org.leverx.ratingapp.exceptions.UnauthorizedException;
import org.leverx.ratingapp.exceptions.InvalidOperationException;
import org.leverx.ratingapp.exceptions.ForbiddenException;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationAndRegistrationServiceImplementation implements AuthenticationAndRegistrationService {
    private final EmailValidatorService emailValidatorService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(authentication)
                .filter(auth -> auth.getPrincipal() instanceof UserDetails)
                .map(auth -> (User) auth.getPrincipal())
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));
    }

    @Override
    public <T> void authorizeUser(T entity, User currentUser) {
        User entityAuthor = null;
        if (entity instanceof Comment comment) {
            entityAuthor = comment.getAuthor();
        } else if (entity instanceof GameObject gameObject) {
            entityAuthor = gameObject.getUser();
        }
        if (entityAuthor == null || !entityAuthor.getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to modify this resource");
        }
    }

    @Override
    public AuthenticationResponseDTO register(RegistrationRequestDTO registrationRequestDTO) {
        boolean isValidEmail = emailValidatorService.test(registrationRequestDTO.email());
        if(!isValidEmail) {
            throw new InvalidOperationException("Invalid email format");
        }
        
        if (userRepository.existsByEmail(registrationRequestDTO.email())) {
            throw new InvalidOperationException("Email already registered");
        }

        var user = User.builder()
                .first_name(registrationRequestDTO.first_name())
                .last_name(registrationRequestDTO.last_name())
                .email(registrationRequestDTO.email())
                .password(passwordEncoder.encode(registrationRequestDTO.password()))
                .role(Role.SELLER)
                .build();
        userRepository.save(user);
        
        var jwtToken = jwtService.generateToken(user);
        confirmationTokenService.saveConfirmationToken(user.getEmail(), jwtToken);

        String link = "http://localhost:8080/auth/confirm?token=" + jwtToken;
        emailSender.send(registrationRequestDTO.email(),
                buildEmail(registrationRequestDTO.first_name(), link));

        return AuthenticationResponseDTO.builder()
                .user(registrationRequestDTO.email())
                .token(jwtToken)
                .Status("Your registration is in progress")
                .build();
    }

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
                  request.email(),
                  request.password()
          )
        );
        User user = userRepository.findByEmail(request.email())
                .filter(User::getIs_activated)
                .orElseThrow(() -> new AccountNotActivatedException(
                        userRepository.existsByEmail(request.email())
                                ? "Please, activate your account"
                                : "Please, check your email"
                ));

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseDTO
                .builder()
                .user(request.email())
                .token(jwtToken)
                .Status("You are authenticated")
                .build();
    }

    @Override
    public String confirmToken(String token) {
        var userEmail = jwtService.extractUsername(token);
        if (userEmail == null) {
            throw new InvalidOperationException("Invalid token");
        }

        var storedToken = confirmationTokenService.getConfirmationToken(userEmail)
                .orElseThrow(() -> new InvalidOperationException("Token not found or expired"));

        if (!token.equals(storedToken)) {
            throw new InvalidOperationException("Invalid token");
        }

        userService.enableUser(userEmail);
        confirmationTokenService.removeConfirmationToken(userEmail);
        return "confirmed";
    }

    @Override
    public AuthenticationResponseDTO initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidOperationException("Email not found"));

        String resetCode = generateResetCode();
        confirmationTokenService.saveResetCode(email, resetCode);
        emailSender.sendPasswordResetEmail(email, resetCode);

        return AuthenticationResponseDTO.builder()
                .user(email)
                .Status("Password reset code sent")
                .build();
    }

    @Override
    public AuthenticationResponseDTO resetPassword(PasswordResetRequestDTO request) {
        String storedCode = confirmationTokenService.getResetCode(request.email());
        if (storedCode == null || !storedCode.equals(request.code())) {
            throw new InvalidOperationException("Invalid or expired reset code");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidOperationException("User not found"));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        confirmationTokenService.removeResetCode(request.email());

        return AuthenticationResponseDTO.builder()
                .user(request.email())
                .Status("Password successfully reset")
                .build();
    }

    @Override
    public AuthenticationResponseDTO verifyResetCode(String email, String code) {
        String storedCode = confirmationTokenService.getResetCode(email);
        boolean isValid = storedCode != null && storedCode.equals(code);

        return AuthenticationResponseDTO.builder()
                .user(email)
                .Status(isValid ? "Valid reset code" : "Invalid reset code")
                .build();
    }

    private String generateResetCode() {
        SecureRandom random = new SecureRandom();
        return random.ints(6, 0, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }

    @Override
    public String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }


}
