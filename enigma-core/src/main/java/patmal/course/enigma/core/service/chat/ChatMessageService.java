package patmal.course.enigma.core.service.chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import patmal.course.enigma.core.dto.chat.ChatMessageDTO;
import patmal.course.enigma.core.dto.chat.MessagesResponseDTO;
import patmal.course.enigma.core.formatter.EnigmaFormatter;
import patmal.course.enigma.dal.db.jpa.JpaChatMessageRepository;
import patmal.course.enigma.dal.db.jpa.JpaConversationRepository;
import patmal.course.enigma.dal.dto.ChatMessageEntity;
import patmal.course.enigma.dal.dto.ConversationEntity;
import patmal.course.enigma.engine.logic.repository.Repository;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ChatMessageService {
    private static final int MAX_MESSAGE_LENGTH = 500;

    private final JpaConversationRepository conversationRepository;
    private final JpaChatMessageRepository messageRepository;
    private final ConversationService conversationService;
    private final ChatCryptoService cryptoService;
    private final ProfileService profileService;
    private final EnigmaFormatter formatter;

    public ChatMessageService(JpaConversationRepository conversationRepository,
                              JpaChatMessageRepository messageRepository,
                              ConversationService conversationService,
                              ChatCryptoService cryptoService,
                              ProfileService profileService,
                              EnigmaFormatter formatter) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.conversationService = conversationService;
        this.cryptoService = cryptoService;
        this.profileService = profileService;
        this.formatter = formatter;
    }

    @Transactional
    public ChatMessageDTO send(UUID userId, UUID conversationId, String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Message text is required");
        }
        if (text.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("Message too long (max " + MAX_MESSAGE_LENGTH + " chars)");
        }
        // Participant check first (readable error), then take the row lock so two
        // concurrent sends advance the shared rotor state strictly one at a time.
        conversationService.requireParticipant(userId, conversationId);
        ConversationEntity conversation = conversationRepository.lockById(conversationId)
                .orElseThrow(() -> new NoSuchElementException("Conversation not found"));

        Repository catalog = conversationService.catalogOf(conversation);
        List<Integer> rotorIds = ChatCodec.parseInts(conversation.getRotorIds());
        List<Character> positionsBefore = ChatCodec.parseChars(conversation.getCurrentPositions());
        Map<Character, Character> plugs = ChatCodec.parsePlugs(conversation.getPlugs());

        ChatCryptoService.Result encrypted = cryptoService.process(
                catalog, rotorIds, positionsBefore, conversation.getReflectorId(), plugs, text);

        long seq = conversation.getLastSeq() + 1;
        ChatMessageEntity message = ChatMessageEntity.builder()
                .conversation(conversation)
                .senderId(userId)
                .seq(seq)
                .ciphertext(encrypted.text())
                .positionsAtEncryption(ChatCodec.joinChars(positionsBefore))
                .codeCompact(formatter.formatCode(
                        rotorIds, positionsBefore, conversation.getReflectorId(), plugs, catalog))
                .build();
        messageRepository.save(message);

        conversation.setCurrentPositions(ChatCodec.joinChars(encrypted.newPositions()));
        conversation.setLastSeq(seq);
        conversationRepository.save(conversation);

        return toDTO(message, conversation, catalog);
    }

    @Transactional(readOnly = true)
    public MessagesResponseDTO getMessages(UUID userId, UUID conversationId, long afterSeq) {
        ConversationEntity conversation = conversationService.requireParticipant(userId, conversationId);
        Repository catalog = conversationService.catalogOf(conversation);

        List<ChatMessageDTO> messages = messageRepository
                .findByConversationIdAndSeqGreaterThanOrderBySeqAsc(conversationId, afterSeq)
                .stream()
                .map(m -> toDTO(m, conversation, catalog))
                .toList();

        List<Integer> rotorIds = ChatCodec.parseInts(conversation.getRotorIds());
        List<Character> currentPositions = ChatCodec.parseChars(conversation.getCurrentPositions());
        return new MessagesResponseDTO(
                messages,
                conversation.getLastSeq(),
                currentPositions,
                formatter.formatPositions(rotorIds, currentPositions, catalog));
    }

    private ChatMessageDTO toDTO(ChatMessageEntity m, ConversationEntity conversation, Repository catalog) {
        // Decrypt on read: rebuild the machine at the positions the message was
        // encrypted at and run the ciphertext through (Enigma is reciprocal).
        List<Integer> rotorIds = ChatCodec.parseInts(conversation.getRotorIds());
        List<Character> positionsAtEncryption = ChatCodec.parseChars(m.getPositionsAtEncryption());
        Map<Character, Character> plugs = ChatCodec.parsePlugs(conversation.getPlugs());

        String plaintext = cryptoService.process(
                catalog, rotorIds, positionsAtEncryption,
                conversation.getReflectorId(), plugs, m.getCiphertext()).text();

        return new ChatMessageDTO(
                m.getId(),
                m.getSeq(),
                m.getSenderId(),
                profileService.usernameOrFallback(m.getSenderId()),
                m.getCiphertext(),
                plaintext,
                positionsAtEncryption,
                m.getCodeCompact(),
                m.getCreatedAt());
    }
}
