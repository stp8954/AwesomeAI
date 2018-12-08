package com.awesome.app.awesomeapp.util;

import android.os.Parcel;
import android.os.Parcelable;

public class EventSelection implements Parcelable {

    private String mEventName;
    private boolean mIsSelected;


    public EventSelection(String eventName , boolean isSelected)
    {
        mEventName = eventName;
        mIsSelected = isSelected;
    }

    protected EventSelection(Parcel in) {
        mEventName = in.readString();
        mIsSelected = in.readByte() != 0;
    }

    public static final Creator<EventSelection> CREATOR = new Creator<EventSelection>() {
        @Override
        public EventSelection createFromParcel(Parcel in) {
            return new EventSelection(in);
        }

        @Override
        public EventSelection[] newArray(int size) {
            return new EventSelection[size];
        }
    };

    public boolean isSelected(){return mIsSelected;}
    public void setIsSelected(boolean selected){mIsSelected = selected;}

    public String getEventName(){return mEventName;}
    public void setEventName(String eventName){mEventName = eventName;}


    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mEventName);
        dest.writeByte((byte) (mIsSelected ? 1 : 0));
    }
}
