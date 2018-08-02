package com.imooc.myo2o.vo;

import java.io.InputStream;

/**
 * 这是一个代码重构，便于公共参数的理解
 * Created by xyzzg on 2018/7/25.
 */
public class ImageHolder {

    private String imageName;
    private InputStream image;

    public ImageHolder(String imageName,InputStream image) {
        this.imageName = imageName;
        this.image = image;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public InputStream getImage() {
        return image;
    }

    public void setImage(InputStream image) {
        this.image = image;
    }
}
