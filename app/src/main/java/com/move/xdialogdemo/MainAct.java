package com.move.xdialogdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.move.xdialog.XDialog;

public class MainAct extends AppCompatActivity {

    private Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            XDialog.close(MainAct.this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
    }

    /**
     * 打开对话框,标记:tag1
     *
     * @param view
     */
    public void open(View view) {
        XDialog.show(this);
        h.sendEmptyMessageDelayed(0, 8000);
    }


}
