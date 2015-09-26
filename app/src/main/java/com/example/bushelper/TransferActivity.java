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
import android.widget.Toast;

import com.example.bushelper.struct.Segment;
import com.example.bushelper.struct.Transfer;
import com.example.bushelper.utils.NetUtils;
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
        setContentView(R.layout.activity_line_transfer);

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
            new TransferAsyncTask().execute(AIBANG_TRANSFER_URL);
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

            //显示progressdialog
            pd = ProgressDialog.show(TransferActivity.this, null, "正在搜索...", false);
        }

        @Override
        protected List<Transfer> doInBackground(String... params) {
            //封装请求参数，发送请求
            Map<String, String> params2 = new HashMap<String, String>();
            params2.put("city", cityName);
            params2.put("app_key", AIBANG_APP_KEY);
            params2.put("alt", "json");
            params2.put("start_addr", init);
            params2.put("end_addr", dest);
            String jsonString = NetUtils.getRequest(params[0], params2);

            //返回数据解析
            return jsonString != null ? jsonToTransferList(jsonString) : new ArrayList<Transfer>();
        }

        @Override
        protected void onPostExecute(List<Transfer> transfers) {
            super.onPostExecute(transfers);

            //没有找到结果，finish
            if(transfers.size() == 0) {
                Toast.makeText(TransferActivity.this, "没有找到线路", Toast.LENGTH_SHORT).show();
                TransferActivity.this.finish();
            } else {
                //改变ActionBar的title
                actionBar.setTitle("找到" + transfers.size() + "个方案");

                //初始化数据
                int count = 0;
                for (Transfer transfer : transfers) {
                    List<String> listTemp = new ArrayList<String>();

                    String title = "<方案" + ++count + ">\t[全长]" + transfer.dist / 1000 + "km\t[换乘]" + transfer.segmentList.size() + "次\n[耗时]" + transfer.time + "min\t[步行]" + transfer.foot_dist + "m";
                    parent.add(title);
                    for (Segment segment:transfer.segmentList) {
                        String detail = "";
                        detail = "[搭乘] " + segment.lineName +
                                "\n[起始站] " + segment.startStat + "\t\t\t\t[终点站] " + segment.endStat +
                                "\n[经过站点]\n" + segment.stats +
                                "\n[行驶距离] " + segment.lineDist / 1000 + "km\t\t\t\t[步行距离] " + segment.footDist + "m";
                        listTemp.add(detail);
                    }
                    child.put(title, listTemp);
                }

                //加载数据到列表
                elv.setAdapter(new MyExpandableListAdapter(TransferActivity.this, parent, child));
            }

            //dismiss pg
            pd.dismiss();
        }
    }
}
