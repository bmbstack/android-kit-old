package com.github.bmbstack.androidkit.component.photopick.model;

import android.os.Parcel;

import java.io.File;
import java.io.Serializable;

public class PhotoInfo implements Serializable {
    public String path;
    public long photoId;
    public int width;
    public int height;
    private static final String prefix = "file://";

    public PhotoInfo(String path) {
        this.path = pathAddPreFix(path);
    }

    protected PhotoInfo(Parcel in) {
        path = in.readString();
        photoId = in.readLong();
        width = in.readInt();
        height = in.readInt();
    }

    public static String pathAddPreFix(String path) {
        if (path == null) {
            path = "";
        }

        if (!path.startsWith(prefix)) {
            path = prefix + path;
        }
        return path;
    }

    public static boolean isLocalFile(String path) {
        return path.startsWith(prefix);
    }


    public static File getLocalFile(String pathSrc) {
        String pathDesc = pathSrc;
        if (isLocalFile(pathSrc)) {
            pathDesc = pathSrc.substring(prefix.length(), pathSrc.length());
        }

        return new File(pathDesc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhotoInfo photoInfo = (PhotoInfo) o;

        if (photoId != photoInfo.photoId) return false;
        if (width != photoInfo.width) return false;
        if (height != photoInfo.height) return false;
        return !(path != null ? !path.equals(photoInfo.path) : photoInfo.path != null);

    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (int) (photoId ^ (photoId >>> 32));
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }
}
