package com.deepknow.goodface.portal.controller;

import com.deepknow.goodface.portal.controller.common.ApiResponse;
import com.deepknow.goodface.interview.api.ModelCatalogService;
import com.deepknow.goodface.interview.api.model.ModelInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/interview")
public class InterviewModelController {
    @DubboReference(url = "dubbo://198.18.0.1:20882", check = false)
    private ModelCatalogService modelCatalogService;

    @GetMapping("/models")
    public ApiResponse<List<ModelInfo>> listModels() {
        List<ModelInfo> models = modelCatalogService.listModels();
        return ApiResponse.success(models);
    }
}