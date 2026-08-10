package patmal.course.enigma.core.service.chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import patmal.course.enigma.core.dto.chat.ChatMessageDTO;
import patmal.course.enigma.core.dto.chat.MessagesResponseDTO;
import patmal.course.enigma.dal.db.jpa.JpaChatMessageRepository;
import patmal.course.enigma.dal.db.jpa.JpaConversationRepository;
import patmal.course.enigma.dal.dto.ChatMessageEntity;
import patmal.course.enigma.dal.dto.ConversationEntity;
import patmal.course.enigma.engine.logic.repository.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Message transport. Encryption and decryption happen in the browser, so this
 * service only validates and stores ciphertext - it never sees plaintext and
 * has no way to recover it.
 */
@Service
public class ChatMessageService {
    private static final int MAX_MESSAGE_LENGTH = 500;

    private final JpaConversationRepository conversationRepository;
    private final JpaChatMessageRepository messageRepository;
    private final ConversationService conversationService;
    private final ProfileService profileService;

    public ChatMessageService(JpaConversationRepository conversationRepository,
                              JpaChatMessageRepository messageRepository,
                              ConversationService conversationService,
                              ProfileService profileService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.conversationService = conversationService;
        this.profileService = profileService;
    }

    @Transactional
    public ChatMessageDTO send(UUID userId, UUID conversationId, String ciphertext,
                               List<Character> startPositions) {
        ConversationEntity conversation = conversationService.requireParticipant(userId, conversationId);
        validate(conversation, ciphertext, startPositions);

        long seq = conversation.getLastSeq() + 1;
        ChatMessageEntity message = ChatMessageEntity.builder()
                .conversation(conversation)
                .senderId(userId)
                .seq(seq)
                .ciphertext(ciphertext)
                .startPositions(ChatCodec.joinChars(startPositions))
                .build();
        messageRepository.save(message);

        conversation.setLastSeq(seq);
        conversationRepository.save(conversation);

        return toDTO(message);
    }

    @Transactional(readOnly = true)
    public MessagesResponseDTO getMessages(UUID userId, UUID conversationId, long afterSeq) {
        ConversationEntity conversation = conversationService.requireParticipant(userId, conversationId);
        List<ChatMessageDTO> messages = messageRepository
                .findByConversationIdAndSeqGreaterThanOrderBySeqAsc(conversationId, afterSeq)
                .stream()
                .map(this::toDTO)
                .toList();
        return new MessagesResponseDTO(messages, conversation.getLastSeq());
    }

    /**
     * Sanity-checks the client's submission against the conversation's machine:
     * right number of start positions, all letters in the alphabet, and a
     * plausible message length.
     */
    private void validate(ConversationEntity conversation, String ciphertext,
                          List<Character> startPositions) {
        if (ciphertext == null || ciphertext.isBlank()) {
            throw new IllegalArgumentException("Ciphertext is required");
        }
        if (ciphertext.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("Message too long (max " + MAX_MESSAGE_LENGTH + " chars)");
        }

        int expectedRotors = ChatCodec.parseInts(conversation.getRotorIds()).size();
        if (startPositions == null || startPositions.size() != expectedRotors) {
            throw new IllegalArgumentException(
                    "Expected " + expectedRotors + " start positions, got "
                            + (startPositions == null ? 0 : startPositions.size()));
        }

        Repository catalog = conversationService.catalogOf(conversation);
        for (Character position : startPositions) {
            if (position == null || !catalog.getKeyboard().isValidChar(position)) {
                throw new IllegalArgumentException("Start position not in this machine's alphabet: " + position);
            }
        }
    }

    private ChatMessageDTO toDTO(ChatMessageEntity m) {
        return new ChatMessageDTO(
                m.getId(),
                m.getSeq(),
                m.getSenderId(),
                profileService.usernameOrFallback(m.getSenderId()),
                m.getCiphertext(),
                ChatCodec.parseChars(m.getStartPositions()),
                m.getCreatedAt());
    }
}
