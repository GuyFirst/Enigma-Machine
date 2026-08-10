package patmal.course.enigma.dal.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * A chat conversation between users sharing one Enigma machine and one rotor state.
 * The machine "code" (rotors/reflector/plugs/original positions) is fixed at creation;
 * currentPositions advances as messages are sent - exactly like a physical machine
 * whose rotors keep turning between messages.
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

    // CSV of starting positions, e.g. "E,B" - immutable after creation (used for display)
    @Column(name = "original_positions", nullable = false)
    private String originalPositions;

    // CSV of the shared current rotor positions - advances with every encrypted char
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
