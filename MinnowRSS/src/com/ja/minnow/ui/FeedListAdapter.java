package com.ja.minnow.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ja.database.Table;
import com.ja.minnow.Constants;
import com.ja.minnow.MinnowRSS;
import com.ja.minnow.R;
import com.ja.minnow.listeners.FeedDataListener;
import com.ja.minnow.services.FeedsService;
import com.ja.minnow.tables.FeedsTableData;
import com.ja.screenhandler.TableListViewAdapter;

public class FeedListAdapter {

	private static final List<Integer> feedIds = new ArrayList<Integer>();
	
	public void updateFeedList(MinnowRSS activity, List<Table> results) {

		final List<Table> feeds = ( results != null ) ? results : new ArrayList<Table>();
		final int count = feeds.size();
		
		((TextView)activity.findViewById(R.id.feeds_title)).setText("Feed Count: (" + count + ")");

		feedIds.clear();
		final ListView feedList = (ListView)activity.findViewById(R.id.feeds_list_rows);
		feedList.setAdapter( new FeedAdapter(activity, R.layout.feeds_row, feeds) );			

		activity.registerForContextMenu(feedList);
		final int pos = Constants.getFeedsservice().getFeedsListPosition();
		if ( pos > 0 ) { 
			feedList.setSelection(pos);
		}
		feedList.setOnItemClickListener(new FeedDataListener(activity));
	}
	
	/**
	 * @return the feedids
	 */
	public static List<Integer> getFeedids() {
		return feedIds;
	}
	
	private static class FeedViewHolder {
		
		public TextView nameView;
		public TextView countView;
		public TextView idView;
		public TextView lastTimeView;
		public ImageView imageView;
	}

	private static class FeedAdapter extends TableListViewAdapter {

		public FeedAdapter(Context activity, int layoutId, List<Table> tables) {
			super(activity, layoutId, tables);
			Log.v("FeedsEditor", "Constructor called");
		}

		@Override
		protected void mapTable(Table table, View listRowView) {
			
			final String name = (String)( ( table.getColumnValue(FeedsTableData.NAME_COL) != null ) ? 
												table.getColumnValue(FeedsTableData.NAME_COL) : "") ;
			final Object cResult = table.getColumnValue(FeedsTableData.FEEDCOUNT_COL);
			final String count = ( cResult != null ) ? " ("+ (Integer)table.getColumnValue(FeedsTableData.FEEDCOUNT_COL) + " articles)" : "(0 articles)";
			
			final String lastUpdateDate = (String)( ( table.getColumnValue(FeedsTableData.LASTUPDATEDATE_COL) != null ) ? 
												table.getColumnValue(FeedsTableData.LASTUPDATEDATE_COL) : "" );
			final Date lastTime = new Date();
			Log.v("FeedsEditor", "last update date is " + lastUpdateDate);
			if ( lastUpdateDate != null && lastUpdateDate.length() > 0 ) {
				lastTime.setTime( Long.parseLong(lastUpdateDate) );
			}
			Log.v("FeedsEditor", "Got back results = " + count);
			
			Bitmap image = null; 		
			if ( table.getColumnValue(FeedsTableData.IMAGE_COL) != null ) {
				final byte imageData[] = (byte[])table.getColumnValue(FeedsTableData.IMAGE_COL);
				image = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);				
			}
			
			// db id
			if ( getFeedids().size() < this.getCount() ) {
				getFeedids().add( table.getId() );
			}

			FeedViewHolder holder = null;
			if ( listRowView.getTag() == null ) {
				holder = new FeedViewHolder(); 
				holder.nameView = ((TextView)listRowView.findViewById(R.id.feeds_row_name));
				holder.countView = ((TextView)listRowView.findViewById(R.id.feeds_row_count));
				holder.lastTimeView = ((TextView)listRowView.findViewById(R.id.feeds_row_count_lud));
				holder.idView = ((TextView)listRowView.findViewById(R.id.feeds_row_id));
				holder.imageView = (ImageView)listRowView.findViewById(R.id.feeds_image);
				holder.imageView.setMaxWidth(FeedsService.MAX_IMAGE_WIDTH);
				holder.imageView.setMaxHeight(FeedsService.MAX_IMAGE_HEIGHT);
				listRowView.setTag(holder);
			} else {
				holder = (FeedViewHolder)listRowView.getTag();
			}
			
			holder.nameView.setText(name);
			holder.countView.setText(count);
			
			// format time
			final Calendar now = Calendar.getInstance();
			now.setTime(lastTime);
			
			final int hour = ( now.get(Calendar.HOUR) == 0 ) ? 12 : now.get(Calendar.HOUR);
			final String minutes = ( now.get(Calendar.MINUTE) < 10 ) 
					? "0" + Integer.valueOf(now.get(Calendar.MINUTE)).toString() 
							: Integer.valueOf(now.get(Calendar.MINUTE)).toString();
			holder.lastTimeView.setText((now.get(Calendar.MARCH) + 1) + "/" 
					+ now.get(Calendar.DAY_OF_MONTH) + "/"
					+ now.get(Calendar.YEAR) + " "
					+ hour + ":" + minutes 
					+ " " + ( ( now.get(Calendar.AM_PM) == Calendar.AM ) ? "AM" : "PM" ) );
			holder.idView.setText(Integer.toString(table.getId()));
			
			if ( image == null ) { 
				holder.imageView.setImageResource(R.drawable.default_feed_icon);
			} else {
				holder.imageView.setImageBitmap(image);
			}
		}
	}
}
