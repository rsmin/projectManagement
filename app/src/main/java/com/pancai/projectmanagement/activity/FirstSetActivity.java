package com.pancai.projectmanagement.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.pancai.projectmanagement.util.L;
import com.pancai.projectmanagement.util.ServerOperations;
import com.pancai.projectmanagement.util.SharePreferenceLoginUtil;
import com.pancai.projectmanagement.app.ApplicationLevel;
import com.pancai.projectmanagement.R;

import org.json.JSONObject;

public class FirstSetActivity extends Activity{
    private final static int LOGIN_SUCCESS = 1;
    private final static int LOGIN_FAIL = -1;
    private EditText mUser;
    private EditText mPassword;
    private Button loginButton;
    private String userString;
    private String passwordString;
    private LoginTask loginTask;
    private SharePreferenceLoginUtil spUtil;
    private ApplicationLevel mApplication;
    private ServerOperations serverOperations;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    startActivity(new Intent(FirstSetActivity.this, MainActivity.class));
                    if(loginTask != null && loginTask.getStatus() != AsyncTask.Status.FINISHED)
                        loginTask.cancel(true);
                    FirstSetActivity.this.finish();
                    break;
                case LOGIN_FAIL:
                    new AlertDialog.Builder(FirstSetActivity.this)
                            .setIcon(getResources().getDrawable(R.drawable.login_error_icon))
                            .setTitle("登录失败")
                            .setMessage("用户名或者密码不正确，\n请检查后重新输入！")
                            .create().show();
                default:
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_set);
        init();
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getEditValue();
                if("".equals(userString) || "".equals(passwordString))   //判断 帐号和密码
                {
                    new AlertDialog.Builder(FirstSetActivity.this)
                            .setIcon(getResources().getDrawable(R.drawable.login_error_icon))
                            .setTitle("登录错误")
                            .setMessage("用户名或者密码不能为空，\n请输入后再登录！")
                            .create().show();
                }
                else {
                    loginTask = new LoginTask();
                    loginTask.execute();
                }
            }
        });
    }

    private void init(){
        mUser=(EditText)findViewById(R.id.login_user_edit);
        mPassword=(EditText)findViewById(R.id.login_passwd_edit);
        loginButton=(Button)findViewById(R.id.login_login_btn);
        mApplication = ApplicationLevel.getInstance();
        spUtil=mApplication.getSpUtil();

    }

    private void getEditValue(){
        userString=mUser.getText().toString();
        passwordString=mPassword.getText().toString();
    }

    public class LoginTask extends AsyncTask<Void, Integer, Void> {
        private ProgressDialog dialog = null;
        ServerOperations serverOperations = new ServerOperations();

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            dialog = ProgressDialog.show(FirstSetActivity.this,"登陆状态","正在登录，请稍后...",false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            serverOperations.login(userString, passwordString);
            publishProgress(serverOperations.status);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.dismiss();
            if (values[0]==1) { //链接成功
                try {
                    JSONObject jsonObjectLogin = new JSONObject(serverOperations.returnResult);
                    Boolean isSuccess=jsonObjectLogin.getBoolean("isSuccess");
                    if(isSuccess) {  //登陆成功
                        mApplication.loginUser.userId = userString;
                        mApplication.loginUser.userPreviledge = jsonObjectLogin.getString("privilige");
                        mHandler.sendEmptyMessage(LOGIN_SUCCESS);
                    }
                    else{
                        mHandler.sendEmptyMessage(LOGIN_FAIL);
                    }
                }catch(Exception e){
                    L.i(e.getMessage());
                }
            }
            else
                mHandler.sendEmptyMessage(values[0]);
        }
    }
}
