package com.pancai.projectmanagement.util;

import com.pancai.projectmanagement.R;
import com.pancai.projectmanagement.app.ApplicationLevel;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

/**
 * Created by rsmin on 2016/5/23.
 */
public class ServerOperations {
    private int connTimeout;
    private static final String loginMethod = "login";
    private ApplicationLevel mApplication;
    private String serverAddress;

    public String returnResult;
    public int status = 0;

    public ServerOperations() {
        mApplication = ApplicationLevel.getInstance();
        serverAddress = mApplication.getString(R.string.server_url);
        connTimeout = Integer.parseInt(mApplication.getString(R.string.connection_out_of_time));
    }

    public void login(String userId, String password) {
        String result = null;
        returnResult = "";
        String url = serverAddress + loginMethod;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        NetConn connectToServer = new NetConn(url);
        //return serverResponse (JsonString)
        returnResult = connectToServer.HttpURLConnection_Post(jsonObject.toString());
        this.status = connectToServer.status;
    }

    private class NetConn {
        private String postURL;
        public int status = 0; //-1 连接失败， 0 连接中，1 连接成功
        private HttpURLConnection urlConn;

        public NetConn(String postURL) {
            this.postURL = postURL;
            urlConn = null;
        }

        public String HttpURLConnection_Post(String strJson) {
            String returnString = "";
            try {
                //通过openConnection 连接
                URL url = new URL(postURL);
                urlConn = (HttpURLConnection) url.openConnection();
                //设置输入和输出流
                urlConn.setDoOutput(true);
                urlConn.setDoInput(true);
                urlConn.setRequestMethod("POST");
                urlConn.setUseCaches(false);
                urlConn.setConnectTimeout(connTimeout);
                // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
                urlConn.setRequestProperty("Content-Type", "application/json");
                // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
                // 要注意的是connection.getOutputStream会隐含的进行connect。
                urlConn.connect();
                //DataOutputStream流
                DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());
                //要上传的参数
                byte[] content = strJson.getBytes("UTF8");
                //将要上传的内容写入流中
                out.write(content, 0, content.length);
                //刷新、关闭
                out.flush();
                out.close();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF8"));
                String inputLine = "";
                while ((inputLine = reader.readLine()) != null) {
                    returnString += inputLine;
                }
                this.status = 1;
            } catch (Exception e) {
                this.status = -1;
                returnString = "";
                e.printStackTrace();
            } finally {
                urlConn.disconnect();
                return returnString;
            }
        }

    }
}
