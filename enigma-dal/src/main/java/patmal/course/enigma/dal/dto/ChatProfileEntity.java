package patmal.course.enigma.dal.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatProfileEntity {
    // The id is NOT generated - it is the auth provider's (Supabase) user UUID,
    // so profile rows are 1:1 with authenticated users.
    @Id
    private UUID id;

    @Column(unique = true, nullable = false, length = 20)
    private String username;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
