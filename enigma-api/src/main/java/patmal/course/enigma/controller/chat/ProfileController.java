package patmal.course.enigma.controller.chat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import patmal.course.enigma.core.dto.chat.ProfileDTO;
import patmal.course.enigma.core.dto.chat.UpsertProfileRequest;
import patmal.course.enigma.core.service.chat.ProfileService;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ProfileDTO get() {
        return profileService.get(CurrentUser.id());
    }

    @PutMapping
    public ProfileDTO upsert(@RequestBody UpsertProfileRequest request) {
        return profileService.upsert(CurrentUser.id(), request.username());
    }
}
