package com.awesome.app.awesomeapp.util;

import android.os.Binder;

public class EventServiceBinder extends Binder {

    EventRecognitionService service;

    public EventServiceBinder(EventRecognitionService service)
    {
        this.service = service;
    }

    public EventRecognitionService getService()
    {
        return this.service;
    }
}
