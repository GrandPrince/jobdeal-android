package com.justraspberry.jobdeal.view.job;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.core.SP;
import com.justraspberry.jobdeal.model.Image;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.model.User;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.BookmarkEvent;
import com.justraspberry.jobdeal.rest.service.event.GetJobByIdEvent;
import com.justraspberry.jobdeal.view.login.LoginActivity;
import com.justraspberry.jobdeal.view.payment.PaymentActivity;
import com.justraspberry.jobdeal.view.profile.DoerBuyerProfile;
import com.justraspberry.jobdeal.view.profile.ImageActivity;
import com.justraspberry.jobdeal.view.profile.MyDoersActivity;
import com.justraspberry.jobdeal.view.register.BankIdActivity;
import com.willy.ratingbar.BaseRatingBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class JobDetailsActivity extends AppCompatActivity {

    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.ivBoost)
    ImageView ivBoost;
    @BindView(R.id.tvDesc)
    TextView tvDesc;
    @BindView(R.id.tvName)
    TextView tvUserName;
    @BindView(R.id.ivAvatar)
    CircleImageView ivUserAvatar;
    @BindView(R.id.tvLocation)
    TextView tvLocation;
    @BindView(R.id.ratingBar)
    BaseRatingBar ratingBar;
    @BindView(R.id.tvRating)
    TextView tvRating;
    @BindView(R.id.slider)
    SliderLayout sliderLayout;
    @BindView(R.id.tvDuration)
    TextView tvDuration;
    @BindView(R.id.ivDuration)
    ImageView ivDuration;
    @BindView(R.id.btnApply)
    RelativeLayout btnApply;
    @BindView(R.id.tvApply)
    TextView tvApply;
    @BindView(R.id.ivBookmark)
    ImageView ivBookmark;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tvAlreadyApplied)
    TextView tvAlreadyApplied;
    @BindView(R.id.ivMore)
    ImageView ivMore;
    @BindView(R.id.tvBidCount)
    TextView tvBidCount;

    GoogleMap map;
    Job job;
    boolean isMyJob = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);
        ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent));
        }

        job = getIntent().getParcelableExtra("job");


        if (job != null) {
            ApiRestClient.getInstance().getJobById(job.getId());
            setViews();
        } else {
            ApiRestClient.getInstance().getJobById(getIntent().getIntExtra("jobId", -1));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setViews() {
        if (job == null)
            finish();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                map.getUiSettings().setAllGesturesEnabled(false);
                map.getUiSettings().setCompassEnabled(false);
                map.getUiSettings().setMapToolbarEnabled(false);
                map.getUiSettings().setZoomControlsEnabled(false);

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(job.getLatitude(), job.getLongitude()), 17));
                map.addMarker(new MarkerOptions().position(new LatLng(job.getLatitude(), job.getLongitude())));

            }
        });

        tvPrice.setText(Util.formatNumber(job.getPrice()) + " " + job.getCurrency());
        tvDesc.setText(job.getDescription());
        Glide.with(this).load(job.getUser().getAvatar()).apply(new RequestOptions().placeholder(R.drawable.placeholder).diskCacheStrategy(DiskCacheStrategy.ALL)).into(ivUserAvatar);
        tvTitle.setText(job.getName());
        tvLocation.setText(job.getUser().getCity());
        tvUserName.setText(job.getUser().getName() + " " + job.getUser().getSurname());
        if (job.getExpired())
            tvDuration.setText(getString(R.string.job_expired));
        else
            tvDuration.setText(job.getExpireAt());
        ivBookmark.setActivated(job.getBookmarked());
        showLoading(false);

        if (job.getSpeedy()) {
            ImageViewCompat.setImageTintList(ivDuration, ContextCompat.getColorStateList(this, R.color.colorRed));
            tvDuration.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
        } else {
            ImageViewCompat.setImageTintList(ivDuration, ContextCompat.getColorStateList(this, R.color.colorRed));
            tvDuration.setTextColor(ContextCompat.getColor(this, R.color.colorRed));
        }

        if (job.getBoost())
            ivBoost.setVisibility(View.VISIBLE);
        else
            ivBoost.setVisibility(View.GONE);


        sliderLayout.removeAllSliders();

        if (job.getImages().size() == 1) {
            sliderLayout.setPresetTransformer(SliderLayout.Transformer.Fade);
            sliderLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        } else {
            sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        }
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);


        for (Image image : job.getImages()) {
            DefaultSliderView sliderView = new DefaultSliderView(this);
            sliderView.image(image.getFullUrl())
                    .setRequestOption(new RequestOptions().centerCrop().dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .setProgressBarVisible(true);

            sliderLayout.addSlider(sliderView);
        }

        if (App.getInstance().getCurrentUser() != null && job.getUser().getId().equals(App.getInstance().getCurrentUser().getId())) {
            tvApply.setText(R.string.check_doers);
            isMyJob = true;

            if (job.getExpired() && !job.getListed()) {
                btnApply.setVisibility(View.GONE);
            }

            if (job.getBidCount() > 0) {
                tvBidCount.setVisibility(View.VISIBLE);
                tvBidCount.setText(job.getBidCount().toString());
            } else {
                tvBidCount.setVisibility(View.INVISIBLE);
            }
        } else {
            isMyJob = false;
            if (job.getApplied()) {
                btnApply.setVisibility(View.INVISIBLE);
                tvAlreadyApplied.setText(getString(R.string.already_applied));
                tvAlreadyApplied.setVisibility(View.VISIBLE);
                if (job.getChoosed()) {
                    tvAlreadyApplied.setText(getString(R.string.you_got_job));
                    tvAlreadyApplied.setVisibility(View.VISIBLE);
                    btnApply.setVisibility(View.VISIBLE);
                    tvApply.setText(getString(R.string.view_buyer));
                }
            } else if (job.getChoosed()) {
                tvAlreadyApplied.setText(getString(R.string.choosed_for_job));
                tvAlreadyApplied.setVisibility(View.VISIBLE);
                btnApply.setVisibility(View.VISIBLE);
                tvApply.setText(getString(R.string.view_buyer));
            } else {
                tvApply.setText(R.string.apply);
                btnApply.setVisibility(View.VISIBLE);
                tvAlreadyApplied.setVisibility(View.GONE);
            }
        }


    }

    @OnClick(R.id.btnApply)
    public void onApplyClick() {
        if (App.getInstance().getCurrentUser() == null) {//guest
            new AlertDialog.Builder(this).setTitle(getString(R.string.login_required_title).toUpperCase())
                    .setMessage(getString(R.string.login_required_body))
                    .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent i = new Intent(JobDetailsActivity.this, LoginActivity.class);
                            i.setAction("logout");
                            startActivity(i);
                            finish();
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
            return;
        }

        if (isMyJob) {
            if (job.getListed()) {
                Intent i = new Intent(this, MyDoersActivity.class);
                i.putExtra("job", job);
                startActivityForResult(i, Consts.REQ_LIST_APPLICANTS);
            } else {
                Intent i = new Intent(this, PaymentActivity.class);
                i.putExtra("job", job);
                i.putExtra("type", Consts.PAY_LIST);
                startActivityForResult(i, Consts.REQ_PAY_LIST);
            }
        } else {

            //TODO ukljuci ovo
           /* if (SP.getInstance().getBankIdLastVerification() - System.currentTimeMillis() < -3600000) { //one hour from last verification
                Intent i = new Intent(JobDetailsActivity.this, BankIdActivity.class);
                startActivityForResult(i, Consts.REQ_BANKID);
                return;
            }*/

            showLoading(true);
            Intent i = new Intent(JobDetailsActivity.this, ApplyJobActivity.class);
            i.putExtra("job", job);
            startActivityForResult(i, Consts.REQ_APPLY);
        }

    }

    public void shareJob() {
        Task<ShortDynamicLink> shortDynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink().setLink(Uri.parse("https://jobdeal.com/job/" + job.getId().toString()))
                .setDomainUriPrefix("https://jobdeal.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder("com.justraspberry.jobdeal").build())
                .setIosParameters(new DynamicLink.IosParameters.Builder("jobdeal").build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(getString(R.string.app_name))
                                .setImageUrl(Uri.parse("https://prod.jobdeal.com/logo.png"))
                                .setDescription(getString(R.string.jobdeal_description))
                                .build())
                .buildShortDynamicLink()
                .addOnCompleteListener(JobDetailsActivity.this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();
                            //Uri flowChartLink = task.getResult().getPreviewLink();
                            Intent shareIntent = new Intent();
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                            copyToClipboard(shortLink.toString());
                            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                        } else {
                            //Timber.e("LinkTask bad check again");
                        }
                    }
                });

    }

    public void copyToClipboard(String text) {
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("share_url",  text);
        clipboard.setPrimaryClip(clip);

    }
    @OnClick(R.id.ivMore)
    public void onMoreClick() {
        PopupMenu popup = new PopupMenu(JobDetailsActivity.this, ivMore);
        popup.getMenuInflater().inflate(R.menu.job_menu, popup.getMenu());
        User currentUser = App.getInstance().getCurrentUser();
        boolean isGuest = currentUser == null;
        if (isGuest) {
            popup.getMenu().removeItem(R.id.menu_job_remove);
        } else  {
            boolean isAdCreator =  job.getUser().getId().equals(currentUser.getId());
            if(!isAdCreator) {
                popup.getMenu().removeItem(R.id.menu_job_remove);
            } else {
                popup.getMenu().removeItem(R.id.menu_job_report);
            }
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_job_share) {
                    Toast.makeText(JobDetailsActivity.this, getString(R.string.generate_share), Toast.LENGTH_SHORT).show();
                    shareJob();
                } else if (item.getItemId() == R.id.menu_job_remove) {
                    new AlertDialog.Builder(JobDetailsActivity.this).setTitle(getString(R.string.remove))
                            .setMessage(getString(R.string.remove_job_desc))
                            .setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ApiRestClient.getInstance().removeJob(job);
                                    Intent i = new Intent();
                                    i.putExtra("job", job);
                                    setResult(Consts.RESULT_DELETE, i);
                                    dialog.dismiss();
                                    finish();
                                }
                            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                } else if (item.getItemId() == R.id.menu_job_report) {
                    Intent i = new Intent(JobDetailsActivity.this, ReportActivity.class);
                    i.putExtra("job", job);
                    startActivityForResult(i, Consts.REQ_REPORT);
                }
                return true;
            }
        });

        popup.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Consts.REQ_REPORT && resultCode == RESULT_OK) {
            Intent i = new Intent();
            i.putExtra("job", job);
            setResult(Consts.RESULT_DELETE, i);
            finish();
        } else if (requestCode == Consts.REQ_PAY_LIST) {
            if (resultCode == RESULT_OK) {
                job.setListed(true);
                Intent i = new Intent(this, MyDoersActivity.class);
                i.putExtra("job", job);
                startActivity(i);
            } else {
                job.setListed(false);
                Snackbar.make(sliderLayout, getString(R.string.error_payment_failed), Snackbar.LENGTH_LONG).show();

            }
        } else if (requestCode == Consts.REQ_APPLY) {
            if (resultCode == RESULT_OK) {
                job = data.getParcelableExtra("job");
            } else if (resultCode == RESULT_FIRST_USER) {
                Snackbar.make(toolbar, getString(R.string.default_error), Snackbar.LENGTH_LONG).show();
            }

            setViews();
        } else if (requestCode == Consts.REQ_LIST_APPLICANTS && resultCode == RESULT_OK){
            job = data.getParcelableExtra("job");
            setViews();
        } else if (requestCode == Consts.REQ_BANKID) {
            if (resultCode == RESULT_OK) {
                Timber.e(data.getStringExtra("bankId"));
                String bankId = data.getStringExtra("bankId");

                if (bankId.equals(App.getInstance().getCurrentUser().getBankId())) { //ok
                    SP.getInstance().setBankidLastVerification(System.currentTimeMillis());
                    onApplyClick();
                } else {
                    new android.app.AlertDialog.Builder(JobDetailsActivity.this).setMessage(getString(R.string.bank_id_verification_error)).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            } else if (resultCode == RESULT_FIRST_USER) {
                new android.app.AlertDialog.Builder(JobDetailsActivity.this).setMessage(data.getStringExtra("error")).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        }
    }

    @OnClick(R.id.ivBookmark)
    public void onBookmarkClick() {
        if (ivBookmark.isActivated())
            ApiRestClient.getInstance().removeBookmark(job);
        else
            ApiRestClient.getInstance().addBookmark(job);
        ivBookmark.setActivated(!ivBookmark.isActivated());
        job.setBookmarked(ivBookmark.isActivated());
    }

    public void showLoading(boolean show) {
        if (show) {
            btnApply.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btnApply.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.rlUser)
    public void onUserBuyerClick() {
        Intent i = new Intent(JobDetailsActivity.this, DoerBuyerProfile.class);
        i.putExtra("isBuyer", true);
        i.putExtra("user", job.getUser());
        startActivityForResult(i, Consts.REQ_DOER_PROFILE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra("job", job);
        setResult(RESULT_OK, i);

        super.onBackPressed();
    }

    @OnClick(R.id.ivAvatar)
    public void onAvatarClick() {
        Intent i = new Intent(JobDetailsActivity.this, ImageActivity.class);
        i.putExtra("imageUrl", job.getUser().getAvatar());
        startActivity(i);
    }

    @OnClick(R.id.flMap)
    public void onMapClick(){
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + job.getLatitude().toString() + "," + job.getLongitude().toString());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookmarkEvent(BookmarkEvent event) {
        if (event.isBookmarked != null) {
            ivBookmark.setActivated(event.isBookmarked);
        } else {
            ivBookmark.setActivated(!ivBookmark.isActivated());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetJobById(GetJobByIdEvent event) {
        if (event.job != null) {
            job = event.job;
            setViews();
        }
    }
}
