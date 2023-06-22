package com.mt.access.application.cors_profile.command;

import com.mt.access.domain.model.cors_profile.CorsProfile;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CorsProfilePatchCommand {
    private String name;
    private String description;

    public CorsProfilePatchCommand(CorsProfile corsProfile) {
        this.name = corsProfile.getName();
        this.description = corsProfile.getDescription();
    }
}
