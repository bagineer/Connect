package com.example.connect;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String HOST = "192.168.1.4";
    private static final int PORT = 8080;
    private static final String TAG = "MyApplication";

    private Socket s;
    private OutputStream sender;
    private EditText editText;
    private String name = "admin";
    private byte[] bytes;

    // For Debugging
    private Set<Thread> threadSet;
    private int cnt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText)findViewById(R.id.msgText);
    }

    public void mOnClick(View v) {
        switch (v.getId()) {
            case R.id.cntBtn:
                connect();
                break;
            case R.id.sendBtn:
                sendMsg();
                break;
        }
    }

    public void connect() {
        s = new Socket();
        InetSocketAddress addr = new InetSocketAddress(HOST, PORT);
        MyThread thread = new MyThread(addr);
        thread.setDaemon(true);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg() {
        cnt++;

        String msg = editText.getText().toString();
        bytes = msg.getBytes();
        Log.d(TAG, "Message : "+msg);

        SendThread st = new SendThread();
        new Thread(st, "Sender"+cnt).start();

        threadSet = Thread.getAllStackTraces().keySet();
        for(Thread thread : threadSet) {
//            Log.d(TAG, "name : "+thread.getName() + ((thread.isDaemon())? "Daemon":"Main"));
            Log.d(TAG, "name : "+thread.getName()+"is alive ? " + thread.isAlive());
        }
//        Log.d(TAG, "active : "+threadSet);
        Log.d(TAG, "active threads are  "+Thread.activeCount());

//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    sender = s.getOutputStream();
//                    sender.write(bytes);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
    }

    class MyThread extends Thread {

        InetSocketAddress addr;

        public MyThread(InetSocketAddress address) {
            addr = address;
        }

        @Override
        public void run() {
            try {
                s.connect(addr);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Connected");
        }
    }

    class SendThread implements Runnable {

        @Override
        public void run() {
            try {
                sender = s.getOutputStream();
                sender.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class MyData {

        private List<String> msg = new ArrayList<String>();

        public MyData(String code, String name, int n, List<String> data) {
            msg = Arrays.asList(code, name, Integer.toString(n), data.toString());
        }

        public String encode() {
            return TextUtils.join("\\", msg);
        }
    }
}