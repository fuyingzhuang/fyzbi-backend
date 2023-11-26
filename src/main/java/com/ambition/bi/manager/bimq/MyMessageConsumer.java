package com.ambition.bi.manager.bimq;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Ambition
 * @date 2023/11/26 13:34
 */


@Component
@Slf4j
public class MyMessageConsumer {

    // 指定程序监听的消息队列和确认机制 @SneakyThrows注解可以抛出异常
    @SneakyThrows
    @RabbitListener(queues = {"direct_queue"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", message);
        channel.basicAck(deliveryTag, false);
    }

}
