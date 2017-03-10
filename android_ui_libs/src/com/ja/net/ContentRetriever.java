package com.ja.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.io.BufferedInputStream;

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
	
	private final String CRLF = "\r\n";
	
	private int recursionCount = 0;
	private static final int MAX_RECURSION = 5;
	
	private int transactionStatus = -1;
	
	private String readStream(BufferedInputStream in) throws IOException {
		
		StringBuilder results = new StringBuilder();
		boolean hasData = true;

		int pos = 0;
		
		while ( hasData ) {
			
			int len = in.available();
			byte data[] = new byte[len + 1];

			int count = in.read(data, pos, len);

			results.append(data);
			if (count > -1) {
				pos += count;
			}
			if ( results.toString().endsWith(CRLF + "." + CRLF)) {
				hasData = false;
			}
	
		}
		
		return results.toString();
	}
	
	/**
	 * simple method to get data from a url and return a 
	 * byte array 
	 * 
	 * @param url
	 * @return
	 */
	public String downloadURL(String url) throws IOException {
		
		String nurl = url;
		if ( url == null ) { 
			return null; 
		}
		
		if ( ! url.startsWith("http://") ) {
			nurl = "http://" + url;
		} 
		
		final URL furl = new URL(url);
		String result = null;
		
		if ( nurl.startsWith("https:") ) {
			HttpsURLConnection.setFollowRedirects(true);
			final HttpsURLConnection urlConnection = (HttpsURLConnection)furl.openConnection();
			urlConnection.setInstanceFollowRedirects(true);
			try {
				final BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
				result = readStream(in);
			} catch(Exception e) {

				
			} finally { 
				urlConnection.disconnect();
			}
		} else {
			HttpURLConnection.setFollowRedirects(true);
			final HttpURLConnection urlConnection = (HttpURLConnection)furl.openConnection();
			urlConnection.setInstanceFollowRedirects(true);
			try {
				final BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
				result = readStream(in);
			} catch(Exception e) {
				Log.e(CR_TAG, e.getMessage());
			} finally { 
				urlConnection.disconnect();
			}
		}		
				   
		Log.e(CR_TAG, "No results found!");
		return result;
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
