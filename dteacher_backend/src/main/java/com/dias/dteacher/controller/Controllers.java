package com.dias.dteacher.controller;

import com.dias.dteacher.validation.Notification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

final class Controllers {

    private Controllers() {}

    static ResponseEntity<Map<String, List<String>>> unprocessable(Notification n) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(toErrors(n));
    }

    static Map<String, List<String>> toErrors(Notification n) {
        return Map.of("errors", n.getErrors().stream().map(e -> e.message()).toList());
    }
}
