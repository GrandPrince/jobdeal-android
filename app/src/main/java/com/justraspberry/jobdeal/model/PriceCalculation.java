package com.justraspberry.jobdeal.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PriceCalculation implements Parcelable {
    @SerializedName("price")
    @Expose
    Double price;
    @SerializedName("currency")
    @Expose
    String currency;
    @SerializedName("descriptionText")
    @Expose
    String descriptionText;
    @SerializedName("swishFee")
    @Expose
    Double swishFee;
    @SerializedName("paymentOptions")
    @Expose
    ArrayList<PaymentOption> paymentOptions = new ArrayList<>();

    public PriceCalculation(Double price, String currency, String descriptionText, Double swishFee, ArrayList<PaymentOption> paymentOptions) {
        this.price = price;
        this.currency = currency;
        this.descriptionText = descriptionText;
        this.swishFee = swishFee;
        this.paymentOptions = paymentOptions;
    }

    public PriceCalculation() {
    }

    protected PriceCalculation(Parcel in) {
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
        currency = in.readString();
        descriptionText = in.readString();
        if (in.readByte() == 0) {
            swishFee = null;
        } else {
            swishFee = in.readDouble();
        }
        paymentOptions = in.createTypedArrayList(PaymentOption.CREATOR);
    }

    public static final Creator<PriceCalculation> CREATOR = new Creator<PriceCalculation>() {
        @Override
        public PriceCalculation createFromParcel(Parcel in) {
            return new PriceCalculation(in);
        }

        @Override
        public PriceCalculation[] newArray(int size) {
            return new PriceCalculation[size];
        }
    };

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public Double getSwishFee() {
        return swishFee;
    }

    public void setSwishFee(Double swishFee) {
        this.swishFee = swishFee;
    }

    public ArrayList<PaymentOption> getPaymentOptions() {
        return paymentOptions;
    }

    public void setPaymentOptions(ArrayList<PaymentOption> paymentOptions) {
        this.paymentOptions = paymentOptions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(price);
        }
        dest.writeString(currency);
        dest.writeString(descriptionText);
        if (swishFee == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(swishFee);
        }
        dest.writeTypedList(paymentOptions);
    }
}
