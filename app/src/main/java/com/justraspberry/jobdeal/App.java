package com.justraspberry.jobdeal;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.os.Build;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.justraspberry.jobdeal.core.SP;
import com.justraspberry.jobdeal.model.Category;
import com.justraspberry.jobdeal.model.FilterBody;
import com.justraspberry.jobdeal.model.Price;
import com.justraspberry.jobdeal.model.User;
import com.justraspberry.jobdeal.rest.service.model.Info;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;

import androidx.annotation.RequiresApi;
import androidx.multidex.MultiDexApplication;

import timber.log.Timber;

public class App extends MultiDexApplication {

    public static final String GENERAL_CHANNEL_ID = "General";
    private static App INSTANCE = new App();
    private Gson gson;
    public User currentUser;
    public boolean isMapActivated = false;
    public FilterBody filterBody;
    public FilterBody wishlistBody;
    public LatLng mapCenter;
    public ArrayList<Category> categories = new ArrayList<>();
    public Price prices;
    public Info info;
    public Integer jobId;
    public Location location;

    public static App getInstance() {
        if (INSTANCE == null) {
            synchronized (App.class) {
                if (INSTANCE == null) {
                    INSTANCE = new App();
                }
            }
        }
        return INSTANCE;
    }

    public App() {
        INSTANCE = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());

        JodaTimeAndroid.init(this);
        gson = new Gson();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    public Gson getGson() {
        if (gson == null)
            gson = new Gson();
        return gson;
    }

    public User getCurrentUser() {
        if (currentUser == null)
            currentUser = SP.getInstance().getUser();

        return currentUser;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public boolean isMapActivated() {
        return isMapActivated;
    }

    public void setMapActivated(boolean mapActivated) {
        isMapActivated = mapActivated;
    }

    public FilterBody getFilterBody() {
        if (filterBody == null)
            filterBody = new FilterBody();

        return filterBody;
    }

    public void setFilterBody(FilterBody filterBody) {
        this.filterBody = filterBody;
    }

    public FilterBody getWishlistBody() {
        if(wishlistBody == null)
            wishlistBody = new FilterBody();

        return wishlistBody;
    }

    public void setWishlistBody(FilterBody wishlistBody) {
        Timber.e("setWishlistBody - " + App.getInstance().getGson().toJson(wishlistBody));
        this.wishlistBody = wishlistBody;
    }

    public LatLng getMapCenter() {
        if (mapCenter == null) {
            mapCenter = new LatLng(59.33258, 18.0649);
        }

        return mapCenter;
    }

    public void setMapCenter(LatLng mapCenter) {
        this.mapCenter = mapCenter;
    }

    public ArrayList<Category> getCategories() {
        if (categories == null) {
            categories = SP.getInstance().getCategories();
        }

        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
        SP.getInstance().setCategories(categories);
    }

    public void setPrices(Price prices) {
        this.prices = prices;
        SP.getInstance().setPrices(prices);
    }

    public Price getPrices() {
        if (prices == null) {
            prices = SP.getInstance().getPrices();
            return prices;
        }

        return prices;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ArrayList<Category> searchCategories(String query) {
        ArrayList<Category> filteredList = new ArrayList<>();

        //BLAH :/
        for (Category category : getCategories()) {
            for (Category c : category.getSubCategory()) {
                for (Category cx : c.getSubCategory()) {
                    if (cx.getName().toLowerCase().contains(query))
                        filteredList.add(cx);
                }
                if (c.getName().toLowerCase().contains(query))
                    filteredList.add(c);
            }
            if (category.getName().toLowerCase().contains(query))
                filteredList.add(category);
        }

        return filteredList;
    }

    public Info getInfo() {
        if (info == null)
            info = SP.getInstance().getInfo();

        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
        SP.getInstance().setInfo(info);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel mainChannel = new NotificationChannel(GENERAL_CHANNEL_ID,
                GENERAL_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
        mainChannel.enableLights(true);
        mainChannel.enableVibration(true);
        mainChannel.setShowBadge(true);
        mainChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        notificationManager.createNotificationChannel(mainChannel);
    }


}
