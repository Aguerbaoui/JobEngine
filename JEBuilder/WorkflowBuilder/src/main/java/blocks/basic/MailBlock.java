package blocks.basic;

import blocks.WorkflowBlock;

import java.util.HashMap;
import java.util.List;

public class MailBlock extends WorkflowBlock {

    private boolean bEnableSSL = false;

    private boolean bUseDefaultCredentials = false;

    private String strSenderAddress;

    private List<String> lstRecieverAddress;

    private String strSMTPServer;

    private int iPort;

    private int iSendTimeOut;

    private HashMap<String, String> emailMessage;

    private String strUserName;

    private String strPassword;

    public boolean isbEnableSSL() {
        return bEnableSSL;
    }

    public void setbEnableSSL(boolean bEnableSSL) {
        this.bEnableSSL = bEnableSSL;
    }

    public boolean isbUseDefaultCredentials() {
        return bUseDefaultCredentials;
    }

    public void setbUseDefaultCredentials(boolean bUseDefaultCredentials) {
        this.bUseDefaultCredentials = bUseDefaultCredentials;
    }

    public String getStrSenderAddress() {
        return strSenderAddress;
    }

    public void setStrSenderAddress(String strSenderAddress) {
        this.strSenderAddress = strSenderAddress;
    }

    public List<String> getLstRecieverAddress() {
        return lstRecieverAddress;
    }

    public void setLstRecieverAddress(List<String> lstRecieverAddress) {
        this.lstRecieverAddress = lstRecieverAddress;
    }

    public String getStrSMTPServer() {
        return strSMTPServer;
    }

    public void setStrSMTPServer(String strSMTPServer) {
        this.strSMTPServer = strSMTPServer;
    }

    public int getiPort() {
        return iPort;
    }

    public void setiPort(int iPort) {
        this.iPort = iPort;
    }

    public int getiSendTimeOut() {
        return iSendTimeOut;
    }

    public void setiSendTimeOut(int iSendTimeOut) {
        this.iSendTimeOut = iSendTimeOut;
    }

    public HashMap<String, String> getEmailMessage() {
        return emailMessage;
    }

    public void setEmailMessage(HashMap<String, String> emailMessage) {
        this.emailMessage = emailMessage;
    }

    public String getStrUserName() {
        return strUserName;
    }

    public void setStrUserName(String strUserName) {
        this.strUserName = strUserName;
    }

    public String getStrPassword() {
        return strPassword;
    }

    public void setStrPassword(String strPassword) {
        this.strPassword = strPassword;
    }
}


/* with auth
* {
  "strSenderAddress": "string",
  "lstRecieverAddress": [
    "string"
  ],
  "strSMTPServer": "string",
  "iPort": 0,
  "iSendTimeOut": 0,
  "emailMessage": {
    "strSubject": "string",
    "strBody": "string"
  },
  "strUserName": "string",
  "strPassword": "string"
}
* */

/* no auth
* {
  "strSenderAddress": "string",
  "lstRecieverAddress": [
    "string"
  ],
  "strSMTPServer": "string",
  "iPort": 0,
  "iSendTimeOut": 0,
  "emailMessage": {
    "strSubject": "string",
    "strBody": "string"
  },
  "bEnableSSL": true,
  "bUseDefaultCredentials": true
}
* */