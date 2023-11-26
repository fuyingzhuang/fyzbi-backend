package com.ambition.bi.manager.bimq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author Ambition
 * @date 2023/11/26 13:38
 * 初始化队列
 * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 */


public class MyMqInitMain {

    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            // 设置RabbitMQ服务器的地址
            factory.setHost("59.110.55.200");
//        设置用户名
            factory.setUsername("ambition");
//        设置密码
            factory.setPassword("w1VJ6kR4");
//        设置端口号
            factory.setPort(5672);
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            String EXCHANGE_NAME = "code_exchange";
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            // 创建队列，随机分配一个队列名称
            String queueName = "code_queue";
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, EXCHANGE_NAME, "my_routingKey");
            System.out.println("初始化完成");
        } catch (Exception e) {

        }

    }
}
