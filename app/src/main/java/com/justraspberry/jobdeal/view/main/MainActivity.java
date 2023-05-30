package com.justraspberry.jobdeal.view.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.BuildConfig;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.core.LocationService;
import com.justraspberry.jobdeal.core.SP;
import com.justraspberry.jobdeal.event.FilterJobEvent;
import com.justraspberry.jobdeal.event.ListScrollEvent;
import com.justraspberry.jobdeal.event.MapActivatedEvent;
import com.justraspberry.jobdeal.event.NewNotificationEvent;
import com.justraspberry.jobdeal.event.TotalJobCountEvent;
import com.justraspberry.jobdeal.misc.OnSwipeListener;
import com.justraspberry.jobdeal.model.Device;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.model.Notification;
import com.justraspberry.jobdeal.model.TotalCount;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.GetUnreadNotificationCountEvent;
import com.justraspberry.jobdeal.view.bookmarks.BookmarksActivity;
import com.justraspberry.jobdeal.view.contact.ContactUs;
import com.justraspberry.jobdeal.view.filter.FilterActivity;
import com.justraspberry.jobdeal.view.job.AddJobActivity;
import com.justraspberry.jobdeal.view.job.JobDetailsActivity;
import com.justraspberry.jobdeal.view.login.LoginActivity;
import com.justraspberry.jobdeal.view.notification.NotificationActivity;
import com.justraspberry.jobdeal.view.myjobs.MyJobsActivity;
import com.justraspberry.jobdeal.view.profile.MyProfileActivity;
import com.justraspberry.jobdeal.view.rate.RateActivity;
import com.justraspberry.jobdeal.view.terms.TermsAndConditionActivity;
import com.justraspberry.jobdeal.view.tutorial.TutorialActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.ivMapList)
    ImageView ivMapList;
    @BindView(R.id.fabAdd)
    ExtendedFloatingActionButton fabAdd;
    @BindView(R.id.tvBookmarks)
    TextView tvBookmarks;
    @BindView(R.id.tvNotifications)
    TextView tvNotifications;
    @BindView(R.id.tvContactUs)
    TextView contactUs;

    //SORT
    @BindView(R.id.rlSort)
    RelativeLayout rlSort;
    @BindView(R.id.btnSort)
    MaterialButton btnSort;
    @BindView(R.id.tvLatest)
    TextView tvLatest;
    @BindView(R.id.tvOldest)
    TextView tvOldest;
    @BindView(R.id.tvLowest)
    TextView tvLowest;
    @BindView(R.id.tvHighest)
    TextView tvHighest;
    @BindView(R.id.tvFirst)
    TextView tvFirst;
    @BindView(R.id.tvLast)
    TextView tvLast;
    @BindView(R.id.tvSort)
    TextView tvSort;
    @BindView(R.id.cvSort)
    MaterialCardView cvSort;
    @BindView(R.id.tvFilter)
    TextView tvFilter;
    @BindView(R.id.svDrawer)
    ScrollView svDrawer;
    @BindView(R.id.svUserDrawer)
    ScrollView svUserDrawer;
    @BindView(R.id.ivProfilePictureDrawer)
    AppCompatImageView ivProfileImage;
    @BindView(R.id.ivProfilePremium)
    AppCompatImageView ivProfilePremium;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvPostedJobs)
    TextView tvPostedJobs;

    //NOTIFICATION
    @BindView(R.id.cvNotification)
    MaterialCardView cvNotification;
    @BindView(R.id.rlNotificationIcon)
    RelativeLayout rlNotificationIcon;
    @BindView(R.id.tvNotificationHeader)
    TextView tvNotificationHeader;
    @BindView(R.id.tvNotificationMsg)
    TextView tvNotificationMsg;
    @BindView(R.id.ivMenu)
    ImageView ivMenu;
    @BindView(R.id.notificationIndicator)
    View notificationIndicator;
    @BindView(R.id.tvNotificationCount)
    TextView tvNotificationCount;
    @BindView(R.id.tvVersion)
    TextView tvVersion;

    Runnable notificationRunnable;


    int[] sortsLayouts = {R.id.llDate, R.id.llPrice, R.id.llPublished};
    TotalCount totalCount = new TotalCount();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setSupportActionBar(toolbar);


        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        });

        viewPager.setAdapter(new MainViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateTotalCount();
            }
        });

        tabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.colorWhiteTrans), ContextCompat.getColor(this, R.color.colorWhite));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorWhite));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorWhite));
        tabLayout.setTabIndicatorFullWidth(true);

        tabLayout.setupWithViewPager(viewPager);

        if (App.getInstance().getCurrentUser() != null) {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }

                            String token = task.getResult().getToken();

                            Device device = new Device();
                            device.setAppVersion(String.valueOf(BuildConfig.VERSION_CODE));
                            device.setModel(Build.MODEL);
                            device.setOsVersion(Build.VERSION.CODENAME);
                            device.setType("2");
                            device.setToken(token);

                            ApiRestClient.getInstance().addDevice(device);
                        }
                    });
        }

        if (App.getInstance().getJobId() != null) {
            Intent i = new Intent(MainActivity.this, JobDetailsActivity.class);
            i.putExtra("jobId", App.getInstance().getJobId());
            startActivityForResult(i, Consts.REQ_JOB_DETAIL);
            App.getInstance().setJobId(null);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationService.startLocationService(this, false);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(MainActivity.this).setMessage(R.string.permission_location).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Consts.REQ_LOCATION_ACCESS);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Consts.REQ_LOCATION_ACCESS);
            }
        }

        tvVersion.setText(BuildConfig.VERSION_NAME);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Timber.e("onNewIntent");
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getStringExtra("action") != null) {
                int notificationType = Integer.parseInt(intent.getStringExtra("action"));
                if (notificationType == Consts.DOER_BID || notificationType == Consts.BUYER_ACCEPTED
                        || notificationType == Consts.WISHLIST_JOB) {
                    Intent i = new Intent(MainActivity.this, JobDetailsActivity.class);
                    i.putExtra("jobId", intent.getIntExtra("jobId", -1));
                    startActivity(i);
                } else if (notificationType == Consts.RATE_BUYER || notificationType == Consts.RATE_DOER) {
                    int notificationId = intent.getIntExtra("notificationId", -1);
                    Intent i = new Intent(MainActivity.this, RateActivity.class);
                    i.putExtra("notificationId", notificationId);
                    startActivityForResult(i, Consts.REQ_NOTIF_RATE);
                } else {
                    Intent i = new Intent(MainActivity.this, NotificationActivity.class);
                    i.putExtra("notificationType", notificationType);
                    startActivity(i);
                }
            }
        }
    }

    @OnClick(R.id.ivMenu)
    public void onMenuClick() {
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupDrawer() {
        if (App.getInstance().getCurrentUser() != null) {//set views for user
            svDrawer.setVisibility(View.GONE);
            svUserDrawer.setVisibility(View.VISIBLE);
            Glide.with(this).load(App.getInstance().getCurrentUser().getAvatar())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.placeholder)
                            .transforms(new CenterCrop(), new CircleCrop()))
                    .into(ivProfileImage);

            if (App.getInstance().getCurrentUser().getSubscription() != null)
                ivProfilePremium.setVisibility(View.VISIBLE);
            else
                ivProfilePremium.setVisibility(View.GONE);

            tvName.setText(App.getInstance().getCurrentUser().getFullName());
        } else {//no user - guest mode
            svDrawer.setVisibility(View.VISIBLE);
            svUserDrawer.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.tvLogout, R.id.tvLoginRegister})
    public void onLogoutClick() {
        ApiRestClient.getInstance().logout();
        SP.getInstance().logout();
        //reset activity

        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        i.setAction("logout");
        startActivity(i);
        finish();
    }

    @OnClick({R.id.tvTutorial, R.id.tvTutorialNoUser})
    public void onTutorialClick() {
        Intent i = new Intent(MainActivity.this, TutorialActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.tvWebsite)
    public void onWebsiteClick() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://jobdeal.com"));
        startActivity(browserIntent);
    }

    @OnClick(R.id.ivMapList)
    public void onMapListClick(View v) {
        ivMapList.setActivated(!ivMapList.isActivated());
        App.getInstance().setMapActivated(ivMapList.isActivated());
        EventBus.getDefault().post(new MapActivatedEvent(ivMapList.isActivated()));

        if (ivMapList.isActivated())
            tvSort.setVisibility(View.GONE);
        else
            tvSort.setVisibility(View.VISIBLE);

        updateTotalCount();
    }

    @OnClick(R.id.tvSort)
    public void onSortOpenClick(View v) {
        v.setActivated(!v.isActivated());


        if (v.isActivated()) {
            updateSortViews();
            rlSort.setVisibility(View.VISIBLE);
        } else {
            rlSort.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.rlSort)
    public void onSortOutsideClick(View v) {
        rlSort.setVisibility(View.GONE);
        tvSort.setActivated(false);
    }

    @OnClick(R.id.tvBookmarks)
    public void onBookmarksClick() {
        drawerLayout.closeDrawer(Gravity.LEFT);
        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, BookmarksActivity.class);
                startActivity(i);
            }
        }, 300);
    }

    @OnClick(R.id.tvNotifications)
    public void onNotificationsClick() {
        drawerLayout.closeDrawer(Gravity.LEFT);
        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                App.getInstance().getCurrentUser().setNotificationCount(0);
                notificationIndicator.setVisibility(View.GONE);
                tvNotificationCount.setVisibility(View.GONE);


                Intent i = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(i);
            }
        }, 300);
    }

    @OnClick(R.id.tvWishList)
    public void onWishlistClick() {
        drawerLayout.closeDrawer(Gravity.LEFT);
        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Disabled wishlist subscription
               /* if(App.getInstance().getCurrentUser().getSubscription() == null){
                    Intent i = new Intent(MainActivity.this, PaymentActivity.class);
                    i.putExtra("type", Consts.PAY_SUBSCRIBE);
                    startActivity(i);
                } else {*/
                // filterActivity ce imati i wishListView zato sto su skoro isti
                Intent i = new Intent(MainActivity.this, FilterActivity.class);
                i.putExtra("isWishlist", true);
                startActivity(i);
                //}
            }
        }, 300);
    }

    @OnClick({R.id.tvLatest, R.id.tvOldest, R.id.tvLowest, R.id.tvHighest, R.id.tvFirst, R.id.tvLast})
    public void onSortOptionClikc(View v) {
        //deactivate alll buttons
        tvLatest.setActivated(false);
        tvOldest.setActivated(false);
        tvLowest.setActivated(false);
        tvHighest.setActivated(false);
        tvFirst.setActivated(false);
        tvLast.setActivated(false);
        //pass tag from view to search button
        btnSort.setTag(v.getTag());
        //activate selected text view
        v.setActivated(true);
    }

    @OnClick(R.id.btnSort)
    public void onSortSearchClick(View v) {
        //get passed tag from selected sort textview, split and get sortby and sort direction.
        // Set on  filter instace and send event to call getJobs on fragment instaces
        String tag = (String) v.getTag();

        String sortBy = tag.split("/")[0];
        String sortDirection = tag.split("/")[1];

        App.getInstance().getFilterBody().setSortBy(sortBy);
        App.getInstance().getFilterBody().setSortDirection(sortDirection);

        EventBus.getDefault().post(new FilterJobEvent());

        onSortOutsideClick(null);
        updateSortViews();
    }

    @OnClick(R.id.tvFilter)
    public void onFilterClick() {
        Intent i = new Intent(this, FilterActivity.class);
        startActivityForResult(i, Consts.REQ_FILTER);
    }

    @OnClick(R.id.fabAdd)
    public void onFabAdd() {
        if (App.getInstance().getCurrentUser() == null) {//guest
            new AlertDialog.Builder(this).setTitle(getString(R.string.login_required_title).toUpperCase())
                    .setMessage(getString(R.string.login_required_body))
                    .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
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

        Intent i = new Intent(this, AddJobActivity.class);
        startActivityForResult(i, Consts.REQ_ADD_JOB);
    }

    @OnClick({R.id.tvContactUs, R.id.tvContactUsNoUser})
    public void onContactUsClikc() {
        drawerLayout.closeDrawer(Gravity.LEFT);
        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, ContactUs.class);
                startActivity(i);
            }
        }, 300);
    }

    @OnClick({R.id.tvTermsAndConditions, R.id.tvTermsAndConditions2})
    public void onTermsAndConditionsClick() {
        drawerLayout.closeDrawer(Gravity.LEFT);
        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, TermsAndConditionActivity.class);
                startActivity(i);
            }
        }, 300);
    }

    @OnClick(R.id.ivProfilePictureDrawer)
    public void onProfilePictureClick() {
        drawerLayout.closeDrawer(Gravity.LEFT);
        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, MyProfileActivity.class);
                startActivity(i);
            }
        }, 300);
    }

    @OnClick(R.id.tvPostedJobs)
    public void onPostedJobsClick() {
        drawerLayout.closeDrawer(Gravity.LEFT);
        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, MyJobsActivity.class);
                i.putExtra("isPostedJobs", true);
                startActivity(i);
            }
        }, 300);
    }

    @OnClick(R.id.tvAppliedJobs)
    public void onAppliedJobsClick() {
        drawerLayout.closeDrawer(Gravity.LEFT);
        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, MyJobsActivity.class);
                startActivity(i);
            }
        }, 300);
    }

    @OnClick(R.id.tvHome)
    public void onHomeClick() {
        drawerLayout.closeDrawer(Gravity.LEFT);
        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(0);
                ivMapList.setActivated(false);
                App.getInstance().setMapActivated(false);
                EventBus.getDefault().post(new MapActivatedEvent(false));
                EventBus.getDefault().post(new FilterJobEvent());
                updateTotalCount();
                tvSort.setVisibility(View.VISIBLE);
            }
        }, 300);
    }

    @OnClick(R.id.tvStatistic)
    public void onStatisticClick() {
        drawerLayout.closeDrawer(Gravity.LEFT);
        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, MyProfileActivity.class);
                startActivity(i);
            }
        }, 300);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Consts.REQ_LOCATION_ACCESS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationService.startLocationService(this, false);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public void showNotification(Notification notification) {
        //Set indicators
        if (App.getInstance().getCurrentUser() != null && App.getInstance().getCurrentUser().getNotificationCount() != null
                && App.getInstance().getCurrentUser().getNotificationCount() > 0) {
            notificationIndicator.setVisibility(View.VISIBLE);
            tvNotificationCount.setVisibility(View.VISIBLE);
            tvNotificationCount.setText(String.valueOf(App.getInstance().getCurrentUser().getNotificationCount()));
        } else {
            notificationIndicator.setVisibility(View.GONE);
            tvNotificationCount.setVisibility(View.GONE);
        }

        Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

        if (notificationRunnable != null)
            cvNotification.removeCallbacks(notificationRunnable);

        notificationRunnable = new Runnable() {
            @Override
            public void run() {
                cvNotification.startAnimation(slideUp);
            }
        };

        tvNotificationHeader.setText(notification.getTitle());
        tvNotificationMsg.setText(notification.getBody());
        cvNotification.setTag(notification);

        switch (notification.getType()) {
            case Consts.DOER_BID:
                rlNotificationIcon.setBackground(ContextCompat.getDrawable(this, R.drawable.oval_shape_gradient_orange));
                break;
            case Consts.BUYER_ACCEPTED:
                rlNotificationIcon.setBackground(ContextCompat.getDrawable(this, R.drawable.oval_shape_gradient_green));
                break;
            case Consts.RATE_DOER:
                rlNotificationIcon.setBackground(ContextCompat.getDrawable(this, R.drawable.oval_shape_gradient_green));
                break;
            case Consts.RATE_BUYER:
                rlNotificationIcon.setBackground(ContextCompat.getDrawable(this, R.drawable.oval_shape_gradient_green));
                break;
            case Consts.WISHLIST_JOB:
                rlNotificationIcon.setBackground(ContextCompat.getDrawable(this, R.drawable.oval_shape_gradient_orange));
                break;
            case Consts.PAYMENT_SUCCESS:
                rlNotificationIcon.setBackground(ContextCompat.getDrawable(this, R.drawable.oval_shape_grey));
                break;
            case Consts.PAYMENT_ERROR:
                rlNotificationIcon.setBackground(ContextCompat.getDrawable(this, R.drawable.oval_shape_grey));
                break;
        }

        slideDown.setInterpolator(new DecelerateInterpolator(2f));
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cvNotification.postDelayed(notificationRunnable, 3000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        slideUp.setInterpolator(new DecelerateInterpolator(2f));
        slideUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cvNotification.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        GestureDetector gestureDetector = new GestureDetector(this, new OnSwipeListener() {

            @Override
            public boolean onSwipe(Direction direction) {
                if (direction == Direction.up) {
                    cvNotification.removeCallbacks(notificationRunnable);
                    cvNotification.startAnimation(slideUp);
                }

                return true;
            }


        });

        cvNotification.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });
        cvNotification.setVisibility(View.VISIBLE);
        cvNotification.startAnimation(slideDown);
    }

    @OnClick(R.id.cvNotification)
    public void onNotificationClick() {
        Notification notification = (Notification) cvNotification.getTag();
        int notificationId = notification.getId();
        ApiRestClient.getInstance().readNotification(notificationId);
        int notificationCount = App.getInstance().getCurrentUser().getNotificationCount();
        App.getInstance().getCurrentUser().setNotificationCount(notificationCount - 1);


        int notificationType = notification.getType();
        if (notificationType == Consts.DOER_BID || notificationType == Consts.BUYER_ACCEPTED
                || notificationType == Consts.WISHLIST_JOB) {
            Intent i = new Intent(MainActivity.this, JobDetailsActivity.class);
            i.putExtra("jobId", notification.getJob().getId());
            startActivity(i);
        } else if (notificationType == Consts.RATE_BUYER || notificationType == Consts.RATE_DOER) {
            Intent i = new Intent(MainActivity.this, RateActivity.class);
            i.putExtra("notificationId", notificationId);
            startActivityForResult(i, Consts.REQ_NOTIF_RATE);
        } else {
            Intent i = new Intent(MainActivity.this, NotificationActivity.class);
            i.putExtra("notificationType", notificationType);
            startActivity(i);
        }
    }

    public void updateSortViews() {
        String sortTag = App.getInstance().getFilterBody().getSortBy() + "/" + App.getInstance().getFilterBody().getSortDirection();
        for (int a = 0; a < sortsLayouts.length; a++) {
            LinearLayout layout = (LinearLayout) findViewById(sortsLayouts[a]);
            for (int i = 0; i < layout.getChildCount(); i++) {
                View v = layout.getChildAt(i);

                if (v.getId() != R.id.btnSort) {//don't look tag of search button
                    if (v.getTag() != null) {
                        String tag = (String) v.getTag();

                        if (tag.equalsIgnoreCase(sortTag)) {
                            v.setActivated(true);
                            TextView textView = (TextView) v;
                            btnSort.setTag(v.getTag());
                            tvSort.setText(tag.split("/")[0].toUpperCase() + " / " + textView.getText().toString().toUpperCase());
                        } else {
                            v.setActivated(false);
                        }
                    }
                }
            }
        }
    }

    public void updateTotalCount() {
        if (!ivMapList.isActivated() && viewPager.getCurrentItem() == 0)
            tvFilter.setText(totalCount.totalList);
        else if (!ivMapList.isActivated() && viewPager.getCurrentItem() == 1)
            tvFilter.setText(totalCount.totalListSpeedy);
        else if (!ivMapList.isActivated() && viewPager.getCurrentItem() == 2)
            tvFilter.setText(totalCount.totalListDelivery);
        else if (ivMapList.isActivated() && viewPager.getCurrentItem() == 0)
            tvFilter.setText(totalCount.totalMap);
        else if (ivMapList.isActivated() && viewPager.getCurrentItem() == 1)
            tvFilter.setText(totalCount.totalMapSpeedy);
        else if (ivMapList.isActivated() && viewPager.getCurrentItem() == 1)
            tvFilter.setText(totalCount.totalMapDelivery);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Consts.REQ_FILTER && resultCode == RESULT_OK) {
            EventBus.getDefault().post(new FilterJobEvent());
        }
        if (requestCode == Consts.REQ_SWISH) {
            Timber.e("Swish Result code: " + String.valueOf(resultCode));
        }

        if (requestCode == Consts.REQ_ADD_JOB && resultCode == RESULT_OK) {
            EventBus.getDefault().post(new FilterJobEvent());
            Job newJob = data.getParcelableExtra("job");
            Intent i = new Intent(this, JobDetailsActivity.class);
            i.putExtra("job", newJob);
            startActivity(i);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSortViews();
        setupDrawer();

        //call categories
        ApiRestClient.getInstance().getCategories();
        ApiRestClient.getInstance().getUnreadNotificationCount();

        App.getInstance().setMapActivated(ivMapList.isActivated());
        EventBus.getDefault().post(new MapActivatedEvent(ivMapList.isActivated()));

        if (ivMapList.isActivated())
            tvSort.setVisibility(View.GONE);
        else
            tvSort.setVisibility(View.VISIBLE);

        tvFilter.setActivated(App.getInstance().getFilterBody().getFilter().isFilterSet());

        if (App.getInstance().getCurrentUser() != null && App.getInstance().getCurrentUser().getNotificationCount() != null
                && App.getInstance().getCurrentUser().getNotificationCount() > 0) {
            notificationIndicator.setVisibility(View.VISIBLE);
            tvNotificationCount.setVisibility(View.VISIBLE);
            tvNotificationCount.setText(String.valueOf(App.getInstance().getCurrentUser().getNotificationCount()));
        } else {
            notificationIndicator.setVisibility(View.GONE);
            tvNotificationCount.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTotalCountEvent(TotalJobCountEvent event) {
        //hmmmm :/
        if (!event.isMap && !event.isSpeedy && !event.isDelivery)
            totalCount.totalList = event.shown + "/" + event.total;
        else if (!event.isMap && event.isSpeedy)
            totalCount.totalListSpeedy = event.shown + "/" + event.total;
        else if (!event.isMap && event.isDelivery)
            totalCount.totalListDelivery = event.shown + "/" + event.total;
        else if (event.isMap && !event.isSpeedy && !event.isDelivery)
            totalCount.totalMap = event.shown + "/" + event.total;
        else if (event.isMap && event.isSpeedy)
            totalCount.totalMapSpeedy = event.shown + "/" + event.total;
        else if (event.isMap && event.isDelivery)
            totalCount.totalMapDelivery = event.shown + "/" + event.total;

        updateTotalCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewNotification(NewNotificationEvent event) {
        if (event.notification != null) {
            App.getInstance().getCurrentUser().setNotificationCount(event.notification.getUnreadCount());
            showNotification(event.notification);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onListScrollEvent(ListScrollEvent event) {
        if (event.scrollDown && fabAdd.isExtended())
            fabAdd.shrink();
        else if (!event.scrollDown && !fabAdd.isExtended())
            fabAdd.extend();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnreadNotificationCountEvent(GetUnreadNotificationCountEvent event) {
        if (event.unreadCount != null) {
            tvNotificationCount.setText(event.unreadCount.getUnreadCount().toString());
        }
    }

    private class MainViewPagerAdapter extends FragmentStatePagerAdapter {

        public MainViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return MainListFragment.newInstance(false, false);
           /* else if (position == 1)
                return MainListFragment.newInstance(true, false);
            else if (position == 2)
                return MainListFragment.newInstance(false, true);*/

            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            String[] titles = {getString(R.string.jobs), getString(R.string.speedy_jobs), getString(R.string.delivery_job)};

            return titles[position];
        }
    }
}
