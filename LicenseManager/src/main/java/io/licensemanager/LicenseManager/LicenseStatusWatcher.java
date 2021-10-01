package io.licensemanager.LicenseManager;

import java.net.InetAddress;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.licensemanager.common.GeneralKeys;
import io.licensemanager.common.SIOTHLicenseRequest;
import io.licensemanager.common.SIOTHLicenseResponse;
import io.licensemanager.common.enums.SIOTHLicenseStatus;
import io.licensemanager.eventlistener.LicenseStatusChangeHandler;
import io.licensemanager.utilities.LicenseUtilities;
import utils.zmq.ZMQRequester;

public class LicenseStatusWatcher implements Runnable {

	int featureCode;
	static ObjectMapper objectMapper = new ObjectMapper();
	static ZMQRequester objZMQRequest;
	boolean listening;

	static SIOTHLicenseStatus siothlicenseStatus;

	public LicenseStatusWatcher(int featureCode, ZMQRequester objZMQRequest) {
		this.featureCode = featureCode;
		this.objZMQRequest = objZMQRequest;
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
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
           int  requestTimeout = 20000;
            while (listening)
            {
                try
                {
                   
                	// ===> get ip address
    				String hostName = InetAddress.getLocalHost().getHostName(); // Retrieve the Name of HOST
    				String myIP = InetAddress.getLocalHost().getHostAddress();
    				//String myIP = InetAddress.getAllByName(hostName)[0].toString();
                    SIOTHLicenseRequest objSIOTHLicenseRequest = ClientLicenseManager.isRegistered? new SIOTHLicenseRequest("GetStatus", myIP, featureCode): new SIOTHLicenseRequest("GetAuthorization", myIP,
                    		"JobEngine", featureCode, 0);

                    //====> Encrypt 
                    String cipheredData = LicenseUtilities.encrypt(objectMapper.writeValueAsString(objSIOTHLicenseRequest), GeneralKeys.passPhraseConnectors);
                    if (cipheredData!=null)
                    {
                        String cipheredSIOTHRequest = cipheredData;
                        String rcvData =  objZMQRequest.sendRequest( cipheredSIOTHRequest,requestTimeout);
                            if (rcvData != null && !rcvData.isEmpty())
                            {
                                //Decrypt rcvData
                            	String plainRcvResponse = LicenseUtilities.decrypt(rcvData,
        								GeneralKeys.passPhraseConnectors);

                                if (plainRcvResponse != null)
                                {
                                    //Deserialize obj
                                	SIOTHLicenseResponse objSIOTHLicenseResponse = objectMapper.readValue(plainRcvResponse,
        									SIOTHLicenseResponse.class);
                                    SIOTHLicenseStatus newSIOTHicenseStatus = objSIOTHLicenseResponse.Status;
                                	if(newSIOTHicenseStatus==SIOTHLicenseStatus.Full || newSIOTHicenseStatus == SIOTHLicenseStatus.Demo)
                                	{
                                		ClientLicenseManager.setRegistered(true);
                                	}

                                    if (newSIOTHicenseStatus != siothlicenseStatus)
                                    {
                                        siothlicenseStatus = newSIOTHicenseStatus;
                                        LicenseStatusChangeHandler.invoke(siothlicenseStatus);
                                    }

                                }
                                else
                                {
                                    siothlicenseStatus = SIOTHLicenseStatus.Corrupted;
                                    LicenseStatusChangeHandler.invoke(siothlicenseStatus);
                                }

                            }
                            else
                            {

                                //not reachable
                                //retry 6 times
                                if (count < 6)
                                {
                                    count++;
                                }
                                else
                                {
                                    siothlicenseStatus = SIOTHLicenseStatus.Corrupted;

                                    LicenseStatusChangeHandler.invoke(siothlicenseStatus);
                                }

                               

                            }


                        }
                        
                    
              
                    Thread.sleep(10000); 
                }
                catch (Exception ex)
                {
                    siothlicenseStatus = SIOTHLicenseStatus.Corrupted;

                    LicenseStatusChangeHandler.invoke(siothlicenseStatus);

                }
	
            }

	}
}
