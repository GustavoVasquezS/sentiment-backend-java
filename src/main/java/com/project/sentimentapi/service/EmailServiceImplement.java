package com.project.sentimentapi.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImplement implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public void sendRecoveryEmail(String to, String token) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + token;

            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto;'>"
                    + "<h2 style='color: #333;'>Recuperación de contraseña</h2>"
                    + "<p>Hemos recibido una solicitud para restablecer tu contraseña.</p>"
                    + "<p>Haz clic en el siguiente botón para crear una nueva contraseña:</p>"
                    + "<a href='" + resetLink + "' style='"
                    + "background-color: #4CAF50; color: white; padding: 12px 24px;"
                    + "text-decoration: none; border-radius: 4px; display: inline-block;'>"
                    + "Restablecer contraseña"
                    + "</a>"
                    + "<p style='color: #888; margin-top: 20px;'>Este enlace expira en 30 minutos.</p>"
                    + "<p style='color: #888;'>Si no solicitaste este cambio, ignora este correo.</p>"
                    + "</div>";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Recuperación de contraseña - SentimentAPI");
            helper.setText(htmlContent, true);
            helper.setFrom("sentimentapi.noreply@gmail.com");

            mailSender.send(message);
            System.out.println("✅ Email de recuperación enviado a: " + to);

        } catch (MessagingException e) {
            System.err.println("❌ Error al enviar email: " + e.getMessage());
            throw new RuntimeException("Error al enviar el correo de recuperación");
        }
    }
}
