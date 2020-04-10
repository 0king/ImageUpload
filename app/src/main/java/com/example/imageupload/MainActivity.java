package com.example.imageupload;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mUploadBtn, mOpenGalleryBtn;
    private ImageView mImageView;
    private RecyclerView mPhotoListView;
    private ImageViewModel mViewModel;

    private static final int RESULT_LOAD_IMAGE = 1234;
    private static final int REQUEST_PERMISSION = 1233;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mOpenGalleryBtn = findViewById(R.id.btnLoad);
        mUploadBtn = findViewById(R.id.btnUpload);
        mPhotoListView = findViewById(R.id.recyclerView);
        mImageView = findViewById(R.id.ivMain);
        mOpenGalleryBtn.setOnClickListener(this);
        mUploadBtn.setOnClickListener(this);

        mViewModel = new ViewModelProvider(this).get(ImageViewModel.class);
        mPhotoListView.setLayoutManager(
                new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        observeData();

    }

    private void observeData(){
        mViewModel.getAllPhotos().observe(this, list -> {
            mPhotoListView.setAdapter(new PhotosAdapter(list, new PhotosAdapter.ClickListener() {
                @Override
                public void onItemClick(Photo photo) {
                    mViewModel.deletePhoto(photo);
                }
            }));
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnLoad:
                checkPerm();
                break;
            case R.id.btnUpload:
                saveImage();
                break;
        }
    }

    private void checkPerm(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);

        }
        else {
            openGallery();
        }
    }

    private void openGallery(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                openGallery();
            } else {
                // User refused to grant permission.
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE &&
                resultCode == RESULT_OK &&
                null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver()
                    .query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            //mImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            Bitmap bmp = null;
            bmp = getScaledBitmap(picturePath, 800, 800);
            mViewModel.setBitmap(bmp);
            mImageView.setImageBitmap(bmp);

            /*try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }

    private void saveImage(){
        //Bitmap photo = (Bitmap) data.getExtras().get("data");
        //imageView.setImageBitmap(photo);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mViewModel.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        //help.insert(byteArray);
        Photo photo = new Photo(byteArray);
        mViewModel.saveBitmap(photo);
    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
}
