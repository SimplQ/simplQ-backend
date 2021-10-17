package me.simplq.service.notification;

import me.simplq.dao.Token;

public interface NotificationChannel {
  void notify(Token token, String payload);
}
