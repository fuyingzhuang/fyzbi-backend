package com.ambition.bi.mq;

import com.rabbitmq.client.*;

/**
 * @author Ambition
 * @date 2023/11/25 21:38
 * 单消费者 消息的接收者
 */
public class SingleConsumer {
    // 定义正在监听的队列名称
    private static final String QUEUE_NAME = "hello";

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
            /*
             *
             * 通过信道声明一个队列
             * String queue,  队列名称
             * boolean durable,  是否持久化
             * boolean exclusive,  是否排他
             * boolean autoDelete, 是否自动删除
             * Map<String, Object> arguments 队列的其他属性
             */
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            // 定义一个消费者
            DefaultConsumer consumer = new DefaultConsumer(channel) {
                /*
                 * consumerTag 消费者标签
                 * envelope 信封，通过envelope
                 * properties 消息的其他属性
                 * body 消息体
                 */
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                    // 获取消息体
                    String message = new String(body);
                    System.out.println(" [x] Received '" + message + "'");
                }
            };
            /*
             * String queue, 队列名称
             * boolean autoAck, 是否自动确认
             * Consumer callback 消费者的回调函数
             */
            channel.basicConsume(QUEUE_NAME, true, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
