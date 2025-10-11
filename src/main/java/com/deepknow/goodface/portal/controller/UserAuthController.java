package com.deepknow.goodface.portal.controller;

import com.deepknow.goodface.portal.controller.request.SendCodeRequest;
import com.deepknow.goodface.portal.gateway.UserAuthGateway;
import com.deepknow.goodface.user.api.dto.CodeResponse;
import com.deepknow.goodface.user.api.dto.LoginByCodeRequest;
import com.deepknow.goodface.user.api.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserAuthController {

    @Autowired
    private UserAuthGateway userAuthGateway;

    @PostMapping("/sms/code")
    public CodeResponse sendSmsCode(@RequestBody SendCodeRequest request) {
        return userAuthGateway.sendSmsCode(request.getPhone());
    }

    @PostMapping("/loginByCode")
    public LoginResponse loginByCode(@RequestBody LoginByCodeRequest request) {
        return userAuthGateway.loginByCode(request);
    }

    @PostMapping("/guest/login")
    public LoginResponse guestLogin() {
        // 生成访客登录信息
        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        // 访客不下发 token，避免前端误判为已登录
        response.setToken(null);
        response.setUsername("访客用户");
        response.setEmail("guest@example.com");
        response.setMembership("访客");
        response.setBalance("0.00");
        response.setGuest(true);
        return response;
    }


}