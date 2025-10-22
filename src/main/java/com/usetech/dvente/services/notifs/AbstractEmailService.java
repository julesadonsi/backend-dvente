package com.usetech.dvente.services.notifs;

import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;

public abstract class AbstractEmailService {

    @Value("${spring.mail.username}")
    protected String fromEmail;

    @Value("${spring.mail.from.name:DVENTE}")
    protected String fromName;

    protected void setDefaultFrom(MimeMessageHelper helper) throws Exception {
        helper.setFrom(new InternetAddress(fromEmail, fromName));
    }
}
