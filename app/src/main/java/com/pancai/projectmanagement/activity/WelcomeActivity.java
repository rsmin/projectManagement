package com.pancai.projectmanagement.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;

import com.pancai.projectmanagement.R;
import com.pancai.projectmanagement.util.NetUtil;
import com.pancai.projectmanagement.util.SharePreferenceLoginUtil;
import com.pancai.projectmanagement.app.ApplicationLevel;
import com.pancai.projectmanagement.util.T;
import com.google.gson.Gson;

import java.util.Timer;

public class WelcomeActivity extends Activity{
    private static final int LOGIN_OUT_TIME = 0;
    private SharePreferenceLoginUtil spLoginUtil;
    private Handler handler;
    private Long delayShowDuration=5000L;
    private String id="";
    private String password="";
    private Boolean isLogin = false;
    private LoginOutTimeProcess mLoginOutTimeProcess;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOGIN_OUT_TIME:
                    if (mLoginOutTimeProcess != null
                            && mLoginOutTimeProcess.running)
                        mLoginOutTimeProcess.stop();
                    T.showShort(WelcomeActivity.this, "登录超时");
                    //add terminating login thread

                    //end of login thread
                    isLogin=false;
                    break;
                default:
                    break;
            }
        }

    };

    Runnable startAct = new Runnable(){
        @Override
        public void run() {
            if(isLogin==false) {
                startActivity(new Intent(WelcomeActivity.this, FirstSetActivity.class));;
            }
            else {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            }
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        spLoginUtil=ApplicationLevel.getInstance().getSpUtil();

        if(!NetUtil.isNetConnected(this)){
            T.showShort(this, R.string.net_error_tip);
            isLogin=false;
        }
        else {
            id = spLoginUtil.getUserId();
            password = spLoginUtil.getPassword();
            //start login process, create a new thread to login

            //end of login process
            if (mLoginOutTimeProcess != null && !mLoginOutTimeProcess.running)
                mLoginOutTimeProcess.start();
        }
        handler = new Handler();
        handler.postDelayed(startAct, delayShowDuration);
    }

    // 登录超时处理线程
    class LoginOutTimeProcess implements Runnable {
        public boolean running = false;
        private long startTime = 0L;
        private Thread thread = null;
        public Long loginTimeCost=0L;

        LoginOutTimeProcess() {
        }

        public void run() {
            while (true) {
                if (!this.running)
                    return;
                if (System.currentTimeMillis() - this.startTime > 2 * 1000L) {
                    mHandler.sendEmptyMessage(LOGIN_OUT_TIME);
                }
                try {
                    Thread.sleep(10L);
                } catch (Exception localException) {
                }
            }
        }

        public void start() {
            try {
                T.showShort(WelcomeActivity.this, "登陆中...");
                this.thread = new Thread(this);
                this.running = true;
                this.startTime = System.currentTimeMillis();
                this.thread.start();
            } finally {
            }
        }

        public void stop() {
            try {
                this.running = false;
                this.thread = null;
                this.loginTimeCost=System.currentTimeMillis() - this.startTime;
                this.startTime = 0L;
            } finally {
            }
        }
    }
}
