package com.usetech.dvente.events.users;

import com.usetech.dvente.entities.users.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {

    private final User user;

    public UserRegisteredEvent(User user, Object source) {
        super(source);
        this.user = user;
    }

}
