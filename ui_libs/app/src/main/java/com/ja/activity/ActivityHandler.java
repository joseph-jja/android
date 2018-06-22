package com.ja.activity;

import android.os.Handler;

public class ActivityHandler extends Handler {

	protected BaseActivity activity;

	public void setActivity(BaseActivity act) {
		this.activity = act;
	}

}
