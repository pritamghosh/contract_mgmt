/**
 *
 */
package com.pns.contractmanagement.helper.impl;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pns.contractmanagement.vo.MailVo;

/**
 * @author Pritam Ghosh
 */
@Component
public class MailServiceHelperImpl {
	@Value("${app.smtp.username}")
	private String username;
	@Value("${app.smtp.password}")
	private String password;
	@Value("${app.smtp.port}")
	private String port;
	@Value("${app.smtp.host}")
	private String host;

	public void sendMail(final MailVo mail) {

		final String to = mailListString(mail.getToList());

		final String cc = mailListString(mail.getCcList());

		final Properties props = new Properties();

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);

		// Get the Session object.
		final Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			final Message message = new MimeMessage(session);
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

			if (!cc.isEmpty()) {
				message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
			}
			if(mail.getForm()!=null) {
				message.setFrom(new InternetAddress(mail.getForm()));
			}

			message.setSubject(mail.getSubject());

			message.setContent(mail.getHtmlText(), "text/html");

			Transport.send(message);

		} catch (final MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	private String mailListString(final List<String> mailList) {
		final StringBuilder toBuilder = new StringBuilder();
		for (final String to : mailList) {
			toBuilder.append(to).append(",");
		}
		return toBuilder.length() == 0 ? "" : toBuilder.deleteCharAt(toBuilder.lastIndexOf(",")).toString();
	}

}
