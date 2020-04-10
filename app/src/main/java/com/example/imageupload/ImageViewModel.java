package com.example.imageupload;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageViewModel extends AndroidViewModel {

    private Bitmap mBitmap;
    private LiveData<List<Photo>> mAllPhotos;// = new MutableLiveData<>();
    private List<Photo> mAllPhoto = new ArrayList<>();
    private MutableLiveData<Bitmap> mBitmapLive = new MutableLiveData<>();

    public LiveData<List<Photo>> getAllPhotos(){
        mAllPhotos = MainRepo.INSTANCE.getAllPhotos(getApplication());
        return mAllPhotos;
    }

    public LiveData<Bitmap> getBitmapLive(){
        return mBitmapLive;
    }

    public void deletePhoto(Photo photo){
        MainRepo.INSTANCE.deletePhoto(getApplication(), photo);
    }

    public Bitmap getBitmap(){
        return mBitmap;
    }

    public void setBitmap(Bitmap b){
        mBitmap = b;
        mBitmapLive.postValue(b);
    }

    public ImageViewModel(@NonNull Application application) {
        super(application);
    }

    public void saveBitmap(Photo photo){
        MainRepo.INSTANCE.savePhoto(getApplication(), photo);
    }
}
