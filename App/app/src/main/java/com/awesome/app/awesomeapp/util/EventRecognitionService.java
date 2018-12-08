package com.awesome.app.awesomeapp.util;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.awesome.app.awesomeapp.R;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventRecognitionService extends Service {

    static {
        System.loadLibrary("audioProcessingLib");
    }

    private static final String LOG_TAG = "AwesomeInference";
    public static final String PRIMARY_CHANNEL = "default";
    private static final int NOTI_PRIMARY1 = 1100;

    private AtomicBoolean isRecording;
    private static List<String> mSelectedEventNames;
    private Thread recThread;
    private EventServiceBinder binder;
    private Callback mCallback;
    private Random rand ;
    private HashMap<Integer, Long> eventTime = new HashMap<>();
    private static int alertInterval = 1;
    private EventStore mStore;
    private TensorflowSoundClassifier classifier;
    private static final String MODEL_PATH = "model3.tflite";
    private static final String LABEL_PATH = "labels.txt";


    private int[] verdictQueue = new int[5];

    public interface Callback {
        void onResult(int result, boolean triggerAlert);
    }

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     *
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        if(binder == null)
        {
            binder = new EventServiceBinder(this);
        }
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "Service Created");
        this.isRecording = new AtomicBoolean(false);

        mStore = EventStore.get(this);
        mSelectedEventNames = mStore.getSelectedEvents();
        rand = new Random(1000);
        try
        {
            classifier = TensorflowSoundClassifier.create(getAssets(), MODEL_PATH, LABEL_PATH, 6144);
        }catch (Exception e) {
            throw new RuntimeException("Error initializing TensorFlow!", e);
        }
        for(int i = 0; i<5 ;i++)
        {
            verdictQueue[i] = -1;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "Starting recording");
        recThread = new Thread() {
            public void run() {
                isRecording.set(true);
                startRecording();
            }
        };

        recThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
        if (isRecording.get()) {
            isRecording.set(false);
        }
        super.onDestroy();
    }

    public  void stopRecording()
    {
        if (isRecording.get()) {
            isRecording.set(false);
        }
    }

    public void startRecording(){
        FrequencyDomain(16000, 400, 80, 64, 800);
        int count = 0;

        while (true)
        {
            if(isRecording.get())
            {
                try {
                    count++;
                    if(count == 1)
                    {
                        Thread.sleep(1000);
                    }

                    float[] image = GetSpectrogram();

                    Calendar start = Calendar.getInstance();
                    float[] probs = classifier.Classify(image);

                    String probString = String.format("Alarm:%.2f , Car Horn:%.2f, Crying:%.2f, Dog:%.2f, Door Knock:%.2f, Doorbell:%.2f, Gun:%.2f, Emergency Siren:%.2f",
                            probs[0] * 100,
                            probs[1] * 100,
                            probs[2] * 100,
                            probs[3] * 100,
                            probs[4] * 100,
                            probs[5] * 100,
                            probs[6] * 100,
                            probs[7] * 100);
                    Log.d(LOG_TAG, probString);
                    int i = 0;
                    int c = -1;
                    for(i = 0 ;i <8 ;i++)
                    {
                        if (probs[i]>= 0.99)
                        {
                            c = i;
                        }
                    }

                    if(c== -1 && probs[1]>= 0.9 )
                    {
                        c= 1;
                    }else if(c== -1 && probs[3]>= 0.9 )
                    {
                        c= 3;
                    }
                    else if(c== -1 && probs[6]>= 0.8 )
                    {
                        c= 6;
                    }

                    for(int j = 0; j< 4 ;j++)
                    {
                        verdictQueue[j] = verdictQueue[j+1];
                    }
                    verdictQueue[4] = c;

                    c = getMaxVote(verdictQueue);
                    int db = rand.nextInt(180);
                    if(c == -1)
                    {
                        if(mCallback != null)
                        {
                            mCallback.onResult( db  , false);
                        }
                    }else
                    {
                        Log.d(LOG_TAG, "Sending Notification");
                        if(mCallback != null)
                        {
                            eventDetected(c);
                        }
                    }

                    Calendar end = Calendar.getInstance();
                    float timeSpan = (end.getTimeInMillis() - start.getTimeInMillis());
                    int sleepTime = (500 - (int)Math.floor(timeSpan));
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else{
                break;
            }

        }

    }

    private int getMaxVote(int[] arr)
    {
        HashMap<Integer, Integer> map = new HashMap<>();
        int result = -1;
        for(int i = 0; i< 5; i++)
        {
            if(map.containsKey(arr[i]))
            {
                map.put(arr[i], map.get(arr[i]) + 1);
            }
            else
            {
                map.put(arr[i], 1);
            }
        }

        for(int key : map.keySet())
        {
            if(map.get(key) >=3)
            {
                result = key;
            }
        }
        return result;
    }

    private void eventDetected(int event)
    {
        if(mSelectedEventNames.contains(Data.labelMap.get(event)))
        {
            long curTime = Calendar.getInstance().getTimeInMillis();
            if(eventTime.containsKey(event))
            {
                long lastSent = eventTime.get(event);
                if((curTime - lastSent) > alertInterval * 60 * 1000)
                {
                    eventTime.put(event, curTime);
                    mCallback.onResult(event , true);
                }
            }
            else
            {
                eventTime.put(event, curTime);
                mCallback.onResult(event , true);
            }
        }
    }

    public static void onAlertIntervalPreferenceChanged(int interval)
    {
        alertInterval = interval;
    }

    public static void onEventSelectionUpdated(Context c)
    {
        mSelectedEventNames = EventStore.get(c).getSelectedEvents();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOG_TAG, "onUnbind");
        mCallback = null;
        return super.onUnbind(intent);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public boolean isRunning()
    {
        return this.isRecording.get();
    }

    // Functions implemented in native library
    private native void FrequencyDomain(int sampleRate, int bufferSize, int block_dim, int num_bands, int window_size);
    private native void Cleanup();
    private native float[] GetSpectrogram();
}
