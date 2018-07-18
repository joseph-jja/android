package com.ja.sbi.utils;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ja.sbi.R;
import com.ja.sbi.SimpleBARTInfo;

import java.util.List;

public class SimpleSpinner {

    private final String LOG_NAME = this.getClass().getName();

    private Spinner dropdown;
    private SimpleBARTInfo context;
    private String selectedText;

    public SimpleSpinner(SimpleBARTInfo sbiContext, int spinnerID,
                         String defaultText, final List<String> spinnerData,
                         final StationListSpinnerIface methodImpl) {

        dropdown = (Spinner) sbiContext.findViewById(spinnerID);

        context = sbiContext;

        ArrayAdapter sourceAdapter = new ArrayAdapter<String>(sbiContext, android.R.layout.simple_spinner_item, spinnerData);
        sourceAdapter.setDropDownViewResource(R.layout.spinner_item);
        this.dropdown.setAdapter(sourceAdapter);

        this.dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                Log.d(LOG_NAME, "Position is everything: "
                        + position + " data = "
                        + spinnerData.get(position));

                selectedText = spinnerData.get(position);

                methodImpl.processSpinnerListData(context);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public String getSelectText() {
        return selectedText;
    }
}
