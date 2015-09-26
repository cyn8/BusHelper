package com.example.bushelper;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.example.bushelper.struct.Line;
import com.example.bushelper.struct.Segment;
import com.example.bushelper.struct.Transfer;
import com.example.bushelper.utils.NetworkStateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chan on 15/9/26.
 */
public class TransferActivity extends Activity{

    private ExpandableListView elv;
    private String cityName;
    private String init;
    private String dest;
    private ProgressDialog pd;
    private boolean NetworkStateTag;
    private ActionBar actionBar;
    private List<String> parent = new ArrayList<String>();
    private Map<String, List<String>> child = new HashMap<String, List<String>>();
    private final String AIBANG_APP_KEY = "d706b1f36e6adfdb862f7f54c132390f";
    private final String AIBANG_TRANSFER_URL = "http://openapi.aibang.com/bus/transfer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //actionBar上添加返回按钮
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //实例化控件
        elv = (ExpandableListView) findViewById(R.id.activity_line_elv);

        //获得MainActivity传过来的参数
        Intent intent = getIntent();
        cityName = intent.getStringExtra("cityName");
        init = intent.getStringExtra("init");
        dest = intent.getStringExtra("dest");

        //检查网络状态
        NetworkStateTag = NetworkStateUtils.checkNetworkStateAndShowAlert(TransferActivity.this);

        if(NetworkStateTag) {
            //run GetLocationAsyncTask
            new LineAsyncTask().execute(AIBANG_LINE_URL);
        }
    }

    private List<Transfer> jsonToTransferList(String json) {

        //实例化ArrayList<BusData>对象
        ArrayList<Transfer> transferList = new ArrayList<Transfer>();

        //获得result_num
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            //获得每个换乘信息
            JSONObject buses = new JSONObject(jsonObject.getString("buses"));
            JSONArray bus = new JSONArray(buses.getString("bus"));
            for(int i = 0; i < bus.length(); i++) {
                Transfer transfer = new Transfer();
                JSONObject busObject = (JSONObject) bus.get(i);
                JSONObject segments = new JSONObject(busObject.getString("segments"));
                JSONArray segmentArr = new JSONArray(segments.getString("segment"));

                transfer.dist = busObject.getInt("dist");
                transfer.time = busObject.getInt("time");
                transfer.foot_dist = busObject.getInt("foot_dist");
                transfer.last_foot_dist = busObject.getInt("last_foot_dist");
                transfer.transTimes = segmentArr.length();

                for(int j = 0; j < segmentArr.length(); j++) {
                    Segment segment = new Segment();
                    JSONObject segmentObj = (JSONObject) segmentArr.get(j);

                    segment.startStat = segmentObj.getString("start_stat");
                    segment.endStat = segmentObj.getString("end_stat");
                    segment.lineName = segmentObj.getString("line_name");
                    segment.stats = segmentObj.getString("stats");
                    segment.lineDist = segmentObj.getInt("line_dist");
                    segment.footDist = segmentObj.getInt("foot_dist");

                    transfer.segmentList.add(segment);
                }
                transferList.add(transfer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //测试用，遍历输出
        for(Transfer transfer:transferList) {
            Log.i("mytag", transfer.dist + "----" + transfer.foot_dist + "----" + transfer.last_foot_dist);
        }

        return transferList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    class TransferAsyncTask extends AsyncTask<String, Void, List<Transfer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Transfer> doInBackground(String... params) {
            return null;
        }

        
    }
}
