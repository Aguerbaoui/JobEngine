package io.licensemanager.LicenseManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.je.utilities.log.JELogger;
import io.licensemanager.common.GeneralKeys;
import io.licensemanager.common.SIOTHLicenseRequest;
import io.licensemanager.common.SIOTHLicenseResponse;
import io.licensemanager.common.enums.SIOTHLicenseStatus;
import io.licensemanager.eventlistener.LicenseStatusChangeHandler;
import io.licensemanager.eventlistener.LicenseStatusListener;
import io.licensemanager.utilities.InitResponse;
import io.licensemanager.utilities.LicenseMessages;
import io.licensemanager.utilities.LicenseUtilities;
import utils.log.LogCategory;
import utils.zmq.ZMQRequester;

import java.net.InetAddress;

public class ClientLicenseManager {
    static ZMQRequester objZMQRequest;
    static int requestTimeout;
    static ObjectMapper objectMapper = new ObjectMapper();
    static boolean isRegistered = false;

    static SIOTHLicenseStatus siothlicenseStatus = SIOTHLicenseStatus.Corrupted;

    private ClientLicenseManager() {

    }

    public static void initListeners(int featureCode) {
        LicenseStatusWatcher listener = new LicenseStatusWatcher(featureCode, objZMQRequest);
        Thread getLicenseStatusThread = new Thread(listener);
        getLicenseStatusThread.setName("GetLicenseStatusThread");
        getLicenseStatusThread.start();

    }

    public static InitResponse initializeLicense(String licenseManagerUrl, int featureCode,
                                                 int tags/* , out SIOTHLicenseStatus licenseStatus, out String strError */) {
        String strError = "";

        try {

            // =====> Ini ZMQ Request
            objZMQRequest = new ZMQRequester(licenseManagerUrl);

            if (objZMQRequest != null) {

                /* TODO : The requested machine attribute take the hash value of the machine biosId */
                // Retrieve the Name of HOST
                String hostName = InetAddress.getLocalHost().getHostName();
                // Get IP Address
                String myIP = InetAddress.getLocalHost().getHostAddress();

                String myProcessName = "JobEngine"; // Process.GetCurrentProcess().ProcessName;

                SIOTHLicenseRequest objSIOTHLicenseRequest = new SIOTHLicenseRequest("GetAuthorization", myIP,
                        myProcessName, featureCode, tags);

                // ====> Encrypt
                String cipheredData = LicenseUtilities.encrypt(objectMapper.writeValueAsString(objSIOTHLicenseRequest),
                        GeneralKeys.passPhraseConnectors);

                if (cipheredData != null) {

                    String cipheredSIOTHRequest = cipheredData; /* new String(Base64Utils.encode(cipheredData)); */

                    requestTimeout = 20000;
                    String rcvData = objZMQRequest.sendRequest(cipheredSIOTHRequest, -1);// requestTimeout);

                    if (rcvData != null && !rcvData.isEmpty()) {
                        // Decrypt rcvData

                        String plainRcvResponse = LicenseUtilities.decrypt(rcvData, GeneralKeys.passPhraseConnectors);

                        if (plainRcvResponse != null) {
                            // Deserialize obj
                            SIOTHLicenseResponse objSIOTHLicenseResponse = objectMapper.readValue(plainRcvResponse,
                                    SIOTHLicenseResponse.class);

                            // FIXME
                            JELogger.debug("Remaining days : " + objSIOTHLicenseResponse.RemainingDays,
                                    LogCategory.SIOTH_APPLICATION, null, null, null);

                            if (objSIOTHLicenseResponse.bAuthorized) {
                                siothlicenseStatus = objSIOTHLicenseResponse.Status;

                                if (siothlicenseStatus == SIOTHLicenseStatus.Backdated
                                        || siothlicenseStatus == SIOTHLicenseStatus.Corrupted
                                        || siothlicenseStatus == SIOTHLicenseStatus.Expired) {
                                    return new InitResponse(false, strError, siothlicenseStatus);
                                }
                                isRegistered = true;
                                return new InitResponse(true, strError, siothlicenseStatus);

                            }
                            strError = objSIOTHLicenseResponse.Error;

                        } else {
                            strError = LicenseMessages.ERROR_RECEIVING_DATA;
                        }
                    } else {
                        strError = LicenseMessages.licenseManagerUnreachable(licenseManagerUrl);
                    }

                } else {
                    strError = LicenseMessages.ERROR_SENDING_REQUEST;
                }

            } else {

                strError = LicenseMessages.initZMQError(strError);
            }

            return new InitResponse(false, strError, siothlicenseStatus);

        } catch (Exception ex) {
            strError = LicenseMessages.initLicenseError(ex.getMessage());
            return new InitResponse(false, strError, siothlicenseStatus);

        }
    }

    public static void register(LicenseStatusListener jeLicenseStatusListener) {
        LicenseStatusChangeHandler.addListener(jeLicenseStatusListener);

    }

    public static boolean isRegistered() {
        return isRegistered;
    }

    public static void setRegistered(boolean isRegistered) {
        ClientLicenseManager.isRegistered = isRegistered;
    }

}
