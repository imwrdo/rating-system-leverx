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
import org.leverx.ratingapp.services.email.EmailService;
import org.leverx.ratingapp.services.email.validation.EmailValidatorService;
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
    private final EmailService emailService;
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
        emailService.sendRegistrationEmail(registrationRequestDTO.email(),
                registrationRequestDTO.first_name(),
                link);

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
    public String confirmUser(String email, Boolean confirm) {

        if (email == null) {
            throw new InvalidOperationException("Invalid email");
        }

        confirmationTokenService.getConfirmationToken(email)
                .orElseThrow(() -> new InvalidOperationException("Token not found or expired"));

        if(!confirm) {
            userRepository.deleteUserByEmail(email);
            confirmationTokenService.removeConfirmationToken(email);
            return "Declined";
        }

        userService.enableUser(email);
        confirmationTokenService.removeConfirmationToken(email);
        return "confirmed";
    }

    @Override
    public AuthenticationResponseDTO initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidOperationException("Email not found"));

        String resetCode = generateResetCode();
        confirmationTokenService.saveResetCode(email, resetCode);
        emailService.sendPasswordResetEmail(email,user.getFirst_name(), resetCode);

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



}
