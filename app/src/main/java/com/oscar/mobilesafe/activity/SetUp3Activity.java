package com.oscar.mobilesafe.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.oscar.mobilesafe.R;
import com.oscar.mobilesafe.utils.ConstentValue;
import com.oscar.mobilesafe.utils.SpUtil;
import com.oscar.mobilesafe.utils.ToastUtil;

public class SetUp3Activity extends BaseSetUpActivity {
    private static final int CONTACT_REQUEST_CODE = 0;
    private Context mContext;
    private EditText mEtNumber;
    private Button mBtnSelectContact;//选择联系人

    private String mContactNumber;//联系人电话号码


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up3);

        mContext = this;

        initViews();

        initDatas();

        initEvents();
    }

    @Override
    public void showNextPage() {
        String contactPhone = mEtNumber.getText().toString();
        if(!TextUtils.isEmpty(contactPhone)) {
            Intent intent = new Intent(mContext, SetUp4Activity.class);
            startActivity(intent);

            finish();

            SpUtil.putString(mContext, ConstentValue.CONTACT_PHONE, contactPhone);
            //平移动画
            overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
        } else {
            ToastUtil.show(mContext, "请输入联系人电话");
        }
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(mContext, SetUp2Activity.class);
        startActivity(intent);

        finish();
        //平移动画
        overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
    }

    private void initDatas() {
        String contactPhone = SpUtil.getString(mContext, ConstentValue.CONTACT_PHONE, "");
        mEtNumber.setText(contactPhone);
    }

    /**
     * 选择联系人
     */
    private void initEvents() {
        mBtnSelectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, CONTACT_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            ContentResolver resolver = getContentResolver();
            Uri contactData = data.getData();
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            while(phoneCursor.moveToNext()) {
                mContactNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mEtNumber.setText(mContactNumber);
            }

            SpUtil.putString(mContext, ConstentValue.CONTACT_PHONE, mContactNumber);
        }
    }

    private void initViews() {
        mEtNumber = (EditText) findViewById(R.id.et_number);
        mBtnSelectContact = (Button) findViewById(R.id.btn_select_contact);
    }


}
