package cn.edu.bupt.backendfinal.services;

public interface MessageService {
  void produce(String key, String value);
  String consume(String key);
}
