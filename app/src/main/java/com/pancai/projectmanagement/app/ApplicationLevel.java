package com.pancai.projectmanagement.app;

import android.app.Application;
import com.pancai.projectmanagement.util.SharePreferenceLoginUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by rsmin on 2016/5/19.
 */
public class ApplicationLevel extends Application {
    public String userPreviledge;
    private static ApplicationLevel applicationLevel;
    private SharePreferenceLoginUtil spLoginUtil;
    private Gson appGson;
    public static final String SP_Login_File_Name = "Login_sp";

    public synchronized static ApplicationLevel getInstance() {
        return applicationLevel;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationLevel = this;
        CrashHandler.getInstance().init(this);
        initData();
    }

    private void initData() {
        appGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .create();
        spLoginUtil = new SharePreferenceLoginUtil(this, SP_Login_File_Name);
    }

    public synchronized SharePreferenceLoginUtil getSpUtil() {
        if (spLoginUtil == null)
            spLoginUtil = new SharePreferenceLoginUtil(this, SP_Login_File_Name);
        return spLoginUtil;
    }

    public synchronized Gson getGson() {
        if (appGson == null)
            // 不转换没有 @Expose 注解的字段
            appGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                    .create();
        return appGson;
    }

    public static enum appPrivilege {
        admin,  
        useradmin,
        uploadUser,
        user;
    }

    public static enum projectPrivilege {
        projectManager,
        projectWorker,
        watcher;
    }

    public static enum taskPrivilege {
        taskManager,
        taskWorker,
        watcher;
    }

    public static enum progressStatus {
        submit,
        inprogress,
        cancelled,
        compelete;
    }
}
