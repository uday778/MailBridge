package com.GEmailReceiver.GEmailReceiver.Entity;


import lombok.Data;

import java.util.Date;

@Data
public class Email {

    private String from;
    private String subject;
    private Date receivedDate;
    private String content;

}
