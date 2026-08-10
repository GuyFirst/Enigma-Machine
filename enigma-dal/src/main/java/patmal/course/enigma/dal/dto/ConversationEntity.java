package patmal.course.enigma.dal.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * A chat conversation: the shared "code book page". It fixes the machine
 * composition (which rotors, reflector and plugs) for its lifetime; the
 * invite code is what hands that composition to the second participant.
 *
 * Rotor start positions are deliberately NOT here - each message carries its
 * own random message key, so messages are independent and there is no shared
 * mutable state to serialize between concurrent senders.
 */
@Entity
@Table(name = "conversations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "machine_id", nullable = false)
    private MachinePersistenceEntity machine;

    @Column(name = "invite_code", unique = true, nullable = false, length = 16)
    private String inviteCode;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    // CSV of rotor ids in left-to-right order, e.g. "2,3"
    @Column(name = "rotor_ids", nullable = false)
    private String rotorIds;

    @Column(name = "reflector_id", nullable = false)
    private String reflectorId;

    // CSV of plug pairs, each stored once, e.g. "A|C,B|E" (may be empty)
    @Column(name = "plugs", nullable = false)
    private String plugs;

    // CSV of the rotor letters the machine was set to when the conversation
    // was created - the ground setting, e.g. "P,O,B"
    @Column(name = "initial_positions", nullable = false)
    private String initialPositions;

    /**
     * Where the shared machine's rotors stand right now. There is one machine
     * per conversation and it keeps turning: each message is encrypted from
     * this position and leaves the rotors further along, so the next message -
     * from either participant - carries on from there.
     */
    @Column(name = "current_positions", nullable = false)
    private String currentPositions;

    // Monotonic per-conversation message sequence; also what clients poll against
    @Column(name = "last_seq", nullable = false)
    private long lastSeq;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
