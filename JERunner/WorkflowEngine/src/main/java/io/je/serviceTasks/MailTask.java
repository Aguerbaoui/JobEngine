package io.je.serviceTasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MailTask extends ActivitiTask {
    private boolean bEnableSSL = false;

    private boolean bUseDefaultCredentials = false;

    private String strSenderAddress;

    private List<String> lstRecieverAddress;

    private List<String> lstAttachementPaths;

    private List<String> lstUploadedFiles;

    private List<String> lstCCs;

    private List<String> lstBCCs;

    private String strSMTPServer;

    private int iPort;

    private int iSendTimeOut;

    private HashMap<String, String> emailMessage;

    private String strUserName;

    private String strPassword;

    private String url;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public List<String> getLstAttachementPaths() {
        if (lstAttachementPaths == null) lstAttachementPaths = new ArrayList<>();
        return lstAttachementPaths;
    }

    public void setLstAttachementPaths(List<String> lstAttachementPaths) {
        this.lstAttachementPaths = lstAttachementPaths;
    }

    public List<String> getLstUploadedFiles() {
        if (lstUploadedFiles == null) lstUploadedFiles = new ArrayList<>();
        return lstUploadedFiles;
    }

    public void setLstUploadedFiles(List<String> lstUploadedFiles) {
        this.lstUploadedFiles = lstUploadedFiles;
    }

    public List<String> getLstCCs() {
        if (lstCCs == null) lstCCs = new ArrayList<>();
        return lstCCs;
    }

    public void setLstCCs(List<String> lstCCs) {
        this.lstCCs = lstCCs;
    }

    public List<String> getLstBCCs() {
        if (lstBCCs == null) lstBCCs = new ArrayList<>();
        return lstBCCs;
    }

    public void setLstBCCs(List<String> lstBCCs) {
        this.lstBCCs = lstBCCs;
    }
}
