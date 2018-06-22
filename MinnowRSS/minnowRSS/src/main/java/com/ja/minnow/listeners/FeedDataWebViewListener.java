package com.ja.minnow.listeners;

import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ja.database.Table;
import com.ja.minnow.Constants;
import com.ja.minnow.MinnowRSS;
import com.ja.minnow.R;
import com.ja.minnow.tables.FeedDataTableData;

/**
 * 
 * @author Joseph Acosta
 *
 */
public class FeedDataWebViewListener implements AdapterView.OnItemClickListener {

	private final Class<?> _self = getClass();
	private final String WV_TAG = _self.getName();
	
	private MinnowRSS activity; 	
	
	public FeedDataWebViewListener(MinnowRSS activity) {
		this.activity = activity;
	}
	
	public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {

		if ( this.activity.getRefreshThread() != null && this.activity.getLocalThread() != null & this.activity.getLocalThread().isAlive() ) {
			this.activity.getRefreshThread().setSleepThread(true);
		}
		Constants.getFeeddataservice().setListPosition(position);
		Log.d(WV_TAG, "Position is everything " + position + ", " + id);

		final String idFeedData = ((TextView)view.findViewById(R.id.feed_data_list_row_id)).getText().toString();
		
		final int feedDataID = Integer.parseInt(idFeedData);
		Log.d(WV_TAG, "Feed id " + idFeedData); 
		final Table feedItem = Constants.getFeeddataservice().getFeedData(this.activity.getDbAdapter(), feedDataID);
		
		Constants.getFeeddataservice().setFeedDataID(feedDataID);
		
		updateWebView(feedItem, false);

		if ( this.activity.getRefreshThread() != null && this.activity.getLocalThread() != null & this.activity.getLocalThread().isAlive() ) {
			this.activity.getRefreshThread().setSleepThread(false);
		}
	}
	
	public void updateWebView(Table feedItem, boolean forceTextView) {
		
		final String url = (String)feedItem.getColumnValue(FeedDataTableData.URL_COL);
		Log.d(WV_TAG, "URL is " + url);
		
		// get data 
		final String feedTitle = (String)feedItem.getColumnValue(FeedDataTableData.TITLE_COL);
		final String description = (String)feedItem.getColumnValue(FeedDataTableData.SUMMARY_COL); 
		final String link = (String)feedItem.getColumnValue(FeedDataTableData.URL_COL);
		Log.d(WV_TAG, "Got description: " + description);

		if ( description.trim().startsWith("<") && ! forceTextView ) {
			// for now we embed a web view, which does not always work
			activity.setContentView(R.layout.feed_web_view);
			setWebViewData(activity, feedTitle, description, link);
		} else {
			// for now we embed a web view, which does not always work
			activity.setContentView(R.layout.feed_text_view);
			setTextViewData(activity, feedTitle, description, link);
		}
	}

	private void setTextViewData(MinnowRSS activity, String feedTitle, String summary, String link) { 
	
		final String feedName = Constants.getFeeddataservice().getFeedName();

		// set title
		final TextView title = (TextView)activity.findViewById(R.id.feed_text_view_title);	
		title.setText(feedName + ": " + Html.fromHtml(feedTitle).toString());
		
		// setup web view
		final TextView textContent = (TextView)activity.findViewById(R.id.feed_text_view_page);

		final WebView linkView = (WebView)activity.findViewById(R.id.feed_text_view_link);
		linkView.getSettings().setJavaScriptEnabled(true); 

		try { 	
			//summary = summary.replaceAll("\\<.*?>","");
			textContent.setBackgroundColor(Color.WHITE);
			textContent.setTextColor(Color.BLACK);
			textContent.setText(Html.fromHtml(summary).toString());
			linkView.loadData("<a href=\"" + link + "\">Click To View Page</a>", "text/html", "utf-8");
		} catch (Exception ex ) {
			ex.printStackTrace();
			Log.e(WV_TAG, "Exception in getting url " + ex.toString());
		}
	}
	
	private void setWebViewData(MinnowRSS activity, String feedTitle, String summary, String link) { 
		
		final String feedName = Constants.getFeeddataservice().getFeedName();

		// set title
		final TextView title = (TextView)activity.findViewById(R.id.feed_web_view_title);	
		title.setText(feedName + ": " + Html.fromHtml(feedTitle).toString());
		
		// setup web view
		final WebView webview = (WebView)activity.findViewById(R.id.feed_web_view_web_page);
		webview.getSettings().setJavaScriptEnabled(true); 

		final WebView linkView = (WebView)activity.findViewById(R.id.feed_web_view_link);
		linkView.getSettings().setJavaScriptEnabled(true); 

		try { 				
			webview.loadData(summary, "text/html", "utf-8");
			linkView.loadData("<a href=\"" + link + "\">Click To View Page</a>", "text/html", "utf-8");
		} catch (Exception ex ) {
			ex.printStackTrace();
			Log.e(WV_TAG, "Exception in getting url " + ex.toString());
		}
	}
}
