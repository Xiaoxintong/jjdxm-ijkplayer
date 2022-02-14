package com.dou361.ijkplayer.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.provider.Settings;
import android.util.Log;

/**
 * [文件描述]
 *
 * @author Luke
 * @date 2018/7/21 下午3:15
 */
public class BrightnessUtil {
        private static final String TAG = BrightnessUtil.class.getSimpleName();
        /**
         * 判断是否开启了自动亮度调节
         */
        public static boolean isAutoBrightness(Activity activity) {
            boolean automicBrightness = false;
            try {
                automicBrightness = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
            } catch (Settings.SettingNotFoundException e){
                e.printStackTrace();
            }
            return automicBrightness;
        }

        /**
         * 获取屏幕的亮度
         */
        public static int getScreenBrightness(Activity activity) {
            if(isAutoBrightness(activity)){
                return getAutoScreenBrightness(activity);
            }else{
                return getManualScreenBrightness(activity);
            }
        }

        /**
         * 获取手动模式下的屏幕亮度
         */
        public static int getManualScreenBrightness(Activity activity) {
            int nowBrightnessValue = 0;
            ContentResolver resolver = activity.getContentResolver();
            try {
                nowBrightnessValue = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return nowBrightnessValue;
        }

        /**
         * 获取自动模式下的屏幕亮度
         */
        public static int getAutoScreenBrightness(Activity activity) {
            float nowBrightnessValue = 0;
            ContentResolver resolver = activity.getContentResolver();
            try {
                //[-1,1],无法直接获取到Setting中的值，以字符串表示
                nowBrightnessValue = Settings.System.getFloat(resolver, "screen_auto_brightness_adj");
                Log.d(TAG, "[ouyangyj] Original AutoBrightness Value:" + nowBrightnessValue);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //[0,2]
            float tempBrightness = nowBrightnessValue + 1.0f;
            float fValue = (tempBrightness/2.0f)*225.0f;
            Log.d(TAG,"[ouyangyj] Converted Value: " + fValue);
            return (int)fValue;
        }

}
