package patmal.course.enigma.core.service.chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import patmal.course.enigma.component.reflector.Reflector;
import patmal.course.enigma.component.rotor.Rotor;
import patmal.course.enigma.core.dto.chat.ConversationDTO;
import patmal.course.enigma.core.dto.chat.MachineSummaryDTO;
import patmal.course.enigma.core.dto.chat.MachineWiringDTO;
import patmal.course.enigma.core.dto.chat.ProfileDTO;
import patmal.course.enigma.dal.api.MachineRepository;
import patmal.course.enigma.dal.db.jpa.JpaChatProfileRepository;
import patmal.course.enigma.dal.db.jpa.JpaConversationParticipantRepository;
import patmal.course.enigma.dal.db.jpa.JpaConversationRepository;
import patmal.course.enigma.dal.db.jpa.JpaMachineRepository;
import patmal.course.enigma.dal.dto.ChatProfileEntity;
import patmal.course.enigma.dal.dto.ConversationEntity;
import patmal.course.enigma.dal.dto.ConversationParticipantEntity;
import patmal.course.enigma.dal.dto.MachinePersistenceEntity;
import patmal.course.enigma.dal.dto.ReflectorPersistenceEntity;
import patmal.course.enigma.dal.dto.RotorPersistenceEntity;
import patmal.course.enigma.engine.logic.repository.Repository;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ConversationService {
    private static final int MAX_PARTICIPANTS = 2;
    private static final String INVITE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // no 0/O/1/I
    private static final int INVITE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final JpaConversationRepository conversationRepository;
    private final JpaConversationParticipantRepository participantRepository;
    private final JpaChatProfileRepository profileRepository;
    private final JpaMachineRepository jpaMachineRepository;
    private final MachineRepository machineRepository;

    public ConversationService(JpaConversationRepository conversationRepository,
                               JpaConversationParticipantRepository participantRepository,
                               JpaChatProfileRepository profileRepository,
                               JpaMachineRepository jpaMachineRepository,
                               MachineRepository machineRepository) {
        this.conversationRepository = conversationRepository;
        this.participantRepository = participantRepository;
        this.profileRepository = profileRepository;
        this.jpaMachineRepository = jpaMachineRepository;
        this.machineRepository = machineRepository;
    }

    public List<MachineSummaryDTO> listMachines() {
        return jpaMachineRepository.findAll().stream()
                .map(m -> new MachineSummaryDTO(
                        m.getName(),
                        m.getAbc(),
                        m.getRotorsCount(),
                        m.getRotors().stream().map(RotorPersistenceEntity::getRotorId).sorted().toList(),
                        m.getReflectors().stream().map(ReflectorPersistenceEntity::getReflectorId).sorted().toList()))
                .toList();
    }

    /** Full wiring so the browser can run the machine itself. */
    public MachineWiringDTO getWiring(String machineName) {
        Repository catalog = requireCatalog(machineName);
        String alphabet = catalog.getKeyboard().toString();

        List<MachineWiringDTO.RotorWiring> rotors = catalog.getAllRotors().values().stream()
                .sorted(Comparator.comparingInt(Rotor::getId))
                .map(r -> new MachineWiringDTO.RotorWiring(
                        r.getId(),
                        List.copyOf(r.getRightColumn()),
                        List.copyOf(r.getLeftColumn()),
                        r.getNotchPosition()))
                .toList();

        List<MachineWiringDTO.ReflectorWiring> reflectors = catalog.getAllReflectors().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    List<Integer> mapping = new ArrayList<>(alphabet.length());
                    for (int i = 0; i < alphabet.length(); i++) {
                        mapping.add(entry.getValue().reflect(i));
                    }
                    return new MachineWiringDTO.ReflectorWiring(entry.getKey(), mapping);
                })
                .toList();

        return new MachineWiringDTO(catalog.getMachineName(), alphabet,
                catalog.getNumOfUsedRotorsInMachine(), rotors, reflectors);
    }

    @Transactional
    public ConversationDTO create(UUID userId, String machineName) {
        requireProfile(userId);
        MachinePersistenceEntity machineEntity = jpaMachineRepository.findByName(machineName)
                .orElseThrow(() -> new NoSuchElementException("Machine not found: " + machineName));

        // Pick this conversation's machine settings - its "code book page".
        // Start positions are per message, so they are not decided here.
        Repository catalog = machineRepository.getMachineByName(machineName);
        List<Integer> rotorIds = catalog.getRandomRotorIds();
        String reflectorId = catalog.getRandomReflectorId();
        Map<Character, Character> plugs = catalog.getRandomPlugboardPairs();

        ConversationEntity conversation = ConversationEntity.builder()
                .machine(machineEntity)
                .inviteCode(generateUniqueInviteCode())
                .createdBy(userId)
                .rotorIds(ChatCodec.joinInts(rotorIds))
                .reflectorId(reflectorId)
                .plugs(ChatCodec.joinPlugs(plugs))
                .lastSeq(0L)
                .build();
        conversation = conversationRepository.save(conversation);

        participantRepository.save(ConversationParticipantEntity.builder()
                .conversation(conversation)
                .userId(userId)
                .build());

        return toDTO(conversation);
    }

    @Transactional
    public ConversationDTO join(UUID userId, String inviteCode) {
        requireProfile(userId);
        if (inviteCode == null || inviteCode.isBlank()) {
            throw new IllegalArgumentException("Invite code is required");
        }
        ConversationEntity conversation = conversationRepository
                .findByInviteCode(inviteCode.trim().toUpperCase())
                .orElseThrow(() -> new NoSuchElementException("No conversation for this invite code"));

        if (participantRepository.existsByConversationIdAndUserId(conversation.getId(), userId)) {
            // Already a member - joining again is a no-op, just return it
            return toDTO(conversation);
        }
        if (participantRepository.countByConversationId(conversation.getId()) >= MAX_PARTICIPANTS) {
            throw new IllegalArgumentException("This conversation is already full");
        }

        participantRepository.save(ConversationParticipantEntity.builder()
                .conversation(conversation)
                .userId(userId)
                .build());
        return toDTO(conversation);
    }

    public List<ConversationDTO> listForUser(UUID userId) {
        return participantRepository.findByUserIdOrderByJoinedAtDesc(userId).stream()
                .map(ConversationParticipantEntity::getConversation)
                .sorted(Comparator.comparing(ConversationEntity::getCreatedAt).reversed())
                .map(this::toDTO)
                .toList();
    }

    public ConversationDTO getForUser(UUID userId, UUID conversationId) {
        return toDTO(requireParticipant(userId, conversationId));
    }

    /** Shared guard: returns the conversation if the user participates in it. */
    ConversationEntity requireParticipant(UUID userId, UUID conversationId) {
        ConversationEntity conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new NoSuchElementException("Conversation not found"));
        if (!participantRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw new SecurityException("You are not a participant of this conversation");
        }
        return conversation;
    }

    Repository catalogOf(ConversationEntity conversation) {
        return requireCatalog(conversation.getMachine().getName());
    }

    private Repository requireCatalog(String machineName) {
        Repository catalog = machineRepository.getMachineByName(machineName);
        if (catalog == null) {
            throw new NoSuchElementException("Machine not found: " + machineName);
        }
        return catalog;
    }

    private ConversationDTO toDTO(ConversationEntity c) {
        List<ProfileDTO> participants = participantRepository.findByConversationId(c.getId()).stream()
                .map(p -> new ProfileDTO(p.getUserId(),
                        profileRepository.findById(p.getUserId())
                                .map(ChatProfileEntity::getUsername).orElse("unknown")))
                .toList();

        // Plugs go out as a both-directions map, ready for the browser machine
        Map<String, String> plugs = new LinkedHashMap<>();
        ChatCodec.parsePlugs(c.getPlugs())
                .forEach((from, to) -> plugs.put(String.valueOf(from), String.valueOf(to)));

        return new ConversationDTO(
                c.getId(),
                c.getMachine().getName(),
                c.getMachine().getAbc(),
                c.getInviteCode(),
                c.getCreatedBy(),
                participants,
                ChatCodec.parseInts(c.getRotorIds()),
                c.getReflectorId(),
                plugs,
                c.getLastSeq(),
                c.getCreatedAt());
    }

    private void requireProfile(UUID userId) {
        if (profileRepository.findById(userId).isEmpty()) {
            throw new IllegalArgumentException("Set a username first (PUT /api/profile)");
        }
    }

    private String generateUniqueInviteCode() {
        for (int attempt = 0; attempt < 10; attempt++) {
            StringBuilder sb = new StringBuilder(INVITE_LENGTH);
            for (int i = 0; i < INVITE_LENGTH; i++) {
                sb.append(INVITE_ALPHABET.charAt(RANDOM.nextInt(INVITE_ALPHABET.length())));
            }
            String code = sb.toString();
            if (conversationRepository.findByInviteCode(code).isEmpty()) {
                return code;
            }
        }
        throw new IllegalStateException("Could not generate a unique invite code");
    }
}
