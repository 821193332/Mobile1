package com.kkkk.l.mobile.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kkkk.l.mobile.R;
import com.kkkk.l.mobile.bean.MediaItem;
import com.kkkk.l.mobile.utils.Utils;

import java.util.ArrayList;

public class LocalVideoAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<MediaItem> datas;
    private final boolean isVideo;
    private Utils utils;

    public LocalVideoAdapter(Context mContext, ArrayList<MediaItem> mediaItems, boolean b) {
        this.mContext = mContext;
        this.datas = mediaItems;
        utils = new Utils();
        this.isVideo =b;
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
            convertView = View.inflate(mContext, R.layout.item_local_video,null);
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

        //根据位置得到对应的数据
        MediaItem mediaItem = datas.get(position);
        viewHolder.tv_name.setText(mediaItem.getName());//设置名称
        //设置文件大小
        viewHolder.tv_size.setText(Formatter.formatFileSize(mContext,mediaItem.getSize()));
        //设置时间
        viewHolder.tv_duration.setText(utils.stringForTime((int) mediaItem.getDuration()));
    if (!isVideo){
        viewHolder.iv_icon.setImageResource(R.drawable.music_default_bg);
    }

        return convertView;
    }

    static class ViewHolder{
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
        ImageView iv_icon;

    }
}
