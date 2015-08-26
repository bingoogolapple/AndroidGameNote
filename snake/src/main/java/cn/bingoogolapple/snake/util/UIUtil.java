package cn.bingoogolapple.snake.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/8/26 下午8:14
 * 描述:
 */
public class UIUtil {
    private UIUtil() {
    }

    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }
}