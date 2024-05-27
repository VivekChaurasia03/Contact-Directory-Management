package com.cdm.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SecondaryRow;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ContactForm {

    @NotBlank(message = "Contact Name is required")
    @Size(min = 3, message = "Min 3 characters required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email Address")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{1,3}[-.\\s]?(?:\\([0-9]{1,6}\\)[-\\s]?)?[0-9]{3,14}(?:[-.\\s]?[0-9]{3,14}){1,2}$",
            message = "Invalid phone number")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    private MultipartFile profileImage;

    @NotBlank(message = "Description is required")
    private String description;

    private boolean favourite = false;

    private String websiteLink;

    private String linkedInLink;
}
