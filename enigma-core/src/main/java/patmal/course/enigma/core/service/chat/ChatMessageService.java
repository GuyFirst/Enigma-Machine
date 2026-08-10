package patmal.course.enigma.core.service.chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import patmal.course.enigma.core.dto.chat.ChatMessageDTO;
import patmal.course.enigma.core.dto.chat.MessagesResponseDTO;
import patmal.course.enigma.core.dto.chat.SendMessageRequest;
import patmal.course.enigma.dal.db.jpa.JpaChatMessageRepository;
import patmal.course.enigma.dal.db.jpa.JpaConversationRepository;
import patmal.course.enigma.dal.dto.ChatMessageEntity;
import patmal.course.enigma.dal.dto.ConversationEntity;
import patmal.course.enigma.engine.logic.repository.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Message transport for a conversation's single, continuously running machine.
 *
 * Encryption and decryption happen in the browser, so this service never sees
 * plaintext. What it owns is the shared rotor state: every accepted message
 * moves the machine forward, and the next message - from either participant -
 * starts where the last one stopped.
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
    public ChatMessageDTO send(UUID userId, UUID conversationId, SendMessageRequest request) {
        conversationService.requireParticipant(userId, conversationId);
        // Lock the row so two simultaneous sends cannot both advance the machine
        ConversationEntity conversation = conversationRepository.lockById(conversationId)
                .orElseThrow(() -> new NoSuchElementException("Conversation not found"));

        validate(conversation, request);

        long seq = conversation.getLastSeq() + 1;
        ChatMessageEntity message = ChatMessageEntity.builder()
                .conversation(conversation)
                .senderId(userId)
                .seq(seq)
                .ciphertext(request.ciphertext())
                .startPositions(ChatCodec.joinChars(request.startPositions()))
                .build();
        messageRepository.save(message);

        // The machine keeps its new position for whoever sends next
        conversation.setCurrentPositions(ChatCodec.joinChars(request.endPositions()));
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
        return new MessagesResponseDTO(messages, conversation.getLastSeq(),
                ChatCodec.parseChars(conversation.getCurrentPositions()));
    }

    private void validate(ConversationEntity conversation, SendMessageRequest request) {
        String ciphertext = request.ciphertext();
        if (ciphertext == null || ciphertext.isBlank()) {
            throw new IllegalArgumentException("Ciphertext is required");
        }
        if (ciphertext.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("Message too long (max " + MAX_MESSAGE_LENGTH + " chars)");
        }

        // Guard only against the real race: the other participant transmitting
        // between this sender encrypting and sending, which would leave the
        // machine somewhere their ciphertext no longer accounts for.
        //
        // The start position itself is deliberately NOT required to match the
        // stored one: an operator may turn the dials before typing, and on a
        // shared machine that simply means the machine is now there. The
        // message records where it started, so it stays decryptable either way.
        if (request.expectedSeq() != null && request.expectedSeq() != conversation.getLastSeq()) {
            throw new StaleMachineStateException(
                    "The machine has moved on - someone else transmitted first. Encrypt again.");
        }

        Repository catalog = conversationService.catalogOf(conversation);
        int expectedRotors = ChatCodec.parseInts(conversation.getRotorIds()).size();
        requirePositions(request.startPositions(), expectedRotors, catalog);
        requirePositions(request.endPositions(), expectedRotors, catalog);
    }

    private void requirePositions(List<Character> positions, int expectedRotors, Repository catalog) {
        if (positions == null || positions.size() != expectedRotors) {
            throw new IllegalArgumentException(
                    "Expected " + expectedRotors + " rotor positions, got "
                            + (positions == null ? 0 : positions.size()));
        }
        for (Character position : positions) {
            if (position == null || !catalog.getKeyboard().isValidChar(position)) {
                throw new IllegalArgumentException(
                        "Rotor position not in this machine's alphabet: " + position);
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
