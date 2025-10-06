package com.deepknow.goodface.portal.gateway.Impl;

import com.deepknow.goodface.portal.gateway.UserAuthGateway;
import com.deepknow.goodface.user.api.UserAuthService;
import com.deepknow.goodface.user.api.dto.CodeResponse;
import com.deepknow.goodface.user.api.dto.LoginByCodeRequest;
import com.deepknow.goodface.user.api.dto.LoginResponse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

@Component
public class UserAuthGatewayImpl implements UserAuthGateway {

    @DubboReference
    private UserAuthService userAuthService;

    @Override
    public CodeResponse sendSmsCode(String phone) {
        return userAuthService.sendSmsCode(phone);
    }

    @Override
    public LoginResponse loginByCode(LoginByCodeRequest request) {
        return userAuthService.loginByCode(request);
    }
}