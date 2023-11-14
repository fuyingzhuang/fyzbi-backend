package com.ambition.bi.test;

public class Main {
    public static void main(String[] args) {
        ThreadSafeLinkedList<Integer> list = new ThreadSafeLinkedList<>();

        // 添加测试数据
        for (int i = 0; i < 5; i++) {
            list.insert(i, i);
        }

        // 创建多个线程进行操作
        Thread writerThread = new Thread(() -> {
            for (int i = 5; i < 10; i++) {
                list.insert(2, i); // 在位置2插入新元素
            }
        });

        Thread readerThread = new Thread(() -> {
            Node<Integer> result = list.find(3); // 查找元素3
            if (result != null) {
                System.out.println("Found: " + result.data);
            } else {
                System.out.println("Element not found");
            }
        });

        // 启动线程
        writerThread.start();
        readerThread.start();

        try {
            writerThread.join();
            readerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 打印链表
        list.print();
    }
}
