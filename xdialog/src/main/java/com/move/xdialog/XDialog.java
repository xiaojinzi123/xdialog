package com.move.xdialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by cxj on 2017/1/16.
 * 统一正在加载对话框的风格,注意这个
 * 弹出一个对话框需要一个
 * Context,而且必须是Activity的,然后一个Context对应一个Dialog
 * <p>
 * 在一个Context下面的可以通过tag多次弹出,但是弹出的时候并不是真的弹出多个,而且维护了这个对话框和这些个tag的关系
 * 只有当这个对话框关联的所有tag都没有了,这个对话框才会消失
 * tag可以是重复的
 */
public class XDialog {

    /**
     * 上下文关联的对话框,这里的上下文其实是一个Activity哦
     */
    private static Map<Activity, MyDialog> dialogMap = new HashMap<>();

    /**
     * 初始化一个Dialog
     *
     * @param context
     */
    private static MyDialog init(final Activity context, final boolean cancelEable) {

        //创建显示的视图
        View contentView = View.inflate(context, R.layout.dialog1, null);

        //创建对话框
        MyDialog dialog = new MyDialog(context, R.style.original_bg_windows_transparent_theme);

        //设置到dialog上面
        dialog.setContentView(contentView);

        //点击额外区域不能销毁
        dialog.setCanceledOnTouchOutside(false);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return !cancelEable;
            }
        });

        //返回对话框
        return dialog;
    }

    /**
     * 弹出不能取消的加载框框
     *
     * @param context
     */
    public static void show(Activity context) {
        show(context, false);
    }

    /**
     * 显示加载框框
     *
     * @param context     上下文
     * @param cancelEable 是否能点击其他区域取消
     */
    public static void show(final Activity context, boolean cancelEable) {

        // 通过上下文获取Dialog
        MyDialog dialog = dialogMap.get(context);

        // 说明这个Activity对应的Context是第一次弹出
        if (dialog == null) {
            // 创建一个对话框
            dialog = init(context, cancelEable);
            // 放入集合中
            dialogMap.put(context, dialog);
        }

        if (dialog.isShowing()) { //如果这个dialog正在显示,那么直接跳过显示的代码
            return;
        }

        //===============================下面是开启对话框的动画================================

        //旋转动画
        RotateAnimation animation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1600);
        //匀速前进
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        dialog.findViewById(R.id.iv).startAnimation(animation);

        //显示对话框
        dialog.show();

        //===============================下面是监听对话框的结束,那么释放相关的资源================================

        //当对话框销毁的时候检查是否在集合中移除了引用
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //首先找到这个对话框关联的上下文
                Context mContext = getContextByDialog((MyDialog) dialog);

                //如果早到了上下文,那么也释放这个上下文对话的tags集合
                if (mContext != null) {
                    dialogMap.remove(mContext);
                }
                //释放对话框
                dialog = null;
            }
        });


    }

    /**
     * 通过对话框找到上下文
     *
     * @param myDialog
     * @return
     */
    private static Activity getContextByDialog(MyDialog myDialog) {
        Iterator<Map.Entry<Activity, MyDialog>> it = dialogMap.entrySet().iterator();
        Activity mContext = null;
        while (it.hasNext()) {
            Map.Entry<Activity, MyDialog> entity = it.next();
            Dialog mDialog = entity.getValue();
            if (myDialog == mDialog) {
                return entity.getKey();
            }
        }
        return mContext;
    }

    /**
     * 关闭对话框
     *
     * @param context
     */
    public static void close(Activity context) {

        //移除上下文对应的Dialog
        Dialog mDialog = dialogMap.remove(context);
        if (mDialog == null) {
            return;
        }
        //让对话框消失
        mDialog.dismiss();
        //释放资源
        mDialog = null;


    }


}
