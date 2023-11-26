package com.ambition.bi.mq;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * @author Ambition
 * @date 2023/11/25 22:00
 * Work模式 消费者1
 * 一对多 轮询的方式接受消息进行消费
 */
public class WorkerTwo {
    // 定义一个静态常量字符串QUEUE_NAME,值为"hello" 表示我们需要向work_queue这个队列发送消息
    private static final String QUEUE_NAME = "work_queue";

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
            // 控制单个消费者在确认之前只发一个消息
            channel.basicQos(1);
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
                    try {
                        /*
                         * 手动确认消息
                         * long deliveryTag, 消息的唯一标识
                         * boolean multiple, 是否批量 是否需要一次性确认之前所有未被当前消费者确认的消息
                         */
                        channel.basicAck(envelope.getDeliveryTag(), false);
//
                        /*
                         * 手动拒绝消息
                         * long deliveryTag, 消息的唯一标识
                         * boolean multiple, 是否批量 是否需要一次性拒绝之前所有未被当前消费者确认的消息
                         * boolean requeue 是否重回队列
                         */
//                        channel.basicNack(envelope.getDeliveryTag(), false, true);
                        /*
                         * 手动拒绝消息
                         * long deliveryTag, 消息的唯一标识
                         * boolean requeue 是否重回队列
                         */
//                        channel.basicReject(envelope.getDeliveryTag(), false);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
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
