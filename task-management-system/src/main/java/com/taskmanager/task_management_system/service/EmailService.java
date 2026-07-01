package com.taskmanager.task_management_system.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private final JavaMailSender mailSender;

	// Constructor to initialize the service with JavaMailSender to enable email
	// sending
	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	// Method to send an email
	public void sendEmail(String to, String subject, String body) throws jakarta.mail.MessagingException {
		jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setTo(to); // Set the recipient
		helper.setSubject(subject); // Set the subject
		helper.setText(body, true); // Set the body of the message (true means HTML content)

		// Send the email using mailSender
		mailSender.send(message);
	}
}
