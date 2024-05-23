package com.cdm.helpers;

import jakarta.persistence.Enumerated;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String content;

    // Using Builder.Default for setting default values
    @Builder.Default
    private MessageType type = MessageType.blue;
}
