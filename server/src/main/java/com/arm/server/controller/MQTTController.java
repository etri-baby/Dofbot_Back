package com.arm.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.arm.server.dto.MQTTMessageDto;
import com.arm.server.service.MQTTService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "Raspberry Pi MQTT 통신 API")
@Controller
public class MQTTController {
    private final MQTTService mqttService;

    public MQTTController(MQTTService mqttService){
        this.mqttService = mqttService;
    }

    @RequestMapping(value="/api/message", method=RequestMethod.POST)
    public @ResponseBody MQTTMessageDto publish(@RequestBody MQTTMessageDto mqttMessageDto) {
        return mqttService.publish(mqttMessageDto);
    }
    


}
