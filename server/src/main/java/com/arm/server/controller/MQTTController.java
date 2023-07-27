package com.arm.server.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.arm.server.config.MqttConfig;
import com.arm.server.model.MqttSubscribeModel;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Raspberry Pi MQTT 통신 API")
@RestController
@RequestMapping(value = "/api/mqtt")
public class MQTTController {

    @Value("${mqtt.broker.host}")
    private String brokerUrl;

    @Value("${mqtt.topic}")
    private String topic;

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
                                   @RequestParam("button_0") double button0,
                                   @RequestParam("button_1") double button1,
                                   @RequestParam("button_2") double button2,
                                   @RequestParam("button_3") double button3,
                                   @RequestParam("button_4") double button4,
                                   @RequestParam("button_5") double button5,
                                   @RequestParam("button_6") double button6,
                                   @RequestParam("button_7") double button7,
                                   @RequestParam("button_8") double button8
                                   ) {
        String payload = String.format("{\"axis_0\": %.2f, \"axis_1\": %.2f, \"button_0\": %.2f, \"button_1\": %.2f, \"button_2\": %.2f, \"button_3\": %.2f, \"button_4\": %.2f, \"button_5\": %.2f, \"button_6\": %.2f, \"button_7\": %.2f, \"button_8\": %.2f}",
                axis0, axis1, button0, button1, button2, button3, button4, button5, button6, button7, button8);

        try {
            MqttClient client = new MqttClient(brokerUrl, MqttClient.generateClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            client.connect(options);

            MqttMessage message = new MqttMessage(payload.getBytes());
            client.publish(topic, message);

            client.disconnect();
            return "게임패드 입력이 성공적으로 전송되었습니다.";
        } catch (MqttException e) {
            return "게임패드 입력 전송 실패: " + e.getMessage();
        }
    }
}