package com.dou361.ijkplayer.listener;

import com.dou361.ijkplayer.domain.LogEnum;

/**
 * ========================================
 * <p/>
 * 版 权：dou361.com 版权所有 （C） 2015
 * <p/>
 * 作 者：陈冠明
 * <p/>
 * 个人网站：http://www.dou361.com
 * <p/>
 * 版 本：1.0
 * <p/>
 * 创建日期：2016/8/12
 * <p/>
 * 描 述：视频中返回键监听
 * <p/>
 * <p/>
 * 修订历史：
 * <p/>
 * ========================================
 */
public interface OnLogListener {

    void onLog(LogEnum logEnum, Object data);

}
