package com.mt.access.application.cache_profile.command;

import com.mt.access.domain.model.cache_profile.CacheProfile;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PatchCacheProfileCommand {
    private String name;
    private String description;

    public PatchCacheProfileCommand(CacheProfile profile) {
        this.name = profile.getName();
        this.description = profile.getDescription();
    }
}
