package com.oriol.customermagnet.event;

import com.oriol.customermagnet.enumeration.EventType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
@Getter
@Setter
public class NewUserEvent extends ApplicationEvent {
    private EventType type;
    private String email;

    public NewUserEvent(EventType type, String email) {
        super(email);
        this.type = type;
        this.email = email;
    }
}
