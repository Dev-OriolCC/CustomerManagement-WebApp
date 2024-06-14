package com.oriol.customermagnet.utils;

import jakarta.servlet.http.HttpServletRequest;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

public class RequestUtils {
    public static String getIpAdress(HttpServletRequest httpServletRequest) {
        String ipAddress = "Unknown IP";
        if (httpServletRequest != null) {
            ipAddress = httpServletRequest.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null || "".equals(ipAddress)) {
                ipAddress = httpServletRequest.getRemoteAddr();

            }
        }
        return ipAddress;
    }

    public static String getDevice(HttpServletRequest httpServletRequest) {
        UserAgentAnalyzer userAgentAnalyzer = UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().withCache(1000).build();
        UserAgent userAgent = userAgentAnalyzer.parse(httpServletRequest.getHeader("user-agent"));
        System.out.println(userAgent);
        return userAgent.getValue(UserAgent.AGENT_NAME) + " - "+ userAgent.getValue(UserAgent.DEVICE_NAME);
    }

}
