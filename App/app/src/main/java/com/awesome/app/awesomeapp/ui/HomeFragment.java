package com.awesome.app.awesomeapp.ui;

import android.Manifest;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.awesome.app.awesomeapp.R;
import com.awesome.app.awesomeapp.util.Data;
import com.awesome.app.awesomeapp.util.EventRecognitionService;
import com.awesome.app.awesomeapp.util.EventServiceBinder;
import com.awesome.app.awesomeapp.util.NotificationHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends Fragment implements ServiceConnection , EventRecognitionService.Callback {

    @BindView(R.id.mic_status_msg)
    TextView mic_status_msg;
    @BindView(R.id.finalClass)
    Button finalClass;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private boolean recording = false;
    private boolean hasPermission = false;
    private int samplingRate = 16000;
    private int bufferSize = 400;
    private static final String MODEL_PATH = "model3.tflite";
    private static final String LABEL_PATH = "labels.txt";
    private static final int INPUT_SIZE = 6144;
    private static boolean mVibrate = true;
    private static boolean mFlash = true;
    private static boolean mFitbit = true;
    private boolean eventDetected;


    private EventRecognitionService mService;
    private Vibrator vibrator;
    private NotificationHelper noti;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.home_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d("AwesomeFrag", "onViewCreated1");
        super.onViewCreated(view, savedInstanceState);
        if(getArguments() != null)
        {
            samplingRate = getArguments().getInt("SamplingRate");
            bufferSize = getArguments().getInt("BufferSize");
            hasPermission = getArguments().getBoolean("HasPermission");
        }
        Log.d("AwesomeFrag", "onViewCreated2");
        ButterKnife.bind(this, getView());
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Home");
        Log.d("AwesomeFrag", "onViewCreated3");
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        Log.d("AwesomeFrag", "onViewCreated4");
        noti = new NotificationHelper(getActivity());
        Log.d("AwesomeFrag", "onViewCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getActivity(), EventRecognitionService.class);
        getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
        Log.d("AwesomeFrag", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("AwesomeFrag", "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mService != null)
        {
            mService.setCallback(null);
            getActivity().unbindService(this);
            mService = null;
        }
        if(recording)
        {
            Intent intent = new Intent(getActivity(), EventRecognitionService.class);
            getActivity().stopService(intent);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        Log.d("AwesomeFrag", "onDestroy");
    }

    @OnClick({R.id.finalClass})
    public void onEventAck()
    {
        if(vibrator != null && vibrator.hasVibrator())
        {
            vibrator.cancel();
        }
        eventDetected = false;
    }

    @OnClick({R.id.fab})
    public void onFabClick()
    {
        if(mService.isRunning())
        {
            fab.setImageResource(R.drawable.microphone);
            Intent intent = new Intent(getActivity(), EventRecognitionService.class);
            mService.stopRecording();
            getActivity().stopService(intent);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            mic_status_msg.setText("Tap button to start listening...");
        }
        else
        {
            fab.setImageResource(R.drawable.microphone_off);
            Intent intent = new Intent(getActivity(), EventRecognitionService.class);
            getActivity().startService(intent);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            mic_status_msg.setText("Listening...");
        }
    }


    /**
     * Called when a connection to the Service has been established, with
     * the {@link IBinder} of the communication channel to the
     * Service.
     *
     * <p class="note"><b>Note:</b> If the system has started to bind your
     * client app to a service, it's possible that your app will never receive
     * this callback. Your app won't receive a callback if there's an issue with
     * the service, such as the service crashing while being created.
     *
     * @param name    The concrete component name of the service that has
     *                been connected.
     * @param service The IBinder of the Service's communication channel,
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("AwesomeFrag", "onServiceConnected");
        mService = ((EventServiceBinder) service).getService();

        if(mService != null)
        {
            mService.setCallback(this);
            if(mService.isRunning())
            {
                fab.setImageResource(R.drawable.microphone_off);
                mic_status_msg.setText("Listening...");
            }
        }
    }

    /**
     * Called when a connection to the Service has been lost.  This typically
     * happens when the process hosting the service has crashed or been killed.
     * This does <em>not</em> remove the ServiceConnection itself -- this
     * binding to the service will remain active, and you will receive a call
     * to {@link #onServiceConnected} when the Service is next running.
     *
     * @param name The concrete component name of the service whose
     *             connection has been lost.
     */
    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("AwesomeFrag", "onServiceDisconnected");
        mService = null;
    }

    @Override
    public void onResult(final int result, final boolean triggerAlert) {

        getActivity().runOnUiThread(
                new Runnable(){
                    public void run() {
                        if(triggerAlert & !eventDetected) {
                            eventDetected = true;
                            String event = Data.labelMap.get(result);
                            updateVerdictView(event);
                        }
                        else if(!eventDetected)
                        {
                            updateDbView(result);
                        }
                    }
                }
        );
    }

    private void updateVerdictView(String verdict)
    {
        Drawable background = finalClass.getBackground();
        if (background instanceof GradientDrawable)
        {
            GradientDrawable  colorDrawable = (GradientDrawable ) background;
            colorDrawable.setColor(ContextCompat.getColor(getActivity(), R.color.dbRed));
        }
        finalClass.setText(verdict);
        if(mVibrate)
        {
            if(vibrator != null && vibrator.hasVibrator())
            {
                vibrator.vibrate(VibrationEffect.createOneShot(600000, 255));
            }
        }
        if(mFitbit)
        {
            sendNotification(verdict);
        }
    }

    private void updateDbView(int db)
    {
        Drawable background = finalClass.getBackground();
        if (background instanceof GradientDrawable)
        {
            GradientDrawable  colorDrawable = (GradientDrawable ) background;
            if(db < 80)
            {
                colorDrawable.setColor(ContextCompat.getColor(getActivity(), R.color.dbGreen));
            }else if(db >= 80 & db < 135)
            {
                colorDrawable.setColor(ContextCompat.getColor(getActivity(), R.color.dbOrange));
            }else if(db >= 135 )
            {
                colorDrawable.setColor(ContextCompat.getColor(getActivity(), R.color.dbRed));
            }
        }
        finalClass.setText(db+"db");
    }

    public void sendNotification(String event)
    {
        Notification.Builder nb =  noti.getNotification2("Event Detected: " + event, event);
        noti.notify(1234, nb);
    }

    public static void onVibratePreferenceChanged(boolean enable)
    {
        mVibrate = enable;
    }

    public static void onFlashPreferenceChanged(boolean enable)
    {
        mFlash = enable;
    }

    public static void onFitbitPreferenceChanged(boolean enable)
    {
        mFitbit = enable;
    }


}