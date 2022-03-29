package com.mt.access.resource;

import com.mt.access.application.cors_profile.representation.CorsProfileRepresentation;
import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json", path = "csrf")
public class CsrfResource {
    //required to get csrf cookie
    @GetMapping
    public ResponseEntity<SumPagedRep<CorsProfileRepresentation>> csrf() {
        return ResponseEntity.ok().build();
    }
}
