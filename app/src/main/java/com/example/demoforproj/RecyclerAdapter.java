package com.example.demoforproj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.interfaces.ItemChangeListener;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<SlideModel> slideModels;
    private ArrayList<Uri> uriArrayList;

    public RecyclerAdapter(List<SlideModel> slideModels,ArrayList<Uri> uriArrayList) {
        this.slideModels = slideModels;
        this.uriArrayList=uriArrayList;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_single_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        holder.imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int i) {
                Context cont = holder.imageSlider.getContext();
                ArrayList<String> selstr = new ArrayList<>();
                for(int l=0;l<uriArrayList.size();l++)
                    selstr.add(uriArrayList.get(l).toString());
                Intent full = new Intent(cont,FullDisp.class);
                full.putStringArrayListExtra("uri",selstr);
                full.putExtra("position",i);
                try{
                    cont.startActivity(full);
                }catch (Exception e){
                    Log.d("error",e.getMessage());
                }
            }

            @Override
            public void doubleClick(int i) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return uriArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageSlider imageSlider;
        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            imageSlider=itemView.findViewById(R.id.imageR);
            imageSlider.setImageList(slideModels);
        }
    }
}
