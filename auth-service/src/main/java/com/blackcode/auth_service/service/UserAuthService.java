package com.blackcode.auth_service.service;

import com.blackcode.auth_service.dto.*;
import jakarta.servlet.http.HttpServletRequest;

public interface UserAuthService {

    JwtRes login(LoginReq loginRequest);

    MessageRes registration(RegistrationReq signUpReq);

    TokenRefreshRes refreshToken(TokenRefreshReq request);

    MessageRes signOut(HttpServletRequest request);

}
