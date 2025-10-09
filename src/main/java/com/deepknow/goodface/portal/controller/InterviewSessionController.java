package com.deepknow.goodface.portal.controller;

import com.deepknow.goodface.portal.controller.common.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/interview")
public class InterviewSessionController {

    @PostMapping(value = "/session", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Map<String, Object>> createSession(@RequestBody(required = false) Map<String, Object> body) {
        // M1 桩：直接生成sessionId并返回面试服务的WS地址
        String sessionId = "sess_" + System.currentTimeMillis();
        Map<String, Object> data = new HashMap<>();
        data.put("sessionId", sessionId);
        // 改为通过 gateway (portal) 代理到 interview 的 WebSocket
        data.put("wsUrl", "ws://127.0.0.1:8001/ws/interview/audio/stream?sessionId=" + sessionId);
        return ApiResponse.success(data);
    }
}