package com.minlu.fosterpig.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.http.OkHttpManger;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.StringUtils;
import com.minlu.fosterpig.util.ToastUtil;
import com.minlu.fosterpig.util.ViewsUitls;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

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
    @Override
    public void onClick(View v) {
       switch (v.getId())
       {
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
                "http://"+"192.168.1.41:8080");

        //创建数据库操作对象

        // 初始化clientid
        SharedPreferencesUtil.saveStirng(this,StringsFiled.CLIENTID,"");

        initView();
        if (mIsAuto){
            login();
        }

    }

    private void initView(){
        mEtPassWord=(EditText)findViewById(R.id.login_user);
        mEtUser= (EditText) findViewById(R.id.login_user);
        showEditHistoryText();
        mRbRemember= (CheckBox) findViewById(R.id.cb_login_remember_password);
        mRbRemember.setChecked(mIsAuto);
        mBLogin= (Button) findViewById(R.id.bt_login_button);
        mBLogin.setOnClickListener(this);
    }

    private void showEditHistoryText(){



    }

    private void login(){
        mUser=mEtUser.getText().toString().trim();
        mPassWord=mEtPassWord.getText().toString().trim();
        if(!StringUtils.isEmpty(mUser)&& !StringUtils.isEmpty(mPassWord)){
            System.out.println("username:"+mUser+"password:"+mPassWord);
             requestIsLoginSuccess(mUser,mPassWord);
        }else{
            ToastUtil.showToast(this,"帐户密码不可为空");
        }

    }


    /*请求网络是否登录成功*/
    private void requestIsLoginSuccess(String userName,String passWord){
        try {
            OkHttpClient okHttpClient= OkHttpManger.getInstance().getOkHttpClient();
            RequestBody formBody=new FormBody.Builder().add("username",userName)
                    .add("password",passWord).build();
            System.out.println(formBody);
            Intent mainActivity=new Intent(ViewsUitls.getContext(),
                    MainActivity.class);
            startActivity(mainActivity);
            finish();
            ToastUtil.showToast(LoginActivity.this,"123");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
