package com.justraspberry.jobdeal.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.reflect.TypeToken;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.model.Category;
import com.justraspberry.jobdeal.model.FilterBody;
import com.justraspberry.jobdeal.model.Price;
import com.justraspberry.jobdeal.model.User;
import com.justraspberry.jobdeal.rest.service.model.Info;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import timber.log.Timber;


public class SP {
    private static SP ourInstance;
    private static SharedPreferences mPref = null;

    private static final String USER = "user";
    private static final String PASS = "pass";
    private static final String JWT = "jwt";
    private static final String FIRST_START = "first_start";
    private static final String FILTER = "filter";
    private static final String WISHLIST = "wishlist";
    private static final String CATEGORIES = "categories";
    private static final String INFO = "info";
    private static final String PRICES = "prices";
    private static final String LOGIN_COUNT = "login_count";
    private static final String REGISTRATION_IN_PROGRESS = "registration_in_progress";
    public static final String BANKID_LAST_VERIFICATION = "bank_id_verification";


    public void setJWT(String jwt){
        saveStringValueToPreferences(JWT, jwt);
    }
    public String getJWT(){
        return getStringValueFromPreferences(JWT, null);
    }

    public void setPassword(String password){
        saveStringValueToPreferences(PASS, password);
    }
    public String getPassword(){
        return getStringValueFromPreferences(PASS, null);
    }

    public boolean isFirstStart(){
        return  getBooleanValueFromPreferences(FIRST_START, true);
    }
    public void setFirstStart(boolean value){
        saveBooleanValueToPreferences(FIRST_START, value);
    }

    public boolean isRegistrationInProgress(){
        return  getBooleanValueFromPreferences(REGISTRATION_IN_PROGRESS, false);
    }
    public void setRegistrationInProgress(boolean value){
        saveBooleanValueToPreferences(REGISTRATION_IN_PROGRESS, value);
    }

    public void setUser(User user){
        saveStringValueToPreferences(USER, App.getInstance().getGson().toJson(user));
    }

    public User getUser(){
        String userString = getStringValueFromPreferences(USER, null);

        if(userString==null)
            return null;

        User user = App.getInstance().getGson().fromJson(userString, User.class);

        return user;
    }

    public FilterBody getFilter(){
        String filterString = getStringValueFromPreferences(FILTER, null);

        FilterBody filterBody;

        if(filterString==null)
            return new FilterBody();
        else
            filterBody = App.getInstance().getGson().fromJson(filterString, FilterBody.class);

        return filterBody;
    }

    public void setFilter(FilterBody filter){
        String filterBody = App.getInstance().getGson().toJson(filter);

        saveStringValueToPreferences(FILTER, filterBody);
    }

    public void setWishlist(FilterBody filter){
        String filterBody;

        if(filter == null){
            filterBody = App.getInstance().getGson().toJson(new FilterBody());
        } else {
            filterBody = App.getInstance().getGson().toJson(filter);
        }
        
        saveStringValueToPreferences(WISHLIST, filterBody);
    }

    public FilterBody getWishlist(){
        String filterString = getStringValueFromPreferences(WISHLIST);

        Timber.e(filterString);

        if(filterString == null || filterString.equalsIgnoreCase("null")) {
            Timber.e("new filter body");
            return new FilterBody();
        }
        FilterBody filterBody = App.getInstance().getGson().fromJson(filterString, FilterBody.class);

        return filterBody;
    }

    public void setCategories(ArrayList<Category> categories){
        String categoriesJson = App.getInstance().getGson().toJson(categories);

        saveStringValueToPreferences(CATEGORIES, categoriesJson);
    }

    public ArrayList<Category> getCategories(){
        String categoriesJson = getStringValueFromPreferences(CATEGORIES, null);

        if(categoriesJson==null)
            return new ArrayList<Category>();

        return App.getInstance().getGson().fromJson(categoriesJson, new TypeToken<ArrayList<Category>>(){}.getType());
    }

    public Info getInfo(){
        String infoJson = getStringValueFromPreferences(INFO, null);

        if(infoJson == null)
            return new Info();

        return App.getInstance().getGson().fromJson(infoJson, Info.class);
    }

    public void setInfo(Info info){
        String infoJson = App.getInstance().getGson().toJson(info);

        saveStringValueToPreferences(INFO, infoJson);
    }

    public Price getPrices(){
        String pricesJson = getStringValueFromPreferences(PRICES, null);

        if(pricesJson == null)
            return null;

        return App.getInstance().getGson().fromJson(pricesJson, Price.class);
    }

    public void setPrices(Price prices){
        String pricesJson = App.getInstance().getGson().toJson(prices);

        saveStringValueToPreferences(PRICES, pricesJson);
    }

    public void setBankidLastVerification(Long lastVerification){
        saveLongValueToPreferences(BANKID_LAST_VERIFICATION, lastVerification);
    }

    public Long getBankIdLastVerification(){
        return  getLongValueFromPreferences(BANKID_LAST_VERIFICATION, 0L);
    }

    public void setLoginCount(int loginCount){
        saveIntValueToPreferences(LOGIN_COUNT, loginCount);
    }

    public int getLoginCount(){
        return getIntValueFromPreferences(LOGIN_COUNT, 0);
    }

    public void logout() {
        saveStringValueToPreferences(USER, null);
        saveStringValueToPreferences(PASS, null);
        saveStringValueToPreferences(JWT, null);
        saveStringValueToPreferences(FILTER, null);
        saveLongValueToPreferences(BANKID_LAST_VERIFICATION, 0L);
        App.getInstance().setCurrentUser(null);
    }

    public static synchronized SP getInstance() {
        if (ourInstance == null) {
            ourInstance = new SP(App.getInstance().getApplicationContext());
        }
        return ourInstance;
    }

    private SP(Context context) {
        init(context);
    }

    private void init(Context context) {
        mPref = context.getSharedPreferences("ConvoySharedPreference", Context.MODE_PRIVATE);
    }

    //SP METHODS
    private String getStringValueFromPreferences(String key) {
        return mPref.getString(key, null);
    }

    private String getStringValueFromPreferences(String key, String defaultValue) {
        return mPref.getString(key, defaultValue);
    }
    private void saveStringValueToPreferences(String key, String value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private boolean getBooleanValueFromPreferences(String key) {
        return mPref.getBoolean(key, false);
    }

    private boolean getBooleanValueFromPreferences(String key, boolean defaultVal) {
        return mPref.getBoolean(key, defaultVal);
    }

    private long getLongValueFromPreferences(String key) {
        return mPref.getLong(key, 0);
    }

    private long getLongValueFromPreferences(String key, Long defaultValue) {
        return mPref.getLong(key, defaultValue);
    }

    private int getIntValueFromPreferences(String key) {
        return mPref.getInt(key, -1);
    }

    private int getIntValueFromPreferences(String key, int def) {
        return mPref.getInt(key, def);
    }

    private float getFloatValueFromPreferences(String key) {
        return mPref.getFloat(key, 14f);
    }

    private Set<Integer> getSetValueFromPreferences(String key) {
        Set<String> tmp = new HashSet<>(mPref.getStringSet(key,new HashSet<String>()));
        Set<Integer> savedSet = new HashSet<>();

        for(String s : tmp){
            savedSet.add(Integer.parseInt(s));
        }

        return savedSet;
    }

    private void saveBooleanValueToPreferences(String key, boolean value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private void saveIntValueToPreferences(String key, int value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private void saveSetValueToPreferences(String key, Set<Integer> value) {
        Set<String> tmp = new HashSet<>();
        for(Integer integer : value)
            tmp.add(String.valueOf(integer));

        SharedPreferences.Editor editor = mPref.edit();
        editor.putStringSet(key, tmp);
        editor.commit();
    }

    private void saveLongValueToPreferences(String key, long value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    private void saveFloatValueToPreferences(String key, float value) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putFloat(key, value);
        editor.commit();
    }
}
