# 📥 GMailReceiver Service

**GMailReceiver** is a Spring Boot application that displays emails in a user-friendly inbox UI and integrates seamlessly with GMailSender to send emails. It allows users to view, compose, and manage emails with ease. The service provides a clean and responsive interface for email operations.

---

## 🛠️ Technology Stack

- Java 17+
- Spring Boot 3.x
- Spring Web, Spring MVC
- Thymeleaf (UI templates)
- RestTemplate (for calling GMailSender service)
- Bootstrap 5 (frontend styling)
- Maven

---

## 🔹 Features

- Display received emails in Inbox, Starred, Sent, Spam, and Bin folders.
- Compose and send emails via GMailSender REST API.
- Show success/error messages after sending emails.
- Back to Inbox button after sending emails.
- Responsive UI with table and modal forms for email operations.

---
## 📸 Screenshots
### Compose Mail
![Compose Page](./gmailReceiver.png)

## ⚙️ Configuration

Update `src/main/resources/application.properties` with your settings:

```properties
spring.application.name=GEmailReceiver

# Gmail IMAP settings (for reading emails)
spring.mail.host=imap.gmail.com
spring.mail.port=993
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# GMailSender service URL
gmailsender.url=http://localhost:8081
```

## 📜 License
This project is licensed under the MIT License.


