package com.example.administrator.parkshare;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private TextView textView;
    private LocationClient client;
    private BaiduMap map;
    private MapView mapView;
    private Button button;
    private boolean isFirstedLocation = true;
    private LatLng mLastLocationData;
    private MyLocationData locData;
    Marker marker = null;
    // 导航相关
    public static final String TAG = "NaviSDkDemo";
    private static final String APP_FOLDER_NAME = "BNSDKDemo";
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    private String mSDCardPath = null;
    String authinfo = null;
    private static final String Url = "http://maps.googleapis.com/maps/api/geocode/json?address=%E5%8C%97%E4%BA%AC%E4%BA%A4%E9%80%9A%E5%A4%A7%E5%AD%A6&sensor=false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mapView=(MapView)findViewById(R.id.mMap);
        map=mapView.getMap();
        map.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        button=(Button)findViewById(R.id.b1);
        initLiastener();
        if (initDirs()){
            initNavi();

        }
    }
    private void initLiastener(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BaiduNaviManager.isNaviInited()){
                    routeplanToNavi();
                }
            }
        });
    }
    private void routeplanToNavi(){
        BNRoutePlanNode.CoordinateType coordinateType= BNRoutePlanNode.CoordinateType.WGS84;
        BNRoutePlanNode sNode=null;
        BNRoutePlanNode eNode=null;
        sNode = new BNRoutePlanNode(116.30142, 40.05087,
                "百度大厦", null, coordinateType);
        eNode = new BNRoutePlanNode(116.39750, 39.90882,
                "北京天安门", null, coordinateType);
        if (sNode!=null&&eNode!=null){
            List<BNRoutePlanNode>list=new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this,list,1,true,new DemoRoutePlanListener(sNode));
        }
    }
    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener{
        private BNRoutePlanNode mBnRoutePlanNode=null;
        public DemoRoutePlanListener(BNRoutePlanNode node){
            mBnRoutePlanNode=node;
        }

        @Override
        public void onJumpToNavigator() {
            Intent intent=new Intent(MainActivity.this,BNDemoGuideActivity.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE,(BNRoutePlanNode)mBnRoutePlanNode);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onRoutePlanFailed() {

        }
    }
    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if ( mSDCardPath == null ) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if ( !f.exists() ) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }
    // 导航 初始化引擎
    private void initNavi() {
//      BaiduNaviManager.getInstance().setNativeLibraryPath(
//              mSDCardPath + "/BaiduNaviSDK_SO");
        BaiduNaviManager.getInstance().init(this, mSDCardPath, APP_FOLDER_NAME,
                new BaiduNaviManager.NaviInitListener() {
                    @Override
                    public void onAuthResult(int status, String msg) {
                        if (0 == status) {
                            authinfo = "key校验成功!";
                        } else {
                            authinfo = "key校验失败, " + msg;
                        }
                        MainActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, authinfo,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    public void initSuccess() {
                        Toast.makeText(MainActivity.this, "百度导航引擎初始化成功",
                                Toast.LENGTH_SHORT).show();
                    }

                    public void initStart() {
                        Toast.makeText(MainActivity.this, "百度导航引擎初始化开始",
                                Toast.LENGTH_SHORT).show();
                    }

                    public void initFailed() {
                        Toast.makeText(MainActivity.this, "百度导航引擎初始化失败",
                                Toast.LENGTH_SHORT).show();
                    }
                },mTTSCallback);
    }

    private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

        @Override
        public void stopTTS() {
            // TODO Auto-generated method stub

        }

        @Override
        public void resumeTTS() {
            // TODO Auto-generated method stub

        }

        @Override
        public void releaseTTSPlayer() {
            // TODO Auto-generated method stub

        }

        @Override
        public int playTTSText(String speech, int bPreempt) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public void phoneHangUp() {
            // TODO Auto-generated method stub

        }

        @Override
        public void phoneCalling() {
            // TODO Auto-generated method stub

        }

        @Override
        public void pauseTTS() {
            // TODO Auto-generated method stub

        }

        @Override
        public void initTTSPlayer() {
            // TODO Auto-generated method stub

        }

        @Override
        public int getTTSState() {
            // TODO Auto-generated method stub
            return 0;
        }
    };
}
