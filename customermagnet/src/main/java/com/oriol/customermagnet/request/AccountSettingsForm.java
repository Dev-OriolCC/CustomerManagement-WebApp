package com.oriol.customermagnet.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountSettingsForm {
    @NotNull(message = "enabled cannot be empty!")
    private Boolean enabled;
    @NotNull(message = "non_locked cannot be empty!")
    private Boolean non_locked;
}
