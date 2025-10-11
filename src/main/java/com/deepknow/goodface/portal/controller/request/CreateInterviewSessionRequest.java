package com.deepknow.goodface.portal.controller.request;

import java.io.Serializable;
import java.util.Map;

public class CreateInterviewSessionRequest implements Serializable {
    private String userId;
    private Map<String, Object> config;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
}