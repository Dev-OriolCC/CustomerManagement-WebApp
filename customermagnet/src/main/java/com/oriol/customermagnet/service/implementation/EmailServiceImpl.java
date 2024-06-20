package com.oriol.customermagnet.service.implementation;

import com.oriol.customermagnet.enumeration.VerificationType;
import com.oriol.customermagnet.exception.ApiException;
import com.oriol.customermagnet.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    @Override
    public void sendVerificationEmail(String name, String email, String verificationUrl, VerificationType verificationType) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(System.getenv("MAIL_USERNAME"));
            message.setTo(email);
            message.setText(getEmailMessage(name, verificationUrl, verificationType));
            message.setSubject(String.format("Customer Magnet - %s - Verification Email", verificationType.getType()));
            log.error("Sending email....");
            mailSender.send(message);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private String getEmailMessage(String name, String verificationUrl, VerificationType verificationType) {
        log.error("Get email message ....");
        switch (verificationType) {
            case PASSWORD -> { return "Hello "+name+", \n\n Reset password request. Please click the following link to reset your password. \n "+verificationUrl+"\n\n Customer Magnet Support Team. :-) \n Thanks,"; }
            case ACCOUNT -> { return "Hello "+name+", \n\n Account verification request. Please click the following link to verify your new account. \n "+verificationUrl+"\n\n Customer Magnet Support Team. :-) \n Thanks,"; }
            default -> throw new ApiException("Unable to send email. Please try again. ");
        }
    }
}
