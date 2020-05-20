package com.springcloudgcp.pubsubexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@SpringBootApplication
public class PubsubexampleApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(PubsubexampleApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(PubsubexampleApplication.class, args);
	}


	@Bean
	public MessageChannel pubsubInputChannel() {
		return new DirectChannel();
	}
	@Bean
	public PubSubInboundChannelAdapter messageChannelAdapter(@Qualifier("pubsubInputChannel") MessageChannel inputChannel, PubSubTemplate pubSubTemplate
			){
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, "testSubscription");
		adapter.setOutputChannel(inputChannel);
		adapter.setAckMode(AckMode.MANUAL);
		return adapter;
	}

	@Bean
	@ServiceActivator(inputChannel = "pubsubInputChannel")
	public MessageHandler messageHandler(){

		return message -> {
		LOGGER.info("Message Arrived Payload!" + new String((byte[]) message.getPayload()));
			BasicAcknowledgeablePubsubMessage pubsubMessage =
					message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
			pubsubMessage.ack();
		};
	}

	@MessagingGateway(defaultRequestChannel = "pubsubOutputChannel")
	public interface PubSubOutboundGateway {
		void sendToPubsub(String text);
	}

	@Bean
	@ServiceActivator(inputChannel = "pubsubOutputChannel")
	public MessageHandler messageSender(PubSubTemplate pubSubTemplate){
		return new PubSubMessageHandler(pubSubTemplate,"testTopic");
	}


}
