package com.ambition.bi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * @author Ambition
 * @date 2023/11/25 22:24
 * topic类型的交换机
 */
public class TopicProducer {
    //    定义一个交换机的名称
    private static final String EXCHANGE_NAME = "topic_exchange";

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
            Channel channel = connection.createChannel();
            /*
             * String exchange, 交换机名称
             * String type, 交换机类型
             * boolean durable, 是否持久化
             * boolean autoDelete, 是否自动删除
             * boolean internal, 是否内置
             * Map<String, Object> arguments 交换机的其他属性
             */
            channel.exchangeDeclare(EXCHANGE_NAME, "topic", false, false, false, null);
            while (scanner.hasNext()) {
                String message = scanner.nextLine();
                if ("exit".equals(message)) {
                    break;
                }
                if (message.length() < 1) {
                    continue;
                }
                // 获取消息内容 和 路由键
                String[] split = message.split(" ");
                String data = split[0];
                String routingKey = split[1];
                /*
                 * String exchange, 交换机名称
                 * String routingKey, 路由键
                 * BasicProperties props, 消息的其他属性
                 * byte[] body 消息体
                 */
                channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

    }
}
