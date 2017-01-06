package com.eggdigital.rateus;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.eggdigital.android.eggrating.EggRating;

public class MainActivity extends AppCompatActivity {

    private Activity mActivity;
    private Button mButton, mButtonSpam;
    private EditText mEditTextFirst, mEditTextRetry;
    private EggRating.Configuration mConfiguration;
    private EggRating mEggRating;
    private ToggleButton mToggleRetry, mToggleDebug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button) findViewById(R.id.button_show);
        mButtonSpam = (Button) findViewById(R.id.button_spam);
        mEditTextFirst = (EditText) findViewById(R.id.edittext_criteria_days);
        mEditTextRetry = (EditText) findViewById(R.id.edittext_retry_days);
        mToggleRetry = (ToggleButton) findViewById(R.id.toggle_retry);
        mToggleDebug = (ToggleButton) findViewById(R.id.toggle_debug);
        mActivity = MainActivity.this;
        mEggRating = new EggRating(mActivity);
        mEggRating.initial(mActivity);
        mConfiguration = mEggRating.getmConfiguration();
        mConfiguration.setmTryAgain(mToggleRetry.isChecked());
        mEggRating.setmDebugMode(mToggleDebug.isChecked());


        mEggRating.showAlertRateUS(new EggRating.OnSelectCallBack() {
            @Override
            public void onPositive(String tag) {

            }

            @Override
            public void onNegative(String tag) {

            }
        });
        mToggleDebug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    mEggRating.setmDebugMode(b);
                } else {
                    mEggRating.setmDebugMode(b);
                }
            }
        });

        mToggleRetry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    mConfiguration.setmTryAgain(b);
                } else {
                    mConfiguration.setmTryAgain(b);
                }
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mEditTextFirst.getText().toString().equals("")) {
                    mConfiguration.setmCriteriaLaunchTimes(Integer.valueOf(mEditTextFirst.getText().toString()));
                }
                if(!mEditTextRetry.getText().toString().equals("")) {
                    mConfiguration.setmCriteriaLaunchTimesReTry(Integer.valueOf(mEditTextRetry.getText().toString()));
                }

                mEggRating.showAlertRateUS(new EggRating.OnSelectCallBack() {
                    @Override
                    public void onPositive(String tag) {
                        Log.d("tag", tag);
                    }

                    @Override
                    public void onNegative(String tag) {
                        Log.d("tag", tag);
                    }
                });
            }
        });

        mButtonSpam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEggRating.spamLaunchDate();
            }
        });

    }

}
