package com.kkkk.l.mobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kkkk.l.mobile.R;
import com.kkkk.l.mobile.bean.MediaItem;
import com.kkkk.l.mobile.utils.Utils;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;

import java.util.ArrayList;


public class NetVideoAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<MediaItem> datas;
    private Utils utils;
    private ImageOptions imageOptions;

    public NetVideoAdapter(Context mContext, ArrayList<MediaItem> mediaItems) {
        this.mContext = mContext;
        this.datas = mediaItems;
        utils = new Utils();

        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                .setRadius(DensityUtil.dip2px(5))

                .setCrop(true)

                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.video_default)
                .setFailureDrawableId(R.drawable.video_default)
                .build();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView = View.inflate(mContext, R.layout.item_net_video,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
            viewHolder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            //设置tag
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }


        MediaItem mediaItem = datas.get(position);
        viewHolder.tv_name.setText(mediaItem.getName());

        viewHolder.tv_size.setText(mediaItem.getDuration()+"秒");

        viewHolder.tv_duration.setText(mediaItem.getDesc());


      //  x.image().bind(viewHolder.iv_icon,mediaItem.getImageUrl(),imageOptions);
        //使用Glide或者Picasso请求图片
//        Picasso.with(mContext)
//                .load(mediaItem.getImageUrl())
//                .placeholder(R.drawable.video_default)
//                .error(R.drawable.video_default)
//                .into(viewHolder.iv_icon);
        Glide.with(mContext)
                .load(mediaItem.getImageUrl())
                .placeholder(R.drawable.video_default)
                .error(R.drawable.video_default)
                .into(viewHolder.iv_icon);


        return convertView;
    }

    static class ViewHolder{
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
        ImageView iv_icon;

    }
}
