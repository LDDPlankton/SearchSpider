package com.webspider.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

public class SpiderConnector
{
	private URL myurl;
	private HttpURLConnection myurlc;
	private String url_data;
	private String openurl;
	
	public SpiderConnector()
	{
		
	}
	
	/*
	 * This function will set a URL to spider against.
	 * 
	 * @param	myurl	The URL to spider
	 */
	public void setURL(String myurl)
	{
		this.openurl = myurl;
	}
	
	/*
	 * This function will return the URL we plan to/have scanned.
	 * 
	 * @return	String
	 */
	public String getURL()
	{
		return this.openurl;
	}
	
	/*
	 * This function will return the HTML Data, we must fetchURLData() first.
	 * 
	 * @return	String
	 */
	public String getURLData()
	{
		return this.url_data;
	}
	
	/*
	 * This function will fetch the URL data by opening a new URL connection.
	 * 
	 */
	public void fetchURLData() throws IOException, SocketTimeoutException
	{
		this.myurl = new URL(this.openurl);
		this.myurlc = (HttpURLConnection)myurl.openConnection();
		this.myurlc.setRequestMethod("GET");
		this.myurlc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0");
		this.myurlc.setUseCaches(false);
		this.myurlc.setConnectTimeout(10*1000);
		String inputLine = "";
		BufferedReader br = new BufferedReader( new InputStreamReader( myurlc.getInputStream() ) );
		while( (inputLine = br.readLine() ) != null)
		{
			this.url_data += inputLine;
		}
		
		br.close();
	}
}
