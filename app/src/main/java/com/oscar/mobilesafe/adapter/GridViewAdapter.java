package com.oscar.mobilesafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oscar.mobilesafe.R;

/**
 * Created by Administrator on 2016/8/30 0030.
 */
public class GridViewAdapter extends BaseAdapter {
    private String[] mTitles;
    private int[] mIcons;

    private LayoutInflater mInflater;

    private Context mContext;

    public GridViewAdapter(Context context, String[] titles, int[] icons) {
        this.mContext = context;
        this.mTitles = titles;
        this.mIcons = icons;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public Object getItem(int position) {
        return mTitles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_gridview, parent, false);
            viewHolder.mTvTitle = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.mIvIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mIvIcon.setBackgroundResource(mIcons[position]);
        viewHolder.mTvTitle.setText(mTitles[position]);
        return convertView;
    }

    class ViewHolder {
        public ImageView mIvIcon;
        public TextView mTvTitle;
    }
}
