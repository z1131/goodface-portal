package com.deepknow.goodface.portal.controller;

import com.deepknow.goodface.portal.controller.common.ApiResponse;
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
    public ApiResponse<CodeResponse> sendSmsCode(@RequestBody SendCodeRequest request) {
        CodeResponse response = userAuthGateway.sendSmsCode(request.getPhone());
        return ApiResponse.success(response);
    }

    @PostMapping("/loginByCode")
    public ApiResponse<LoginResponse> loginByCode(@RequestBody LoginByCodeRequest request) {
        LoginResponse response = userAuthGateway.loginByCode(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/guest/login")
    public ApiResponse<LoginResponse> guestLogin() {
        // 生成访客登录信息
        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        response.setToken("guest-" + java.util.UUID.randomUUID().toString());
        response.setUsername("访客用户");
        response.setEmail("guest@example.com");
        response.setMembership("访客");
        response.setBalance("0.00");
        return ApiResponse.success(response);
    }
}