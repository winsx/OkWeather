package net.gility.okweather.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * @author Alimy
 */

public class LaunchActivity extends Activity {

    private SwitchHandler mHandler = new SwitchHandler(Looper.getMainLooper(), this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler.sendEmptyMessageDelayed(1, 1000);
    }

    class SwitchHandler extends Handler {
        private WeakReference<LaunchActivity> mWeakReference;

        public SwitchHandler(Looper mLooper, LaunchActivity activity) {
            super(mLooper);
            mWeakReference = new WeakReference<LaunchActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Intent i = new Intent(LaunchActivity.this, MainActivity.class);
            LaunchActivity.this.startActivity(i);
            //activity切换的淡入淡出效果
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            mWeakReference.get().finish();
        }
    }
}
