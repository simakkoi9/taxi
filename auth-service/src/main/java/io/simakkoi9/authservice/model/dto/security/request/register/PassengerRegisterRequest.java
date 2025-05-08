package io.simakkoi9.authservice.model.dto.security.request.register;

import static io.simakkoi9.authservice.util.RegularExpressionConstants.NAME_REGEX;
import static io.simakkoi9.authservice.util.RegularExpressionConstants.PASSWORD_REGEX;
import static io.simakkoi9.authservice.util.RegularExpressionConstants.PHONE_REGEX;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PassengerRegisterRequest {
    @NotBlank(message = "{name.not.blank}")
    @Pattern(regexp = NAME_REGEX, message = "{name.not.valid}")
    private String name;

    @NotBlank(message = "{email.not.blank}")
    @Email(message = "{email.not.valid}")
    @Size(max = 320, message = "{email.max.size}")
    private String email;

    @NotBlank(message = "{phone.not.blank}")
    @Pattern(regexp = PHONE_REGEX, message = "{phone.not.valid}")
    private String phone;

    @NotBlank(message = "{pass.not.blank}")
    @Pattern(regexp = PASSWORD_REGEX, message = "{pass.not.valid}")
    private String password;
} 