package com.justraspberry.jobdeal.rest.service.api;

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
import com.justraspberry.jobdeal.rest.service.model.BankIdAuth;
import com.justraspberry.jobdeal.rest.service.model.BankIdCollect;
import com.justraspberry.jobdeal.rest.service.model.Email;
import com.justraspberry.jobdeal.rest.service.model.ImageUploadResponse;
import com.justraspberry.jobdeal.rest.service.model.JobResponse;
import com.justraspberry.jobdeal.rest.service.model.KlarnaRequest;
import com.justraspberry.jobdeal.rest.service.model.LoginRegisterResponse;
import com.justraspberry.jobdeal.rest.service.model.SwishRequest;
import com.justraspberry.jobdeal.rest.service.model.UserName;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {

    /*USER*/

    @POST("user/login")
    Call<LoginRegisterResponse> login(@Body User user);

    @POST("user/register")
    Call<LoginRegisterResponse> register(@Body User user);

    @POST("user/email/check")
    Call<ResponseBody> checkEmail(@Body Email email);

    @POST("user/check/username")
    Call<ResponseBody> checkUsername(@Body UserName userName);

    @POST("user/update")
    Call<User> updateUser(@Body User user);

    @POST("user/device/add")
    Call<Device> addDevice(@Body Device device);

    @GET("user/logout")
    Call<ResponseBody> logout();

    @POST("user/delete")
    Call<ResponseBody> deleteAccount();

    @GET("user/get/{id}")
    Call<User> getUserById(@Path("id") int id);

    @POST("user/password/forgot")
    Call<ResponseBody> forgotPass(@Body User user);

    @POST("user/location/update")
    Call<ResponseBody> updateLocation(@Body CurrentLocation currentLocation);

    @Multipart
    @POST("user/image/upload")
    Call<ImageUploadResponse> uploadUserImage(@Part("description") RequestBody description, @Part MultipartBody.Part file);

    @Multipart
    @POST("job/image/upload")
    Call<ArrayList<ImageUploadResponse>> uploadJobImage(@Part("description") RequestBody description, @Part MultipartBody.Part file);

    /*JOB*/
    @POST("job/filter/{type}/{page}")
    Call<JobResponse> filterJob(@Body FilterBody filterBody, @Path("type") int type, @Path("page") int page);

    @POST("job/add")
    Call<Job> addJob(@Body Job job);

    @GET("job/buyer/getAll/{userId}/{page}")
    Call<ArrayList<Job>> getPostedJobs(@Path("userId") int userId, @Path("page") int page);

    @GET("job/doer/v2/getAll/{type}/{page}")
    Call<ArrayList<Job>> getAppliedJobs(@Path("page") int page, @Path("type") int type);

    @GET("job/applicants/{jobId}")
    Call<ArrayList<Applicant>> getJobApplicants(@Path("jobId") int jobId);

    @GET("job/get/{id}")
    Call<Job> getJobById(@Path("id") int id);

    @POST("job/apply")
    Call<Job> applyToJob(@Body Applicant applicant);

    @POST("job/delete")
    Call<ResponseBody> removeJob(@Body Job job);

    @POST("job/applicants/choose/{jobId}")
    Call<Job> chooseApplicant(@Body Applicant applicant, @Path("jobId") int jobId);

    @POST("job/report")
    Call<Report> reportJob(@Body Report report);

    /*GET RATE*/
    @GET("rate/byDoerId/{doerId}/{page}")
    Call<ArrayList<Rate>> getRateByDoerId(@Path("doerId") int doerId, @Path("page") int page);

    @GET("rate/byBuyerId/{buyerId}/{page}")
    Call<ArrayList<Rate>> getRateByBuyerId(@Path("buyerId") int buyerId, @Path("page") int page);

    @POST("rate/add")
    Call<Rate> addRate(@Body Rate rate);

    @GET("category/all/0")
    Call<ArrayList<Category>> getCategories();

    /*NOTIFICATIONS*/
    @GET("notification/types/{type}/{page}")
    Call<ArrayList<Notification>> getNotifications(@Path("type") int type, @Path("page") int page);

    @GET("notification/get/{id}")
    Call<Notification> getNotificationById(@Path("id") int id);

    @GET("notification/read/{id}")
    Call<Notification> readNotification(@Path("id") int id);

    @GET("notification/unread")
    Call<UnreadCount> getUnreadNotificationCount();

    /*MAP*/
    @GET("directions/location/{address}")
    Call<ResponseBody> getLocationFromAddress(@Path("address") String address);

    @GET("directions/address/{lat}/{lng}")
    Call<ResponseBody> getAddressFromLocation(@Path("lat") Double lat, @Path("lng") Double lng);

    /*PAYMENT & BANKID*/
    @POST("bankid/auth")
    Call<BankIdAuth> getBankId();

    @POST("bankid/collect")
    Call<BankIdCollect> collectBankId(@Body BankIdCollect bankIdCollect);

    @GET("payment/klarna/complete/{orderId}")
    Call<Payment> klarnaComplete(@Path("orderId") String orderId);

    @POST("payment/klarna/pay/job/{type}")
    Call<KlarnaRequest> klarnaPayJob(@Body Job job, @Path("type") int type);

    @POST("payment/klarna/pay/choose")
    Call<KlarnaRequest> klarnaPayChoose(@Body Applicant applicant);

    @POST("payment/klarna/pay/subscribe")
    Call<KlarnaRequest> klarnaPaySubcribe();

    @POST("payment/klarna/subscribe/cancel")
    Call<User> klarnaCancelSubscribe();

    @POST("payment/swish/pay/job/{type}")
    Call<SwishRequest> swishPayJob(@Body Job job, @Path("type") int type);

    @POST("payment/swish/pay/choose")
    Call<SwishRequest> swishPayChoose(@Body Applicant applicant);

    @GET("payment/swish/complete/{orderId}")
    Call<Payment> swishComplete(@Path("orderId") String orderId);

    @POST("price/calculate")
    Call<PriceCalculation> priceCalculation(@Body PriceCalculationRequest priceCalculationRequest);

    /*BOOKMARK*/
    @GET("bookmark/all/{page}")
    Call<ArrayList<Job>> getBookmarkedJobs(@Path("page") int page);

    @POST("bookmark/add")
    Call<Job> addBookmark(@Body Job job);

    @POST("bookmark/remove")
    Call<Job> removeBookmark(@Body Job job);

    /*WISHLIST*/
    @POST("wishlist/update")
    Call<FilterBody> updateWishlist(@Body FilterBody filterBody);

    @GET("wishlist/get")
    Call<FilterBody> getWishlist();

    /*VERIFICATION*/
    @POST("verification/send")
    Call<User> requestVerification(@Body User user);

    @POST("verification/verify")
    Call<User> verifyVerification(@Body User user);
}
