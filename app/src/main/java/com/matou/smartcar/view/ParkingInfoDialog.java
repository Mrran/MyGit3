package com.matou.smartcar.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.elvishew.xlog.XLog;
import com.matou.smartcar.R;
import com.matou.smartcar.adapter.ItemAdapter;
import com.matou.smartcar.bean.BaseResult;
import com.matou.smartcar.bean.ParkBean;
import com.matou.smartcar.net.Api;
import com.matou.smartcar.net.BaseObserver;
import com.matou.smartcar.net.NetManager;
import com.matou.smartcar.net.RxSchedulers;
import com.matou.smartcar.util.CommUtils;
import com.matou.smartcar.util.DisplayUtil;
import com.matou.smartcar.util.MD5Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


/**
 * 关闭箱门提醒对话框
 * @author ranfeng
 */
public class ParkingInfoDialog extends Dialog {

    private RecyclerView parkingRv;

    private double lon;

    private double lat;

    private int searchRadius;

    private List<ParkBean.ParkingListBean> parkingList = new ArrayList<>();
    private ItemAdapter adapter;

    private ProgressBar loadingPb;

    private LinearLayout tipsLlyt;
    private TextView tipsTv;

    /**
     * 界面状态
     */
    private enum Status {
        SHOW_LOADING,
        SHOW_DATA,
        SHOW_NO_DATA,
        SHOW_TIMEOUT,
        SHOW_ERROR
    }

    public ParkingInfoDialog(Context context, double lon, double lat) {
        super(context);
        this.lon = lon;
        this.lat = lat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_info_dialog);

        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = window.getAttributes();
            // 获取屏幕宽度
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            int screenWidth = displayMetrics.widthPixels;

            lp.width = screenWidth / 2 - DisplayUtil.dpToPx(getContext(), 30);
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.START;
            lp.dimAmount = 0.0f;
            lp.x = DisplayUtil.dpToPx(getContext(), 50);
            window.setAttributes(lp);
        }


        init();
    }

    private void init() {
        findViewById(R.id.llyt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        parkingRv = findViewById(R.id.rv_parking);
        //parkingRv.addItemDecoration(new GridSpacingItemDecoration(DisplayUtil.dpToPx(getContext(), 15)));
        parkingRv.setLayoutManager(new LinearLayoutManager(getContext()));

        // 创建自定义适配器
        adapter = new ItemAdapter(getContext(), lon, lat);
        adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {

            }

            @Override
            public void onItemNaviClick(int pos) {
                dismiss();
            }
        });
        parkingRv.setAdapter(adapter);

        loadingPb = findViewById(R.id.loadingView);
        tipsLlyt =  findViewById(R.id.llyt_tips);
        tipsTv = findViewById(R.id.tv_tips);

        searchRadius = 1;
        getParkingFromPlatform(lon, lat);
    }

    /**
     * 从平台获取停车场数据
     * @param lng
     * @param lat
     */
    private void getParkingFromPlatform(double lng, double lat) {
        XLog.w("---> getParkingFromPlatform lng = " + lng + ", lat = " + lat + ", searchRadius = " + searchRadius);
        showStatus(Status.SHOW_LOADING);
        parkingList.clear();
        String time = String.valueOf(System.currentTimeMillis());
        // String encrypt = MD5Utils.encrypt(NetManager.getAppId() + "|" + time + "|" + "test");
        String encrypt = MD5Utils.encrypt(NetManager.getAppId() + "|" + time + "|" + "QAZzaq1!");
        NetManager.getApi(NetManager.mBaseUrl, Api.class)
                .getParks(NetManager.getAppId(), time, encrypt, lng, lat, searchRadius)
                .compose(RxSchedulers.applySchedulers())
                .subscribe(new BaseObserver<BaseResult<ParkBean>>() {
                    @Override
                    public void onSuccess(BaseResult<ParkBean> baseResult) {
                        XLog.e("---> getParkingFromPlatform onSuccess = " + baseResult);
                        if(baseResult != null && baseResult.data != null){
                            List<ParkBean.ParkingListBean> platParkingList = baseResult.data.getParkingList();
                            platParkingList.forEach(bean -> {
                                bean.setLine(true);
                                bean.setDistance(CommUtils.getDistance(lon, lat, bean.getLng(), bean.getLat()));
                            });

                            platParkingList.sort((o1, o2) -> o1.getDistance() < o2.getDistance() ? -1 : 1);

                            parkingList.addAll(platParkingList);
                        }
                        getParkingFromAMap(lon, lat);
                    }

                    @Override
                    public void onErrorMsg(String msg) {
                        XLog.e("---> getParkingFromPlatform onErrorMsg = " + msg);
                        getParkingFromAMap(lon, lat);
                    }
                });
    }

    private void showStatus(Status status) {

        switch (status){
            case SHOW_LOADING:
                loadingPb.setVisibility(View.VISIBLE);
                parkingRv.setVisibility(View.INVISIBLE);
                tipsLlyt.setVisibility(View.INVISIBLE);
                break;

            case SHOW_DATA:
                loadingPb.setVisibility(View.INVISIBLE);
                parkingRv.setVisibility(View.VISIBLE);
                tipsLlyt.setVisibility(View.INVISIBLE);
                break;

            case SHOW_NO_DATA:
                loadingPb.setVisibility(View.INVISIBLE);
                parkingRv.setVisibility(View.INVISIBLE);
                tipsLlyt.setVisibility(View.VISIBLE);
                tipsTv.setText("附近没找到停车场！");
                break;

            case SHOW_TIMEOUT:
                loadingPb.setVisibility(View.INVISIBLE);
                parkingRv.setVisibility(View.INVISIBLE);
                tipsLlyt.setVisibility(View.VISIBLE);
                tipsTv.setText("网络开小差！");
                break;

            case SHOW_ERROR:
                loadingPb.setVisibility(View.INVISIBLE);
                parkingRv.setVisibility(View.INVISIBLE);
                tipsLlyt.setVisibility(View.VISIBLE);
                tipsTv.setText("获取数据失败！");
                break;

            default:

                break;
        }

    }

    /**
     * 高德地图接口获取停车场
     *
     * @param lon
     * @param lat
     */
    public void getParkingFromAMap(double lon, double lat) {
        XLog.w("---> getParkingFromAMap lng = " + lon + ", lat = " + lat + ", searchRadius = " + searchRadius);

        PoiSearch.Query query = new PoiSearch.Query("停车场", "", "");
        query.setPageSize(10);
        query.setPageNum(1);

        try {
            PoiSearch poiSearch = new PoiSearch(getContext(), query);
            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(lat, lon), searchRadius * 1000));
            poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
                @Override
                public void onPoiSearched(PoiResult poiResult, int code) {
                    XLog.e("---> getParkingFromAMap onPoiSearched code = " + code + ", poiResult = " + poiResult);
                    if (code == 1000) {
                        if (poiResult != null) {
                            if (!poiResult.getPois().isEmpty()) {
                                List<ParkBean.ParkingListBean> aMapParkingList = convertParkingData(poiResult.getPois());
                                aMapParkingList.sort((o1, o2) -> o1.getDistance() < o2.getDistance() ? -1 : 1);

                                parkingList.addAll(aMapParkingList);
                            }

                            if(!parkingList.isEmpty()){ //  获取到数据，列表显示
                                showStatus(Status.SHOW_DATA);
                                adapter.updateData(parkingList);
                            }else {
                                if (searchRadius == 1) {
                                    searchRadius = 2;
                                    getParkingFromPlatform(lon, lat);
                                }else {
                                    showStatus(Status.SHOW_NO_DATA);
                                }
                            }
                        }
                    }else if(code >= 1800 && code <= 1806){ // 网络异常相关
                        if(!parkingList.isEmpty()){
                            showStatus(Status.SHOW_DATA);
                        }else {
                            showStatus(Status.SHOW_TIMEOUT);
                        }
                    }else {
                        if(!parkingList.isEmpty()){
                            showStatus(Status.SHOW_DATA);
                        }else {
                            showStatus(Status.SHOW_ERROR);
                        }
                    }

                }

                @Override
                public void onPoiItemSearched(PoiItem poiItem, int i) {

                }
            });
            poiSearch.searchPOIAsyn();
        } catch (AMapException e) {
            e.printStackTrace();
        }

    }

    /**
     * 高德停车数据到平台数据的转换
     * @param poiList
     * @return
     */
    private List<ParkBean.ParkingListBean> convertParkingData(ArrayList<PoiItem> poiList) {

        List<ParkBean.ParkingListBean> parkingList = new ArrayList<>();
        poiList.forEach(poiItem -> {
            ParkBean.ParkingListBean bean = new ParkBean.ParkingListBean();
            bean.setName(poiItem.getTitle());
            bean.setLine(false);
            bean.setDistance(poiItem.getDistance());
            bean.setLat(poiItem.getLatLonPoint().getLatitude());
            bean.setLng(poiItem.getLatLonPoint().getLongitude());
            bean.setPrice("");
            bean.setNum(-1);
            parkingList.add(bean);
        });
        return parkingList;
    }


}
