package com.chaeeun.extsearchbatch.controller;

import com.chaeeun.extsearchbatch.service.RestTemplateService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.CharacterCodingException;

@Component
@RestController
@RequestMapping("/api")
public class ApiController {
    private final RestTemplateService templateService;

    public ApiController(RestTemplateService templateService) {
        this.templateService = templateService;
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    @GetMapping("/naver")
    public String naver() throws CharacterCodingException {
        return templateService.naverApiTest();
    }
}
