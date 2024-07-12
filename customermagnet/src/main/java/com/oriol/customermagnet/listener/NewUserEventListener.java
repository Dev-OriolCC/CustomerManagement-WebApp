package com.oriol.customermagnet.listener;

import com.oriol.customermagnet.event.NewUserEvent;
import com.oriol.customermagnet.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.oriol.customermagnet.utils.RequestUtils.getDevice;
import static com.oriol.customermagnet.utils.RequestUtils.getIpAdress;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewUserEventListener {
    private final EventService eventService;
    private final HttpServletRequest httpServletRequest;

    @EventListener
    public void onNewUserEvent(NewUserEvent event) {
        eventService.addUserEvent(event.getEmail(), event.getType(), getDevice(httpServletRequest), getIpAdress(httpServletRequest));
    }

}
