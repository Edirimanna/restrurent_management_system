package com.cafe.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtil {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleMsg(String to, String subject, String text , List<String> list){
        SimpleMailMessage message =  new SimpleMailMessage();
        message.setFrom("ediritest1@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        if (list!=null && list.size() > 0){
            message.setCc(getCcArray(list));
        }

        javaMailSender.send(message);
    }

    public String[] getCcArray(List<String> ccList){
        String[] cc =   new String[ccList.size()];
        for (int i=0;i< ccList.size();i++){
            cc[i]= ccList.get(i);
        }
        return cc;
    }
}
