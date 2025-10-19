package com.deepknow.goodface.portal.controller;

import com.deepknow.goodface.portal.controller.common.ApiResponse;
import com.deepknow.goodface.portal.controller.request.CreateInterviewSessionRequest;
import com.deepknow.goodface.portal.gateway.InterviewSessionGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/interview")
public class InterviewSessionController {

    @Autowired
    private InterviewSessionGateway interviewSessionGateway;

    @PostMapping(value = "/session", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Map<String, Object>> createSession(@RequestBody(required = false) CreateInterviewSessionRequest body, HttpServletRequest request) {
        String sessionId = "sess_" + System.currentTimeMillis();
        String userId = body != null ? body.getUserId() : null;
        Map<String, Object> config = body != null ? body.getConfig() : null;

        try {
            // 调用 interview 应用（经由 Dubbo）创建会话
            interviewSessionGateway.createSession(sessionId, userId, config);
        } catch (Exception e) {
            // 若后端暂未发布 Dubbo 服务或调用失败，返回错误码与信息
            return ApiResponse.error(500, "createSession failed: " + e.getMessage());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("sessionId", sessionId);
        // 统一返回相对路径，交由前端所在域的 Nginx /ws 反向代理到门户
        data.put("wsUrl", "/ws/interview/audio/stream?sessionId=" + sessionId);
        return ApiResponse.success(data);
    }
}