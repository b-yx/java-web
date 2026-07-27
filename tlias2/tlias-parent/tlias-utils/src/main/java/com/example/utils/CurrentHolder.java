package com.example.utils;

public class CurrentHolder {

    private static final ThreadLocal<Integer> CURRENT_ID = new ThreadLocal<>();

    public static void setCurrentId(Integer id) {
        CURRENT_ID.set(id);
    }

    public static Integer getCurrentId() {
        return CURRENT_ID.get();
    }

    public static void remove() {
        CURRENT_ID.remove();
    }
}
