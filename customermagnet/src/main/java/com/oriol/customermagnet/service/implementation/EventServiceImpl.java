package com.oriol.customermagnet.service.implementation;

import com.oriol.customermagnet.domain.UserEvent;
import com.oriol.customermagnet.enumeration.EventType;
import com.oriol.customermagnet.repository.EventRepository;
import com.oriol.customermagnet.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public Collection<UserEvent> getUserEventsByUserId(Long userId) {
        return this.eventRepository.getUserEventsByUserId(userId);
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {

    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
        this.eventRepository.addUserEvent(email, eventType, device, ipAddress);
    }
}
