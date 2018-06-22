package com.ja.minnow.ui;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ja.database.Table;
import com.ja.minnow.Constants;
import com.ja.minnow.MinnowRSS;
import com.ja.minnow.R;
import com.ja.minnow.services.FeedDataService;
import com.ja.minnow.tables.FeedDataTableData;
import com.ja.screenhandler.TableListViewAdapter;

public class FeedDataListAdapter {

	public void processData(MinnowRSS activity) {
		
		int feedDataID = Constants.getFeeddataservice().getFeedID();
		final List<Table> tables = Constants.getFeeddataservice().getAllFeedData(activity.getDbAdapter(), feedDataID);
		
		// set activity to the feed list
		activity.setContentView(R.layout.feed_data_list);
		
		if ( tables != null && tables.get(0) != null && tables.get(0).getColumnValue("image") != null ) {
			final byte imageData[] = (byte[]) tables.get(0).getColumnValue("image");
			final Bitmap image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
			ImageView imageView = (ImageView)activity.findViewById(R.id.feeds_data_list_image);
			imageView.setImageBitmap(image);	
			imageView.setMaxWidth(FeedDataService.MAX_IMAGE_WIDTH);
			imageView.setMaxHeight(FeedDataService.MAX_IMAGE_HEIGHT);
		}

		((TextView) activity.findViewById(R.id.feed_data_list_title)).setText(Constants.getFeeddataservice().getFeedName());
		((TextView) activity.findViewById(R.id.feed_data_list_count)).setText(Constants.getFeeddataservice().getFeedCount());

		// find the list view
		final ListView feedList = (ListView) activity.findViewById(R.id.feed_data_list_rows);

		// now do the row thing
		feedList.setAdapter(new FeedContentAdapter(activity,R.layout.feed_data_list_row, tables));
		activity.registerForContextMenu(feedList);
		feedList.setOnItemClickListener(activity.getWebView());

		final int listPostion = Constants.getFeeddataservice().getListPosition();
		feedList.setSelection(listPostion);
	}

	private static class FeedDataViewHolder {
		
		public TextView titleView;
		public TextView summaryView;
		public TextView dbidView;
		public TextView feedIdView;
	}
	
	private static class FeedContentAdapter extends TableListViewAdapter {
	
		public FeedContentAdapter(Context activity, int layoutId,
				List<Table> tables) {
			super(activity, layoutId, tables);
		}
	
		@Override
		protected void mapTable(Table table, View listRowView) {
	
			final String title = (String) table.getColumnValue(FeedDataTableData.TITLE_COL);
			final Object feedID = table.getColumnValue(FeedDataTableData.FEED_ID_COL);								
			final int dbid = table.getId();			
	
			String summary = (String) table.getColumnValue(FeedDataTableData.SUMMARY_COL);
			// clean up summary and remove the HTML code from it and cr lf and tabs
			summary = summary.replaceAll("\\<.*?>", "");
			summary = summary.replaceAll("\n", "");
			summary = summary.replaceAll("\r", "");
			summary = summary.replaceAll("\t", " ");
			summary = summary.trim();
			if (summary.startsWith("#")) {
				final int indexOfSpace = summary.indexOf(' ');
				// trim gizmodo tags
				summary = summary.substring(indexOfSpace);
				summary = summary.trim();
			}
			summary = Html.fromHtml(summary).toString();
			
			summary = (summary != null && summary.length() > 100) ? summary.substring(0, 100) : summary;
			summary = (summary != null) ? summary.substring(0, summary.lastIndexOf(" ")) + " ..." : summary;
									
			FeedDataViewHolder holder = null;
			if ( listRowView.getTag() == null ) { 
				holder = new FeedDataViewHolder();
				holder.titleView = ((TextView) listRowView.findViewById(R.id.feed_data_list_row_name)); 
				holder.summaryView = ((TextView) listRowView.findViewById(R.id.feed_data_list_row_summary)); 
				holder.dbidView = ((TextView) listRowView.findViewById(R.id.feed_data_list_row_feed_id)); 
				holder.feedIdView = ((TextView) listRowView.findViewById(R.id.feed_data_list_row_id)); 
				
				listRowView.setTag(holder);
			} else {
				holder = (FeedDataViewHolder)listRowView.getTag();
			}
				
			holder.titleView.setText(Html.fromHtml(title).toString());
			holder.summaryView.setText(summary);
			holder.dbidView.setText(feedID.toString());
			holder.feedIdView.setText(Integer.toString(dbid));	
		}
	}
}
