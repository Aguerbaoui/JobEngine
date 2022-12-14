package io.licensemanager.eventlistener;

import io.licensemanager.common.enums.SIOTHLicenseStatus;

import java.util.ArrayList;
import java.util.List;

public class LicenseStatusChangeHandler {

    static SIOTHLicenseStatus currentStatus;
    private static List<LicenseStatusListener> listeneres = new ArrayList<>();

    /*
     * static class
     */
    private LicenseStatusChangeHandler() {

    }

    public static void invoke(SIOTHLicenseStatus status) {
        if (currentStatus != status) {
            currentStatus = status;
            for (LicenseStatusListener listener : listeneres) {
                listener.onLicenseChange(currentStatus);
            }
        }
    }

    public static void addListener(LicenseStatusListener listener) {
        listeneres.add(listener);
    }

}
