package com.pancai.projectmanagement.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;

import com.pancai.projectmanagement.R;
import com.pancai.projectmanagement.util.L;
import com.pancai.projectmanagement.util.NetUtil;
import com.pancai.projectmanagement.util.ServerOperations;
import com.pancai.projectmanagement.util.SharePreferenceLoginUtil;
import com.pancai.projectmanagement.app.ApplicationLevel;
import com.pancai.projectmanagement.util.T;

import org.json.JSONObject;

import java.util.Timer;

public class WelcomeActivity extends Activity{
    private final static int LOGIN_SUCCESS = 1;
    private final static int LOGIN_FAIL = -1;
    private SharePreferenceLoginUtil spLoginUtil;
    private Handler handler;
    private Long delayShowDuration=5000L;
    private String id="";
    private String password="";
    private Boolean isLogin = false;
    private LoginTask loginTask;
    private ApplicationLevel mApplication;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOGIN_FAIL:
                    T.showShort(WelcomeActivity.this, "登录超时");
                    //add terminating login thread
                    loginTask.cancel(true);
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
                loginTask.cancel(true);
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
        mApplication = ApplicationLevel.getInstance();
        spLoginUtil=mApplication.getSpUtil();

        if(!NetUtil.isNetConnected(this)){
            T.showShort(this, R.string.net_error_tip);
            isLogin=false;
        }
        else {
            id = spLoginUtil.getUserId();
            password = spLoginUtil.getPassword();
            //start login process, create a new thread to login
            if(id.length()!=0 && password.length()!=0){
                loginTask = new LoginTask();
                loginTask.execute();
            }
        }
        handler = new Handler();
        handler.postDelayed(startAct, delayShowDuration);
    }

    public class LoginTask extends AsyncTask<Void, Integer, Void> {
        ServerOperations serverOperations = new ServerOperations();
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            serverOperations.login(id, password);
            publishProgress(serverOperations.status);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values[0]==1) { //登陆成功
                try {
                    JSONObject jsonObjectLogin = new JSONObject(serverOperations.returnResult);
                    mApplication.loginUser.userId=jsonObjectLogin.getString("userId");
                    mApplication.loginUser.userPreviledge = jsonObjectLogin.getString("userPreviledge");
                    isLogin=true;
                }catch(Exception e){
                    L.i(e.getMessage());
                }
            }
            else
                mHandler.sendEmptyMessage(values[0]);
        }
    }

}
