package com.example.imageupload.data;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.imageupload.data.local.PhotoDao;
import com.example.imageupload.data.local.PhotoDb;
import com.example.imageupload.data.model.Photo;
import com.example.imageupload.data.remote.Api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public enum MainRepo {
    INSTANCE; //singleton

    private LiveData<List<Photo>> mAllPhotos = null;
    private PhotoDao mPhotoDao = null;
    private Api mApi;

    MainRepo(){
        initRetrofit();
    }

    public LiveData<List<Photo>> getAllPhotos(Application a){
        if (mAllPhotos ==null)
            initDb(a);
        return mAllPhotos;
    }

    public void deletePhoto(Application a, Photo photo){
        if (mPhotoDao==null)
            initDb(a);
        PhotoDb.databaseWriteExecutor.execute(()->{
            mPhotoDao.delete(photo);
        });
    }

    public void savePhoto(Application a, Bitmap bmp){
        if (mPhotoDao==null)
            initDb(a);

        PhotoDb.databaseWriteExecutor.execute(()->{
            //save in db
            //Bitmap photo = (Bitmap) data.getExtras().get("data");
            //imageView.setImageBitmap(photo);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            //help.insert(byteArray);
            Photo photo = new Photo(byteArray);

            mPhotoDao.insert(photo);

            //upload to server
            byte[] arr = photo.getImage();
            createFile(arr);
        });
    }

    private void createFile(byte[] fileData) {
        try {
            //Create directory..
            File root = Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_PICTURES
                            + "/photos");
            File dir = new File(root + File.separator);
            if (!dir.exists()) dir.mkdir();

            //Create file..
            File file = new File(root + File.separator + "photo.jpg");
            file.createNewFile();

            FileOutputStream out = new FileOutputStream(file);
            out.write(fileData);
            out.close();

            //upload
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);

            MultipartBody.Part multipartBody =MultipartBody.Part.createFormData("file",
                    file.getName(),
                    requestFile);

            mApi.postPhoto(multipartBody).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("durga", response.toString());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("durga", t.toString());
                }
            });

            /*//run on background thread
            PhotoDb.databaseWriteExecutor.execute(()->{

            });*/

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            //e.printStackTrace();
        }
    }

    private void initDb(Application a){
        PhotoDb db = PhotoDb.getDatabase(a);
        mPhotoDao = db.photoDao();
        mAllPhotos = mPhotoDao.getAll();
    }

    private void initRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                //.addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                //.client(client)
                .build();
        mApi = retrofit.create(Api.class);
    }

}
