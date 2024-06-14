package com.oriol.customermagnet.repository.implementation;

import com.oriol.customermagnet.domain.UserEvent;
import com.oriol.customermagnet.enumeration.EventType;
import com.oriol.customermagnet.repository.EventRepository;
import com.oriol.customermagnet.rowmapper.UserEventRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

import static com.oriol.customermagnet.query.EventQuery.INSERT_EVENT_QUERY;
import static com.oriol.customermagnet.query.EventQuery.SELECT_EVENTS_BY_USER_ID_QUERY;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EventRepositoryImpl implements EventRepository {
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Collection<UserEvent> getUserEventsByUserId(Long userId) {
        return jdbc.query(SELECT_EVENTS_BY_USER_ID_QUERY, Map.of("id", userId), new UserEventRowMapper());
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {

    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
        try {
            log.info("eventType: {}", eventType);
            jdbc.update(INSERT_EVENT_QUERY, Map.of("email", email, "type", eventType.toString(), "device", device, "ipAddress", ipAddress));
        } catch (Exception e) {
            log.info("Error: {}", e.getMessage());
        }

    }
}
