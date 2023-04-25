package com.justraspberry.jobdeal.view.job;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.renderscript.ScriptGroup;
import android.util.Pair;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.slidertypes.DefaultSliderView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.core.SP;
import com.justraspberry.jobdeal.misc.FileUtils;
import com.justraspberry.jobdeal.model.Category;
import com.justraspberry.jobdeal.model.Image;
import com.justraspberry.jobdeal.model.Job;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.AddJobEvent;
import com.justraspberry.jobdeal.rest.service.event.UploadImageEvent;
import com.justraspberry.jobdeal.view.categories.CategoryActivity;
import com.justraspberry.jobdeal.view.filter.MapFilterActivity;
import com.justraspberry.jobdeal.view.payment.PaymentActivity;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddJobActivity extends AppCompatActivity {

    @BindView(R.id.btnUploadImages)
    FloatingActionButton fabUpload;
    @BindView(R.id.slider)
    SliderLayout sliderLayout;
    @BindView(R.id.tvNumOfImagesUploaded)
    TextView tvNumOfImagesUploaded;
    @BindView(R.id.fabDelete)
    FloatingActionButton fabDelete;
    @BindView(R.id.pbUpload)
    ProgressBar pbUpload;
    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.rlJobLocation)
    RelativeLayout rlJobLocation;
    @BindView(R.id.tvAddLocation)
    TextView tvAddLocation;
    @BindView(R.id.tvJobCategory)
    TextView tvJobCategory;
    @BindView(R.id.pbPublish)
    ProgressBar pbPublish;
    @BindView(R.id.btnPublishJob)
    MaterialButton btnPublish;
    @BindView(R.id.etJobName)
    EditText etJobName;
    @BindView(R.id.etJobDescription)
    EditText etJobDescription;
    @BindView(R.id.etJobPrice)
    EditText etJobPrice;
    @BindView(R.id.tvJobDuration)
    TextView tvDuration;
    @BindView(R.id.scSpeedyJob)
    SwitchCompat scSpeedy;
    @BindView(R.id.scDeliveryJob)
    SwitchCompat scDelivery;
    @BindView(R.id.scBoostAd)
    SwitchCompat scBoostAd;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvBoostAd)
    TextView tvBoost;
    @BindView(R.id.tvBoostAdDesc)
    TextView tvBoostDesc;
    @BindView(R.id.boostShuttle)
    ImageView boostShuttleIv;



    boolean isUploding = false;

    Job job;
    GoogleMap map;
    Marker jobMarker;
    File cameraFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                map.getUiSettings().setZoomControlsEnabled(false);
                map.getUiSettings().setMapToolbarEnabled(false);
                map.getUiSettings().setAllGesturesEnabled(false);
                map.getUiSettings().setCompassEnabled(false);

                map.setPadding(0, Util.dpToPx(AddJobActivity.this, 16), 0, 0);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(App.getInstance().getMapCenter(), 13));

                jobMarker = map.addMarker(new MarkerOptions().position(App.getInstance().getMapCenter()).icon(BitmapDescriptorFactory.defaultMarker(148.0f)));
            }
        });

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.stopAutoCycle();
        job = new Job();

        if (job.getImages().size() == 0)
            fabDelete.hide();

        tvNumOfImagesUploaded.setText(getString(R.string.images_upload, job.getImages().size()));

        hideBoost(); // <-- Remove this to display boost feature

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @OnCheckedChanged(R.id.scDeliveryJob)
    public void onDeliveryCheckBoxChanged(CompoundButton button, boolean isChecked) {
        scSpeedy.setChecked(false);
        scDelivery.setChecked(isChecked);
    }

    @OnCheckedChanged(R.id.scSpeedyJob)
    public void onSpeedyCheckBoxChanged(CompoundButton button, boolean isChecked) {
        scDelivery.setChecked(false);
        scSpeedy.setChecked(isChecked);
    }

    @OnClick(R.id.btnUploadImages)
    public void onFabUpload() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE))
                Snackbar.make(sliderLayout, getString(R.string.request_write_permission), Snackbar.LENGTH_LONG).show();

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Consts.REQ_STORAGE_PERMISSION);

            return;
        }

        if (isUploding)
            return;

        if (job.getImages().size() < 5) {
            CharSequence[] items = {getString(R.string.take_a_photo), getString(R.string.gallery), getString(R.string.cancel)};
            new MaterialAlertDialogBuilder(this)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0: pickFromCamera(); break;
                                case 1: {
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(intent,"Select Picture"), Consts.REQ_MULTI_IMAGE);
                                }; break;
                                case 2: {
                                    dialog.dismiss();
                                }
                            }
                        }
                    })
                    .setTitle(getString(R.string.choose_photo_title))
                    .show();

        } else {
            Snackbar.make(fabUpload, getString(R.string.max_images), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Consts.REQ_STORAGE_PERMISSION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onFabUpload();
        }
    }

    @OnClick(R.id.fabDelete)
    public void onDeleteClick() {
        if (isUploding)
            return;

        if (job.getImages().size() > 0) {
            job.getImages().remove(sliderLayout.getCurrentPosition());
            sliderLayout.removeSliderAt(sliderLayout.getCurrentPosition());
        }

        if (job.getImages().size() <= 1)
            fabDelete.hide();

        tvNumOfImagesUploaded.setText(getString(R.string.images_upload, job.getImages().size()));
    }

    @OnClick(R.id.rlJobLocation)
    public void onJobLocationClick() {
        Intent i = new Intent(this, MapFilterActivity.class);
        startActivityForResult(i, Consts.REQ_MAP_FILTER);
    }

    @OnClick(R.id.tvJobCategory)
    public void onJobCategoryClick() {
        Intent i = new Intent(this, CategoryActivity.class);
        startActivityForResult(i, Consts.REQ_CATEGORY);
    }

    @OnClick(R.id.btnPublishJob)
    public void onPublishJobClick() {
        if (!checkFields())
            return;

        //check if BankID verification is nessesery
        //TODO Ukljuci ovo
       /* Timber.e("Last Verification: " + String.valueOf(System.currentTimeMillis() - SP.getInstance().getBankIdLastVerification()));
        if (SP.getInstance().getBankIdLastVerification() - System.currentTimeMillis() < -3600000) { //one hour from last verification
            Intent i = new Intent(AddJobActivity.this, BankIdActivity.class);
            startActivityForResult(i, Consts.REQ_BANKID);
            return;
        }*/

        btnPublish.setVisibility(View.INVISIBLE);
        pbPublish.setVisibility(View.VISIBLE);

        job.setName(etJobName.getText().toString());
        job.setBoost(scBoostAd.isChecked());
        job.setSpeedy(scSpeedy.isChecked());
        job.setDelivery(scDelivery.isChecked());
        job.setDescription(etJobDescription.getText().toString());
        //job.setExpireAt(tvDuration.getText().toString());
        job.setUser(App.getInstance().currentUser);
        job.setPrice(Double.parseDouble(etJobPrice.getText().toString()));

        ApiRestClient.getInstance().addJob(job);
    }

    @OnClick(R.id.tvJobDuration)
    public void onTvDurationClick() {
        Calendar newCalendar = Calendar.getInstance();

        newCalendar.set(newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                job.setExpireAt(job.getExpireAt() + " " + String.valueOf(hourOfDay) + ":" + String.valueOf(minute));

                tvDuration.setText(tvDuration.getText() + " " + String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance(Locale.ENGLISH);
                newDate.set(year, monthOfYear, dayOfMonth);
                String date = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH).format(newDate.getTime());
                tvDuration.setText(date);
                tvDuration.setTextColor(getResources().getColor(R.color.colorText));

                job.setExpireAt(date);

                timePickerDialog.show();
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.getDatePicker().setMinDate(newCalendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void pickFromCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            cameraFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (cameraFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.justraspberry.jobdeal.provider",
                    cameraFile);
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePicture, Consts.REQ_CAMERA_IMAGE);
        }
    }

    private void hideBoost(){
        scBoostAd.setVisibility(View.INVISIBLE);
        boostShuttleIv.setVisibility(View.INVISIBLE);
        tvBoost.setText("");
        tvBoostDesc.setText("");
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = AddJobActivity.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    public boolean checkFields() {
        boolean isValid = true;

        if (etJobName.getText().length() < 2) {
            Snackbar.make(sliderLayout, getString(R.string.error_job_name), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (etJobDescription.getText().length() < 10) {
            Snackbar.make(sliderLayout, getString(R.string.error_job_desc), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (etJobPrice.getText().length() == 0 || (etJobPrice.getText().length() > 0 && Double.parseDouble(etJobPrice.getText().toString()) < 1)) {
            Snackbar.make(sliderLayout, getString(R.string.error_job_price), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (job.getCategoryId() == null) {
            Snackbar.make(sliderLayout, getString(R.string.error_job_category), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (job.getImages().size() == 0) {
            Snackbar.make(sliderLayout, getString(R.string.error_job_image), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (job.getAddress() == null) {
            Snackbar.make(sliderLayout, getString(R.string.error_job_location), Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (job.getExpireAt() == null) {
            Snackbar.make(sliderLayout, getString(R.string.error_job_expired_at), Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == Consts.REQ_CAMERA_IMAGE) {
            if (cameraFile != null) {//from camera
                Uri photoURI = FileProvider.getUriForFile(AddJobActivity.this,
                        "com.justraspberry.jobdeal.provider",
                        cameraFile);

                startCrop(photoURI);

                cameraFile = null;


            }
        }
        if (requestCode == Consts.REQ_MULTI_IMAGE && resultCode == RESULT_OK) {
            // Get the Image from data

            if(data == null){
                return;
            }
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            if(data.getData()!=null){

                Uri mImageUri=data.getData();

                // Get the cursor
                Cursor cursor = getContentResolver().query(mImageUri,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                Timber.e("Image Uri: " + mImageUri.toString());

                if (job.getImages().size() < 5) {
                    job.getImages().add(new Image(-1, mImageUri.toString(), job.getImages().size()));
                }

                cursor.close();

                refreshSlider();
                proccessSelectedImages();
            } else {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {

                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        mArrayUri.add(uri);
                        // Get the cursor
                        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        a : for (Uri u: mArrayUri){
                            for (Image img : job.getImages()){
                                if (img.getFullUrl().equalsIgnoreCase(u.toString()))
                                    continue a;
                            }

                            if (job.getImages().size() < 5) {
                                job.getImages().add(new Image(-1, u.toString(), job.getImages().size()));
                            }

                        }

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                        cursor.close();

                    }
                    refreshSlider();
                    proccessSelectedImages();
                }
            }
        }
        else if (requestCode == Consts.REQ_MAP_FILTER && resultCode == RESULT_OK) {
            map.moveCamera(CameraUpdateFactory.newLatLng((LatLng) data.getParcelableExtra("latLng")));
            jobMarker.setPosition((LatLng) data.getParcelableExtra("latLng"));
            tvAddLocation.setText(data.getStringExtra("address"));
            job.setLatitude(jobMarker.getPosition().latitude);
            job.setLongitude(jobMarker.getPosition().longitude);
            job.setAddress(tvAddLocation.getText().toString());
        }
        else if (requestCode == Consts.REQ_CATEGORY && resultCode == RESULT_OK) {
            Category category = data.getParcelableExtra("category");

            tvJobCategory.setText(category.getName());
            job.setCategoryId(category.getId());
        }
        else if (requestCode == Consts.REQ_PAYMENT) {
            if (resultCode == RESULT_OK) {
                job = data.getParcelableExtra("job");
                Intent i = new Intent();
                i.putExtra("job", job);
                setResult(RESULT_OK, i);
                finish();
            } else {
                Snackbar.make(sliderLayout, getString(R.string.error_payment_failed), Snackbar.LENGTH_LONG).show();
            }
        }
        else if (requestCode == Consts.REQ_BANKID) {
            if (resultCode == RESULT_OK) {
                Timber.e(data.getStringExtra("bankId"));
                String bankId = data.getStringExtra("bankId");

                if (bankId.equals(App.getInstance().getCurrentUser().getBankId())) { //ok
                    SP.getInstance().setBankidLastVerification(System.currentTimeMillis());
                    onPublishJobClick();
                } else {
                    new AlertDialog.Builder(AddJobActivity.this).setMessage(getString(R.string.bank_id_verification_error)).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }
            } else if (resultCode == RESULT_FIRST_USER) {
                new AlertDialog.Builder(AddJobActivity.this).setMessage(data.getStringExtra("error")).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        } else if (data != null) {
            if (requestCode == UCrop.REQUEST_CROP) {
                Uri resultUri = UCrop.getOutput(data);

                String filePath = null;
                if (resultUri != null && "content".equals(resultUri.getScheme())) {
                    Cursor cursor = getContentResolver().query(resultUri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
                    cursor.moveToFirst();
                    filePath = cursor.getString(0);
                    cursor.close();
                } else {
                    filePath = resultUri.getPath();
                }

                if (job.getImages().size() < 5) {
                    job.getImages().add(new Image(-2, filePath, job.getImages().size()));


                    sliderLayout.removeAllSliders();
                    for (Image image : job.getImages()) {
                        DefaultSliderView sliderView = new DefaultSliderView(this);
                        sliderView
                                .image(image.getFullUrl())
                                .setRequestOption(new RequestOptions().centerCrop().dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL))
                                .setProgressBarVisible(true);

                        sliderLayout.addSlider(sliderView);
                    }
                }

                proccessSelectedImages();
            } else if (resultCode == UCrop.RESULT_ERROR) {
                Timber.e("ERROR");
            }
        }
    }

    public Pair<RequestBody, MultipartBody.Part> createRequestBody(@NonNull Uri uri) {
        try {

            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r", null);
            FileInputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
            File file = new File(getCacheDir(), getFileNameFromUri(uri));
            OutputStream outputStream = new FileOutputStream(file);

            byte[] b = new byte[(int)inputStream.getChannel().size()];
            while(inputStream.read(b, 0, (int)inputStream.getChannel().size()) > 0){
                outputStream.write(b);
            }

            inputStream.close();
            outputStream.close();

            RequestBody req = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            return new Pair<>(description, MultipartBody.Part.createFormData("image", uri.getLastPathSegment(), req));
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static Pair<RequestBody, MultipartBody.Part> createRequestBody(@NonNull File file) {

        Timber.e("File: " + file.getAbsolutePath());
        RequestBody req = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return new Pair<>(description, MultipartBody.Part.createFormData("image", file.getName(), req));
    }

    private String getFileNameFromUri(Uri uri){
        String name = "";
        Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
        if (returnCursor != null) {
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            name = returnCursor.getString(nameIndex);
            returnCursor.close();
        }
        return name;
    }

    private void refreshSlider(){
        sliderLayout.removeAllSliders();
        for (Image image : job.getImages()) {
            DefaultSliderView sliderView = new DefaultSliderView(this);
            sliderView
                    .image(image.getFullUrl())
                    .setRequestOption(new RequestOptions().centerCrop().dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .setProgressBarVisible(true);

            sliderLayout.addSlider(sliderView);
        }
    }

    private void proccessSelectedImages() {
        tvNumOfImagesUploaded.setText(getString(R.string.images_upload, job.getImages().size()));

        if (job.getImages().size() == 0)
            fabDelete.hide();
        else
            fabDelete.show();

        if (job.getFirstNotUploadedImage() != null) {
            isUploding = true;
            pbUpload.setVisibility(View.VISIBLE);

            if(job.getFirstNotUploadedImage().getId() == -2)
                ApiRestClient.getInstance().uploadJobImage(createRequestBody(new File(job.getFirstNotUploadedImage().getFullUrl())));
            else
                ApiRestClient.getInstance().uploadJobImage(createRequestBody(Uri.parse(job.getFirstNotUploadedImage().getFullUrl())));

            //job.getFirstNotUploadedImage().setUploaded(true);
        }
    }

    private String getPathFromUri(Uri uri) {
        String filePath = null;
        if (uri != null && "content".equals(uri.getScheme())) {
            Cursor cursor = getContentResolver().query(uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
            cursor.moveToFirst();
            filePath = cursor.getString(0);
            cursor.close();
        } else {
            filePath = uri.getPath();
        }

        return filePath;
    }

    public void startCrop(Uri uri) {
        String destinationFileName = String.valueOf(System.currentTimeMillis());

        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(false);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(80);
        options.setFreeStyleCropEnabled(true);
        options.setShowCropGrid(true);
        options.setShowCropFrame(true);
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        options.setLogoColor(ContextCompat.getColor(this, R.color.colorWhite));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.colorWhite));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorAccent));

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)))
                .withAspectRatio(1, 1).withMaxResultSize(800, 800).withOptions(options);
        uCrop.start(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        mapView.onStop();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImageUpload(UploadImageEvent event) {
        if (event.imageUploadResponse != null) {
            //Timber.e("Image: " + String.valueOf(job.getFirstNotUploadedImage().getPosition()));
            job.getFirstNotUploadedImage().setPath(event.imageUploadResponse.getPath());
            job.getFirstNotUploadedImage().setUploaded(true);
            if (job.getFirstNotUploadedImage() != null) {
                //Timber.e("NEXT Image: " + String.valueOf(job.getFirstNotUploadedImage().getPosition()));
                ApiRestClient.getInstance().uploadJobImage(createRequestBody(Uri.parse(job.getFirstNotUploadedImage().getFullUrl())));
            } else {
                isUploding = false;
                pbUpload.setVisibility(View.GONE);

                //Timber.e("ALL Image: " + App.getInstance().getGson().toJson(job.getImages()));
            }
        } else {
            isUploding = false;
            pbUpload.setVisibility(View.GONE);
            Toast.makeText(AddJobActivity.this, getString(R.string.default_error), Toast.LENGTH_LONG).show();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddJobEvent(AddJobEvent event) {
        btnPublish.setVisibility(View.VISIBLE);
        pbPublish.setVisibility(View.INVISIBLE);
        if (event.job != null) {
            if (job.getBoost() && job.getSpeedy()) {//start payment method
                Intent i = new Intent(AddJobActivity.this, PaymentActivity.class);
                i.putExtra("type", Consts.PAY_BOOST_SPEEDY);
                i.putExtra("job", event.job);
                startActivityForResult(i, Consts.REQ_PAYMENT);
            } else if (job.getBoost()) {//start paymnent method
                Intent i = new Intent(AddJobActivity.this, PaymentActivity.class);
                i.putExtra("type", Consts.PAY_BOOST);
                i.putExtra("job", event.job);
                startActivityForResult(i, Consts.REQ_PAYMENT);
            } else if (job.getSpeedy()) {
                Intent i = new Intent(AddJobActivity.this, PaymentActivity.class);
                i.putExtra("type", Consts.PAY_SPEEDY);
                i.putExtra("job", event.job);
                startActivityForResult(i, Consts.REQ_PAYMENT);
            } else {//return result to main activity
                Intent i = new Intent();
                i.putExtra("job", event.job);
                setResult(RESULT_OK, i);
                finish();
            }
        } else {
            if(event.errorMessage != null){
                Snackbar.make(sliderLayout, event.errorMessage, Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(sliderLayout, getString(R.string.default_error), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}
