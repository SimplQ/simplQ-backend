package me.simplq.service.notification;

import me.simplq.dao.Token;
import me.simplq.service.message.Message;

public interface NotificationChannel {
  void notify(Token token, Message message);
}
