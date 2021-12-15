package com.mt.systemtest.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "test")
public class TestResource {
    @GetMapping("delay/{delay}")
    public ResponseEntity<?> delay(@PathVariable String delay) {
        try {
            Thread.sleep(Long.parseLong(delay));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok("test");
    }

    @GetMapping("status/{status}")
    public ResponseEntity<?> status(@PathVariable String status) {
        HttpStatus resolve = HttpStatus.resolve(Integer.parseInt(status));
        return ResponseEntity.status(resolve).body("\"test\":\"test\"");
    }
}
