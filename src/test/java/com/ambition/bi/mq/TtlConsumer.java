package com.ambition.bi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;

/**
 * @author Ambition
 * @date 2023/11/26 12:56
 * 延迟队列
 */
public class TtlConsumer {
    // 定义一个静态常量字符串QUEUE_NAME,值为"hello" 表示我们需要向hello这个队列发送消息
    private static final String QUEUE_NAME = "ttl_queue";

    public static void main(String[] args) {
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
            // 通过连接工厂创建一个连接
            Connection connection = connectionFactory.newConnection();
            // 通过连接创建一个信道
            Channel channel = connection.createChannel();
            // 创建队列 指定消息的过期参数
            HashMap<String, Object> params = new HashMap<>();
            // 指定队列中的消息过期时间 单位毫秒
            params.put("x-message-ttl", 10000);
            /*
             *
             * 通过信道声明一个队列
             * String queue,  队列名称
             * boolean durable,  是否持久化
             * boolean exclusive,  是否排他
             * boolean autoDelete, 是否自动删除
             * Map<String, Object> arguments 队列的其他属性
             */
            channel.queueDeclare(QUEUE_NAME, false, false, false, params);
            // 定义一个消息
            String message = "Hello 付英壮";
            /*
             * String exchange, 交换机名称
             * String routingKey, 路由键
             * BasicProperties props, 消息的其他属性
             * byte[] body 消息体
             */
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
            // 关闭信道
            channel.close();
            // 关闭连接
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
