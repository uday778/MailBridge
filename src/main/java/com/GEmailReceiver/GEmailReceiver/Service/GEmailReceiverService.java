package com.GEmailReceiver.GEmailReceiver.Service;

import com.GEmailReceiver.GEmailReceiver.Entity.Email;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.*;
import jakarta.mail.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class GEmailReceiverService {
    private List<Email> allEmailsList = new ArrayList<>();
    private int lastMessageCount = -1;
    private boolean initialFetch = false;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;

    @PostConstruct
    public void initialMails() {
        try {
            fetchAllMails();
            initialFetch = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 10000) //10 seconds
    public void newlyReceivedMails() throws MessagingException {
        if (!initialFetch) {
            initialMails();
        }
        try {
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "imap");
            properties.put("mail.imap.host", "imap.gmail.com");
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.ssl.enable", "true");
            Session session = Session.getInstance(properties);
            Store store = session.getStore("imap");
            store.connect("imap.gmail.com", username, password);
            Folder inboxFolder = store.getFolder("INBOX");
            inboxFolder.open(Folder.READ_ONLY);
            int currentMailsCount = inboxFolder.getMessageCount();
            if (currentMailsCount > lastMessageCount) {
                Message[] newMailMessages = inboxFolder.getMessages(lastMessageCount + 1, currentMailsCount);
                System.out.println("New Mails Count " + newMailMessages.length);
                for (Message message : newMailMessages) {
                    Email email = new Email();
                    email.setFrom(InternetAddress.toString(message.getFrom()));
                    email.setSubject(message.getSubject());
                    email.setReceivedDate(message.getReceivedDate());
                    email.setContent(getTextFromMessage(message));
                    allEmailsList.add(0, email); //contents may have attachments deal with it
                }
                lastMessageCount = currentMailsCount;
            } else {
                System.out.println("No new MAils is Found ");
            }
            inboxFolder.close(false);
            store.close();
        } catch (Exception e) {
            System.out.println("Error in fetching newly received mails " + e.getMessage());
        }
    }

    private void fetchAllMails() throws MessagingException, IOException {
        try {
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "imap");
            properties.put("mail.imap.host", "imap.gmail.com");
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.ssl.enable", "true");
            Session session = Session.getInstance(properties);
            Store store = session.getStore("imap");
            store.connect("imap.gmail.com", username, password);
            Folder inboxFolder = store.getFolder("INBOX");
            inboxFolder.open(Folder.READ_ONLY);
            Folder[] folders = store.getDefaultFolder().list("*");
            for (Folder folder : folders) {
                System.out.println(folder.getFullName());
            }
            Message[] allMails = inboxFolder.getMessages();
            System.out.println("Initial Fetch total mails count : " + allMails.length);

            for (int i = allMails.length - 1; i >= 0; i--) {
                Message message = allMails[i];
                Email email = new Email();
                email.setFrom(InternetAddress.toString(message.getFrom()));
                email.setSubject(message.getSubject());
                email.setReceivedDate(message.getReceivedDate());
                email.setContent(getTextFromMessage(message));
                allEmailsList.add(email); // now newest first
            }
            lastMessageCount = inboxFolder.getMessageCount();
            System.out.println("last message count is : " + lastMessageCount);
            inboxFolder.close(false);
            store.close();
        } catch (Exception e) {
            System.out.println("error in fetching initial mails" + e.getMessage());
        }
    }

    public String getTextFromMessage(Message message) {
        try {
            if (message.isMimeType("text/plain")) {
                try {
                    return message.getContentType().toString();
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            } else if (message.isMimeType("multipart/*")) {
                MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
                return getTextFromMimeMultipart(mimeMultipart);
            }
            return "";
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mimeMultipart.getCount(); i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                stringBuilder.append(bodyPart.getContent());
            }
        }
        return stringBuilder.toString();
    }

    public List<Email> getEmails() {
        return allEmailsList;
    }
}