package com.oscar.mobilesafe;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.oscar.mobilesafe.dao.BlackNumDao;

/**
 * Created by Administrator on 2016/10/14 0014.
 */
public class BlackNumTest extends ApplicationTestCase<Application> {
    public BlackNumTest() {
        super(Application.class);
    }

    public void testInsert() {
        BlackNumDao blackNumDao = BlackNumDao.getInstance(getContext());
        for(int i=0; i<100; i++) {
            blackNumDao.insert("110" + i, "1");
        }
    }

    public void testDelete() {
        BlackNumDao blackNumDao = BlackNumDao.getInstance(getContext());
        blackNumDao.delete("110");
    }

    public void testUpdate() {
        BlackNumDao blackNumDao = BlackNumDao.getInstance(getContext());
        blackNumDao.update("110", "2");
    }

    public void testFindAll() {
        BlackNumDao blackNumDao = BlackNumDao.getInstance(getContext());
        blackNumDao.findAll();
    }
}
