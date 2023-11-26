package com.ambition.bi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * @author Ambition
 * @date 2023/11/25 22:24
 * topic类型的交换机
 */
public class TopicConsumer {
    //    定义一个交换机的名称
    private static final String EXCHANGE_NAME = "topic_exchange";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 创建一个ConnectionFactory,并进行配置 这个对象可以用户创建链接到RabbitMQ服务器的链接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        // 设置RabbitMQ服务器的地址
        connectionFactory.setHost("localhost");
//        设置用户名
        connectionFactory.setUsername("ambition");
//        设置密码
        connectionFactory.setPassword("w1VJ6kR4");
//        设置端口号
        connectionFactory.setPort(5672);

        try {
            Connection connection = connectionFactory.newConnection();
            // 创建两个通道
            Channel channel = connection.createChannel();
            // 创建前端队列
            String front_queue = "front_queue";
            channel.queueDeclare(front_queue, false, false, false, null);
            channel.queueBind(front_queue, EXCHANGE_NAME, "#.front.#");
            // 创建后端队列
            String back_queue = "back_queue";
            channel.queueDeclare(back_queue, false, false, false, null);
            channel.queueBind(back_queue, EXCHANGE_NAME, "#.back.#");
            // 创建产品队列
            String product_queue = "product_queue";
            channel.queueDeclare(product_queue, false, false, false, null);
            channel.queueBind(product_queue, EXCHANGE_NAME, "#.product.#");

//            打印消息
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            // 创建消息的回调 front_queue
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                // 打印消息
                System.out.println(" [" + front_queue + "] Received '" + message + "'");
            };
            // 创建消息的回调 back_queue
            DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                // 打印消息
                System.out.println(" [" + back_queue + "] Received '" + message + "'");
            };
            // 创建消息的回调 product_queue
            DeliverCallback deliverCallback3 = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                // 打印消息
                System.out.println(" [" + product_queue + "] Received '" + message + "'");
            };
            // 开始接收消息
            channel.basicConsume(front_queue, true, deliverCallback, consumerTag -> {
            });
            channel.basicConsume(back_queue, true, deliverCallback2, consumerTag -> {
            });
            channel.basicConsume(product_queue, true, deliverCallback3, consumerTag -> {
            });



        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

    }
}
