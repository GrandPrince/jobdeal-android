package com.justraspberry.jobdeal.rest.service.client;

import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.BuildConfig;
import com.justraspberry.jobdeal.core.SP;
import com.justraspberry.jobdeal.event.NewNotificationEvent;
import com.justraspberry.jobdeal.event.PriceCalculationEvent;
import com.justraspberry.jobdeal.model.Applicant;
import com.justraspberry.jobdeal.model.Category;
import com.justraspberry.jobdeal.model.CurrentLocation;
import com.justraspberry.jobdeal.model.Device;
import com.justraspberry.jobdeal.model.FilterBody;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.model.Notification;
import com.justraspberry.jobdeal.model.Payment;
import com.justraspberry.jobdeal.model.PriceCalculation;
import com.justraspberry.jobdeal.model.PriceCalculationRequest;
import com.justraspberry.jobdeal.model.Rate;
import com.justraspberry.jobdeal.model.Report;
import com.justraspberry.jobdeal.model.UnreadCount;
import com.justraspberry.jobdeal.model.User;
import com.justraspberry.jobdeal.rest.service.api.ApiInterface;
import com.justraspberry.jobdeal.rest.service.event.AddDeviceEvent;
import com.justraspberry.jobdeal.rest.service.event.AddJobEvent;
import com.justraspberry.jobdeal.rest.service.event.ApplyJobEvent;
import com.justraspberry.jobdeal.rest.service.event.BankIdCollectEvent;
import com.justraspberry.jobdeal.rest.service.event.BankIdEvent;
import com.justraspberry.jobdeal.rest.service.event.BookmarkEvent;
import com.justraspberry.jobdeal.rest.service.event.CheckEmailEvent;
import com.justraspberry.jobdeal.rest.service.event.CheckUserNameEvent;
import com.justraspberry.jobdeal.rest.service.event.ChooseApplicantEvent;
import com.justraspberry.jobdeal.rest.service.event.ForgotPassEvent;
import com.justraspberry.jobdeal.rest.service.event.GetAddressEvent;
import com.justraspberry.jobdeal.rest.service.event.GetBookmarkedJobsEvent;
import com.justraspberry.jobdeal.rest.service.event.GetJobApplicantsEvent;
import com.justraspberry.jobdeal.rest.service.event.GetJobByIdEvent;
import com.justraspberry.jobdeal.rest.service.event.GetLocationFromAddressEvent;
import com.justraspberry.jobdeal.rest.service.event.GetNotificationEvent;
import com.justraspberry.jobdeal.rest.service.event.GetRatesEvent;
import com.justraspberry.jobdeal.rest.service.event.GetUnreadNotificationCountEvent;
import com.justraspberry.jobdeal.rest.service.event.GetUserEvent;
import com.justraspberry.jobdeal.rest.service.event.JobFilterEvent;
import com.justraspberry.jobdeal.rest.service.event.KlarnaCancelEvent;
import com.justraspberry.jobdeal.rest.service.event.KlarnaRequestEvent;
import com.justraspberry.jobdeal.rest.service.event.LoginEvent;
import com.justraspberry.jobdeal.rest.service.event.PaymentEvent;
import com.justraspberry.jobdeal.rest.service.event.PostedAppliedJobsEvent;
import com.justraspberry.jobdeal.rest.service.event.RateEvent;
import com.justraspberry.jobdeal.rest.service.event.RegisterEvent;
import com.justraspberry.jobdeal.rest.service.event.ReportEvent;
import com.justraspberry.jobdeal.rest.service.event.SendVerificationEvent;
import com.justraspberry.jobdeal.rest.service.event.SwishRequestEvent;
import com.justraspberry.jobdeal.rest.service.event.UploadImageEvent;
import com.justraspberry.jobdeal.rest.service.event.UserUpdateEvent;
import com.justraspberry.jobdeal.rest.service.event.VerifyVerificationEvent;
import com.justraspberry.jobdeal.rest.service.event.WishlistEvent;
import com.justraspberry.jobdeal.rest.service.interceptor.AuthInterceptor;
import com.justraspberry.jobdeal.rest.service.model.BankIdAuth;
import com.justraspberry.jobdeal.rest.service.model.BankIdCollect;
import com.justraspberry.jobdeal.rest.service.model.Email;
import com.justraspberry.jobdeal.rest.service.model.ImageUploadResponse;
import com.justraspberry.jobdeal.rest.service.model.JobResponse;
import com.justraspberry.jobdeal.rest.service.model.KlarnaRequest;
import com.justraspberry.jobdeal.rest.service.model.LoginRegisterResponse;
import com.justraspberry.jobdeal.rest.service.model.SwishRequest;
import com.justraspberry.jobdeal.rest.service.model.UserName;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class ApiRestClient {

    public static String BASE_URL = "http://dev.jobdeal.com/api/";
    private static final int TIMEOUT = 20;
    private static final ApiRestClient INSTANCE = new ApiRestClient();

    private Retrofit retrofit;
    private ApiInterface apiInterface;
    private Call<ResponseBody> emailCall;
    private Call<ResponseBody> userNameCall;
    private Call<ResponseBody> defaultCall;


    public static ApiRestClient getInstance() {
        return INSTANCE;
    }

    /**
     * Singleton construct.
     */
    private ApiRestClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.connectTimeout(TIMEOUT, TimeUnit.SECONDS);
        okHttpClientBuilder.readTimeout(TIMEOUT, TimeUnit.SECONDS);
        okHttpClientBuilder.writeTimeout(TIMEOUT, TimeUnit.SECONDS);

        if (BuildConfig.DEBUG) {
            BASE_URL = "http://dev.jobdeal.com/api/";
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClientBuilder.addInterceptor(httpLoggingInterceptor);
        } else {
            BASE_URL = "http://dev.jobdeal.com/api/";
        }

        //simulate long running request
        /*if (USE_SLEEP_INTERCEPTOR) {
            NetworkSleepInterceptor networkSleepInterceptor = new NetworkSleepInterceptor(
                    SLEEP_TIMEOUT, TimeUnit.SECONDS);
            okHttpClientBuilder
                    .addInterceptor(networkSleepInterceptor);
        }*/

        okHttpClientBuilder.addInterceptor(new AuthInterceptor());

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build())
                .build();

        apiInterface = retrofit.create(ApiInterface.class);
    }

    /*------------------------------------ USER -------------------------------------------*/
    //LOGIN
    public void login(User user) {
        Call<LoginRegisterResponse> loginCall = apiInterface.login(user);

        loginCall.enqueue(new Callback<LoginRegisterResponse>() {
            @Override
            public void onResponse(Call<LoginRegisterResponse> call, Response<LoginRegisterResponse> response) {
                if (response.isSuccessful()) {
                    App.getInstance().setInfo(response.body().getInfo());
                    App.getInstance().setPrices(response.body().getPrices());
                    App.getInstance().setWishlistBody(response.body().getWishlist());
                    EventBus.getDefault().postSticky(new LoginEvent(response.body()));
                } else {
                    Timber.e("Login not seccessful!");
                    try {
                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                        EventBus.getDefault().postSticky(new LoginEvent(null, errorJson.getString("message"), response.code()));
                    } catch (Exception e) {
                        EventBus.getDefault().postSticky(new LoginEvent(null, null, response.code()));
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginRegisterResponse> call, Throwable t) {
                t.printStackTrace();
                EventBus.getDefault().postSticky(new LoginEvent(null));
            }
        });
    }

    //REGISTER
    public void register(User user) {
        Call<LoginRegisterResponse> registerCall = apiInterface.register(user);

        registerCall.enqueue(new Callback<LoginRegisterResponse>() {
            @Override
            public void onResponse(Call<LoginRegisterResponse> call, Response<LoginRegisterResponse> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new RegisterEvent(response.body()));
                    App.getInstance().setInfo(response.body().getInfo());
                    App.getInstance().setPrices(response.body().getPrices());
                    App.getInstance().setWishlistBody(response.body().getWishlist());
                } else {
                    try {
                        JSONObject errorJson = new JSONObject(response.errorBody().string());
                        EventBus.getDefault().postSticky(new RegisterEvent(null, errorJson.getString("message"), response.code()));
                    } catch (Exception e) {
                        EventBus.getDefault().postSticky(new RegisterEvent(null, null, response.code()));
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginRegisterResponse> call, Throwable t) {
                EventBus.getDefault().postSticky(new RegisterEvent(null));
            }
        });
    }

    public void logout() {
        Call<ResponseBody> call = apiInterface.logout();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void deleteAccount() {
        Call<ResponseBody> call = apiInterface.deleteAccount();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void forgotPass(User user) {
        Call<ResponseBody> call = apiInterface.forgotPass(user);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful())
                    EventBus.getDefault().post(new ForgotPassEvent(true));
                else
                    EventBus.getDefault().post(new ForgotPassEvent(false));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                EventBus.getDefault().post(new ForgotPassEvent(false));
            }
        });
    }

    public void getUserById(int id) {
        Call<User> call = apiInterface.getUserById(id);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful())
                    EventBus.getDefault().post(new GetUserEvent(response.body()));
                else
                    EventBus.getDefault().post(new GetUserEvent(null));

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                EventBus.getDefault().post(new GetUserEvent(null));
            }
        });
    }

    public void addDevice(Device device) {
        Call<Device> call = apiInterface.addDevice(device);

        call.enqueue(new Callback<Device>() {
            @Override
            public void onResponse(Call<Device> call, Response<Device> response) {
                if (response.isSuccessful())
                    EventBus.getDefault().post(new AddDeviceEvent(response.body()));
                else
                    EventBus.getDefault().post(new AddDeviceEvent(null));
            }

            @Override
            public void onFailure(Call<Device> call, Throwable t) {
                EventBus.getDefault().post(new AddDeviceEvent(null));
            }
        });
    }

    public void updateLocation(CurrentLocation currentLocation){
        Call<ResponseBody> call = apiInterface.updateLocation(currentLocation);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Timber.e("updateLocation success");
                } else {
                    Timber.e("updateLocation failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    //checkEmail
    public void checkEmail(String email) {
        Email mail = new Email(email);

        if (emailCall != null)
            emailCall.cancel();

        emailCall = apiInterface.checkEmail(mail);

        emailCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Boolean isExist = jsonObject.getBoolean("result");

                        EventBus.getDefault().post(new CheckEmailEvent(isExist));
                    } else {
                        EventBus.getDefault().post(new CheckEmailEvent(null));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                EventBus.getDefault().post(new CheckEmailEvent(null));
            }
        });

    }

    //checkUserName
    public void checkUserName(String username) {
        UserName userName = new UserName(username);

        if (userNameCall != null)
            userNameCall.cancel();


        userNameCall = apiInterface.checkUsername(userName);


        userNameCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Boolean isExist = jsonObject.getBoolean("result");

                        EventBus.getDefault().post(new CheckUserNameEvent(isExist));
                    } else {
                        EventBus.getDefault().post(new CheckUserNameEvent(null));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                EventBus.getDefault().post(new CheckEmailEvent(null));
            }
        });
    }

    public void uploadAvatar(Pair<RequestBody, MultipartBody.Part> req) {

        final Call<ImageUploadResponse> registerCall = apiInterface.uploadUserImage(req.first, req.second);

        registerCall.enqueue(new Callback<ImageUploadResponse>() {
            @Override
            public void onResponse(Call<ImageUploadResponse> call, Response<ImageUploadResponse> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new UploadImageEvent(response.body()));
                } else {
                    EventBus.getDefault().postSticky(new UploadImageEvent(null));
                }
            }

            @Override
            public void onFailure(Call<ImageUploadResponse> call, Throwable t) {
                EventBus.getDefault().postSticky(new UploadImageEvent(null));
            }
        });
    }

    public void uploadJobImage(Pair<RequestBody, MultipartBody.Part> req) {

        final Call<ArrayList<ImageUploadResponse>> uploadJobImage = apiInterface.uploadJobImage(req.first, req.second);

        uploadJobImage.enqueue(new Callback<ArrayList<ImageUploadResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<ImageUploadResponse>> call, Response<ArrayList<ImageUploadResponse>> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().postSticky(new UploadImageEvent(response.body().get(0)));
                } else {
                    EventBus.getDefault().postSticky(new UploadImageEvent(null));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ImageUploadResponse>> call, Throwable t) {
                EventBus.getDefault().postSticky(new UploadImageEvent(null));
            }
        });
    }
    /*------------------------------------- UPDATE USER ------------------------------------------*/

    public void updateUser(User user) {
        Call<User> call = apiInterface.updateUser(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new UserUpdateEvent(response.body()));
                    App.getInstance().setCurrentUser(response.body());
                } else {
                    EventBus.getDefault().post(new UserUpdateEvent(App.getInstance().getCurrentUser()));
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                EventBus.getDefault().post(new UserUpdateEvent(App.getInstance().getCurrentUser()));
            }
        });
    }

    /*------------------------------------- JOB ------------------------------------------*/

    public void filterJob(boolean isSpeedy, boolean isDelivery, boolean isMap, int page) {
        int type = 0;

        if (isSpeedy || isDelivery) {
            if (isSpeedy)
                type = 1;
            else
                type = 2;
        }


        FilterBody filterBody = App.getInstance().getFilterBody();

        if (!isMap)
            filterBody.getFilter().setMapLocation(null);

        Call<JobResponse> call = apiInterface.filterJob(App.getInstance().getFilterBody(), type, page);

        call.enqueue(new Callback<JobResponse>() {
            @Override
            public void onResponse(Call<JobResponse> call, Response<JobResponse> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new JobFilterEvent(response.body(), isSpeedy, isDelivery, isMap));
                } else {
                    EventBus.getDefault().post(new JobFilterEvent(null, isSpeedy, isDelivery, isMap));
                }
            }

            @Override
            public void onFailure(Call<JobResponse> call, Throwable t) {
                EventBus.getDefault().post(new JobFilterEvent(null, isSpeedy,isDelivery, isMap));
            }
        });
    }

    public void addJob(Job job) {
        Call<Job> call = apiInterface.addJob(job);

        call.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job> call, Response<Job> response) {
                if (response.isSuccessful())
                    EventBus.getDefault().post(new AddJobEvent(response.body()));
                else {
                    if(response.code() == 470){
                        try {
                            JSONObject errorJson = new JSONObject(response.errorBody().string());
                            EventBus.getDefault().post(new AddJobEvent(null, errorJson.getString("message")));
                        } catch (Exception e) {
                            EventBus.getDefault().post(new AddJobEvent(null));
                        }
                    } else {
                        EventBus.getDefault().post(new AddJobEvent(null));
                    }
                }
            }

            @Override
            public void onFailure(Call<Job> call, Throwable t) {
                EventBus.getDefault().post(new AddJobEvent(null));
            }
        });
    }

    public void removeJob(Job job) {
        Call<ResponseBody> call = apiInterface.removeJob(job);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void reportJob(Report report) {
        Call<Report> call = apiInterface.reportJob(report);

        call.enqueue(new Callback<Report>() {
            @Override
            public void onResponse(Call<Report> call, Response<Report> response) {
                if (response.isSuccessful())
                    EventBus.getDefault().post(new ReportEvent(response.body()));
                else
                    EventBus.getDefault().post(new ReportEvent(null));
            }

            @Override
            public void onFailure(Call<Report> call, Throwable t) {
                EventBus.getDefault().post(new ReportEvent(null));

            }
        });
    }

    public void getPostedJobs(int userId, int page) {
        Call<ArrayList<Job>> call = apiInterface.getPostedJobs(userId, page);

        call.enqueue(new Callback<ArrayList<Job>>() {
            @Override
            public void onResponse(Call<ArrayList<Job>> call, Response<ArrayList<Job>> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new PostedAppliedJobsEvent(response.body(), 1));
                } else {
                    EventBus.getDefault().post(new PostedAppliedJobsEvent(null, 1));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Job>> call, Throwable t) {
                EventBus.getDefault().post(new PostedAppliedJobsEvent(null, 1));
            }
        });
    }

    public void getAppliedJobs(int page, int type) {
        Call<ArrayList<Job>> call = apiInterface.getAppliedJobs(page, type);

        call.enqueue(new Callback<ArrayList<Job>>() {
            @Override
            public void onResponse(Call<ArrayList<Job>> call, Response<ArrayList<Job>> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new PostedAppliedJobsEvent(response.body(), type));
                } else {
                    EventBus.getDefault().post(new PostedAppliedJobsEvent(null, type));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Job>> call, Throwable t) {
                EventBus.getDefault().post(new PostedAppliedJobsEvent(null, type));
            }
        });
    }

    public void getJobById(int id) {
        Call<Job> call = apiInterface.getJobById(id);

        call.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job> call, Response<Job> response) {
                if (response.isSuccessful())
                    EventBus.getDefault().post(new GetJobByIdEvent(response.body()));
                else
                    EventBus.getDefault().post(new GetJobByIdEvent(null));
            }

            @Override
            public void onFailure(Call<Job> call, Throwable t) {
                EventBus.getDefault().post(new GetJobByIdEvent(null));
            }
        });
    }

    public void applyJob(Applicant applicant) {
        Call<Job> call = apiInterface.applyToJob(applicant);

        call.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job> call, Response<Job> response) {
                if (response.isSuccessful())
                    EventBus.getDefault().post(new ApplyJobEvent(response.body()));
                else
                    EventBus.getDefault().post(new ApplyJobEvent(null));
            }

            @Override
            public void onFailure(Call<Job> call, Throwable t) {
                EventBus.getDefault().post(new ApplyJobEvent(null));
            }
        });
    }

    /*---------------------------------- GET RATE ----------------------------------------*/

    public void getRateByDoerId(int doerId, int page) {
        Call<ArrayList<Rate>> call = apiInterface.getRateByDoerId(doerId, page);

        call.enqueue(new Callback<ArrayList<Rate>>() {
            @Override
            public void onResponse(Call<ArrayList<Rate>> call, Response<ArrayList<Rate>> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new GetRatesEvent(response.body()));
                } else {
                    EventBus.getDefault().post(new GetRatesEvent(null));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Rate>> call, Throwable t) {
                EventBus.getDefault().post(new GetRatesEvent(null));
            }
        });
    }

    public void getRateByBuyerId(int buyerId, int page) {
        Call<ArrayList<Rate>> call = apiInterface.getRateByBuyerId(buyerId, page);

        call.enqueue(new Callback<ArrayList<Rate>>() {
            @Override
            public void onResponse(Call<ArrayList<Rate>> call, Response<ArrayList<Rate>> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new GetRatesEvent(response.body()));
                } else {
                    EventBus.getDefault().post(new GetRatesEvent(null));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Rate>> call, Throwable t) {
                EventBus.getDefault().post(new GetRatesEvent(null));
            }
        });
    }

    public void addRate(Rate rate) {
        Call<Rate> call = apiInterface.addRate(rate);

        call.enqueue(new Callback<Rate>() {
            @Override
            public void onResponse(Call<Rate> call, Response<Rate> response) {
                if (response.isSuccessful())
                    EventBus.getDefault().post(new RateEvent(response.body()));
                else
                    EventBus.getDefault().post(new RateEvent(null));

            }

            @Override
            public void onFailure(Call<Rate> call, Throwable t) {
                EventBus.getDefault().post(new RateEvent(null));
            }
        });
    }


    /*---------------------------------- APPLICANTS ----------------------------------------*/

    public void getJobApplicants(int jobId) {
        Call<ArrayList<Applicant>> call = apiInterface.getJobApplicants(jobId);

        call.enqueue(new Callback<ArrayList<Applicant>>() {
            @Override
            public void onResponse(Call<ArrayList<Applicant>> call, Response<ArrayList<Applicant>> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new GetJobApplicantsEvent(response.body()));
                } else {
                    EventBus.getDefault().post(new GetJobApplicantsEvent(null));
                }
            }


            @Override
            public void onFailure(Call<ArrayList<Applicant>> call, Throwable t) {
                EventBus.getDefault().post(new GetJobApplicantsEvent(null));
            }
        });
    }

    public void chooseApplicant(Applicant applicant, int jobId) {
        Call<Job> call = apiInterface.chooseApplicant(applicant, jobId);

        call.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job> call, Response<Job> response) {
                if (response.isSuccessful())
                    EventBus.getDefault().post(new ChooseApplicantEvent(response.body()));
                else
                    EventBus.getDefault().post(new ChooseApplicantEvent(null));
            }

            @Override
            public void onFailure(Call<Job> call, Throwable t) {
                EventBus.getDefault().post(new ChooseApplicantEvent(null));
            }
        });
    }

    /*---------------------------------- BOOKMARKS ----------------------------------------*/

    public void getBookmarkedJobs(int page) {
        Call<ArrayList<Job>> call = apiInterface.getBookmarkedJobs(page);

        call.enqueue(new Callback<ArrayList<Job>>() {
            @Override
            public void onResponse(Call<ArrayList<Job>> call, Response<ArrayList<Job>> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new GetBookmarkedJobsEvent(response.body()));
                } else {
                    EventBus.getDefault().post(new GetBookmarkedJobsEvent(null));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Job>> call, Throwable t) {
                EventBus.getDefault().post(new GetBookmarkedJobsEvent(null));
            }
        });
    }

    public void addBookmark(Job job) {
        Call<Job> call = apiInterface.addBookmark(job);

        call.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job> call, Response<Job> response) {
                if (response.isSuccessful())
                    EventBus.getDefault().post(new BookmarkEvent(true));
                else
                    EventBus.getDefault().post(new BookmarkEvent(null));
            }

            @Override
            public void onFailure(Call<Job> call, Throwable t) {
                EventBus.getDefault().post(new BookmarkEvent(null));
            }
        });
    }

    public void removeBookmark(Job job) {
        Call<Job> call = apiInterface.removeBookmark(job);

        call.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job> call, Response<Job> response) {
                if (response.isSuccessful())
                    EventBus.getDefault().post(new BookmarkEvent(false));
                else
                    EventBus.getDefault().post(new BookmarkEvent(null));
            }

            @Override
            public void onFailure(Call<Job> call, Throwable t) {
                EventBus.getDefault().post(new BookmarkEvent(null));
            }
        });
    }

    /*---------------------------------- NOTIFICATIONS ----------------------------------------*/

    public void getNotifications(int page, int type) {
        Call<ArrayList<Notification>> call = apiInterface.getNotifications(type, page);

        call.enqueue(new Callback<ArrayList<Notification>>() {
            @Override
            public void onResponse(Call<ArrayList<Notification>> call, Response<ArrayList<Notification>> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new GetNotificationEvent(response.body(), type));
                } else {
                    EventBus.getDefault().post(new GetNotificationEvent(null, type));
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Notification>> call, Throwable t) {
                EventBus.getDefault().post(new GetNotificationEvent(null, type));
            }
        });
    }

    public void getNotificationById(int id) {
        Call<Notification> call = apiInterface.getNotificationById(id);

        call.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                if (response.isSuccessful())
                    EventBus.getDefault().post(new NewNotificationEvent(response.body()));
                else
                    EventBus.getDefault().post(new NewNotificationEvent(null));
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                EventBus.getDefault().post(new NewNotificationEvent(null));
            }
        });
    }

    public void readNotification(int id) {
        Call<Notification> call = apiInterface.readNotification(id);

        call.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {

            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {

            }
        });
    }

    public void getUnreadNotificationCount() {
        Call<UnreadCount> call = apiInterface.getUnreadNotificationCount();

        call.enqueue(new Callback<UnreadCount>() {
            @Override
            public void onResponse(Call<UnreadCount> call, Response<UnreadCount> response) {
                if(response.isSuccessful())
                    EventBus.getDefault().post(new GetUnreadNotificationCountEvent(response.body()));
            }

            @Override
            public void onFailure(Call<UnreadCount> call, Throwable t) {

            }
        });
    }

    /*---------------------------------- CATEGORIES/FILTER ----------------------------------------*/

    public void getCategories() {
        Call<ArrayList<Category>> call = apiInterface.getCategories();

        call.enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(Call<ArrayList<Category>> call, Response<ArrayList<Category>> response) {
                if (response.isSuccessful()) {
                    App.getInstance().setCategories(response.body());
                } else {
                    Timber.e("Error getting categories!");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Category>> call, Throwable t) {
                Timber.e("Error getting categories!");
            }
        });
    }

    public void getAddress(LatLng latLng) {
        if (defaultCall != null)
            defaultCall.cancel();

        defaultCall = apiInterface.getAddressFromLocation(latLng.latitude, latLng.longitude);

        defaultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        EventBus.getDefault().post(new GetAddressEvent(jsonObject.getString("address")));
                    } catch (Exception e) {
                        e.printStackTrace();
                        EventBus.getDefault().post(new GetAddressEvent(null));
                    }

                } else {
                    EventBus.getDefault().post(new GetAddressEvent(null));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                EventBus.getDefault().post(new GetAddressEvent(null));
            }
        });
    }

    public void getLocationFromAddress(String address) {
        if (defaultCall != null)
            defaultCall.cancel();

        defaultCall = apiInterface.getLocationFromAddress(address);

        defaultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        EventBus.getDefault().post(new GetLocationFromAddressEvent(new LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("lng"))));
                    } catch (Exception e) {
                        e.printStackTrace();
                        EventBus.getDefault().post(new GetLocationFromAddressEvent(null));
                    }

                } else {
                    EventBus.getDefault().post(new GetLocationFromAddressEvent(null));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                EventBus.getDefault().post(new GetLocationFromAddressEvent(null));
            }
        });
    }

    //-----------------------------------BANK ID & PAYMENT------------------------------------------

    public void authBankId() {
        Call<BankIdAuth> call = apiInterface.getBankId();

        call.enqueue(new Callback<BankIdAuth>() {
            @Override
            public void onResponse(Call<BankIdAuth> call, Response<BankIdAuth> response) {
                if (response.isSuccessful())
                    EventBus.getDefault().post(new BankIdEvent(response.body()));
                else
                    EventBus.getDefault().post(new BankIdEvent(null));
            }

            @Override
            public void onFailure(Call<BankIdAuth> call, Throwable t) {
                EventBus.getDefault().post(new BankIdEvent(null));
            }
        });
    }

    public void collectBankId(String orderRef) {
        BankIdCollect bankIdCollect = new BankIdCollect();
        bankIdCollect.setOrderRef(orderRef);
        Call<BankIdCollect> call = apiInterface.collectBankId(bankIdCollect);

        call.enqueue(new Callback<BankIdCollect>() {
            @Override
            public void onResponse(Call<BankIdCollect> call, Response<BankIdCollect> response) {
                if (response.isSuccessful())
                    EventBus.getDefault().post(new BankIdCollectEvent(response.body()));
                else
                    EventBus.getDefault().post(new BankIdCollectEvent(null));
            }

            @Override
            public void onFailure(Call<BankIdCollect> call, Throwable t) {
                EventBus.getDefault().post(new BankIdCollectEvent(null));
            }
        });
    }

    public void klarnaComplete(String orderId) {
        Call<Payment> call = apiInterface.klarnaComplete(orderId);

        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                if (response.isSuccessful()) {
                    App.getInstance().setCurrentUser(response.body().getUser());
                    EventBus.getDefault().postSticky(new PaymentEvent(response.body()));
                } else {
                    EventBus.getDefault().postSticky(new PaymentEvent(null));
                }
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                EventBus.getDefault().postSticky(new PaymentEvent(null));
            }
        });
    }

    public void klarnaPayJob(Job job, int type) {
        Call<KlarnaRequest> call = apiInterface.klarnaPayJob(job, type);

        call.enqueue(new Callback<KlarnaRequest>() {
            @Override
            public void onResponse(Call<KlarnaRequest> call, Response<KlarnaRequest> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new KlarnaRequestEvent(response.body()));
                } else {
                    EventBus.getDefault().post(new KlarnaRequestEvent(null));
                }
            }

            @Override
            public void onFailure(Call<KlarnaRequest> call, Throwable t) {
                EventBus.getDefault().post(new KlarnaRequestEvent(null));
            }
        });
    }

    public void klarnaPayChoose(Applicant applicant) {
        Call<KlarnaRequest> call = apiInterface.klarnaPayChoose(applicant);

        call.enqueue(new Callback<KlarnaRequest>() {
            @Override
            public void onResponse(Call<KlarnaRequest> call, Response<KlarnaRequest> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new KlarnaRequestEvent(response.body()));
                } else {
                    EventBus.getDefault().post(new KlarnaRequestEvent(null));
                }
            }

            @Override
            public void onFailure(Call<KlarnaRequest> call, Throwable t) {
                EventBus.getDefault().post(new KlarnaRequestEvent(null));
            }
        });
    }

    public void klarnaPaySubscribe() {
        Call<KlarnaRequest> call = apiInterface.klarnaPaySubcribe();

        call.enqueue(new Callback<KlarnaRequest>() {
            @Override
            public void onResponse(Call<KlarnaRequest> call, Response<KlarnaRequest> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new KlarnaRequestEvent(response.body()));
                } else {
                    EventBus.getDefault().post(new KlarnaRequestEvent(null));
                }
            }

            @Override
            public void onFailure(Call<KlarnaRequest> call, Throwable t) {
                EventBus.getDefault().post(new KlarnaRequestEvent(null));
            }
        });
    }

    public void klarnaCancelSubscribe() {
        Call<User> call = apiInterface.klarnaCancelSubscribe();

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    App.getInstance().setCurrentUser(response.body());
                    EventBus.getDefault().post(new KlarnaCancelEvent(response.body()));
                } else {
                    EventBus.getDefault().post(new KlarnaCancelEvent(null));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                EventBus.getDefault().post(new KlarnaCancelEvent(null));
            }
        });
    }

    public void swishPayJob(Job job, int type) {
        Call<SwishRequest> call = apiInterface.swishPayJob(job, type);

        call.enqueue(new Callback<SwishRequest>() {
            @Override
            public void onResponse(Call<SwishRequest> call, Response<SwishRequest> response) {
                if (response.isSuccessful())
                    EventBus.getDefault().post(new SwishRequestEvent(response.body()));
                else
                    EventBus.getDefault().post(new SwishRequestEvent(null));
            }

            @Override
            public void onFailure(Call<SwishRequest> call, Throwable t) {
                EventBus.getDefault().post(new SwishRequestEvent(null));
            }
        });
    }

    public void swishPayChoose(Applicant applicant) {
        Call<SwishRequest> call = apiInterface.swishPayChoose(applicant);

        call.enqueue(new Callback<SwishRequest>() {
            @Override
            public void onResponse(Call<SwishRequest> call, Response<SwishRequest> response) {
                if (response.isSuccessful())
                    EventBus.getDefault().post(new SwishRequestEvent(response.body()));
                else
                    EventBus.getDefault().post(new SwishRequestEvent(null));
            }

            @Override
            public void onFailure(Call<SwishRequest> call, Throwable t) {
                EventBus.getDefault().post(new SwishRequestEvent(null));
            }
        });
    }

    public void swishPayComplete(String refId) {
        Call<Payment> call = apiInterface.swishComplete(refId);

        call.enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(Call<Payment> call, Response<Payment> response) {
                if (response.isSuccessful())
                    EventBus.getDefault().postSticky(new PaymentEvent(response.body()));
                else
                    EventBus.getDefault().postSticky(new PaymentEvent(null));
            }

            @Override
            public void onFailure(Call<Payment> call, Throwable t) {
                t.printStackTrace();
                EventBus.getDefault().postSticky(new PaymentEvent(null));
            }
        });
    }

    public void getPriceCalculation(PriceCalculationRequest priceCalculationRequest){
        Call<PriceCalculation> call = apiInterface.priceCalculation(priceCalculationRequest);

        call.enqueue(new Callback<PriceCalculation>() {
            @Override
            public void onResponse(Call<PriceCalculation> call, Response<PriceCalculation> response) {
                if(response.isSuccessful()){
                    EventBus.getDefault().post(new PriceCalculationEvent(response.body()));
                } else {
                    EventBus.getDefault().post(new PriceCalculationEvent(null));
                }
            }

            @Override
            public void onFailure(Call<PriceCalculation> call, Throwable t) {
                EventBus.getDefault().post(new PriceCalculationEvent(null));
            }
        });
    }

    //--------------------------------- WISHLIST ------------------------------------------

    public void updateWishlist(FilterBody filterBody) {
        Call<FilterBody> call = apiInterface.updateWishlist(filterBody);

        call.enqueue(new Callback<FilterBody>() {
            @Override
            public void onResponse(Call<FilterBody> call, Response<FilterBody> response) {
                SP.getInstance().setWishlist(response.body());

                if (response.isSuccessful())
                    EventBus.getDefault().post(new WishlistEvent(response.body()));
                else
                    EventBus.getDefault().post(new WishlistEvent(null));

            }

            @Override
            public void onFailure(Call<FilterBody> call, Throwable t) {
                EventBus.getDefault().post(new WishlistEvent(null));
            }
        });
    }

    public void getWishlist() {
        Call<FilterBody> call = apiInterface.getWishlist();

        call.enqueue(new Callback<FilterBody>() {
            @Override
            public void onResponse(Call<FilterBody> call, Response<FilterBody> response) {
                SP.getInstance().setWishlist(response.body());

                if (response.isSuccessful())
                    EventBus.getDefault().post(new WishlistEvent(response.body()));
                else
                    EventBus.getDefault().post(new WishlistEvent(null));

            }

            @Override
            public void onFailure(Call<FilterBody> call, Throwable t) {
                EventBus.getDefault().post(new WishlistEvent(null));
            }
        });
    }

    //-----------------------------------VERIFICATION-----------------------------------------------

    public void requestVerification(User user) {
        Call<User> call = apiInterface.requestVerification(user);
        Log.e("here", "here");
        Log.e("here", user.toString());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.e("here1", response.toString());
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new SendVerificationEvent(response.body()));
                } else {
                    EventBus.getDefault().post(new SendVerificationEvent(null));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("here2", t.toString());
                EventBus.getDefault().post(new SendVerificationEvent(null));
            }
        });
    }

    public void verifyVerification(User user) {
        Call<User> call = apiInterface.verifyVerification(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    EventBus.getDefault().post(new VerifyVerificationEvent(response.body()));
                } else {
                    EventBus.getDefault().post(new VerifyVerificationEvent(null));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                EventBus.getDefault().post(new VerifyVerificationEvent(null));
            }
        });
    }

}
