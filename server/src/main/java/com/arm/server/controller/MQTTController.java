package com.arm.server.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.arm.server.config.MqttConfig;
import com.arm.server.model.MqttSubscribeModel;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Raspberry Pi MQTT 통신 API")
@RestController
@RequestMapping(value = "/api/mqtt")
public class MqttController {

    @PostMapping("publish")
    public void publishMessage(@RequestParam("topic") String topic,
                           @RequestParam("message") String message,
                           @RequestParam("retained") Boolean retained,
                           @RequestParam("qos") Integer qos) throws org.eclipse.paho.client.mqttv3.MqttException {
        
                            // 요청 파라미터를 이용하여 MqttMessage 객체 생성
                            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                            mqttMessage.setQos(qos);
                            mqttMessage.setRetained(retained);
                            // MqttConfig를 이용하여 메시지 게시
                            MqttConfig.getInstance().publish(topic, mqttMessage);
    }

    @GetMapping("subscribe")
    public List<MqttSubscribeModel> subscribeChannel(@RequestParam(value = "topic") String topic,
                                                     @RequestParam(value = "wait_millis") Integer waitMillis)
                                                     throws InterruptedException, org.eclipse.paho.client.mqttv3.MqttException {
                List<MqttSubscribeModel> messages = new ArrayList<>();
                CountDownLatch countDownLatch = new CountDownLatch(10);
                MqttConfig.getInstance().subscribeWithResponse(topic, (s, mqttMessage) -> {
                    MqttSubscribeModel mqttSubscribeModel = new MqttSubscribeModel();
                    mqttSubscribeModel.setId(mqttMessage.getId());
                    mqttSubscribeModel.setMessage(new String(mqttMessage.getPayload()));
                    mqttSubscribeModel.setQos(mqttMessage.getQos());
                    messages.add(mqttSubscribeModel);
                    countDownLatch.countDown();
                });
                countDownLatch.await(waitMillis, TimeUnit.MILLISECONDS);
                return messages;
            }
}