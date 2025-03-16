package org.leverx.ratingapp.services.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtServiceImplementation is the concrete implementation of the {@link JwtService} interface.
 * It provides functionalities to fetch the current user and authorize resource modifications
 * based on the current user's identity.
 */
@Service
public class JwtServiceImplementation implements JwtService {
    // The secret key for signing the JWT token, retrieved from environment variables
    private static final String SECRET_KEY = System.getenv("JWT_SECRET_KEY");

    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token the JWT token.
     * @return the username extracted from the token.
     */
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the JWT token based on the provided claim resolver function.
     *
     * @param token the JWT token.
     * @param claimsResolver a function to extract a specific claim.
     * @param <T> the type of the claim.
     * @return the value of the extracted claim.
     */
    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails the user details containing the username.
     * @return the generated JWT token.
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token with additional claims for the given user details.
     *
     * @param extraClaims additional claims to include in the token.
     * @param userDetails the user details containing the username.
     * @return the generated JWT token.
     */
    @Override
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails) {
        // Create and return the JWT token
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact(); // Return the token as a compact string
    }

    /**
     * Validates whether the given JWT token is valid based on the user details.
     *
     * @param token the JWT token to validate.
     * @param userDetails the user details to compare against the token.
     * @return true if the token is valid, false otherwise.
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    /**
     * Checks whether the JWT token has expired.
     *
     * @param token the JWT token to check.
     * @return true if the token has expired, false otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date of the JWT token.
     *
     * @param token the JWT token.
     * @return the expiration date of the token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token the JWT token.
     * @return the claims extracted from the token.
     */
    @Override
    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }
    /**
     * Retrieves the signing key used for signing JWT tokens.
     *
     * @return the signing key.
     */
    private Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // Decode the base64-encoded secret key
        return Keys.hmacShaKeyFor(keyBytes); // Create a HMAC signing key
    }
}
