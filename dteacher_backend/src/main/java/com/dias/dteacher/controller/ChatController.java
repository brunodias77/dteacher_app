package com.dias.dteacher.controller;

import com.dias.dteacher.usecase.chat.send.SendChatMessageRequest;
import com.dias.dteacher.usecase.chat.send.SendChatMessageUseCase;
import com.dias.dteacher.usecase.chat.start.StartChatSessionRequest;
import com.dias.dteacher.usecase.chat.start.StartChatSessionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final StartChatSessionUseCase startChatSessionUseCase;
    private final SendChatMessageUseCase  sendChatMessageUseCase;

    @PostMapping("/sessions")
    public ResponseEntity<?> startSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody StartSessionBody body) {
        var request = new StartChatSessionRequest(
                userDetails.getUsername(),
                body.mode(),
                body.cefrLevel()
        );
        return startChatSessionUseCase.execute(request).fold(
                Controllers::unprocessable,
                resp -> ResponseEntity.status(HttpStatus.CREATED).body(resp)
        );
    }

    @PostMapping("/sessions/{id}/messages")
    public ResponseEntity<?> sendMessage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id,
            @RequestBody SendMessageBody body) {
        var request = new SendChatMessageRequest(
                userDetails.getUsername(),
                id,
                body.content()
        );
        return sendChatMessageUseCase.execute(request).fold(
                Controllers::unprocessable,
                resp -> ResponseEntity.status(HttpStatus.CREATED).body(resp)
        );
    }

    record StartSessionBody(String mode, String cefrLevel) {}

    record SendMessageBody(String content) {}
}
