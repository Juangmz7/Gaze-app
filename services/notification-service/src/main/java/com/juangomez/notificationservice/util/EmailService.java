package com.juangomez.notificationservice.util;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String sendTo, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlBody = wrapInTemplate(subject, body);

            helper.setTo(sendTo);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            log.info("Sending styled email to {}", sendTo);
            mailSender.send(message);

        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage(), e);
            throw new RuntimeException("Error sending mail: " + e.getMessage(), e);
        }
    }


    /**
     * Wraps plain postContent in a styled CampusConnect HTML template.
     */
    private String wrapInTemplate(String title, String content) {
        return """
        <html>
        <body style="font-family: 'Segoe UI', Arial, sans-serif; background-color:#f7f9fb; color:#333; padding:20px;">
            <div style="max-width:600px; margin:0 auto; background:#fff; border-radius:12px; box-shadow:0 2px 10px rgba(0,0,0,0.1); overflow:hidden;">
                
                <div style="background:#0055cc; color:white; padding:16px 24px;">
                    <h2 style="margin:0;">Notification's microservice</h2>
                </div>

                <div style="padding:24px;">
                    <h3 style="color:#0055cc; margin-top:0;">%s</h3>
                    <p style="white-space:pre-line; line-height:1.6;">%s</p>
                </div>

                <div style="background:#f0f3f8; padding:12px 24px; font-size:13px; color:#666;">
                    <p style="margin:0;">This message was sent automatically by the application.</p>
                    <p style="margin:0;">© 2025 DB Juan Gomez. All rights reserved.</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(escapeHtml(title), content.replace("\n", "<br>"));
    }

    /**
     * Escapes special characters in HTML (basic version).
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
