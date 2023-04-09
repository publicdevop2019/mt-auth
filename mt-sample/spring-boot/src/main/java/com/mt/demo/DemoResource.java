package com.mt.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json")
public class DemoResource {

    @GetMapping(path = "demo")
    public ResponseEntity<String> demo() {
        return ResponseEntity.ok().body("{\"msg\":\"success\"}");
    }

}
