package com.back.ourlog;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void notFound() {
        // 아무것도 반환하지 않고 404 응답
    }
}

