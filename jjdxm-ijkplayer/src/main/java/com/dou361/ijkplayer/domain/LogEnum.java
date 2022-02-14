package com.dou361.ijkplayer.domain;

/**
 * 日志类型
 *
 * @author Luke
 * @date 2018/7/11 上午11:00
 */
public enum LogEnum {


    /** 播放 */
    LOG_PLAY_RECORDE(1),

    /**  开始播放 */
    LOG_PLAY(2),

    /** 播放 */
    LOG_CLICK_PLAY(3),

    /** 暂停 */
    LOG_CLICK_PAUSE(4),

    /** 全屏 */
    LOG_CLICK_FULLSCREEN_ENTER(5),

    /** 退出全屏 */
    LOG_CLICK_FULLSCREEN_EXIT(6),

    LOG_SEEK_START(7),

    /** 暂停 */
    LOG_SEEK_END(8),

    /** 切换通道 */
    LOG_SWITCH_CHANNEL(9),

    /** 观看时长 */
    LOG_WATCH_TIME(10);

    private int id;

    LogEnum(int id) {

    }
}
