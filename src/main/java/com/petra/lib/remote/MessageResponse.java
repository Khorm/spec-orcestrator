package com.petra.lib.remote;

import com.petra.lib.variable.container.ValueDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.util.List;

@FieldDefaults( makeFinal = true)
@RequiredArgsConstructor
public class MessageResponse {
    private HttpStatus httpStatus;
    private String message;

    @Getter
    private List<ValueDto> resultValues;


    public static String OK = "OK";
    public static String REPEAT = "REPEAT";
    public static String ERROR = "ERROR";


    public boolean isOk() {
        return httpStatus.is2xxSuccessful() && message.equals("OK");
    }

    public boolean isRepeat() {
        return httpStatus.is2xxSuccessful() && message.contains("REPEAT");
    }

    public boolean isError() {
        return !httpStatus.is2xxSuccessful() || message.equals("ERROR");
    }

}
