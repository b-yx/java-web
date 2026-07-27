package com.example.utils;

public class CurrentHolder {

    // ThreadLocal 为每个线程提供独立的存储空间
    private static final ThreadLocal<Integer> CURRENT_ID = new ThreadLocal<>();

    // 存储当前登录员工ID
    public static void setCurrentId(Integer id) {
        CURRENT_ID.set(id);
    }

    // 获取当前登录员工ID
    public static Integer getCurrentId() {
        return CURRENT_ID.get();
    }

    // 清除当前线程的存储数据（防止内存泄漏）
    public static void remove() {
        CURRENT_ID.remove();
    }
}
