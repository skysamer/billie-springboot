package com.lab.smartmobility.billie.global.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.mail.SimpleMailMessage;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomSimpleMailMessage extends SimpleMailMessage {
    @Nullable
    private String from;

    @Nullable
    private String replyTo;

    @Nullable
    private String[] to;

    @Nullable
    private String[] cc;

    @Nullable
    private String[] bcc;

    @Nullable
    private Date sentDate;

    @Nullable
    private String subject;

    @Nullable
    private String text;

    @Override
    public void setTo(String to) {
        super.setTo(to);
    }
}
