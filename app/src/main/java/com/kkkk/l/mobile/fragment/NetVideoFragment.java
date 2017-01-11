package com.kkkk.l.mobile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.kkkk.l.mobile.R;
import com.kkkk.l.mobile.activity.SystemVideoPlayerActivity;
import com.kkkk.l.mobile.adapter.NetVideoAdapter;
import com.kkkk.l.mobile.base.BaseFragment;
import com.kkkk.l.mobile.bean.MediaItem;
import com.kkkk.l.mobile.utils.CacheUtils;
import com.kkkk.l.mobile.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;


/**
 * Created by KangYueLong on 2017/1/6.
 */

public class NetVideoFragment extends BaseFragment {


    private ArrayList<MediaItem> mediaItems;
    private NetVideoAdapter adapter;

    @ViewInject(R.id.listview)
    private ListView listview;

    @ViewInject(R.id.tv_no_media)
    private  TextView tv_no_media;
    @ViewInject(R.id.refresh)
    MaterialRefreshLayout refreshLayout;

    @Override
    public View initView() {
        Log.e("TAG","网络视频ui初始化了。。");
        View view  = View.inflate(mContext, R.layout.fragment_net_video,null);
        //把view注入到xUtils3框中
        x.view().inject(NetVideoFragment.this,view);
        //才初始化好的

        listview.setOnItemClickListener(new MyOnItemClickListener());

        refreshLayout.setMaterialRefreshListener(new MyMaterialRefreshListener());

        return view;
    }
    private boolean isLoadMore =false;
  class MyMaterialRefreshListener extends MaterialRefreshListener{

      @Override
      public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
          isLoadMore =false;
         getDataFromNet();

      }

      @Override
      public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
          super.onRefreshLoadMore(materialRefreshLayout);
          isLoadMore=true;
          getDataFromNet();
      }
  }
    class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent,View view, int position, long id) {
            //传递列表数据
            Intent intent = new Intent(mContext,SystemVideoPlayerActivity.class);

            Bundle bundle = new Bundle();
            //列表数据
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            //传递点击的位置
            intent.putExtra("position",position);


            startActivity(intent);

        }
    }

    @Override
    public void initData() {
        super.initData();
        Log.e("TAG","网络视频数据初始化了。。");
       String json = CacheUtils.getString(mContext,Constant.NET_URL);
        if (!TextUtils.isEmpty(json)){
            processData(json);
        }
        getDataFromNet();
    }


    private void getDataFromNet() {
        //网络的路径
        RequestParams params  = new RequestParams(Constant.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Log.e("TA","xUtils3联网请求成功==");
                Log.e("TAG","线程名称=="+Thread.currentThread().getName());
                CacheUtils.putString(mContext,Constant.NET_URL,result);
                processData(result);
                if (!isLoadMore){
                    refreshLayout.finishRefresh();
                }else {
                    refreshLayout.finishRefreshLoadMore();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG","xUtils3请求失败了=="+ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }



    private void processData(String json) {
        if (!isLoadMore){
            mediaItems = parsedJson(json);

            Log.e("TAG","mediaItems.get(0).getName()=="+mediaItems.get(0).getName());
            if(mediaItems != null && mediaItems.size() >0){
                //有数据
                tv_no_media.setVisibility(View.GONE);
                adapter = new NetVideoAdapter(mContext,mediaItems);
                //设置适配器
                listview.setAdapter(adapter);

            }else{
                tv_no_media.setVisibility(View.VISIBLE);
            }
        }else {
            ArrayList<MediaItem> mediaItem =parsedJson(json);
             mediaItems.addAll(mediaItem);
            adapter.notifyDataSetChanged();

        }

    }


    private ArrayList<MediaItem> parsedJson(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("trailers");


            for (int i=0;i<jsonArray.length();i++){

                MediaItem mediaItem = new MediaItem();

                mediaItems.add(mediaItem);//添加到集合中

                JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
                String name = jsonObjectItem.optString("movieName");
                mediaItem.setName(name);
                String desc = jsonObjectItem.optString("videoTitle");
                mediaItem.setDesc(desc);
                String url = jsonObjectItem.optString("url");
                mediaItem.setData(url);
                String hightUrl = jsonObjectItem.optString("hightUrl");
                mediaItem.setHeightUrl(hightUrl);
                String coverImg = jsonObjectItem.optString("coverImg");
                mediaItem.setImageUrl(coverImg);
                int videoLength = jsonObjectItem.optInt("videoLength");
                mediaItem.setDuration(videoLength);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return mediaItems;
    }

    @Override
    public void onRefrshData() {
        super.onRefrshData();

    }
}