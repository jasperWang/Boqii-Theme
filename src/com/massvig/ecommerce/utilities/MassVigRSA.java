package com.massvig.ecommerce.utilities;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.Key;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.Cipher;

public class MassVigRSA {
	private RSAPublicKey publickKey;

	public MassVigRSA() {
		try {
			publickKey = (RSAPublicKey) readFromFile("key");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String GetRSAString(String str){
		String result = "";
		try {
			byte[] encbyte = encrypt(str, publickKey);
			result = getString(encbyte);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private static String getString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {

			int val = ((int) b[i]) & 0xff;
			if (val < 16) {
				sb.append("0");
			}
			sb.append(Integer.toHexString(val));
		}
		return sb.toString();
	}
	
	/**
	 * 加密,key可以是公钥，也可以是私钥
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public byte[] encrypt(String message, Key key) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(message.getBytes());
	}

	/**
	 * 从文件读取object
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	private Object readFromFile(String fileName) throws Exception {
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(fileName));
		Object obj = input.readObject();
		input.close();
		return obj;
	}
}
