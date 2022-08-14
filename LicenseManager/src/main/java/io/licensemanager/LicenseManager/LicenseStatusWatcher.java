package io.licensemanager.LicenseManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.utilities.log.JELogger;
import io.licensemanager.common.GeneralKeys;
import io.licensemanager.common.SIOTHLicenseRequest;
import io.licensemanager.common.SIOTHLicenseResponse;
import io.licensemanager.common.enums.SIOTHLicenseStatus;
import io.licensemanager.eventlistener.LicenseStatusChangeHandler;
import io.licensemanager.utilities.LicenseUtilities;
import utils.log.LogCategory;
import utils.log.LogSubModule;
import utils.log.LoggerUtils;
import utils.zmq.ZMQRequester;

import java.net.InetAddress;

public class LicenseStatusWatcher implements Runnable {

    static ObjectMapper objectMapper = new ObjectMapper();
    static ZMQRequester objZMQRequest;
    static SIOTHLicenseStatus siothlicenseStatus;
    int featureCode;
    boolean listening = true;

    public LicenseStatusWatcher(int featureCode, ZMQRequester objZMQRequest) {
        this.featureCode = featureCode;
        LicenseStatusWatcher.objZMQRequest = objZMQRequest;
    }

    public boolean isListening() {
        return listening;
    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }

    @Override
    public void run() {

        int count = 0;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            LoggerUtils.logException(e);
        }
        int requestTimeout = 20000;
        while (listening) {
            try {

                // ===> get ip address
                String hostName = InetAddress.getLocalHost().getHostName(); // Retrieve the Name of HOST
                String myIP = InetAddress.getLocalHost().getHostAddress();
                //String myIP = InetAddress.getAllByName(hostName)[0].toString();

                SIOTHLicenseRequest objSIOTHLicenseRequest = ClientLicenseManager.isRegistered ? new SIOTHLicenseRequest("GetStatus", myIP, featureCode) :
                        new SIOTHLicenseRequest("GetAuthorization", myIP, "JobEngine", featureCode, 0);

                //====> Encrypt
                String cipheredData = LicenseUtilities.encrypt(objectMapper.writeValueAsString(objSIOTHLicenseRequest), GeneralKeys.passPhraseConnectors);
                if (cipheredData != null) {
                    String cipheredSIOTHRequest = cipheredData;
                    String rcvData = objZMQRequest.sendRequest(cipheredSIOTHRequest); // FIXME requestTimeout);
                    if (rcvData != null && !rcvData.isEmpty()) {
                        //Decrypt rcvData
                        String plainRcvResponse = LicenseUtilities.decrypt(rcvData,
                                GeneralKeys.passPhraseConnectors);

                        if (plainRcvResponse != null) {
                            //Deserialize obj
                            SIOTHLicenseResponse objSIOTHLicenseResponse = objectMapper.readValue(plainRcvResponse,
                                    SIOTHLicenseResponse.class);

                            SIOTHLicenseStatus newSIOTHicenseStatus = objSIOTHLicenseResponse.Status;
                            if (newSIOTHicenseStatus == SIOTHLicenseStatus.Full || newSIOTHicenseStatus == SIOTHLicenseStatus.Demo) {
                                ClientLicenseManager.setRegistered(true);
                            }

                            if (newSIOTHicenseStatus != siothlicenseStatus) {
                                siothlicenseStatus = newSIOTHicenseStatus;
                                LicenseStatusChangeHandler.invoke(siothlicenseStatus);
                            }

                            if (objSIOTHLicenseResponse.Error != null && !objSIOTHLicenseResponse.Error.isEmpty()) {
                                JELogger.error("Checking License status returns error : " + objSIOTHLicenseResponse.Error,
                                        LogCategory.SIOTH_APPLICATION, "", null, "");
                            }

                        } else {
                            siothlicenseStatus = SIOTHLicenseStatus.Corrupted;
                            LicenseStatusChangeHandler.invoke(siothlicenseStatus);
                        }

                    } else {

                        //not reachable
                        //retry 6 times
                        if (count < 6) {
                            count++;
                        } else {
                            siothlicenseStatus = SIOTHLicenseStatus.Corrupted;

                            LicenseStatusChangeHandler.invoke(siothlicenseStatus);
                        }

                    }

                }

                Thread.sleep(10000);
            } catch (Exception ex) {
                siothlicenseStatus = SIOTHLicenseStatus.Corrupted;

                LicenseStatusChangeHandler.invoke(siothlicenseStatus);

            }

        }

    }
}
