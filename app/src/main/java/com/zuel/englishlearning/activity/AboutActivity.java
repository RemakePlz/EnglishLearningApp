package com.zuel.englishlearning.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.lihang.ShadowLayout;
import com.zuel.englishlearning.R;
import com.zuel.englishlearning.config.ConfigData;
import com.zuel.englishlearning.config.ConstantData;
import com.zuel.englishlearning.util.MyApplication;
import com.zuel.englishlearning.util.NumberController;

public class AboutActivity extends BaseActivity {

    private TextView textVersion, textName, textContent;
    private ShadowLayout cardcancelLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        init();

        textVersion.setText("当前版本：" + getAppVersionName(AboutActivity.this) + "（" + getAppVersionCode(AboutActivity.this) + "）");
        textContent.setText(ConstantData.phrases[NumberController.getRandomNumber(0, ConstantData.phrases.length - 1)]);
        textName.setText(getAppName(MyApplication.getContext()));

        cardcancelLogin.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
            builder.setTitle("提示")
                    .setMessage("您确定要退出登录吗")
                    .setPositiveButton("继续", (dialog, which) -> {
                        ConfigData.setIsLogged(false);
                        ConfigData.setSinaNumLogged(-1);

                        Intent intent = new Intent(this,LoginActivity.class);
                        startActivity(intent);
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

    }

    private void init() {
        textVersion = findViewById(R.id.text_about_version);
        textName = findViewById(R.id.text_about_name);
        textContent = findViewById(R.id.text_about_content);
         cardcancelLogin = findViewById(R.id.card_cancel_sina_login);
    }

    public static String getAppVersionCode(Context context) {
        int versioncode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            // versionName = pi.versionName;
            versioncode = pi.versionCode;
        } catch (Exception e) {
            //Log.e("VersionInfo", "Exception", e);
        }
        return versioncode + "";
    }

    public static String getAppVersionName(Context context) {
        String versionName = null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            //Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public static String getAppName(Context context) {
        if (context == null) {
            return null;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            return String.valueOf(packageManager.getApplicationLabel(context.getApplicationInfo()));
        } catch (Throwable e) {
        }
        return null;
    }


}
