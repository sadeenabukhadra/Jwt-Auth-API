package com.example.jwt_auth_api.Service;

import com.example.jwt_auth_api.Model.RefreshToken;
import com.example.jwt_auth_api.Model.User;
import com.example.jwt_auth_api.Repository.RefreshTokenRepository;
import com.example.jwt_auth_api.Repository.UserRepository;
import com.example.jwt_auth_api.Util.JWTUtil;
import com.example.jwt_auth_api.Exception.CustomException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Map<String, String> signup(String username, String email, String password) {
        if(username == null || email == null || password == null)
            throw new CustomException("username, email and password are required", 400);

        if(userRepository.existsByEmail(email))
            throw new CustomException("Email already exists", 400);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");

        userRepository.save(user);
        return Map.of("message", "User registered successfully");
    }

    public Map<String, String> login(String email, String password) {
        if(email == null || password == null)
            throw new CustomException("email and password required", 400);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("Invalid credentials", 401));

        if(!passwordEncoder.matches(password, user.getPassword()))
            throw new CustomException("Invalid credentials", 401);

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        RefreshToken tokenEntity = new RefreshToken();
        tokenEntity.setToken(refreshToken);
        tokenEntity.setUser(user);
        tokenEntity.setExpiryDate(System.currentTimeMillis() + jwtUtil.getRefreshExpiryMillis());
        refreshTokenRepository.save(tokenEntity);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    public void logout(String refreshToken) {
        if(refreshToken == null) throw new CustomException("refreshToken required", 400);
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public RefreshTokenRepository getRefreshTokenRepository() {
        return refreshTokenRepository;
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public JWTUtil getJwtUtil() {
        return jwtUtil;
    }

    public String refreshAccessToken(String refreshToken) {
        if(refreshToken == null) throw new CustomException("refreshToken required", 400);

        RefreshToken entity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CustomException("Invalid refresh token", 401));

        if(entity.getExpiryDate() < System.currentTimeMillis()) {
            refreshTokenRepository.delete(entity);
            throw new CustomException("Refresh token expired", 401);
        }

        return jwtUtil.generateAccessToken(entity.getUser());

    }

}
