package com.minlu.fosterpig.fragment;

import android.view.View;
import android.widget.ListView;

import com.minlu.fosterpig.R;
import com.minlu.fosterpig.StringsFiled;
import com.minlu.fosterpig.adapter.MainToAlreadyWarnAdapter;
import com.minlu.fosterpig.base.BaseFragment;
import com.minlu.fosterpig.base.ContentPage;
import com.minlu.fosterpig.bean.AlreadySureWarn;
import com.minlu.fosterpig.util.SharedPreferencesUtil;
import com.minlu.fosterpig.util.StringUtils;
import com.minlu.fosterpig.util.ViewsUitls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/11/23.
 */
public class MainToAlreadyWarnFragment extends BaseFragment<AlreadySureWarn> {
    private List<AlreadySureWarn> allAlreadySureWarn;
    private ListView listView;
    private MainToAlreadyWarnAdapter sureWarnAdapter;

    private String mResultString;

    @Override
    protected void onSubClassOnCreateView() {
        loadDataAndRefresh();
    }

    @Override
    protected View onCreateSuccessView() {

        View inflate = ViewsUitls.inflate(R.layout.layout_listview);

        listView = (ListView) inflate.findViewById(R.id.have_swipe_refresh_list_view);

        sureWarnAdapter = new MainToAlreadyWarnAdapter(allAlreadySureWarn);
        listView.setAdapter(sureWarnAdapter);

        return inflate;
    }

    @Override
    protected ContentPage.ResultState onLoad() {
        switch (getBundleValue()) {
            case StringsFiled.MAIN_TO_WARN_VALUE_AMMONIA:
                mResultString = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_AMMONIA_JSON, "");
                break;
            case StringsFiled.MAIN_TO_WARN_VALUE_TEMPERATURE:
                mResultString = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_TEMPERATURE_JSON, "");
                break;
            case StringsFiled.MAIN_TO_WARN_VALUE_HUMIDITY:
                mResultString = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_HUMIDITY_JSON, "");
                break;
            case StringsFiled.MAIN_TO_WARN_VALUE_POWER_SUPPLY:
                mResultString = SharedPreferencesUtil.getString(ViewsUitls.getContext(), StringsFiled.MAIN_TO_ALREADY_WARN_POWER_SUPPLY_JSON, "");
                break;
        }
        if (!StringUtils.isEmpty(mResultString)) {
            analysisJsonDate();
        } else {
            allAlreadySureWarn = null;
        }
        return chat(allAlreadySureWarn);
    }

    private void analysisJsonDate() {

        // TODO 测试数据
//        try {
//            InputStream is = getActivity().getAssets().open("textJson3.txt");
//            mResultString = readTextFromSDcard(is);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        // TODO 测试数据

        if (StringUtils.interentIsNormal(mResultString)) {
            System.out.println(mResultString);
            try {
                JSONArray jsonArray = new JSONArray(mResultString);
                if (allAlreadySureWarn == null) {
                    allAlreadySureWarn = new ArrayList<>();
                } else {
                    allAlreadySureWarn.clear();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject singleInformation = jsonArray.getJSONObject(i);
                    allAlreadySureWarn.add(new AlreadySureWarn(singleInformation.optString("alarmTime"),
                            singleInformation.optString("handleTime"),
                            singleInformation.optString("areaName"),
                            singleInformation.optString("stationName"),
                            singleInformation.optInt("type"),
                            singleInformation.optDouble("value")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            allAlreadySureWarn = null;
        }

    }

    private String readTextFromSDcard(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        return buffer.toString();
    }
}
