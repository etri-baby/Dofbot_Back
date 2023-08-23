package com.arm.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

@Service
public class MQTTService {

    // application.properties 파일에서 mqtt.broker.host 항목의 값
    @Value("${mqtt.broker.host}")
    private String brokerUrl;

    @Value("${mqtt.topic}")
    private String topic;

    private MqttClient client;
    
    @PostConstruct
    public void initialize() {
        try {
            String clientId = MqttClient.generateClientId();
            client = new MqttClient(brokerUrl, clientId, null);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            client.connect(options);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("Connection lost!");
                    initialize();
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    // 메시지 수신 시 호출되는 메서드
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    // 메시지가 성공적으로 전달되면 호출되는 메서드
                }
            });

            client.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    


    @PreDestroy
    public void cleanup() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setPayload(message.getBytes());

            client.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}