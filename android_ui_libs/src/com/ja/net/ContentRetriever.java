package com.ja.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/** 
 * simple class to make downloading from the Internet easier

 * @author Joseph Acosta
 *
 */
public class ContentRetriever {

	private final Class<?> _self = getClass();
	private final String CR_TAG = _self.getName();
	
	private int recursionCount = 0;
	private static final int MAX_RECURSION = 5;
	
	private int transactionStatus = -1;
	
	/**
	 * simple method to get data from a url and return a 
	 * byte array 
	 * 
	 * @param url
	 * @return
	 */
	public String downloadURL(String url) throws IOException, ClientProtocolException {
		
		String nurl = url;
		if ( url == null ) { return null; }
		if ( ! url.startsWith("http://") ) {
			nurl = "http://" + url;
		}
		
		this.transactionStatus = -1;  /// initialize status to something http does not return
		
		final DefaultHttpClient httpClient = new DefaultHttpClient();		
		final HttpGet httpGet = new HttpGet(nurl);

		final HttpResponse response = httpClient.execute(httpGet);
		this.transactionStatus = response.getStatusLine().getStatusCode();
		// handle redirects like 301, 302 and other codes
		if ( this.transactionStatus == HttpStatus.SC_OK ) {
			final HttpEntity urlData = response.getEntity();
			if ( urlData != null ) {
				final byte[] results = EntityUtils.toByteArray(urlData);
				Log.v(CR_TAG, "Http data returned is = " +  new String(results));
				return new String(results);	
			}
		} else if ( this.transactionStatus == HttpStatus.SC_MOVED_PERMANENTLY 
				|| this.transactionStatus == HttpStatus.SC_MOVED_TEMPORARILY ) {
			
			Log.d(CR_TAG, "Got status 301/302 and recursing " + this.recursionCount);
			// we'll try 5 times to download a redirect
			this.recursionCount += 1;
			if ( this.recursionCount < MAX_RECURSION ) {
				final Header headers[] = response.getAllHeaders();
				nurl = "";
				for ( Header hdr : headers ) {
					if ( hdr.getName().equalsIgnoreCase("location") ) {
						nurl = hdr.getValue();
						break;
					}
				}
				if ( ! nurl.equalsIgnoreCase("") ) {
					return downloadURL(nurl);
				}
			}
		}
		Log.e(CR_TAG, "No results found!");
		return null;
	}
	
	public Bitmap downloadImage(String imageURL) throws IOException  {
		
		String nurl = imageURL;
		if ( imageURL == null ) { return null; }
		if ( ! imageURL.startsWith("http://") ) {
			nurl = "http://" + imageURL;
		}		
		final URL imageFileURL = new URL(nurl);
		
		HttpURLConnection conn= (HttpURLConnection)imageFileURL.openConnection();
		InputStream is = conn.getInputStream();

		return BitmapFactory.decodeStream(is);		
	}

	/**
	 * @return the transactionStatus
	 */
	public int getTransactionStatus() {
		return transactionStatus;
	}

	/**
	 * @param recursionCount the recursionCount to set
	 */
	public void setRecursionCount(int recursionCount) {
		this.recursionCount = recursionCount;
	}

	/**
	 * @return the recursionCount
	 */
	public int getRecursionCount() {
		return recursionCount;
	}
}
