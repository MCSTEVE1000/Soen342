package com.project;

public class Session {
    private static ThreadLocal<Users> userSession = new ThreadLocal<>();

    private Session() {
    }

    public static void getInstance(Users user) {
        userSession.set(user);
    }

    public static Users getUser() {
        return userSession.get();
    }

    public static void clearSession() {
        userSession.remove();
    }
}
