package com.minlu.fosterpig.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.minlu.fosterpig.IpFiled;
import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.http.OkHttpManger;
import com.minlu.fosterpig.sqlite.MySQLiteOpenHelper;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.StringUtils;
import com.minlu.fosterpig.util.ToastUtil;
import com.minlu.fosterpig.util.ViewsUitls;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by pxj on 2016/11/22.
 */
public class LoginActivity extends FragmentActivity implements View.OnClickListener {

    private EditText mEtUser;
    private EditText mEtPassWord;

    private CheckBox mRbRemember;
    private boolean mIsAuto;

    private String mUser;
    private String mPassWord;

    private String mHistoryPassWord;
    private String mHistoryUser;

    private Button mBLogin;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private SQLiteDatabase writableDatabase;
    private String loginResult;
    private String loginResultMessage;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login_button:
                login();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //设置ip
        SharedPreferencesUtil.saveStirng(getApplicationContext(), StringsFiled.IP_ADDRESS_PREFIX,
                "http://" + "192.168.1.35:8083");

        //创建数据库操作对象
        mySQLiteOpenHelper = new MySQLiteOpenHelper(ViewsUitls.getContext());
        writableDatabase = mySQLiteOpenHelper.getWritableDatabase();

        getData();// 获取上次登录成功后的历史数据

        initView();

        if (mIsAuto) {
            login();
        }


        // TODO 编写JSON
     /*   ArrayList<MainAllInformation> objects = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            objects.add(new MainAllInformation("1#猪舍", i+"#站点", 0, 1000+i, true));
        }

        ArrayList<AllSiteBean> objects = new ArrayList<>();

        ArrayList<FacilityDetail> objects1 = new ArrayList<>();
        objects1.add(new FacilityDetail(2000,0,"1号站点","区域1",1));
        objects1.add(new FacilityDetail(1000,1,"1号站点","区域1",0));
        objects1.add(new FacilityDetail(3000,2,"1号站点","区域1",1));
        objects1.add(new FacilityDetail(2000,0,"2号站点","区域1",0));
        objects1.add(new FacilityDetail(1000,1,"2号站点","区域1",0));
        objects1.add(new FacilityDetail(5000,2,"2号站点","区域1",1));

        objects.add(new AllSiteBean("区域1",50,20,objects1));
        objects.add(new AllSiteBean("区域2",50,10,objects1));
        objects.add(new AllSiteBean("区域3",50,30,objects1));
        objects.add(new AllSiteBean("区域4",50,20,objects1));
        objects.add(new AllSiteBean("区域5",50,10,objects1));


        String gsonString = GsonTools.createGsonString(objects);

        System.out.println(gsonString);*/

    }

    private void getData() {

        mIsAuto = SharedPreferencesUtil.getboolean(ViewsUitls.getContext(),
                StringsFiled.IS_AUTO_LOGIN, false);
        // mHistoryPassward = SharedPreferencesUtil.getString(
        // ViewsUitls.getContext(), "mPassWord", "");
        /*
         * 参数1:表名 参数2:要查询的字段 参数3:where表达式 参数4:替换?号的真实值 参数5:分组 null
		 * 参数6:having表达式null 参数7:排序规则 c_age desc
		 */
        Cursor cursor = writableDatabase.query("t_user",
                new String[]{"c_password"}, "c_pw>?", new String[]{"0"},
                null, null, null);
        while (cursor.moveToNext()) {
            mHistoryPassWord = cursor.getString(0);
        }
        cursor.close();

        mHistoryUser = SharedPreferencesUtil.getString(ViewsUitls.getContext(),
                StringsFiled.LOGIN_USER, "");

    }

    private void initView() {
        mEtPassWord = (EditText) findViewById(R.id.login_password);
        mEtUser = (EditText) findViewById(R.id.login_user);
        mRbRemember = (CheckBox) findViewById(R.id.cb_login_remember_password);
        mRbRemember.setChecked(mIsAuto);
        mBLogin = (Button) findViewById(R.id.bt_login_button);
        mBLogin.setOnClickListener(this);

        // 根据历史记录来设置显示
        if (!mHistoryUser.isEmpty() && !mHistoryPassWord.isEmpty()) {
            mEtUser.setText(mHistoryUser);
            mEtPassWord.setText(mHistoryPassWord);
        }
    }


    private void login() {
        mUser = mEtUser.getText().toString().trim();
        mPassWord = mEtPassWord.getText().toString().trim();
        if (!StringUtils.isEmpty(mUser) && !StringUtils.isEmpty(mPassWord)) {
            System.out.println("username:" + mUser + "password:" + mPassWord);
            requestIsLoginSuccess(mUser, mPassWord);
        } else {
            ToastUtil.showToast(this, "帐户密码不可为空");
        }

    }


    /*请求网络是否登录成功*/
    private void requestIsLoginSuccess(String userName, String passWord) {
        OkHttpClient okHttpClient = OkHttpManger.getInstance().getOkHttpClient();
        RequestBody formBody = new FormBody.Builder().add("username", userName)
                .add("password", passWord).build();

        String address = SharedPreferencesUtil.getString(
                ViewsUitls.getContext(), StringsFiled.IP_ADDRESS_PREFIX, "");

        System.out.println(address + IpFiled.LOGIN);

        Request request = new Request.Builder()
                .url(address + IpFiled.LOGIN)
                .post(formBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("=========================onFailure=============================");
                ViewsUitls.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(LoginActivity.this, "网络异常,请稍候");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    loginResult = response.body().string().toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(loginResult + "      +++++++++++++++++++++++++++++++++");
                ViewsUitls.runInMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (StringUtils.interentIsNormal(loginResult)) {
                            try {
                                JSONObject jsonObject = new JSONObject(loginResult);
                                if (jsonObject.has("resStr")) {
                                    loginResultMessage = jsonObject.optString("resStr");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (loginResult.contains("true")) {
                                saveSuccessPassWardUserName();
                                Intent mainActivity = new Intent(ViewsUitls.getContext(),
                                        MainActivity.class);
                                startActivity(mainActivity);
                                ToastUtil.showToast(LoginActivity.this, "登录成功");
                                finish();
                            } else {
                                ToastUtil.showToast(LoginActivity.this, loginResultMessage);
                            }
                        } else {
                            ToastUtil.showToast(LoginActivity.this, "服务器异常,请稍候");
                        }
                    }
                });
            }
        });


    }


    /*当登录成功后需要将帐号密码进行保存*/
    private void saveSuccessPassWardUserName() {
        if (StringUtils.isEmpty(mHistoryPassWord)) {// 当数据库中没有保存过密码时需要第一次插入密码数据
            ContentValues values = new ContentValues();
            values.put("c_password", mPassWord);
            values.put("c_pw", 1);
            writableDatabase.insert("t_user", null, values);
        } else {// 修改数据
            if (!mHistoryPassWord.equals(mPassWord)) {// EditText中的密码与历史密码不一样
                ContentValues values = new ContentValues();
                values.put("c_password", mPassWord);
                writableDatabase.update("t_user", values, "c_pw>?",
                        new String[]{"0"});
            }
        }
        writableDatabase.close();
        mySQLiteOpenHelper.close();

        if (!mHistoryUser.equals(mUser)) {// EditText中的帐号与历史帐号不一样
            SharedPreferencesUtil.saveStirng(ViewsUitls.getContext(),
                    StringsFiled.LOGIN_USER, mUser);
        }
        SharedPreferencesUtil.saveboolean(ViewsUitls.getContext(),
                StringsFiled.IS_AUTO_LOGIN, mRbRemember.isChecked());
    }
}
