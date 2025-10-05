package com.blackcode.auth_service.controller;

import com.blackcode.auth_service.dto.*;
import com.blackcode.auth_service.service.UserAuthService;
import com.blackcode.auth_service.utils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserAuthService userAuthService;

    public AuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginReq loginRequest) {
        JwtRes petugasJwtRes = userAuthService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login Success", 200, petugasJwtRes));
    }

    @PostMapping("/registration")
    public ResponseEntity<ApiResponse<?>> registration(@Valid @RequestBody RegistrationReq signUpReq) {
        MessageRes messageRes = userAuthService.registration(signUpReq);
        return ResponseEntity.ok(ApiResponse.success("Success Registration", 200, messageRes));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<?>> refreshToken(@Valid @RequestBody TokenRefreshReq request) {
        TokenRefreshRes petugasTokenRefreshRes = userAuthService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", 200, petugasTokenRefreshRes));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletRequest request) {
        String userIdFromHeader = request.getHeader("X-User-Id");
        if (userIdFromHeader == null || userIdFromHeader.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Missing X-User-Id header", 400));
        }
        MessageRes petugasMessageRes = userAuthService.signOut(request);
        return ResponseEntity.ok(ApiResponse.success("Logout successful", 200, petugasMessageRes));
    }

}
