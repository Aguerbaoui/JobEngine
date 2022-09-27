package io.je.serviceTasks;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SMSTask extends ActivitiTask {
    //Constants
    private String serverType;
    private List<String> receiverPhoneNumbers;
    private String accountSID;
    private String accountToken;
    private String senderPhoneNumber;
    private String message;
    private String inputType;
    private String URI;
    private String validity;
    private String smsType;
    private String modem;
    private boolean priority;
    private boolean sendAsUnicode;
    private boolean twilioServer;
}
