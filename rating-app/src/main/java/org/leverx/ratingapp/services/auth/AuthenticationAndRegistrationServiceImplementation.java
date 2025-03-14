package org.leverx.ratingapp.services.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.leverx.ratingapp.dtos.auth.AuthenticationRequestDTO;
import org.leverx.ratingapp.dtos.auth.AuthenticationResponseDTO;
import org.leverx.ratingapp.dtos.auth.PasswordResetRequestDTO;
import org.leverx.ratingapp.dtos.auth.registration.RegistrationRequestDTO;
import org.leverx.ratingapp.entities.User;
import org.leverx.ratingapp.exceptions.AccountNotActivatedException;
import org.leverx.ratingapp.repositories.UserRepository;
import org.leverx.ratingapp.services.auth.jwt.JwtService;
import org.leverx.ratingapp.services.auth.token.ConfirmationTokenService;
import org.leverx.ratingapp.services.pendingcomment.PendingCommentService;
import org.leverx.ratingapp.services.user.UserService;
import org.leverx.ratingapp.services.email.EmailService;
import org.leverx.ratingapp.services.email.validation.EmailValidatorService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.leverx.ratingapp.enums.Role;
import org.leverx.ratingapp.exceptions.InvalidOperationException;
import java.security.SecureRandom;
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
    private final PendingCommentService pendingCommentService;
    private final AuthorizationService authorizationService;

    @Override
    public User getCurrentUser() {
        return authorizationService.getRequiredCurrentUser();
    }

    @Override
    public <T> void authorizeUser(T entity, User currentUser) {
        authorizationService.authorizeResourceModification(entity, currentUser);
    }

    @Transactional
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
                .firstName(registrationRequestDTO.firstName())
                .lastName(registrationRequestDTO.lastName())
                .email(registrationRequestDTO.email())
                .password(passwordEncoder.encode(registrationRequestDTO.password()))
                .role(Role.SELLER)
                .build();
        userRepository.save(user);
        
        var jwtToken = jwtService.generateToken(user);
        confirmationTokenService.saveConfirmationToken(user.getEmail(), jwtToken);

        String link = "http://localhost:8080/auth/confirm?token=" + jwtToken;
        emailService.sendRegistrationEmail(registrationRequestDTO.email(),
                registrationRequestDTO.firstName(),
                link);

        return AuthenticationResponseDTO.builder()
                .user(registrationRequestDTO.email())
                .token(jwtToken)
                .Status("Your registration is in progress")
                .build();
    }

    @Override
    public AuthenticationResponseDTO registerWithPendingComment(RegistrationRequestDTO registrationRequestDTO, Long sellerId, String comment) {
        // Register user
        AuthenticationResponseDTO response = register(registrationRequestDTO);
        
        // Store pending comment
        pendingCommentService.savePendingComment(registrationRequestDTO.email(), sellerId, comment);
        
        return response;
    }

    @Transactional
    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
                  request.email(),
                  request.password()
          )
        );
        User user = userRepository.findByEmail(request.email())
                .filter(User::getIsActivated)
                .orElseThrow(() -> new AccountNotActivatedException(
                        userRepository.existsByEmail(request.email())
                                ? "Please wait, we are validating your account"
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

    @Transactional
    @Override
    public String confirmEmail(String token) {
        var userEmail = jwtService.extractUsername(token);
        if (userEmail == null) {
            throw new InvalidOperationException("Invalid token");
        }

        var storedToken = confirmationTokenService.getConfirmationToken(userEmail)
                .orElseThrow(() -> new InvalidOperationException("Token not found or expired"));

        if (!token.equals(storedToken)) {
            throw new InvalidOperationException("Invalid token");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new InvalidOperationException("User not found"));
        user.setIsEmailConfirmed(true);
        userRepository.save(user);
        
        return "Email confirmed. Waiting for admin approval.";
    }

    @Transactional
    @Override
    public String confirmUser(String email, Boolean confirm) {
        if (email == null) {
            throw new InvalidOperationException("Invalid email");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidOperationException("User not found"));

        if (!user.getIsEmailConfirmed()) {
            throw new InvalidOperationException("Email not confirmed by user");
        }

        if (!confirm) {
            userRepository.deleteUserByEmail(email);
            confirmationTokenService.removeConfirmationToken(email);
            return "User registration declined";
        }

        userService.enableUser(email);
        confirmationTokenService.removeConfirmationToken(email);
        pendingCommentService.processPendingComment(email);
        
        return "User approved and activated with pending comments processed";
    }

    @Transactional
    @Override
    public AuthenticationResponseDTO initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidOperationException("Email not found"));

        String resetCode = generateResetCode();
        confirmationTokenService.saveResetCode(email, resetCode);
        emailService.sendPasswordResetEmail(email,user.getFirstName(), resetCode);

        return AuthenticationResponseDTO.builder()
                .user(email)
                .Status("Password reset code sent")
                .build();
    }

    @Transactional
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
