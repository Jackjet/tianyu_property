package com.vguang.utils;

import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;

public class MqttManager {
	
	private MqttPahoMessageHandler mqttService;

	public MqttPahoMessageHandler getMqttService() {
		return mqttService;
	}

	public void setMqttService(MqttPahoMessageHandler mqttService) {
		this.mqttService = mqttService;
	} 
	
	
	
}
