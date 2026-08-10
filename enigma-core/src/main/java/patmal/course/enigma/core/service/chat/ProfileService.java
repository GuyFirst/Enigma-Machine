package patmal.course.enigma.core.service.chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import patmal.course.enigma.core.dto.chat.ProfileDTO;
import patmal.course.enigma.dal.db.jpa.JpaChatProfileRepository;
import patmal.course.enigma.dal.dto.ChatProfileEntity;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class ProfileService {
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,20}$");

    private final JpaChatProfileRepository profileRepository;

    public ProfileService(JpaChatProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public ProfileDTO get(UUID userId) {
        ChatProfileEntity profile = profileRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("No profile yet for this user"));
        return new ProfileDTO(profile.getId(), profile.getUsername());
    }

    @Transactional
    public ProfileDTO upsert(UUID userId, String username) {
        if (username == null || !USERNAME_PATTERN.matcher(username.trim()).matches()) {
            throw new IllegalArgumentException(
                    "Username must be 3-20 characters: letters, digits or underscore");
        }
        String trimmed = username.trim();

        // Reject a username already taken by a DIFFERENT user
        profileRepository.findByUsernameIgnoreCase(trimmed).ifPresent(existing -> {
            if (!existing.getId().equals(userId)) {
                throw new IllegalArgumentException("Username already taken: " + trimmed);
            }
        });

        ChatProfileEntity profile = profileRepository.findById(userId)
                .orElseGet(() -> ChatProfileEntity.builder().id(userId).build());
        profile.setUsername(trimmed);
        profileRepository.save(profile);
        return new ProfileDTO(userId, trimmed);
    }

    String usernameOrFallback(UUID userId) {
        return profileRepository.findById(userId)
                .map(ChatProfileEntity::getUsername)
                .orElse("unknown");
    }
}
