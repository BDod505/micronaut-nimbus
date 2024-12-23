package com.micronaut.nimbus.controller;

import com.micronaut.nimbus.models.transformer.AnalysisResult;
import com.micronaut.nimbus.models.transformer.UserExample;
import com.micronaut.nimbus.service.JsonTransformerService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.serde.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Controller("/transformer")
public class JsonTransformerController {
    private static final Logger log = LoggerFactory.getLogger(JsonTransformerController.class);
    private final JsonTransformerService jsonTransformerService;
    private final ObjectMapper mapper;

    public JsonTransformerController(JsonTransformerService jsonTransformerService, ObjectMapper mapper) {
        this.jsonTransformerService = jsonTransformerService;
        this.mapper = mapper;
    }

    @Post("/transform")
    public HttpResponse<String> transform(@Body UserExample input) {
        try {
            Object result = jsonTransformerService.jsonTransformerTransform(input);
            return HttpResponse.ok(result.toString());
        } catch (IllegalAccessException e) {
            log.error("error has occured : {}", e.getMessage());
            return HttpResponse.serverError("Internal Server Error");
        }
    }

    @Post("/benchmark")
    public HttpResponse<String> benchmark(@Body UserExample input) {
        try {
            log.info("Starting the benchmark");
            AnalysisResult analysisResult = jsonTransformerService.performAnalysis(input);
            return HttpResponse.ok(mapper.writeValueAsString(analysisResult));
        } catch (IOException e) {
            log.error("Object Mapper Loading failed");
            return HttpResponse.serverError("Internal Server Error");
        }
    }
}
