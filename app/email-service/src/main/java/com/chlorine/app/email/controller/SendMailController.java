package com.chlorine.app.email.controller;

import com.chlorine.app.email.util.SendMailUtil;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/sendMail")
public class SendMailController {
    @PostMapping("/send")
    public boolean send(String subject, String content,
                        String fromName,
                        String[] to,
                        String[] cc,
                        String[] bcc) {
        return SendMailUtil.sendEmail(subject, content, fromName, to, cc, bcc);
    }

    @PostMapping("/send1")
    public boolean send1(@RequestBody SendParam sendParam) {
        return SendMailUtil.sendEmail(sendParam.subject, sendParam.content, sendParam.fromName, sendParam.to, sendParam.cc, sendParam.bcc);
    }

    @Data
    static class SendParam {
       private String subject;
        private String content;
        private String fromName;
        private String[] to;
        private String[] cc;
        private String[] bcc;
    }
}
