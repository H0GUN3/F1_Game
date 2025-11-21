package com.mygame.f1.data;

public interface AuthStore {
    boolean register(String username, String password);
    boolean verify(String username, String password);
}

