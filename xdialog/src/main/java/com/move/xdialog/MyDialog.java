package com.move.xdialog;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;

/**
 * Created by cxj on 2017/2/8.
 */
public class MyDialog extends Dialog {

    public MyDialog(Context context) {
        super(context);
    }

    public MyDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mOnPrepareOnDismissListener != null) {
            boolean b = mOnPrepareOnDismissListener.onPrepareOnDismiss(this);
            if (b) {
                return super.onTouchEvent(event);
            } else {
                return false;
            }
        }
        return super.onTouchEvent(event);
    }

    private OnPrepareOnDismissListener mOnPrepareOnDismissListener;

    public void setOnPrepareOnDismissListener(OnPrepareOnDismissListener mOnPrepareOnDismissListener) {
        this.mOnPrepareOnDismissListener = mOnPrepareOnDismissListener;
    }

    /**
     * 准备销毁的监听
     */
    public interface OnPrepareOnDismissListener {
        /**
         * 准备销毁的回调,如果返回true表示销毁,false表示不销毁
         *
         * @param dialog
         * @return
         */
        boolean onPrepareOnDismiss(MyDialog dialog);
    }


}
