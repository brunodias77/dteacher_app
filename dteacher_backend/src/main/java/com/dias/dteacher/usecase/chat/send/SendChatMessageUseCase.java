package com.dias.dteacher.usecase.chat.send;

import com.dias.dteacher.contract.UseCase;
import com.dias.dteacher.error.ChatError;
import com.dias.dteacher.exception.NotFoundException;
import com.dias.dteacher.model.ChatMessage;
import com.dias.dteacher.model.ChatSession;
import com.dias.dteacher.repository.ChatMessageRepository;
import com.dias.dteacher.repository.ChatSessionRepository;
import com.dias.dteacher.service.OllamaService;
import com.dias.dteacher.validation.Notification;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SendChatMessageUseCase implements UseCase<SendChatMessageRequest, SendChatMessageResponse> {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final OllamaService         ollamaService;

    @Override
    @Transactional
    public Either<Notification, SendChatMessageResponse> execute(SendChatMessageRequest request) {
        Notification notification = Notification.create();

        if (request.content() == null || request.content().isBlank())
            notification.append(ChatError.CONTENT_REQUIRED);

        if (notification.hasError()) return Either.left(notification);

        ChatSession session = chatSessionRepository
                .findByIdAndUserEmail(request.sessionId(), request.email())
                .orElseThrow(() -> NotFoundException.of("ChatSession", request.sessionId()));

        ChatMessage userMessage = ChatMessage.builder()
                .session(session)
                .sender("USER")
                .content(request.content().strip())
                .build();
        chatMessageRepository.save(userMessage);

        List<OllamaService.ChatTurn> history = chatMessageRepository
                .findBySessionIdOrderByCreatedAtAsc(session.getId())
                .stream()
                .filter(m -> !m.getId().equals(userMessage.getId()))
                .map(m -> new OllamaService.ChatTurn(m.getSender(), m.getContent()))
                .toList();

        OllamaService.ChatResponse aiResponse = ollamaService.chat(
                session.getCefrLevel(), history, request.content().strip()
        );

        ChatMessage aiMessage = ChatMessage.builder()
                .session(session)
                .sender("AI")
                .content(aiResponse.text())
                .translation(aiResponse.translation())
                .feedback(aiResponse.feedback())
                .build();
        chatMessageRepository.save(aiMessage);

        session.setLastMessageAt(OffsetDateTime.now());
        chatSessionRepository.save(session);

        return Either.right(new SendChatMessageResponse(
                new SendChatMessageResponse.MessageDto(
                        aiMessage.getId(),
                        aiMessage.getSender(),
                        aiMessage.getContent(),
                        aiMessage.getTranslation(),
                        aiMessage.getFeedback(),
                        aiMessage.getCreatedAt()
                )
        ));
    }
}
