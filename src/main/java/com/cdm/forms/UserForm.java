package com.cdm.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserForm {

    @NotBlank(message = "Username is required")
    @Size(min = 3, message = "Min 3 characters required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email Address")
    private String email;

    @Size(min = 8, message = "Min 8 characters required")
    /*
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
     */
    private String password;

    @NotBlank(message = "About is required")
    private String about;

    @Pattern(regexp = "^\\+?[0-9]{1,3}[-.\\s]?(?:\\([0-9]{1,6}\\)[-\\s]?)?[0-9]{3,14}(?:[-.\\s]?[0-9]{3,14}){1,2}$",
            message = "Invalid phone number")
    private String phoneNumber;
}

