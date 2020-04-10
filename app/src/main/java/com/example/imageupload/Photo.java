package com.example.imageupload;

import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "photos")
public class Photo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private long id = System.currentTimeMillis();

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image;

    public Photo(byte[] image) {
        //this.id = id;
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
