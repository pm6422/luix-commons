package com.luixtech.uidgenerator.usagedemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Slf4j
public class HomeController {

    @Resource
    private Environment               env;
    @Autowired(required = false)
    private SpringDocConfigProperties springDocConfigProperties;

    /**
     * Home page.
     */
    @GetMapping("/")
    public ResponseEntity<String> home(HttpServletResponse response) throws IOException {
        if (springDocConfigProperties != null && springDocConfigProperties.getApiDocs().isEnabled()) {
            response.sendRedirect("swagger-ui/index.html");
        }
        return ResponseEntity.ok(env.getProperty("spring.application.name") + " Home Page");
    }
}
