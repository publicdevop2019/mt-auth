package com.mt.access.resource;

import com.mt.access.application.cors_profile.representation.CORSProfileRepresentation;
import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = "application/json", path = "csrf")
public class CSRFResource {
    //required to get csrf cookie
    @PostMapping
    public ResponseEntity<SumPagedRep<CORSProfileRepresentation>> csrf() {
        return ResponseEntity.ok().build();
    }
}
