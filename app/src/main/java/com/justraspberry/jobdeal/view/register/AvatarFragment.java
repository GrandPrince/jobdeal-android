package com.justraspberry.jobdeal.view.register;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.justraspberry.jobdeal.App;
import com.justraspberry.jobdeal.Consts;
import com.justraspberry.jobdeal.R;
import com.justraspberry.jobdeal.Util;
import com.justraspberry.jobdeal.core.SP;
import com.justraspberry.jobdeal.event.uCropEvent;
import com.justraspberry.jobdeal.model.User;
import com.justraspberry.jobdeal.rest.service.client.ApiRestClient;
import com.justraspberry.jobdeal.rest.service.event.RegisterEvent;
import com.justraspberry.jobdeal.rest.service.event.UploadImageEvent;
import com.yalantis.ucrop.UCrop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.shagi.filepicker.ExtFile;
import org.shagi.filepicker.FilePicker;
import org.shagi.filepicker.FilePickerDialog;
import org.shagi.filepicker.FilePickerFragment;
import org.shagi.filepicker.FilePickerSettings;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class AvatarFragment extends Fragment {
    User user;

    @BindView(R.id.ivAvatar)
    CircleImageView ivAvatar;
    @BindView(R.id.btnNext)
    MaterialButton btnNext;
    @BindView(R.id.pbLogin)
    ProgressBar pbLogin;

    boolean isUploading = false;
    File cameraFile;


    public AvatarFragment() {
        // Required empty public constructor
    }


    public static AvatarFragment newInstance(User user) {
        AvatarFragment fragment = new AvatarFragment();
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           user = getArguments().getParcelable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_avatar, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void showLoading(boolean show) {
        if (show) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btnNext.getLayoutParams();
            layoutParams.width = (int) Util.dpToPx(getContext(), 44);
            btnNext.setLayoutParams(layoutParams);
            btnNext.setCornerRadius(Util.dpToPx(getContext(), 32));
            btnNext.setText("");
            btnNext.setEnabled(false);
            pbLogin.setVisibility(View.VISIBLE);
            isUploading = true;
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btnNext.getLayoutParams();
            layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            btnNext.setLayoutParams(layoutParams);
            btnNext.setText(getString(R.string.register));
            btnNext.setEnabled(true);
            btnNext.setCornerRadius(Util.dpToPx(getContext(), 8));
            pbLogin.setVisibility(View.INVISIBLE);
            isUploading = false;
        }
    }

    public static Pair<RequestBody, MultipartBody.Part> createRequestBody(@NonNull File file) {
        RequestBody req = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return new Pair<>(description, MultipartBody.Part.createFormData("image", file.getName(), req));
    }

    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    @OnClick({R.id.ivAvatar, R.id.btnGallery, R.id.btnTakePhoto})
    public void onAvatarClick(View view){
        if(view.getId() == R.id.btnGallery){
            pickFromGallery();
        } else if (view.getId() == R.id.btnTakePhoto){
            pickFromCamera();
        }
    }

    @OnClick(R.id.btnNext)
    public void onNextClick(){
        if(user.getAvatar() != null && !user.getAvatar().equalsIgnoreCase("")) {
            user.setLocale(getString(R.string.locale));
            user.setCountry(Util.getCountry(getContext()));
            showLoading(true);
            ApiRestClient.getInstance().register(user);
        } else {
            Snackbar snackbar = Snackbar.make(btnNext, getString(R.string.avatar_image_mandatory), Snackbar.LENGTH_LONG);
            View snackBarView = snackbar.getView();
            int marginBottom = Util.dpToPx(getContext(), 48);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getLayoutParams();

            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin + marginBottom);

            snackBarView.setLayoutParams(params);
            snackbar.show();
        }
    }

    private void pickFromGallery() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Consts.REQ_STORAGE_PERMISSION);
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
        options.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        options.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorGreen));
        options.setLogoColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
        options.setToolbarWidgetColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
        options.setActiveWidgetColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));


        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getActivity().getCacheDir(), destinationFileName)))
                .withAspectRatio(1, 1).withMaxResultSize(400, 400).withOptions(options);
        uCrop.start(getActivity(), this);
    }

    public void uploadImage(Uri uri) {
        showLoading(true);

        String filePath = null;
        if (uri != null && "content".equals(uri.getScheme())) {
            Cursor cursor = getActivity().getContentResolver().query(uri, new String[]{android.provider.MediaStore.Images.ImageColumns.DATA}, null, null, null);
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
            Uri photoURI = FileProvider.getUriForFile(getActivity(),
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
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.justraspberry.jobdeal.provider",
                        cameraFile);

                startCrop(photoURI);
            }
        } else {
            if(data != null) {
                if (requestCode == UCrop.REQUEST_CROP) {
                    handleCropResult(data);
                }
                if (resultCode == UCrop.RESULT_ERROR) {
                    handleCropError(data);
                }
            }
        }
    }

    private void handleCropResult(@NonNull Intent result) {
        Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            Glide.with(this).load(resultUri).apply(RequestOptions.placeholderOf(R.drawable.placeholder))
                    .apply(RequestOptions.bitmapTransform(
                            new MultiTransformation<Bitmap>(new CenterCrop(), new CircleCrop())))
                    .into(ivAvatar);
            uploadImage(resultUri);
            showLoading(true);
        } else {
            Snackbar.make(btnNext, getString(R.string.default_error), Snackbar.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Snackbar.make(btnNext, getString(R.string.default_error), Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(btnNext, getString(R.string.default_error), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUploadEvent(UploadImageEvent event){
        showLoading(false);
        if(event.imageUploadResponse != null){
            user.setAvatar(event.imageUploadResponse.getPath());
        } else {
            Util.displaySnackBar(Snackbar.make(btnNext, getString(R.string.default_error), Snackbar.LENGTH_LONG), 0, Util.dpToPx(getContext(), 48));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onRegisterEvent(RegisterEvent event) {
        EventBus.getDefault().removeStickyEvent(RegisterEvent.class);
        showLoading(false);
        if (event.registerResponse != null) {
            SP.getInstance().setJWT(event.registerResponse.getJwt());
            SP.getInstance().setUser(event.registerResponse.getUser());
            App.getInstance().setCurrentUser(event.registerResponse.getUser());

            FinishFragment finishFragment = FinishFragment.newInstance();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.flRoot, finishFragment);
            transaction.addToBackStack("finish");
            transaction.commit();
        } else {
            if(event.errorMessage != null && !event.errorMessage.equalsIgnoreCase("")) {
                Snackbar.make(btnNext, event.errorMessage, Snackbar.LENGTH_LONG).setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onNextClick();
                    }
                }).show();
            } else {
                Snackbar.make(btnNext, getString(R.string.default_error), Snackbar.LENGTH_LONG).setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onNextClick();
                    }
                }).show();
            }
        }
    }
}
