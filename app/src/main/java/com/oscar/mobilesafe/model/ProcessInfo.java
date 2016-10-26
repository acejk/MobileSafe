package com.oscar.mobilesafe.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/10/20 0020.
 */
public class ProcessInfo {
    public String name;//应用名称
    public Drawable icon;//应用图标
    public long memSize;//内存大小
    public String packageName;//如果进程没有名称,则将其所在应用的包名则为名称
    public boolean isSystem;//是否是系统应用
    public boolean isCheck;//是否被选中

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }
}
