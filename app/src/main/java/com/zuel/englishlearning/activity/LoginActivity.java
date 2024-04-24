package com.zuel.englishlearning.activity;

import static com.zuel.englishlearning.config.SinaData.APP_KEY;
import static com.zuel.englishlearning.config.SinaData.REDIRECT_URL;
import static com.zuel.englishlearning.config.SinaData.SCOPE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.lihang.ShadowLayout;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.SdkListener;
import com.sina.weibo.sdk.openapi.WBAPIFactory;
import com.zuel.englishlearning.R;
import com.zuel.englishlearning.config.ConfigData;
import com.zuel.englishlearning.database.User;
import com.zuel.englishlearning.database.UserConfig;
import com.zuel.englishlearning.util.ActivityCollector;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends BaseActivity {

    public IWBAPI mWBAPI;
    private ImageView imgPic;

    // 登录按钮
    private ShadowLayout cardLogin;

    private ShadowLayout register;

    private LinearLayout linearLayout;

    private static final String TAG = "LoginActivity";

    private final int SUCCESS = 1;
    private final int FAILED = 2;

    private ProgressDialog progressDialog;
    private String content;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case FAILED:
                    Toast.makeText(LoginActivity.this, "登录失败，请检查服务器与网络状态", Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS:
                    ActivityCollector.startOtherActivity(LoginActivity.this, ChooseWordDBActivity.class);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        Glide.with(this).load(R.drawable.pic_learning).into(imgPic);

        // 渐变动画
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(2000);
        imgPic.startAnimation(animation);
        cardLogin.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setTitle("提示")
                    .setMessage("本软件仅收集用户名、ID、头像三个必要的信息，我们不会泄露您的个人隐私，仅作为标识使用。请放心使用")
                    .setPositiveButton("继续", (dialog, which) -> {
                        initSinaLogin();
                        //wangtest();
                    })
                    .setNegativeButton("取消", null)
                    .show();


        });


    }

    private void init() {
        imgPic = findViewById(R.id.img_inbetweening);
        cardLogin = findViewById(R.id.card_sina_login);
        // register = findViewById(R.id.card_register);
        linearLayout = findViewById(R.id.linear_login);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("提示")
                .setMessage("确定要退出吗?")
                .setPositiveButton("确定", (dialog, which) -> ActivityCollector.finishAll())
                .setNegativeButton("取消", null)
                .show();
    }


    private void wangtest(){
        int id = 99999;
        String name = "王老师";
        String img = "https://xagx.zuel.edu.cn/_upload/article/images/60/2d/c32fad9044ee9a085af791f2b20b/6180b86b-a8db-478c-8c8d-72bb9456c5c8.png";
        List<User> users = LitePal.where("userId = ?", id+"").find(User.class);
        if (users.isEmpty()) {
            User user = new User();
            user.setUserName(name);
            user.setUserProfile(img);
            user.setUserId(id);
            // 测试
            user.setUserMoney(0);
            user.setUserWordNumber(0);
            user.save();
        }
        // 查询在用户配置表中，是否存在该用户，若没有，则新建数据
        List<UserConfig> userConfigs = LitePal.where("userId = ?", id+"").find(UserConfig.class);
        if (userConfigs.isEmpty()) {
            UserConfig userConfig = new UserConfig();
            userConfig.setUserId(id);
            userConfig.setCurrentBookId(-1);
            userConfig.save();
        }
        // 默认已登录并设置已登录的微博ID
        ConfigData.setIsLogged(true);
        ConfigData.setSinaNumLogged(id);
        Message message = new Message();
        message.what = SUCCESS;
        handler.sendMessage(message);
    }

    private void initSinaLogin() {
        AuthInfo authInfo = new AuthInfo(this, APP_KEY, REDIRECT_URL, SCOPE);
        mWBAPI = WBAPIFactory.createWBAPI(this);
        Log.d(TAG, "initSinaLogin: ");
        mWBAPI.registerApp(this, authInfo, new SdkListener() {
            @Override
            public void onInitSuccess() {
                Log.d(TAG, "初始化成功");
            }

            @Override
            public void onInitFailure(Exception e) {
                Log.d(TAG, "初始化失败");
            }
        });
        mWBAPI.authorize(this, new WbAuthListener() {
            @Override
            public void onComplete(Oauth2AccessToken token) {
                Toast.makeText(LoginActivity.this, "微博授权成功", Toast.LENGTH_SHORT).show();


               //调用微博的用户请求地址，获取用户信息，新版的微博，用户信息将不再直接返回，必须调用以下的接口才能返回用户的详细信息 https://api.weibo.com/2/users/show.json
                Map<String, Object> map = new HashMap<>();
                map.put("access_token", token.getAccessToken());
                map.put("uid", token.getUid());
                Set<Map.Entry<String, Object>> entries = map.entrySet();
                StringBuffer absUrl = new StringBuffer();
                int size = 0;
                for (Map.Entry<String, Object> item : entries) {
                    if (size == 0) {
                        absUrl.append("https://api.weibo.com/2/users/show.json").append("?").append(item.getKey()).append("=").append(item.getValue());
                        Log.d(TAG, "这里是onComplete: "+absUrl);
                        size++;
                    } else {
                        absUrl.append("&").append(item.getKey()).append("=").append(item.getValue());
                    }
                }

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(absUrl.toString())
                        .build();

                client.newCall(request).enqueue(new okhttp3.Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String result = response.body().string();
                           // String jsonResult = new Gson().toJson(response.body());
                            Log.d(TAG, "这里是JSON Result:"+result);
                            if (result == null) {
                                return;
                            }
                            JSONObject JSONresult;
                            try {
                                JSONresult = new JSONObject(result);
                                Log.d(TAG, "这里是JSON result:"+JSONresult);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }




                           // JsonSina weib = new Gson().fromJson(result , JsonSina.class);

                          //  if (weib == null) {
                          //      return;
                           // }


                            long id = 00001;
                            try {
                                id = (long) JSONresult.get("id");
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            String name = "admin";
                            try {
                                name = JSONresult.get("name").toString();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            String img = "";
                            try {
                                img = JSONresult.get("profile_image_url").toString();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            Log.d(TAG, "这里是id"+id);
                            Log.d(TAG, "这里是name"+name);
                            Log.d(TAG, "这里是picurl"+img);
                            List<User> users = LitePal.where("userId = ?", id + "").find(User.class);
                            if (users.isEmpty()) {
                                User user = new User();
                                user.setUserName(name);
                                user.setUserProfile(img);
                                user.setUserId((int) id);
                                // 测试
                                user.setUserMoney(0);
                                user.setUserWordNumber(0);
                                user.save();
                            }
                            // 查询在用户配置表中，是否存在该用户，若没有，则新建数据
                            List<UserConfig> userConfigs = LitePal.where("userId = ?", id + "").find(UserConfig.class);
                            if (userConfigs.isEmpty()) {
                                UserConfig userConfig = new UserConfig();
                                userConfig.setUserId((int) id);
                                userConfig.setCurrentBookId(-1);
                                userConfig.save();
                            }
                            // 默认已登录并设置已登录的微博ID
                            ConfigData.setIsLogged(true);
                            ConfigData.setSinaNumLogged((int) id);
                            Message message = new Message();
                            message.what = SUCCESS;
                            handler.sendMessage(message);
                        }
                    }
                });


            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(LoginActivity.this, "微博SDK初始化成功，请重新点击", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "微博授权取消", Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mWBAPI != null) {
            mWBAPI.authorizeCallback(this ,requestCode, resultCode, data);
        }
    }

}
