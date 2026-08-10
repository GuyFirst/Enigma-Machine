package patmal.course.enigma.dal.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * One message. The server receives, stores and serves ONLY ciphertext - the
 * plaintext never reaches it. `startPositions` is the message key indicator,
 * sent in the clear so a recipient who has the conversation's machine settings
 * can set their rotors and decrypt locally.
 */
@Entity
@Table(name = "chat_messages",
        uniqueConstraints = @UniqueConstraint(columnNames = {"conversation_id", "seq_no"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationEntity conversation;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "seq_no", nullable = false)
    private long seq;

    @Column(name = "ciphertext", nullable = false, length = 600)
    private String ciphertext;

    // CSV of the rotor letters the sender started from, e.g. "F,C"
    @Column(name = "start_positions", nullable = false)
    private String startPositions;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
