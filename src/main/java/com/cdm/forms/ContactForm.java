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

    @NotBlank(message = "Contact Email is required")
    @Email(message = "Invalid Email Address")
    private String email;

    @NotBlank(message = "Contact Phone Number is required")
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$",
            message = "Invalid phone number")
    private String phoneNumber;

    @NotBlank(message = "Contact Address is required")
    private String address;

    // Creating a custom annotation to validate the files - size, resolutions
    private MultipartFile contactImage;

    @NotBlank(message = "Contact Description is required")
    private String description;

    private boolean favourite = false;

    private String websiteLink;

    private String linkedInLink;
}
