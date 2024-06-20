package com.oriol.customermagnet.service;

import com.oriol.customermagnet.enumeration.VerificationType;

public interface EmailService {

    void sendVerificationEmail(String name, String email, String verificationUrl, VerificationType verificationType);
}
