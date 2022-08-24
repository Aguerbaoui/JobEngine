package io.licensemanager.common;

public class Constants {

    static final int KEYSIZE = 128;// This constant is used to determine the keysize of the encryption algorithm in bits.
    // We divide this by 8 within the code below to get the equivalent number of bytes.
    static final int DERIVATION_ITERATIONS = 1000; // This constant determines the number of iterations for the password bytes generation function.
    static final int CHECK_LICENSE_STATUS_RATE = 10000;

    static final int BACK_DATING_TOLERANCE = 4;

    static final int TIMEOUTGETLICENSE = 360000;

}
