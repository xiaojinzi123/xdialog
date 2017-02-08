package com.move.xdialogdemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.move.xdialog.XDialog;

public class MainAct extends AppCompatActivity {

    private int[] arr = new int[]{1,3,4,2};

    private Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int what = msg.what;

            if (what == arr.length) {
                what = 0;
            }

            int flag = arr[what];

            switch (flag) {
                case 1:
                    openWithTa1(null);
                    break;
                case 2:
                    closeWithTa1(null);
                    break;
                case 3:
                    openCanCloseWithTa1(null);
                    break;
                case 4:
                    closeWithTa2(null);
                    break;
            }



            h.sendEmptyMessageDelayed(what + 1, 4000);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        h.sendEmptyMessageDelayed(0, 2000);

    }

    /**
     * 打开对话框,标记:tag1
     *
     * @param view
     */
    public void openWithTa1(View view) {
        XDialog.show(this, "tag1");
        Toast.makeText(this, "打开不可关闭加载框:tag1", Toast.LENGTH_LONG).show();
    }

    public void closeWithTa1(View view) {
        XDialog.close("tag1");
        Toast.makeText(this, "关闭加载框:tag1", Toast.LENGTH_LONG).show();
    }

    /**
     * 打开可销毁对话框,标记:tag2
     *
     * @param view
     */
    public void openCanCloseWithTa1(View view) {
        XDialog.show(this, "tag2", true);
        Toast.makeText(this, "打开可以关闭加载框:tag2", Toast.LENGTH_LONG).show();
    }

    public void closeWithTa2(View view) {
        XDialog.close("tag2");
        Toast.makeText(this, "关闭加载框:tag2", Toast.LENGTH_LONG).show();
    }

}
