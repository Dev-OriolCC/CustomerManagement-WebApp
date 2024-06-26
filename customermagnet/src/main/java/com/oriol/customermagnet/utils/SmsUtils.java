package com.oriol.customermagnet.utils;

import com.oriol.customermagnet.exception.ApiException;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.concurrent.CompletableFuture;

import static com.twilio.rest.api.v2010.account.Message.creator;


public class SmsUtils {
    public static final String FROM_NUMBER = "";
    public static final String SID_KEY = "";
    public static final String TOKEN_KEY = "";

    public static void sendSMS(String to, String messageBody) {

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                Twilio.init(SID_KEY, TOKEN_KEY);
                Message message = creator(new PhoneNumber("+52"+to), new PhoneNumber(FROM_NUMBER), messageBody).create();
                System.out.println("Twilio Message: "+ message);
            } catch (Exception exception) {
                throw new ApiException("Error. Could not send email.");
            }
        });

    }
}
