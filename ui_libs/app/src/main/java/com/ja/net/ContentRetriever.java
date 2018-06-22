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

	private final String CR_TAG = this.getClass().getName();
	
	private final String CRLF = "\r\n";
	
	private int recursionCount = 0;
	private static final int MAX_RECURSION = 5;
	
	private int transactionStatus = -1;

	//public static void main(String args[]) throws Exception {
		
	//	ContentRetriever c = new ContentRetriever();
		
	//	c.downloadURL("http://slashdot.org");
	//}

	private String readStream(InputStream in) throws IOException {
		
		StringBuilder results = new StringBuilder();

		int i; 
		
		//System.out.println(in);
		while ( ( i = in.read() ) > -1 ) {
			
			char c = (char)i;
			results.append(c);
		}
		//System.out.println(results.toString());
		return results.toString();
	}
	
	private boolean checkStatus(int status) { 
		
		boolean redirect = false;
		
		if (status != HttpURLConnection.HTTP_OK
				|| status == HttpURLConnection.HTTP_MOVED_TEMP
				|| status == HttpURLConnection.HTTP_MOVED_PERM
				|| status == HttpURLConnection.HTTP_SEE_OTHER) {
			redirect = true;
		} else if (status != HttpsURLConnection.HTTP_OK
				|| status == HttpsURLConnection.HTTP_MOVED_TEMP
				|| status == HttpsURLConnection.HTTP_MOVED_PERM
				|| status == HttpsURLConnection.HTTP_SEE_OTHER) {
			redirect = true;
		}

		return redirect;
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
		
		recursionCount++;
		
		if ( recursionCount > 5 ) { 
			
			return null;
		}
		
		Log.v(CR_TAG, "In the dowloader!");
		
		if ( nurl.startsWith("https:") ) {
			final HttpsURLConnection urlConnection = (HttpsURLConnection)furl.openConnection();
			urlConnection.setInstanceFollowRedirects(true);
			HttpsURLConnection.setFollowRedirects(true);
			if ( checkStatus(urlConnection.getResponseCode()) ) { 
				// get redirect url from "location" header field
				String newUrl = urlConnection.getHeaderField("Location");
				return downloadURL(newUrl);	
			}
			try {
				final InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				result = readStream(in);
			} catch(Exception e) {
				Log.e(CR_TAG, e.getMessage());				
			} finally { 
				urlConnection.disconnect();
			}
		} else {
			final HttpURLConnection urlConnection = (HttpURLConnection)furl.openConnection();
			urlConnection.setInstanceFollowRedirects(true);
			HttpURLConnection.setFollowRedirects(true);
			if ( checkStatus(urlConnection.getResponseCode()) ) { 
				// get redirect url from "location" header field
				String newUrl = urlConnection.getHeaderField("Location");
				return downloadURL(newUrl);	
			}
			try {
				final InputStream in = new BufferedInputStream(urlConnection.getInputStream());
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
