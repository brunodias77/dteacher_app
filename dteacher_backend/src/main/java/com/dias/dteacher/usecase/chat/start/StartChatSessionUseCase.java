package com.dias.dteacher.usecase.chat.start;

import com.dias.dteacher.contract.UseCase;
import com.dias.dteacher.error.ChatError;
import com.dias.dteacher.exception.NotFoundException;
import com.dias.dteacher.model.ChatMessage;
import com.dias.dteacher.model.ChatSession;
import com.dias.dteacher.model.User;
import com.dias.dteacher.repository.ChatMessageRepository;
import com.dias.dteacher.repository.ChatSessionRepository;
import com.dias.dteacher.repository.UserRepository;
import com.dias.dteacher.validation.Notification;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StartChatSessionUseCase implements UseCase<StartChatSessionRequest, StartChatSessionResponse> {

    private static final String WELCOME_CONTENT     = "Hello! Ready to practice some English today?";
    private static final String WELCOME_TRANSLATION = "Olá! Pronto para praticar um pouco de inglês hoje?";
    private static final Set<String> VALID_CEFR     = Set.of("A1", "A2", "B1", "B2", "C1", "C2");

    private final UserRepository        userRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    @Transactional
    public Either<Notification, StartChatSessionResponse> execute(StartChatSessionRequest request) {
        Notification notification = Notification.create();
        validate(request, notification);
        if (notification.hasError()) return Either.left(notification);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> NotFoundException.of("User", request.email()));

        ChatSession session = ChatSession.builder()
                .user(user)
                .mode("chat")
                .cefrLevel(request.cefrLevel().toUpperCase())
                .build();
        chatSessionRepository.save(session);

        ChatMessage welcome = ChatMessage.builder()
                .session(session)
                .sender("AI")
                .content(WELCOME_CONTENT)
                .translation(WELCOME_TRANSLATION)
                .build();
        chatMessageRepository.save(welcome);

        return Either.right(new StartChatSessionResponse(
                session.getId(),
                List.of(new StartChatSessionResponse.MessageDto(
                        welcome.getId(),
                        welcome.getSender(),
                        welcome.getContent(),
                        welcome.getTranslation(),
                        welcome.getCreatedAt()
                ))
        ));
    }

    private void validate(StartChatSessionRequest req, Notification n) {
        if (req.cefrLevel() == null || req.cefrLevel().isBlank())
            n.append(ChatError.CEFR_LEVEL_REQUIRED);
        else if (!VALID_CEFR.contains(req.cefrLevel().toUpperCase()))
            n.append(ChatError.CEFR_LEVEL_INVALID);
    }
}
