package org.leverx.ratingapp.services.auth.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

/**
 * JwtService interface defines the contract for services that handle JWT operations.
 * It includes methods to extract information from a JWT, generate new JWTs, and validate tokens.
 */
public interface JwtService {
     // Extracts the username (subject) from the given JWT token
     String extractUsername(String token);
     // Extracts a specific claim from the given JWT token.
     <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
     // Generates a JWT token based on the given UserDetails
     String generateToken(UserDetails userDetails);
     // Generates a JWT token with additional claims, based on the given UserDetails.
     String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);
     // Validates whether the given JWT token is valid based on the user details.
     boolean isTokenValid(String token, UserDetails userDetails);
     // Extracts all claims from the given JWT token.
     Claims extractAllClaims(String token);
}
