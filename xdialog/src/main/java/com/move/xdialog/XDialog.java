package com.move.xdialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

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
     * 关闭这个对话框,并且删除这个对话框所有的tag
     * 如果传入这个tag,那么就关闭这个对话框并且释放这个对话框对应的所有的联系
     */
    public static final String All_CANCEL_TAG = "xDialog_all_cancel";

    /**
     * 上下文关联的对话框,这里的上下文其实是一个Activity哦
     */
    private static Map<Context, MyDialog> dialogMap = new HashMap<Context, MyDialog>();

    /**
     * 上下文关联的一组tag
     */
    private static Map<Context, Map<String, Boolean>> tagMap = new HashMap<Context, Map<String, Boolean>>();


    /**
     * 初始化一个Dialog
     *
     * @param context
     */
    private static MyDialog init(Context context) {
        //创建显示的视图
        View contentView = View.inflate(context, R.layout.dialog1, null);

        //创建对话框
        MyDialog dialog = new MyDialog(context, R.style.original_bg_windows_transparent_theme);

        //设置到dialog上面
        dialog.setContentView(contentView);

        //点击额外区域不能销毁
        //dialog.setCanceledOnTouchOutside(false);

        //返回对话框
        return dialog;
    }

    /**
     * 弹出不能取消的加载框框
     *
     * @param context
     * @param tag
     */
    public static void show(Context context, String tag) {
        show(context, tag, false);
    }


    /**
     * 显示加载框
     *
     * @param context     上下文
     * @param tag         标识
     * @param cancelEable 是否能点击其他区域取消
     */
    public static void show(Context context, String tag, boolean cancelEable) {
        show(context, tag, "正在加载", cancelEable);
    }


    /**
     * 显示加载框框
     *
     * @param context     上下文
     * @param tag         标识
     * @param content     提示的内容
     * @param cancelEable 是否能点击其他区域取消
     */
    public static void show(final Context context, String tag, String content, boolean cancelEable) {

        //通过上下文获取Dialog
        MyDialog dialog = dialogMap.get(context);

        //说明这个Activity对应的Context是第一次弹出
        if (dialog == null) {
            //创建一个对话框
            dialog = init(context);
            //放入集合中
            dialogMap.put(context, dialog);
        }

        //显示文本
        TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
        tv_content.setText(content);

        //拿到上下文关联的Tag集合
        Map<String, Boolean> cancelEableMap = tagMap.get(context);
        //如果集合是空的就创建
        if (cancelEableMap == null) {
            cancelEableMap = new HashMap<String, Boolean>();
            tagMap.put(context, cancelEableMap);
        }

        //添加新的tag到set集合中,如果有重复就覆盖
        cancelEableMap.put(tag, cancelEable);

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
                    tagMap.remove(mContext);
                }
                //释放对话框
                dialog = null;
            }
        });

        //监听准备关闭的监听
        dialog.setOnPrepareOnDismissListener(new MyDialog.OnPrepareOnDismissListener() {
            @Override
            public boolean onPrepareOnDismiss(MyDialog dialog) {
                //首先找到这个对话框关联的上下文
                Context mContext = getContextByDialog(dialog);
                //如果早到了上下文,那么拿到了上下文对话的tags集合
                if (mContext != null) {
                    //检查这个tags集合是否还有一个tag设置了不能取消的属性
                    Map<String, Boolean> tagsMap = tagMap.get(mContext);
                    Iterator<Map.Entry<String, Boolean>> it = tagsMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<String, Boolean> entry = it.next();
                        Boolean value = entry.getValue();
                        if (!value) { //表示找到了一个不能取消的tag,那么这个对话框就不应该被销毁
                            return false;
                        }
                    }
                    //如果找到最后都没有找到一个tag是不能取消的,那么就统一销毁对话框
                    return true;
                }
                return true;
            }
        });


    }

    /**
     * 通过对话框找到上下文
     *
     * @param myDialog
     * @return
     */
    private static Context getContextByDialog(MyDialog myDialog) {
        Iterator<Map.Entry<Context, MyDialog>> it = dialogMap.entrySet().iterator();
        Context mContext = null;
        while (it.hasNext()) {
            Map.Entry<Context, MyDialog> entity = it.next();
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
     * @param tag
     */
    public static void close(String tag) {
        //首先通过tag找到上下文
        Iterator<Map.Entry<Context, Map<String, Boolean>>> it = tagMap.entrySet().iterator();
        //循环上下文-tags集合的集合
        while (it.hasNext()) {
            //拿到上下文-tags集合
            Map.Entry<Context, Map<String, Boolean>> mapEntry = it.next();
            //拿到tags集合
            Map<String, Boolean> mTagMap = mapEntry.getValue();
            //如果包含这个key,说明这个上下文显示对话框之前已经调用过了
            if (mTagMap.containsKey(tag)) { //表示这个tag在之前show方法调用过的
                //移除这个tag
                mTagMap.remove(tag);
                //检查是否map中是否还有其他的tag
                int size = mTagMap.size();
                if (size == 0) { //如果为0,说明这个Context对应的tag集合已经空了,那么就销毁对话框
                    //拿到上下文
                    Context context = mapEntry.getKey();
                    //移除这个Context对应的所有tag集合
                    tagMap.remove(context);
                    //移除上下文对应的Dialog
                    Dialog mDialog = dialogMap.remove(context);
                    //让对话框消失
                    mDialog.dismiss();
                    //释放资源
                    mDialog = null;
                }
            }
        }
    }


}
