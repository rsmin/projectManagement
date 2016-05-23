package com.pancai.projectmanagement.util;

/**
 * Created by rsmin on 2016/5/19.
 */

import android.content.SharedPreferences;
import android.content.Context;

public class SharePreferenceLoginUtil {
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    public SharePreferenceLoginUtil(Context context, String file) {
        sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public String getAppId() {
        return sp.getString("appid", "");
    }

    public void setAppId(String appid) {
        spEditor.putString("appid", appid);
        spEditor.commit();
    }

    public String getUserId() {
        return sp.getString("userid", "");
    }

    public void setUserId(String userid) {
        spEditor.putString("userid", userid);
        spEditor.commit();
    }

    public String getPassword() {
        return sp.getString("password", "");
    }

    public void setPassword(String password) {
        spEditor.putString("password", password);
        spEditor.commit();
    }

    public void setUserPrivilege(String Privilege) {
        spEditor.putString("Privilege", Privilege);
        spEditor.commit();
    }

    public String getPrivilege() {
        return sp.getString("Privilege", "");
    }
}
