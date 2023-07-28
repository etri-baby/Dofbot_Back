package com.arm.server.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.arm.server.config.MqttConfig;
import com.arm.server.model.MqttSubscribeModel;
import com.arm.server.service.MQTTService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Raspberry Pi MQTT 통신 API")
@RestController
@RequestMapping(value = "/api/mqtt")
public class MQTTController {
    private final MQTTService mqttService;

    @Autowired
    public MQTTController(MQTTService mqttService) {
        this.mqttService = mqttService;
    }

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

    @PostMapping("/send_pad_data")
    @ResponseBody
    public String sendGamepadInput(@RequestParam("axis_0") double axis0,
                                   @RequestParam("axis_1") double axis1,
                                   @RequestParam("button_0") boolean button0,
                                   @RequestParam("button_1") boolean button1,
                                   @RequestParam("button_2") boolean button2,
                                   @RequestParam("button_3") boolean button3,
                                   @RequestParam("button_4") boolean button4,
                                   @RequestParam("button_5") boolean button5,
                                   @RequestParam("button_6") boolean button6,
                                   @RequestParam("button_7") boolean button7,
                                   @RequestParam("button_8") boolean button8) {
        String payload = String.format("{\"axis_0\": %.2f, \"axis_1\": %.2f, \"button_0\": %b, \"button_1\": %b, \"button_2\": %b, \"button_3\": %b, \"button_4\": %b, \"button_5\": %b, \"button_6\": %b, \"button_7\": %b, \"button_8\": %b}",
                axis0, axis1, button0, button1, button2, button3, button4, button5, button6, button7, button8);

                mqttService.publishMessage(payload);
                return "게임패드 입력이 성공적으로 전송되었습니다.";
                
    }
}