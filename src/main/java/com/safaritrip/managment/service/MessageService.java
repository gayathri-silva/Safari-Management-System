package com.safaritrip.management.service;

import com.safaritrip.management.model.User;

public interface MessageService {
    void sendMessage(User sender, User recipient, String content);
}