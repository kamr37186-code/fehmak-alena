package com.fahmak.alena.user.service;

import com.fahmak.alena.user.dto.AuthResponse;
import com.fahmak.alena.user.entity.AuthProvider;
import com.fahmak.alena.user.entity.Role;
import com.fahmak.alena.user.entity.User;
import com.fahmak.alena.user.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserRepository userRepository;

    @Value("${google.client.id:not-configured}")
    private String googleClientId;

    @Transactional
    public AuthResponse authenticateWithGoogle(String idToken, Role requestedRole) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken token = verifier.verify(idToken);
            if (token == null) {
                throw new RuntimeException("Invalid Google token");
            }

            GoogleIdToken.Payload payload = token.getPayload();
            String email = payload.getEmail();
            String googleId = payload.getSubject();
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");
            String avatar = (String) payload.get("picture");

            User user = findOrCreateUser(email, googleId, AuthProvider.GOOGLE, firstName, lastName, avatar, requestedRole);
            
            user.setLastLoginDate(LocalDateTime.now());
            userRepository.save(user);

            return AuthResponse.builder()
                    .message("Login successful")
                    .userId(user.getId())
                    .role(user.getRole().name())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .build();
        } catch (Exception e) {
            log.error("Google authentication failed", e);
            throw new RuntimeException("Google authentication failed: " + e.getMessage());
        }
    }

    @Transactional
    public AuthResponse authenticateWithFacebook(String accessToken, Role requestedRole) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://graph.facebook.com/me?fields=id,first_name,last_name,email,picture";
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setBearerAuth(accessToken);
            org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                String facebookId = (String) body.get("id");
                String email = (String) body.get("email");
                String firstName = (String) body.get("first_name");
                String lastName = (String) body.get("last_name");
                
                String avatar = null;
                Object pictureObj = body.get("picture");
                if (pictureObj instanceof Map) {
                    Map<?, ?> pictureData = (Map<?, ?>) pictureObj;
                    Object dataObj = pictureData.get("data");
                    if (dataObj instanceof Map) {
                        Map<?, ?> data = (Map<?, ?>) dataObj;
                        avatar = (String) data.get("url");
                    }
                }
                
                if (email == null) {
                    throw new RuntimeException("Facebook account does not have an email associated");
                }
                
                User user = findOrCreateUser(email, facebookId, AuthProvider.FACEBOOK, firstName, lastName, avatar, requestedRole);
                
                user.setLastLoginDate(LocalDateTime.now());
                userRepository.save(user);
                
                return AuthResponse.builder()
                        .message("Login successful")
                        .userId(user.getId())
                        .role(user.getRole().name())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .build();
            } else {
                throw new RuntimeException("Invalid Facebook token");
            }
        } catch (Exception e) {
            log.error("Facebook authentication failed", e);
            throw new RuntimeException("Facebook authentication failed: " + e.getMessage());
        }
    }

    private User findOrCreateUser(String email, String providerId, AuthProvider provider, String firstName, String lastName, String avatar, Role requestedRole) {
        return userRepository.findByProviderIdAndAuthProvider(providerId, provider)
                .orElseGet(() -> {
                    Optional<User> existingByEmail = userRepository.findByEmail(email);
                    if (existingByEmail.isPresent()) {
                        User existing = existingByEmail.get();
                        existing.setAuthProvider(provider);
                        existing.setProviderId(providerId);
                        existing.setAvatarUrl(avatar);
                        return userRepository.save(existing);
                    }
                    
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(email.split("@")[0]);
                    newUser.setFirstName(firstName);
                    newUser.setLastName(lastName);
                    if (requestedRole == Role.ADMIN) {
                        throw new IllegalArgumentException("Registration of ADMIN accounts is not allowed via social login.");
                    }
                    newUser.setRole(requestedRole != null ? requestedRole : Role.STUDENT);
                    newUser.setAuthProvider(provider);
                    newUser.setProviderId(providerId);
                    newUser.setAvatarUrl(avatar);
                    newUser.setRegistrationDate(LocalDateTime.now());
                    newUser.setStatus("ACTIVE");
                    return userRepository.save(newUser);
                });
    }
}
