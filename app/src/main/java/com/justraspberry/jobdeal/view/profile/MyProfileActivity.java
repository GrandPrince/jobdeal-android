package com.justraspberry.jobdeal.view.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.core.SP;
import com.justraspberry.jobdeal.event.uCropEvent;
import com.justraspberry.jobdeal.misc.PhoneValidator;
import com.justraspberry.jobdeal.model.User;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.CheckEmailEvent;
import com.justraspberry.jobdeal.rest.service.event.SendVerificationEvent;
import com.justraspberry.jobdeal.rest.service.event.UploadImageEvent;
import com.justraspberry.jobdeal.rest.service.event.UserUpdateEvent;
import com.justraspberry.jobdeal.view.job.JobDetailsActivity;
import com.justraspberry.jobdeal.view.login.LoginActivity;
import com.justraspberry.jobdeal.view.main.MainActivity;
import com.justraspberry.jobdeal.view.myjobs.MyJobsActivity;
import com.justraspberry.jobdeal.view.register.RegisterActivity;
import com.justraspberry.jobdeal.view.statistic.StatisticActivity;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.shagi.filepicker.ExtFile;
import org.shagi.filepicker.FilePicker;
import org.shagi.filepicker.FilePickerDialog;
import org.shagi.filepicker.FilePickerFragment;

import java.io.File;
import java.io.IOException;

public class MyProfileActivity extends AppCompatActivity {
    @BindView(R.id.ivProfilePicture)
    ImageView ivProfilePicture;
    @BindView(R.id.tvUserName)
    TextView tvUsername;
    @BindView(R.id.ivEditAccount)
    ImageView editAccount;
    @BindView(R.id.ivEditUserPhoto)
    ImageView ivEditMainImage;
    @BindView(R.id.etName)
    TextInputEditText etName;
    @BindView(R.id.etSurname)
    TextInputEditText etSurname;
    @BindView(R.id.etMobileNumber)
    TextInputEditText etMobileNumber;
    @BindView(R.id.etEmail)
    TextInputEditText etEmail;
    @BindView(R.id.etBankId)
    TextInputEditText etBankId;
    @BindView(R.id.etPassword)
    TextView tvPassword;
    @BindView(R.id.tvResetPass)
    TextView tvResetPass;
    @BindView(R.id.etAddress)
    TextInputEditText etAdress;
    @BindView(R.id.etZipCode)
    TextInputEditText etZipCode;
    @BindView(R.id.etCity)
    TextInputEditText etCity;
    @BindView(R.id.etAboutMe)
    TextInputEditText etAboutMe;
    @BindView(R.id.ivEditAboutMe)
    ImageView ivEditAboutMe;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvUserSince)
    TextView tvUserSince;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.btnSaveChanges)
    MaterialButton btnSaveChanges;
    @BindView(R.id.cvMyJobs)
    MaterialCardView cvMyJobs;

    private boolean isEdit, isEditAboutMe = false;
    private boolean emailExists = false;
    private boolean isEmailChanged, isMobileChanged = false;
    private boolean isUploading = false;

    File cameraFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_arrow_back));

        setupProfile(App.getInstance().getCurrentUser());

        etAboutMe.setBackground(null);
        etAboutMe.setFocusableInTouchMode(false);
        etAboutMe.setFocusable(false);

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 6) {

                    if (!App.getInstance().getCurrentUser().getEmail().equalsIgnoreCase(etEmail.getText().toString()))
                        isEmailChanged = true;
                    else
                        isEmailChanged = false;

                    ApiRestClient.getInstance().checkEmail(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 6) {

                    String phoneNumber = PhoneValidator.validate(App.getInstance().getCurrentUser().getCountry()
                            , etMobileNumber.getText().toString()
                            , App.getInstance().getCurrentUser().getCountry());

                    if (phoneNumber != null) {
                        if (!App.getInstance().getCurrentUser().getMobile().equalsIgnoreCase(phoneNumber))
                            isMobileChanged = true;
                        else
                            isMobileChanged = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public static Pair<RequestBody, MultipartBody.Part> createRequestBody(@NonNull File file) {
        RequestBody req = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return new Pair<>(description, MultipartBody.Part.createFormData("image", file.getName(), req));
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    private void pickFromGallery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Consts.REQ_STORAGE_PERMISSION);
        } else {
            //Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*").addCategory(Intent.CATEGORY_OPENABLE);
            //startActivityForResult(pickImageIntent, Const.REQUEST_CODE_GALLERY);
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            Intent chooserIntent = Intent.createChooser(pickIntent, getString(R.string.gallery));
            startActivityForResult(chooserIntent, Consts.REQ_IMAGE_GALLERY);
        }

    }

    public void startCrop(Uri uri) {
        String destinationFileName = String.valueOf(System.currentTimeMillis());

        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(80);
        options.setFreeStyleCropEnabled(false);
        options.setShowCropGrid(false);
        options.setShowCropFrame(false);
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorGreen));
        options.setLogoColor(ContextCompat.getColor(this, R.color.colorWhite));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.colorWhite));
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorAccent));


        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)))
                .withAspectRatio(1, 1).withMaxResultSize(400, 400).withOptions(options);
        uCrop.start(this);
    }

    public void uploadImage(Uri uri) {
        showLoading(true);

        String filePath = null;
        if (uri != null && "content".equals(uri.getScheme())) {
            Cursor cursor = getContentResolver().query(uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
            cursor.moveToFirst();
            filePath = cursor.getString(0);
            cursor.close();
        } else {
            filePath = uri.getPath();
        }

        ApiRestClient.getInstance().uploadAvatar(createRequestBody(new File(filePath)));

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
            startActivityForResult(takePicture, Consts.REQ_IMAGE_CAMERA);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.e("OnActivityResult Fragment");

        if (resultCode == RESULT_OK && (requestCode == Consts.REQ_IMAGE_CAMERA || requestCode == Consts.REQ_IMAGE_GALLERY)) {
            if (data != null) { //its from gallery
                Uri imageUri = data.getData();
                if (imageUri != null)
                    startCrop(imageUri);
            } else if (cameraFile != null) {//from camera
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.justraspberry.jobdeal.provider",
                        cameraFile);

                startCrop(photoURI);
            }
        } else if (requestCode == Consts.REQ_SMS_VERIFY){
            if (resultCode == RESULT_OK){
                User u = data.getParcelableExtra("user");

                App.getInstance().getCurrentUser().setMobile(u.getMobile());
                App.getInstance().getCurrentUser().setUid(u.getUid());

                ApiRestClient.getInstance().updateUser(App.getInstance().getCurrentUser());
            }
        } else if (requestCode == UCrop.REQUEST_CROP && data != null) {
            if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            } else if (resultCode == UCrop.RESULT_ERROR) {
                handleCropError(data);
            }
        }

    }

    private void handleCropResult(@NonNull Intent result) {
        Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            Glide.with(this).load(resultUri).apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                    .apply(RequestOptions.bitmapTransform(
                            new MultiTransformation<Bitmap>(new CenterCrop(), new CircleCrop())))
                    .into(ivProfilePicture);
            uploadImage(resultUri);
            showLoading(true);
        } else {
            Snackbar.make(ivProfilePicture, getString(R.string.default_error), Snackbar.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        Snackbar.make(ivProfilePicture, getString(R.string.default_error), Snackbar.LENGTH_LONG).show();
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void showLoading(boolean show) {
        if (show) {
            isUploading = true;
            progressBar.setVisibility(View.VISIBLE);
        } else {
            isUploading = false;
            progressBar.setVisibility(View.GONE);
        }

    }

    public void setupProfile(User user) {

        Glide.with(this).load(user.getAvatar())
                .apply(new RequestOptions().transforms(new CenterCrop(), new CircleCrop()).placeholder(R.drawable.placeholder))
                .into(ivProfilePicture);


        tvUsername.setText(user.getFullName());
        etName.setText(user.getName());
        etSurname.setText(user.getSurname());
        etMobileNumber.setText(user.getMobile());
        etEmail.setText(user.getEmail());
        tvPassword.setText(SP.getInstance().getPassword());
        etAdress.setText(user.getAddress());
        etZipCode.setText(user.getZip());
        etCity.setText(user.getCity());
        tvUserSince.setText(getString(R.string.user_since, user.getCreatedAt()));
        etBankId.setText(user.getBankId());
        etAboutMe.setText(user.getAboutMe());

        if (!isEdit) {
            enableEdit(false);
            etName.setBackground(null);
            etSurname.setBackground(null);
            etMobileNumber.setBackground(null);
            etEmail.setBackground(null);
            tvPassword.setBackground(null);
            etAdress.setBackground(null);
            etZipCode.setBackground(null);
            etCity.setBackground(null);
            etBankId.setBackground(null);
            tvResetPass.setVisibility(View.GONE);
        } else {
            enableEdit(true);
            etName.setBackground(getResources().getDrawable(R.drawable.round_shape));
            etSurname.setBackground(getResources().getDrawable(R.drawable.round_shape));
            etMobileNumber.setBackground(getResources().getDrawable(R.drawable.round_shape));
            etEmail.setBackground(getResources().getDrawable(R.drawable.round_shape));
            etAdress.setBackground(getResources().getDrawable(R.drawable.round_shape));
            etZipCode.setBackground(getResources().getDrawable(R.drawable.round_shape));
            etCity.setBackground(getResources().getDrawable(R.drawable.round_shape));
            etBankId.setBackground(getResources().getDrawable(R.drawable.round_shape));
            tvResetPass.setVisibility(View.VISIBLE);

        }
    }

    @OnClick(R.id.btnDeleteAccount)
    public void onDeleteAccountClick(){
        new AlertDialog.Builder(this).setTitle(getString(R.string.delete_account).toUpperCase())
                .setMessage(getString(R.string.delete_account_confirmation))
                .setPositiveButton(R.string.delete_account, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        ApiRestClient.getInstance().deleteAccount();
                        ApiRestClient.getInstance().logout();
                        SP.getInstance().logout();

                        Intent i = new Intent(MyProfileActivity.this, LoginActivity.class);
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
    }


    @OnClick(R.id.ivEditAccount)
    public void onEditClick() {
        isEdit = true;
        setupProfile(App.getInstance().getCurrentUser());
    }

    @OnClick(R.id.ivEditAboutMe)
    public void onEditAboutMeClick() {
        ivEditAboutMe.setActivated(!ivEditAboutMe.isActivated());

        if (ivEditAboutMe.isActivated()) {
            etAboutMe.setBackground(getResources().getDrawable(R.drawable.round_shape));
            etAboutMe.setFocusableInTouchMode(true);
            etAboutMe.setFocusable(true);
        } else {
            etAboutMe.setBackground(null);
            etAboutMe.setFocusable(false);
        }
    }

    @OnClick(R.id.ivEditUserPhoto)
    public void onEditPhotoClick() {
        new AlertDialog.Builder(this).setTitle(R.string.choose_picture_source).setMessage(R.string.choose_gallery_or_camera).setPositiveButton(R.string.gallery, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                pickFromGallery();
            }
        }).setNegativeButton(R.string.camera, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                pickFromCamera();
            }
        }).show();
    }

    public void enableEdit(boolean enable) {
        if (enable) {
            etName.setFocusableInTouchMode(true);
            etName.setFocusable(true);
            etSurname.setFocusableInTouchMode(true);
            etSurname.setFocusable(true);
            etMobileNumber.setFocusableInTouchMode(true);
            etMobileNumber.setFocusable(true);
            etEmail.setFocusable(true);
            etEmail.setFocusableInTouchMode(true);
            etAdress.setFocusableInTouchMode(true);
            etAdress.setFocusable(true);
            etZipCode.setFocusableInTouchMode(true);
            etZipCode.setFocusable(true);
            etCity.setFocusableInTouchMode(true);
            etCity.setFocusable(true);
            etBankId.setFocusableInTouchMode(true);
            etBankId.setFocusable(true);
        } else {
            etName.setFocusable(false);
            etSurname.setFocusable(false);
            etEmail.setFocusable(false);
            etMobileNumber.setFocusable(false);
            tvPassword.setFocusable(false);
            etAdress.setFocusable(false);
            etZipCode.setFocusable(false);
            etCity.setFocusable(false);
            etBankId.setFocusable(false);
        }

    }


    private boolean isFieldsValid() {
        boolean isValid = true;

        if (etName.getText().toString().isEmpty()) {
            isValid = false;
            etName.setError(getString(R.string.error_name));
        }
        if (etSurname.getText().toString().isEmpty()) {
            isValid = false;
            etSurname.setError(getString(R.string.error_surname));
        }

        if (etEmail.getText().toString().isEmpty() || !etEmail.getText().toString().contains(".") || !etEmail.getText().toString().contains("@")) {
            isValid = false;
            etEmail.setError(getString(R.string.error_email));
        }

        if (etAdress.getText().toString().length() < 2) {
            isValid = false;
            etAdress.setError(getString(R.string.error_address));
        }

        if (etZipCode.getText().toString().length() == 0) {
            isValid = false;
            etZipCode.setError(getString(R.string.error_zip));
        }

        if (etCity.getText().toString().length() < 2) {
            isValid = false;
            etCity.setError(getString(R.string.error_city));
        }

        /*if (etBankId.getText().toString().length() < 10 && etBankId.getText().toString().length() > 12) {
            isValid = false;
            etBankId.setError(getString(R.string.bank_id_validate_error));
        }*/

        return isValid;
    }

    @OnClick(R.id.cvMyJobs)
    public void onPostedJobsClick(){
        Intent i = new Intent(MyProfileActivity.this, MyJobsActivity.class);
        i.putExtra("isPostedJobs", true);
        startActivity(i);
    }

    @OnClick(R.id.btnSaveChanges)
    public void saveChanges() {
        if (isMobileChanged) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.verification))
                    .setMessage(getString(R.string.mobile_number_changed))
                    .setPositiveButton(getString(R.string.verify), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            User user = new User();
                            user.setMobile(etMobileNumber.getText().toString());
                            user.setLocale(App.getInstance().getCurrentUser().getLocale());
                            user.setCountry(App.getInstance().getCurrentUser().getCountry());

                            ApiRestClient.getInstance().requestVerification(user);
                        }
                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    etMobileNumber.setText(App.getInstance().getCurrentUser().getMobile());
                    dialog.dismiss();
                }
            }).show();
            return;
        }

        if (isFieldsValid()) {
            App.getInstance().getCurrentUser().setName(etName.getText().toString());
            App.getInstance().getCurrentUser().setSurname(etSurname.getText().toString());

            if (isEmailChanged)
                App.getInstance().getCurrentUser().setEmail(etEmail.getText().toString());


            App.getInstance().getCurrentUser().setAddress(etAdress.getText().toString());
            App.getInstance().getCurrentUser().setZip(etZipCode.getText().toString());
            App.getInstance().getCurrentUser().setCity(etCity.getText().toString());
            App.getInstance().getCurrentUser().setBankId(etBankId.getText().toString());
            App.getInstance().getCurrentUser().setAboutMe(etAboutMe.getText().toString());

            ApiRestClient.getInstance().updateUser(App.getInstance().getCurrentUser());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUser(UserUpdateEvent event) {
        if (event.user != null) {
            if(isMobileChanged){
                isMobileChanged = false;
                App.getInstance().setCurrentUser(event.user);
                SP.getInstance().setUser(App.getInstance().getCurrentUser());
                btnSaveChanges.callOnClick();
                return;
            }

            App.getInstance().setCurrentUser(event.user);
            isEdit = false;
            enableEdit(false);
            setupProfile(App.getInstance().getCurrentUser());
            Snackbar.make(btnSaveChanges, R.string.successfully_update, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(btnSaveChanges, R.string.default_error, Snackbar.LENGTH_LONG).show();
            setupProfile(App.getInstance().getCurrentUser());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCheckEmail(CheckEmailEvent event) {
        if (event.isExist != null) {
            if (event.isExist) {
                emailExists = true;

            } else {
                emailExists = false;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUploadEvent(UploadImageEvent event) {
        showLoading(false);
        if (event.imageUploadResponse != null) {
            App.getInstance().getCurrentUser().setAvatar(event.imageUploadResponse.getPath());
        } else {
            Util.displaySnackBar(Snackbar.make(btnSaveChanges, getString(R.string.default_error), Snackbar.LENGTH_LONG), 0, Util.dpToPx(this, 48));
        }
    }

    @OnClick(R.id.tvAccountStats)
    public void accStats() {
        Intent i = new Intent(this, StatisticActivity.class);
        startActivity(i);
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestVerificationEvent(SendVerificationEvent event){
        showLoading(false);
        if(event.user != null){
            User u = new User();
            u.setUid(event.user.getUid());
            u.setId(event.user.getId());
            u.setMobile(etMobileNumber.getText().toString());

            Intent intent = new Intent(MyProfileActivity.this, SmsVerifyActivity.class);
            intent.putExtra("user", u);
            startActivityForResult(intent, Consts.REQ_SMS_VERIFY);
        } else {
            Util.displaySnackBar(Snackbar.make(btnSaveChanges, getString(R.string.default_error), Snackbar.LENGTH_LONG), Util.dpToPx(this, 0), Util.dpToPx(this, 48));
        }
    }
}
