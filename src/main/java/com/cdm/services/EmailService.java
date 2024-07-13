package com.cdm.services;

public interface EmailService {

    // Send the verification link
    void sendEmail(String to, String subject, String body);

    // Some other methods
    void sendEmailWithHtml();

    void sendEmailWithAttachment();
}
