package com.minlu.fosterpig.activity;

import android.util.Log;
import android.view.View;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.base.BaseActivity;
import com.minlu.fosterpig.fragment.AllWarnFragment;
import com.minlu.fosterpig.observer.MySubject;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.ViewsUitls;

/**
 * Created by user on 2016/11/25.
 */
public class NotificationToWarnActivity extends BaseActivity {
    @Override
    public void onCreateContent() {

        Log.v("alarm", "进入报警详情页面");
        MySubject.getInstance().operation(StringsFiled.OBSERVER_MEDIA_PLAYER_IS_PLAYING, -1, -1);

        if (SharedPreferencesUtil.getboolean(ViewsUitls.getContext(), StringsFiled.IS_ALLOW_SOUND_PLAY, false) && SharedPreferencesUtil.getboolean(ViewsUitls.getContext(), StringsFiled.MEDIA_IS_PLAYING, false)) {
            Log.v("alarm", "此时的状态是允许报警声音，且正在播放报警，所以需要暂停报警，并设置一段时间后在重启报警");
            MySubject.getInstance().operation(StringsFiled.OBSERVER_MEDIA_PLAYER_PAUSE, -1, -1);
            MySubject.getInstance().operation(StringsFiled.OBSERVER_UPDATE_NOTIFICATION, -1, -1);
            // 暂停了报警声音，就必须把循序播放-暂停前的if判断改为false
            SharedPreferencesUtil.saveboolean(ViewsUitls.getContext(), StringsFiled.IS_ALLOW_SOUND_PLAY, false);
            // 并记录暂停报警声音一段时间后具体可以继续开启报警声音的时间
            SharedPreferencesUtil.saveLong(ViewsUitls.getContext(), StringsFiled.IS_ALLOW_SOUND_PLAY_TIME, System.currentTimeMillis() + 20000);
        }

        getThreeLine().setVisibility(View.GONE);
        setBackVisibility(View.VISIBLE);
        setSettingVisibility(View.GONE);

        getBaseTitle().setText("报警信息");

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_base_content, new AllWarnFragment()).commit();

    }
}
