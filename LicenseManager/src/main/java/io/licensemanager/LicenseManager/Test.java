package io.licensemanager.LicenseManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;

import org.springframework.util.Base64Utils;

import io.licensemanager.common.GeneralKeys;
import io.licensemanager.utilities.LicenseUtilities;

public class Test {

	public static void main(String[] args) {
		
		String test = "hello , my friend";
		//String crypted = new String(Base64Utils.encode(LicenseUtilities.encrypt(test, GeneralKeys.passPhraseConnectors)));
		String crypted = LicenseUtilities.encrypt(test, GeneralKeys.passPhraseConnectors);
		System.out.println("crypted request : "+ crypted);
		String t = LicenseUtilities.decrypt(crypted,GeneralKeys.passPhraseConnectors);
		System.out.println("decrypted data:"+t);
	}

}
