package com.GEmailReceiver.GEmailReceiver.Entity;

import lombok.Data;

@Data
public class EmailRequests {
    private String to;
    private String subject;
    private String message;
}
