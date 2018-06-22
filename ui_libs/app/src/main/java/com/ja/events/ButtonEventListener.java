package com.ja.events;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ja.activity.BaseActivity;
import com.ja.database.DatabaseAdapter;

public abstract class ButtonEventListener implements View.OnClickListener { 

	protected static final List<Integer> buttons = new ArrayList<Integer>();
	
	protected BaseActivity activity;
    protected DatabaseAdapter db;

    private final Class<?> _self = getClass();
    protected final String BEL_TAG = _self.getName();

	public ButtonEventListener(BaseActivity act) { 
        this.activity = (BaseActivity)act; 
        this.db = ((BaseActivity)act).getDbAdapter();
    }
	
    public void attachClickEventForView() { 
        List<Integer> buttonList = getButtonList();
        int buttonCount = buttonList.size();

        Log.d(BEL_TAG, "Button count: " + buttonCount);
        Button buttons[] = new Button[buttonCount];
        for ( int i = 0; i < buttonCount; i+=1 ) { 
        	try { 
        		buttons[i] = (Button)this.activity.findViewById(buttonList.get(i).intValue());
        		Log.d(BEL_TAG, "Button at i: " + buttons[i] + ", " + i);
        		if ( buttons[i] != null ) { 
        			buttons[i].setOnClickListener(this);
        		}
        	} catch (Exception ex) {
        		ex.printStackTrace();
        	}
        }
    }
    
    public abstract List<Integer> getButtonList();
    
}
