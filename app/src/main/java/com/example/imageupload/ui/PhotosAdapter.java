package com.example.imageupload.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imageupload.R;
import com.example.imageupload.data.model.Photo;

import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> {

    public interface ClickListener{
        void onItemClick(Photo photo);
    }

    private ClickListener listener;

    private List<Photo> mList;

    public PhotosAdapter(List<Photo> list, ClickListener l){
        mList = list;
        listener = l;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private Button button;
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivPhoto);
            button = itemView.findViewById(R.id.btnDelete);
            button.setOnClickListener(v -> {
                if (listener!=null)
                    listener.onItemClick(mList.get(getAdapterPosition()));
            });
        }

        void bind(Photo photo){
            byte[] arr = photo.getImage();
            Bitmap bmp = BitmapFactory.decodeByteArray(arr, 0, arr.length);
            imageView.setImageBitmap(bmp);
        }
    }


}
