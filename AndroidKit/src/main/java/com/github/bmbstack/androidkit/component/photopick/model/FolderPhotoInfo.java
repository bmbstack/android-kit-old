package com.github.bmbstack.androidkit.component.photopick.model;

public class FolderPhotoInfo {
    private PhotoInfo mPhotoInfo;
    private int mCount = 0;
    private String mName = "";

    public FolderPhotoInfo(String name, PhotoInfo mPhotoInfo, int count) {
        mName = name;
        this.mPhotoInfo = mPhotoInfo;
        mCount = count;
    }

    public String getPath() {
        return mPhotoInfo.path;
    }

    public int getCount() {
        return mCount;
    }

    public String getmName() {
        return mName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FolderPhotoInfo that = (FolderPhotoInfo) o;

        if (mCount != that.mCount) return false;
        return mPhotoInfo.equals(that.mPhotoInfo);

    }

    @Override
    public int hashCode() {
        int result = mPhotoInfo.hashCode();
        result = 31 * result + mCount;
        return result;
    }
}
