package com.deepknow.goodface.portal.gateway.Impl;

import com.deepknow.goodface.portal.gateway.InterviewSessionGateway;
import com.deepknow.goodface.interview.api.SessionCreateService;
import com.deepknow.goodface.interview.api.request.CreateSessionRequest;
import com.deepknow.goodface.interview.api.request.EndSessionRequest;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InterviewSessionGatewayImpl implements InterviewSessionGateway {

    @DubboReference
    private SessionCreateService sessionCreateService;

    @Override
    public void createSession(String sessionId, String userId, Map<String, Object> config) {
        CreateSessionRequest req = new CreateSessionRequest();
        req.setSessionId(sessionId);
        req.setUserId(userId);
        req.setConfig(config);
        sessionCreateService.createSession(req);
    }

    @Override
    public void endSession(String sessionId) {
        EndSessionRequest req = new EndSessionRequest();
        req.setSessionId(sessionId);
        sessionCreateService.endSession(req);
    }
}