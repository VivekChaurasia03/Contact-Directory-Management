package com.cdm.forms;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ContactSearchForm {

    public String field;
    public String keyword;
}
