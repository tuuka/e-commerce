package net.tuuka.ecommerce.service.email;

public interface EmailSender {
    void send(String to, String email);
}
