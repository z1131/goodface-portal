package com.deepknow.goodface.portal.gateway;

import java.util.Map;

public interface InterviewSessionGateway {
    void createSession(String sessionId, String userId, Map<String, Object> config);
    void endSession(String sessionId);
}