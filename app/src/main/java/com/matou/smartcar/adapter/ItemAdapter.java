package com.matou.smartcar.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.elvishew.xlog.XLog;
import com.matou.smartcar.R;
import com.matou.smartcar.base.BaseApplication;
import com.matou.smartcar.bean.ParkBean;
import com.matou.smartcar.global.GlobalVariables;
import com.matou.smartcar.util.CommUtils;

import java.util.List;


/**
 *
 * @author ranfeng
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private final LayoutInflater mInflater;

    private List<ParkBean.ParkingListBean> parkingList;

    private double lon;

    private double lat;
    private int select;

    private Context context;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ItemAdapter(Context context, double lon, double lat) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.lon = lon;
        this.lat = lat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.park_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        XLog.w("---> onBindViewHolder");
        ParkBean.ParkingListBean bean = parkingList.get(position);
        holder.idTv.setText(String.valueOf(position + 1));
        holder.nameTv.setText(bean.getName());
        holder.priceTv.setText(TextUtils.isEmpty(bean.getPrice()) ? "价格: 未知" : bean.getPrice());
        holder.emptyNumTv.setText("空余: " + (bean.getNum() < 0 ? "未知" : bean.getNum()));
        if(bean.isLine()){
            holder.distanceTv.setText("距离: " + CommUtils.formatDistance(bean.getDistance()));
        }else {
            holder.distanceTv.setText("距离: " + CommUtils.formatDistance(bean.getDistance()));
        }

        holder.itemClyt.setOnClickListener(v -> {
            select = position;
            notifyDataSetChanged();
            if(onItemClickListener != null){
                onItemClickListener.onItemClick(position);
            }
        });

        holder.naviIv.setOnClickListener(v -> {
            if(onItemClickListener != null){
                onItemClickListener.onItemNaviClick(position);
            }
            goNavi(lon, lat, bean.getLng(), bean.getLat(), bean.getName());
        });

        if(position == select){
            holder.itemClyt.setBackgroundResource(R.mipmap.bg_item_select);
            holder.dividerV.setVisibility(View.INVISIBLE);
        }else {
            holder.itemClyt.setBackgroundColor(Color.TRANSPARENT);
            holder.dividerV.setVisibility(View.VISIBLE);
        }

        if (position == parkingList.size() - 1){
            holder.dividerV.setVisibility(View.INVISIBLE);
        }
    }

    private void goNavi(double startLon, double startLat, double endLon, double endLat, String name) {
        Poi start = new Poi("", new LatLng(startLat, startLon), "");
        if (endLat == 0 || endLon == 0) {
            Toast.makeText(context, "停车场地址信息错误", Toast.LENGTH_SHORT).show();
            return;
        }
        Poi end = new Poi(name, new LatLng(endLat, endLon), "");
        AmapNaviParams amapNaviParams = new AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.NAVI);
        amapNaviParams.setUseInnerVoice(true);
        //amapNaviParams.showVoiceAssistEnable();
        // mock
        // amapNaviParams.setNaviMode(2);
        GlobalVariables.amapNaviParams = amapNaviParams;
        AmapNaviPage.getInstance().showRouteActivity(BaseApplication.getInstance(), amapNaviParams, iNaviInfoCallback);

        /*Intent intent = new Intent(context, RouteNaviActivity.class);
        // todo: 正式环境应该为true
        intent.putExtra("gps", true);
        context.startActivity(intent);*/

    }

    public static INaviInfoCallback iNaviInfoCallback = new INaviInfoCallback() {
        @Override
        public void onInitNaviFailure() {

        }

        @Override
        public void onGetNavigationText(String s) {

        }

        @Override
        public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

        }

        @Override
        public void onArriveDestination(boolean b) {

        }

        @Override
        public void onStartNavi(int i) {
            GlobalVariables.startNavi = true;
        }

        @Override
        public void onCalculateRouteSuccess(int[] ints) {

        }

        @Override
        public void onCalculateRouteFailure(int i) {

        }

        @Override
        public void onStopSpeaking() {

        }

        @Override
        public void onReCalculateRoute(int i) {

        }

        @Override
        public void onExitPage(int i) {
            GlobalVariables.startNavi = false;
            GlobalVariables.amapNaviParams = null;
        }

        @Override
        public void onStrategyChanged(int i) {

        }

        @Override
        public void onArrivedWayPoint(int i) {

        }

        @Override
        public void onMapTypeChanged(int i) {

        }

        @Override
        public void onNaviDirectionChanged(int i) {

        }

        @Override
        public void onDayAndNightModeChanged(int i) {

        }

        @Override
        public void onBroadcastModeChanged(int i) {

        }

        @Override
        public void onScaleAutoChanged(boolean b) {

        }

        @Override
        public View getCustomMiddleView() {
            return null;
        }

        @Override
        public View getCustomNaviView() {
            return null;
        }

        @Override
        public View getCustomNaviBottomView() {
            return null;
        }
    };



    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<ParkBean.ParkingListBean> newData) {
        parkingList = newData;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return parkingList == null ? 0 : parkingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ConstraintLayout itemClyt;
        public TextView idTv;
        public TextView nameTv;
        public TextView priceTv;
        public TextView emptyNumTv;
        public TextView distanceTv;
        public ImageView naviIv;

        public View dividerV;

        ViewHolder(View itemView) {
            super(itemView);
            itemClyt = itemView.findViewById(R.id.clyt_item);
            idTv = itemView.findViewById(R.id.tv_id);
            nameTv = itemView.findViewById(R.id.tv_name);
            priceTv = itemView.findViewById(R.id.tv_price);
            emptyNumTv = itemView.findViewById(R.id.tv_empty_num);
            distanceTv = itemView.findViewById(R.id.tv_distance);
            naviIv = itemView.findViewById(R.id.iv_navi);
            dividerV = itemView.findViewById(R.id.v_divider);
        }
    }

    public interface OnItemClickListener{

        void onItemClick(int pos);

        void onItemNaviClick(int pos);

    }

}
