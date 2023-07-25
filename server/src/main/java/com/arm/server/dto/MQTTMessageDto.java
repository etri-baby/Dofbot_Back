package com.arm.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MQTTMessageDto {
    private String topic;
    private String message;

    // 메시지가 null인 경우 빈 문자열("")로 반환하도록 수정
    public String getMessage() {
        return message != null ? message : "";
    }
}
