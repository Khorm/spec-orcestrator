package com.petra.lib.remote;

import org.springframework.http.HttpStatus;

public class MessageResponse {
    private final HttpStatus httpStatus;
    private final String message;

    MessageResponse(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public boolean isOk(){
        return httpStatus.is2xxSuccessful() && message.equals("OK");
    }

    public boolean isRepeat() {
        return httpStatus.is2xxSuccessful() && message.contains("REPEAT");
    }

    public boolean isError(){
        return !httpStatus.is2xxSuccessful();
    }

}
