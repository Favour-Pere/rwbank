package com.robert.rwbank.services;

import com.robert.rwbank.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
    void sendEmailWithAttachement(EmailDetails emailDetails);
}
