package com.oriol.customermagnet.query;

public class EventQuery {
    public static final String SELECT_EVENTS_BY_USER_ID_QUERY = "SELECT uev.id, e.type, e.description, uev.device, uev.ip_address, uev.created_at FROM Events e JOIN UserEvents uev ON uev.event_id = e.id JOIN Users u ON u.id = uev.user_id WHERE u.id = :id ORDER BY uev.created_at DESC LIMIT 10";
    public static final String INSERT_EVENT_QUERY = "INSERT INTO UserEvents (user_id, event_id, device, ip_address) VALUES ((SELECT id from Users WHERE email = :email), (SELECT id FROM Events WHERE type = :type), :device, :ipAddress)";
}
