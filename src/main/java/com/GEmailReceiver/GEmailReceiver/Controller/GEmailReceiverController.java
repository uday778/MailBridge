package com.GEmailReceiver.GEmailReceiver.Controller;

import com.GEmailReceiver.GEmailReceiver.Entity.Email;
import com.GEmailReceiver.GEmailReceiver.Entity.EmailRequests;
import com.GEmailReceiver.GEmailReceiver.Service.GEmailReceiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;

@Controller
public class GEmailReceiverController {
    @Autowired
    private GEmailReceiverService gEmailReceiverService;


    private RestTemplate restTemplate;
    @Autowired
    public GEmailReceiverController(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }



    @GetMapping("/")
    public String inbox(Model model) {
        List<Email> emailsList = gEmailReceiverService.getEmails();
        model.addAttribute("emailsList", emailsList);
        return "inbox";
    }

    @PostMapping("/sendmail")
    public String sendMail(@ModelAttribute EmailRequests emailRequests, RedirectAttributes redirectAttributes) {
        try {

            String url = "http://localhost:8081";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<EmailRequests> httpEntity = new HttpEntity<>(emailRequests, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url +"/sendmail",httpEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                redirectAttributes.addFlashAttribute("success", "✅ Email sent successfully!");
            } else {
                redirectAttributes.addFlashAttribute("failed", "❌ Failed to send email. Response: " + response.getStatusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("ErrorMessage", "Process Failed with message: " + e.getMessage());
        }

        return "redirect:/";
    }

}
