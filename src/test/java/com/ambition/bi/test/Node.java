package com.ambition.bi.test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 *  线程安全型双向链表的实现
 *  编写一个线程安全的双向链表，所谓线程安全，就是该链表能够实现多个线程同时正确的
 *  增删改查链表结点，也就是能够实现对链表这个临界货源的保护。需要实现的函数包括：
 *  InitList 函数：初始化一个空的双向链表，并初始化各个用于保护链表的信号量
 *  Insert 函数：向链表指定位置插入一个结点
 *  Erase 函数：删除指定位置的结点
 *  Clear 兩数：删除链表中的所有结点
 *  Find 函数：查找链表中是否有指定的元素，若有，返回能够访问该结点的指针,若无 返回 NULL
 *  Print 函数：打印当前链表中的所有元素。
 **/

// 链表节点
class Node<T> {
    T data;
    Node<T> prev;
    Node<T> next;

    Node(T data) {
        this.data = data;
    }
}

// 线程安全的链表
class ThreadSafeLinkedList<T> {
    private Node<T> head;
    private Node<T> tail;
    private Lock lock;

    // 初始化链表和锁
    ThreadSafeLinkedList() {
        head = null;
        tail = null;
        lock = new ReentrantLock();
    }

    // 在指定位置插入新节点
    public void insert(int index, T data) {
        lock.lock(); // 获取锁
        try {
            if (index < 0 || index > size()) {
                throw new IndexOutOfBoundsException();
            }

            Node<T> newNode = new Node<>(data);
            if (index == 0) { // 插入到链表头部
                newNode.next = head;
                if (head != null) {
                    head.prev = newNode;
                }
                head = newNode;
                if (tail == null) {
                    tail = newNode;
                }
            } else if (index == size()) { // 插入到链表尾部
                newNode.prev = tail;
                if (tail != null) {
                    tail.next = newNode;
                }
                tail = newNode;
                if (head == null) {
                    head = newNode;
                }
            } else { // 插入到中间位置
                Node<T> current = getNode(index);
                newNode.prev = current.prev;
                newNode.next = current;
                current.prev.next = newNode;
                current.prev = newNode;
            }
        } finally {
            lock.unlock(); // 释放锁
        }
    }

    // 删除指定位置的节点
    public void erase(int index) {
        lock.lock(); // 获取锁
        try {
            if (index < 0 || index >= size()) {
                throw new IndexOutOfBoundsException();
            }

            if (index == 0) { // 删除链表头部节点
                head = head.next;
                if (head != null) {
                    head.prev = null;
                }
                if (head == null) {
                    tail = null;
                }
            } else if (index == size() - 1) { // 删除链表尾部节点
                tail = tail.prev;
                if (tail != null) {
                    tail.next = null;
                }
                if (tail == null) {
                    head = null;
                }
            } else { // 删除中间节点
                Node<T> current = getNode(index);
                current.prev.next = current.next;
                current.next.prev = current.prev;
            }
        } finally {
            lock.unlock(); // 释放锁
        }
    }

    // 清空链表
    public void clear() {
        lock.lock(); // 获取锁
        try {
            head = null;
            tail = null;
        } finally {
            lock.unlock(); // 释放锁
        }
    }

    // 查找包含特定数据的节点
    public Node<T> find(T data) {
        lock.lock(); // 获取锁
        try {
            Node<T> current = head;
            while (current != null) {
                if (current.data.equals(data)) {
                    return current;
                }
                current = current.next;
            }
            return null;
        } finally {
            lock.unlock(); // 释放锁
        }
    }

    // 打印链表元素
    public void print() {
        lock.lock(); // 获取锁
        try {
            Node<T> current = head;
            while (current != null) {
                System.out.print(current.data + " ");
                current = current.next;
            }
            System.out.println();
        } finally {
            lock.unlock(); // 释放锁
        }
    }

    // 获取链表长度
    private int size() {
        int count = 0;
        Node<T> current = head;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }

    // 获取指定位置的节点
    private Node<T> getNode(int index) {
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }
}
