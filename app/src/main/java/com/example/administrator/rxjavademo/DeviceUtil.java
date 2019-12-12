package com.example.administrator.rxjavademo;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.util.Locale;

/** Created by lizhen on 2016/12/5. */
public class DeviceUtil {
  /**
   * 获取手机分辨率 480*800的格式输出
   *
   * @param context
   * @return
   */
  public static String getDeviceDisplayMetrics(Context context) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    return String.valueOf(displayMetrics.widthPixels)
        .concat("*")
        .concat(String.valueOf(displayMetrics.heightPixels));
  }

  /**
   * 获取屏幕的宽度
   *
   * @param context
   * @return
   */
  public static int getScreenWidth(Context context) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    return displayMetrics.widthPixels;
  }

  /**
   * 获取屏幕的宽度
   *
   * @param context
   * @return
   */
  public static int getRealScreenWidth(Context context) {
    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    final Display display = windowManager.getDefaultDisplay();
    Point outPoint = new Point();
    if (Build.VERSION.SDK_INT >= 19) {
      // 可能有虚拟按键的情况
      display.getRealSize(outPoint);
    } else {
      // 不可能有虚拟按键
      display.getSize(outPoint);
    }
    return outPoint.x;
  }

  /**
   * 获取屏幕的高度
   *
   * @param context
   * @return
   */
  public static int getScreenHeight(Context context) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    return displayMetrics.heightPixels;
  }

  /** UA信息，设备型号 */
  public static String getProductType() {
    return Build.MODEL;
  }

  /** 生产厂商 */
  public static String getManufacturer() {
    return Build.MANUFACTURER;
  }

  /** 系统版本 */
  public static String getSystemVersion() {
    return Build.VERSION.RELEASE;
  }

  /**
   * 获取系统首选语言
   *
   * @return
   */
  public static String getSysLanguage() {
    Locale locale;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      locale = LocaleList.getDefault().get(0);
    } else {
      locale = Locale.getDefault();
    }

    String language = locale.getLanguage() + "-" + locale.getCountry();
    return language;
  }

  public static int dp2px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  public static float sp2px(Context context, float spValue) {
    final float scale = context.getResources().getDisplayMetrics().scaledDensity;
    return spValue * scale;
  }
}
