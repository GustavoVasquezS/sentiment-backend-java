package com.project.sentimentapi.event;

import com.project.sentimentapi.entity.User;
import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class UserRegisteredEvent extends ApplicationEvent {
    private final User usuario;

    public UserRegisteredEvent(Object source, User usuario) {
        super(source);
        this.usuario = usuario;
    }
}
