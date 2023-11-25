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
 * 发布订阅模式 生产者
 * fanout类型的交换机
 */
public class FanoutConsumer {
    //    定义一个交换机的名称
    private static final String EXCHANGE_NAME = "fanout_exchange";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 创建一个ConnectionFactory,并进行配置 这个对象可以用户创建链接到RabbitMQ服务器的链接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        // 设置RabbitMQ服务器的地址
        connectionFactory.setHost("59.110.55.200");
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
            Channel channel2 = connection.createChannel();
            /*
             * String exchange, 交换机名称
             * String type, 交换机类型
             * boolean durable, 是否持久化
             * boolean autoDelete, 是否自动删除
             * boolean internal, 是否内置
             * Map<String, Object> arguments 交换机的其他属性
             */
            // 声明交换机
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout", false, false, false, null);
            // 创建队列1
            String queueName1 = "fyz_queue";
            channel.queueDeclare(queueName1, false, false, false, null);
            channel.queueBind(queueName1, EXCHANGE_NAME, "");
            // 创建队列2
            String queueName2 = "ambition_queue";
            channel2.queueDeclare(queueName2, false, false, false, null);
            channel2.queueBind(queueName2, EXCHANGE_NAME, "");

            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            // 创建一个回调的消费者处理类
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [fyz] Received '" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            };
            DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                System.out.println(" [ambition] Received '" +
                        delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
            };

            // 开始获取消息 channel
            channel.basicConsume(queueName1, true, deliverCallback, consumerTag -> {
            });
            channel2.basicConsume(queueName2, true, deliverCallback2, consumerTag -> {
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

    }
}
