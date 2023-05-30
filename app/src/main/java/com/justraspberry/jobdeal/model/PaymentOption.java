package com.justraspberry.jobdeal.model;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentOption implements Parcelable {
    @SerializedName("id")
    @Expose
    Integer id;
    @SerializedName("name")
    @Expose
    String name;

    public PaymentOption(Integer id, String name) {
        this.id = id;
        this.name = name;
    }


    protected PaymentOption(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
    }

    public static final Creator<PaymentOption> CREATOR = new Creator<PaymentOption>() {
        @Override
        public PaymentOption createFromParcel(Parcel in) {
            return new PaymentOption(in);
        }

        @Override
        public PaymentOption[] newArray(int size) {
            return new PaymentOption[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(name);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
