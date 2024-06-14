package com.oriol.customermagnet.service;

import com.oriol.customermagnet.domain.UserEvent;
import com.oriol.customermagnet.enumeration.EventType;

import java.util.Collection;

public interface EventService {
    Collection<UserEvent> getUserEventsByUserId(Long userId);
    void addUserEvent(Long userId, EventType eventType, String device, String ipAddress);
    void addUserEvent(String email, EventType eventType, String device, String ipAddress);
}
