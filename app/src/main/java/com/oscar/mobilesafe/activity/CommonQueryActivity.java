package com.oscar.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.engine.CommonNumDao;

import java.util.List;

public class CommonQueryActivity extends Activity {
    private ExpandableListView mEblv;

    private List<CommonNumDao.Group> mGroups;

    private CommonNumAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_quey);

        initViews();

        initDatas();

        initEvents();

    }

    private void initEvents() {
        mEblv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                startCall(mAdapter.getChild(groupPosition, childPosition).number);
                return false;
            }
        });
    }

    /**
     * 拨打电话
     */
    private void startCall(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDatas() {
        CommonNumDao dao = new CommonNumDao();
        mGroups = dao.getGroup();
        mAdapter = new CommonNumAdapter();
        mEblv.setAdapter(mAdapter);
    }

    class CommonNumAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return mGroups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mGroups.get(groupPosition).childs.size();
        }

        @Override
        public CommonNumDao.Group getGroup(int groupPosition) {
            return mGroups.get(groupPosition);
        }

        @Override
        public CommonNumDao.Child getChild(int groupPosition, int childPosition) {
            return mGroups.get(groupPosition).childs.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getApplicationContext());
            tv.setText("   " + mGroups.get(groupPosition).name);
            tv.setTextColor(Color.BLUE);
            tv.setTextSize(20);
            return tv;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.expandlistview_child_item, null);
            TextView tvName = (TextView) view.findViewById(R.id.tv_commonnum_name);
            TextView tvPhone = (TextView) view.findViewById(R.id.tv_commonnum_phone);

            tvName.setText(getChild(groupPosition, childPosition).name);
            tvPhone.setText(getChild(groupPosition, childPosition).number);

            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }


    private void initViews() {
        mEblv = (ExpandableListView) findViewById(R.id.eblv);
    }
}
