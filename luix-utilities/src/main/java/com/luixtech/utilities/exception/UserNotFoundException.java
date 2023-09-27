package com.luixtech.utilities.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserNotFoundException extends RuntimeException {

    private static final long   serialVersionUID = 3389857462571862368L;
    private final        String username;

    public UserNotFoundException(String username) {
        super("User not found!");
        this.username = username;
    }
}