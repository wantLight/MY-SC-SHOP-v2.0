package com.imooc.myo2o.exception;

/**
 * Created by xyzzg on 2018/7/11.
 * 对RuntimeException封装
 */
public class ShopOperationException extends RuntimeException{

    public ShopOperationException(String message) {
        super(message);
    }
}
