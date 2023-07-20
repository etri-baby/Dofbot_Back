package com.arm.server.controller;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "테스트 API")
@RestController
public class TestController {
    @Operation(summary = "리액트 연결 확인용 API")
    @GetMapping(value="/api/ping")
    public String pong() {
        return "Pong!";
    }
}
