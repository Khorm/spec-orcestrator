package com.petra.lib.remote;

import org.springframework.http.HttpStatus;

public class MessageResponse {
    private final HttpStatus httpStatus;

    MessageResponse(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public boolean isOk(){
        return httpStatus.is2xxSuccessful();
    }
}
