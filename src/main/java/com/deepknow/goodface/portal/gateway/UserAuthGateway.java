package com.deepknow.goodface.portal.gateway;

import com.deepknow.goodface.user.api.dto.CodeResponse;
import com.deepknow.goodface.user.api.dto.LoginByCodeRequest;
import com.deepknow.goodface.user.api.dto.LoginResponse;

public interface UserAuthGateway {
    CodeResponse sendSmsCode(String phone);
    LoginResponse loginByCode(LoginByCodeRequest request);
}
