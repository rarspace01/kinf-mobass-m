package edu.denishamann.io;

/*********************************************************************
 *	Helper Class for HTTP gets
 * 
 * @author
 *    Denis Hamann
 * @version
 *    30.11.2010
 * @license
 *    EUPL
 *    
 *********************************************************************/


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class HttpHelper {

	public static String getPage(String surl){
		
		//Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.com", 80));

		
		String sPage="";
		
		URLConnection connection = null;		
		// Zerlegt einen String und f�gt jeweils ein Zeilenende ein
		try {			
			URL urlpage=new URL(surl);			
			connection = urlpage.openConnection();
			//connection = urlpage.openConnection(proxy);
			connection.setDoOutput(true);			
                        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			  
			  String line = null;
			  
			  while ((line = rd.readLine()) != null) {
				  sPage+=line+System.getProperty("line.separator");
				} 		
			  
		} catch (ConnectException e) {			
			e.printStackTrace();
		} catch (UnknownHostException e) {			
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		return sPage;
	}	
	
public static String getBasicAuthPage(String surl, String usr, String pwd){
		
		String sPage="";
		
		String userPassword = usr + ":" + pwd;
		String encoding = Base64Converter.encode (userPassword.getBytes());
		URLConnection connection = null;		
		// Zerlegt einen String und f�gt jeweils ein Zeilenende ein
		try {			
			URL urlpage=new URL(surl);			
			connection = urlpage.openConnection();
			connection.setRequestProperty("Authorization", "Basic " + encoding);
			connection.setDoOutput(true);			
                        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			  
			  String line = null;
			  
			  while ((line = rd.readLine()) != null) {
				  sPage+=line+System.getProperty("line.separator");
				} 		
			  
		} catch (ConnectException e) {			
			
		} catch (UnknownHostException e) {			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		return sPage;
	}	



}