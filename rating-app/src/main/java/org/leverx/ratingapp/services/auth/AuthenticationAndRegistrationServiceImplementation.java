package org.leverx.ratingapp.services.auth;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.leverx.ratingapp.dtos.auth.AuthenticationRequestDTO;
import org.leverx.ratingapp.dtos.auth.AuthenticationResponseDTO;
import org.leverx.ratingapp.dtos.auth.PasswordResetRequestDTO;
import org.leverx.ratingapp.dtos.auth.registration.RegistrationRequestDTO;
import org.leverx.ratingapp.models.entities.User;
import org.leverx.ratingapp.models.enums.Status;
import org.leverx.ratingapp.exceptions.AccountNotActivatedException;
import org.leverx.ratingapp.exceptions.ConflictException;
import org.leverx.ratingapp.exceptions.ResourceNotFoundException;
import org.leverx.ratingapp.repositories.UserRepository;
import org.leverx.ratingapp.services.auth.authorization.AuthorizationServiceImplementation;
import org.leverx.ratingapp.services.auth.jwt.JwtService;
import org.leverx.ratingapp.services.auth.resetcode.ResetCodeService;
import org.leverx.ratingapp.services.auth.token.ConfirmationTokenService;
import org.leverx.ratingapp.services.pendingcomment.PendingCommentService;
import org.leverx.ratingapp.services.user.UserService;
import org.leverx.ratingapp.services.email.EmailService;
import org.leverx.ratingapp.services.email.validation.EmailValidatorService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.leverx.ratingapp.models.enums.Role;
import org.leverx.ratingapp.exceptions.InvalidOperationException;
import java.security.SecureRandom;
import java.util.stream.Collectors;

/**
 * AuthenticationAndRegistrationServiceImplementation is the concrete implementation
 * of the {@link AuthenticationAndRegistrationService} interface.
 * It provides methods for user authentication, registration, and related processes.
 * This service manages the lifecycle of user accounts, including registration, login, email confirmation,
 * password reset, and user authorization.
 */
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
    private final AuthorizationServiceImplementation authorizationService;
    private final ResetCodeService resetCodeService;

    /**
     * Retrieves the current authenticated user.
     *
     * @return the current authenticated {@link User} object.
     */
    @Override
    public User getCurrentUser() {
        return authorizationService.getRequiredCurrentUser();
    }

    /**
     * Authorizes the user to perform an action on a given entity.
     *
     * @param entity the entity the {@link User} is trying to interact with.
     * @param currentUser the current authenticated {@link User} attempting the action.
     * @param <T> the type of the entity.
     */
    @Override
    public <T> void authorizeUser(T entity, User currentUser) {
        authorizationService.authorizeResourceModification(entity, currentUser);
    }

    /**
     * Registers a new user with the provided registration details.
     *
     * @param registrationRequestDTO the registration details.
     * @return an {@link AuthenticationResponseDTO} containing the token and user info.
     */
    @Transactional
    @Override
    public AuthenticationResponseDTO register(RegistrationRequestDTO registrationRequestDTO) {
        boolean isValidEmail = emailValidatorService.test(registrationRequestDTO.email());
        if(!isValidEmail) {
            throw new InvalidOperationException("Invalid email format");
        }
        
        if (userRepository.existsByEmail(registrationRequestDTO.email())) {
            throw new ConflictException("Email already registered");
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

        String link = String.format("https://%s/auth/confirm?token=%s",System.getenv("APP_DOMAIN"),jwtToken);
        emailService.sendRegistrationEmail(registrationRequestDTO.email(),
                registrationRequestDTO.firstName(),
                link);

        return AuthenticationResponseDTO.builder()
                .user(registrationRequestDTO.email())
                .token(jwtToken)
                .status(Status.PENDING.getValueOfStatus())
                .build();
    }

    /**
     * Registers a new user with pending comment data, allowing for initial feedback during the registration process.
     *
     * @param registrationRequestDTO the registration details of the user.
     * @param sellerId the ID of the seller the user is associated with.
     * @param comment the initial comment or feedback from the user.
     * @param grade the rating or grade given by the user.
     * @return an {@link AuthenticationResponseDTO} containing the token and user info.
     */
    @Transactional
    @Override
    public AuthenticationResponseDTO registerWithPendingComment(RegistrationRequestDTO registrationRequestDTO, Long sellerId, String comment, Integer grade) {
        // Register user
        AuthenticationResponseDTO response = register(registrationRequestDTO);
        
        // Store pending comment
        pendingCommentService.savePendingComment(registrationRequestDTO.email(), sellerId, comment, grade);
        
        return response;
    }

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param request the authentication details (username and password).
     * @return an {@link AuthenticationResponseDTO} containing the token and user info.
     */
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
                .status(Status.AUTHENTICATED.getValueOfStatus())
                .build();
    }

    /**
     * Confirms the user's email by validating the provided token.
     *
     * @param token the confirmation token sent to the user's email.
     * @return a confirmation message.
     */
    @Transactional
    @Override
    public String confirmEmail(String token) {
        var userEmail = jwtService.extractUsername(token);
        if (userEmail == null) {
            throw new InvalidOperationException("Invalid token");
        }

        var storedToken = confirmationTokenService.getConfirmationToken(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found or expired"));

        if (!token.equals(storedToken)) {
            throw new InvalidOperationException("Invalid token");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsEmailConfirmed(true);
        userRepository.save(user);
        
        return String.format("Email is %s. Waiting for admin approval.",
                Status.ACTIVE.getValueOfStatus());
    }

    /**
     * Confirms or denies a user's registration based on their email and confirmation status.
     *
     * @param email the email of the user to be confirmed.
     * @param confirm the confirmation status (true or false).
     * @return a message indicating the confirmation result.
     */
    @Transactional
    @Override
    public String confirmUser(String email, Boolean confirm) {
        if (email == null) {
            throw new InvalidOperationException("Invalid email");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getIsEmailConfirmed()) {
            throw new InvalidOperationException("Email not confirmed by user");
        }

        if (!confirm) {
            userRepository.deleteUserByEmail(email);
            confirmationTokenService.removeConfirmationToken(email);
            return String.format("User registration is %s",
                    Status.DELETED.getValueOfStatus());
        }

        userService.enableUser(email);
        confirmationTokenService.removeConfirmationToken(email);
        pendingCommentService.processPendingComment(email);
        
        return String.format("User %s and %s with pending comments processed",
                Status.ACTIVE.getValueOfStatus(), Status.APPROVED.getValueOfStatus());
    }

    /**
     * Initiates a password reset by sending a reset code to the user's email.
     *
     * @param email the email address of the user who requested the reset.
     * @return an {@link AuthenticationResponseDTO} containing the reset status.
     */
    @Transactional
    @Override
    public AuthenticationResponseDTO initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidOperationException("Email not found"));

        String resetCode = generateResetCode();
        resetCodeService.saveResetCode(email, resetCode);
        emailService.sendPasswordResetEmail(email,user.getFirstName(), resetCode);

        return AuthenticationResponseDTO.builder()
                .user(email)
                .status(Status.SENT.getValueOfStatus())
                .build();
    }

    /**
     * Resets the user's password using the provided new password and reset code.
     *
     * @param request the password reset details including the code and new password.
     * @return an {@link AuthenticationResponseDTO} containing the updated user info.
     */
    @Transactional
    @Override
    public AuthenticationResponseDTO resetPassword(PasswordResetRequestDTO request) {
        String storedCode = resetCodeService.getResetCode(request.email());
        if (storedCode == null || !storedCode.equals(request.code())) {
            throw new InvalidOperationException("Invalid or expired reset code");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        resetCodeService.removeResetCode(request.email());

        return AuthenticationResponseDTO.builder()
                .user(request.email())
                .status("Password successfully reset")
                .build();
    }

    /**
     * Verifies the provided reset code for a given email address.
     *
     * @param email the email address of the user requesting verification.
     * @param code the reset code to be verified.
     * @return an {@link AuthenticationResponseDTO} containing the status of the verification.
     */
    @Override
    public AuthenticationResponseDTO verifyResetCode(String email, String code) {
        String storedCode = resetCodeService.getResetCode(email);
        boolean isValid = storedCode != null && storedCode.equals(code);

        return AuthenticationResponseDTO.builder()
                .user(email)
                .status(isValid ? "Valid reset code" : "Invalid reset code")
                .build();
    }

    /**
     * Helping function, which generates reset code
     *
     * @return 6-digits reset code
     */
    private String generateResetCode() {
        SecureRandom random = new SecureRandom();
        return random.ints(6, 0, 10)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }



}
