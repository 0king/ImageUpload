package com.example.imageupload.ui;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.imageupload.data.MainRepo;
import com.example.imageupload.data.model.Photo;

import java.util.ArrayList;
import java.util.List;

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

    public void saveBitmap(){
        MainRepo.INSTANCE.savePhoto(getApplication(), mBitmap);
    }
}
