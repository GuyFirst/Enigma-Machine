package patmal.course.enigma.dal.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * One chat message. Only the CIPHERTEXT is persisted - the plaintext never touches
 * the database. Decryption happens on read: rebuilding the machine from the
 * conversation's fixed code + this message's positionsAtEncryption.
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

    // CSV of rotor positions at the moment of encryption, e.g. "F,C" -
    // everything else needed to decrypt is constant on the conversation.
    @Column(name = "positions_at_encryption", nullable = false)
    private String positionsAtEncryption;

    // Human-readable code snapshot like <2,3><E(3),B(1)><II><A|C> - display only
    @Column(name = "code_compact", nullable = false)
    private String codeCompact;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
