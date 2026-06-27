package com.local.chatwithdocbackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @GetMapping("/patient")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<String> patientEndpoint(Principal principal) {
        return ResponseEntity.ok("Hello Patient! Authenticated as: " + principal.getName());
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> doctorEndpoint(Principal principal) {
        return ResponseEntity.ok("Hello Doctor! Authenticated as: " + principal.getName());
    }

    @GetMapping("/any")
    public ResponseEntity<String> authenticatedEndpoint(Principal principal) {
        return ResponseEntity.ok("Hello User! Authenticated as: " + principal.getName());
    }
}
