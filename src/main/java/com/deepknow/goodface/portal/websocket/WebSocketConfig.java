package com.deepknow.goodface.portal.websocket;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.websocket.server.ServerContainer;

@Configuration
public class WebSocketConfig implements ServletContextInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        Object attr = servletContext.getAttribute("javax.websocket.server.ServerContainer");
        if (attr instanceof ServerContainer) {
            ServerContainer container = (ServerContainer) attr;
            try {
                container.addEndpoint(InterviewWsProxyEndpoint.class);
            } catch (Exception e) {
                servletContext.log("Failed to register WS proxy endpoint: " + e.getMessage());
            }
        }
    }
}