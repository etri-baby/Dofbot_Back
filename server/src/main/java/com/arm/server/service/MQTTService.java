package com.arm.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.arm.server.dto.MQTTMessageDto;

import jakarta.annotation.PostConstruct;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MQTTService implements MqttCallback {
    // application.properties 파일에서 mqtt.broker.host 항목의 값
    @Value("${mqtt.broker.host}")
    private String host;

    // application.properties 파일에서 mqtt.broker.port 항목의 값
    @Value("${mqtt.broker.port}")
    private String port;

    private MqttClient client;

    @PostConstruct
    public void connect(){
        String serverURI = getURI();
        String uuid = UUID.randomUUID().toString();

        log.info("MQTT Broker Server : "+serverURI);
        log.info("UUID : "+uuid);

        MqttConnectOptions option = new MqttConnectOptions();
        option.setCleanSession(true);

        try {
            client = new MqttClient(serverURI, uuid);
            client.setCallback(this);
            client.connect(option);
        } catch(MqttException e){
            log.error("MQTT connection error");
        }
    }

    public String getURI(){
        return "tcp://"+host+":"+String.valueOf(port);
    }

    public MQTTMessageDto publish(MQTTMessageDto mqttMessageDto){
        log.info("Message is published : "+mqttMessageDto.getTopic()+"/"
                +mqttMessageDto.getMessage());

        MqttMessage message = new MqttMessage();
        try {
            message.setPayload(mqttMessageDto.getMessage().getBytes("UTF-8"));
            client.publish(mqttMessageDto.getTopic(),message);
        } catch(Exception e){
            log.error(e.getMessage());
        }

        return mqttMessageDto;
    }



    @Override
    public void connectionLost(Throwable cause) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    

}
