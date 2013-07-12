package edu.denishamann.io;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * class for SHA generation, coppied from: <a href="http://stackoverflow.com/questions/4895523/java-string-to-sha1">http://stackoverflow.com/questions/4895523/java-string-to-sha1</a>
 * @author denis
 *
 */
public class HashUtil {

	/**
	 * 
	 * @param inputString - String to be hashed
	 * @return HexString Result of SHA1
	 */
	public static String sha1(String inputString){
		
	    String sha1 = "";
	    try
	    {
	        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
	        crypt.reset();
	        crypt.update(inputString.getBytes("UTF-8"));
	        sha1 = byteArrayToHexString(crypt.digest());
	    }
	    catch(NoSuchAlgorithmException e)
	    {
	        e.printStackTrace();
	    }
	    catch(UnsupportedEncodingException e)
	    {
	        e.printStackTrace();
	    }
		return sha1;
	}
	
	/**
	 * byte to hexstring conversion
	 * @param b
	 * @return
	 */
	public static String byteArrayToHexString(byte[] b) {
		  String result = "";
		  for (int i=0; i < b.length; i++) {
		    result +=
		          Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		  }
		  return result;
		}
	
}
