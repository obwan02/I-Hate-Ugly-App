package com.example.clothingapp.adapters;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.example.clothingapp.R;
import com.example.clothingapp.activities.ClothingItemActivity;
import com.example.clothingapp.data.ClothingItem;
import com.example.clothingapp.data.ImageDownloader;
import com.example.clothingapp.fragments.ClothingItemImage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClothingItemImageAdapter extends FragmentStateAdapter {

    private ClothingItem clothingItem;
    private FragmentActivity activity;
    private ImageDownloader downloader = new ImageDownloader(false);

    public ClothingItemImageAdapter(@NonNull FragmentActivity activity, @NonNull ClothingItem item) {
        super(activity);
        this.clothingItem = item;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        ClothingItemImage fragment = new ClothingItemImage();
        fragment.setDownloader(downloader);
        if(position == 0) {
            fragment.setSharedItemTransitionName("");
        }

        Bundle args = new Bundle();
        args.putString(ClothingItemImage.BUNDLE_IMAGE_URL_KEY, clothingItem.getImageUrls().get(position));
        args.putSerializable(ClothingItemImage.BUNDLE_ITEM_KEY, clothingItem);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return clothingItem.getImageUrls().size();
    }

    public ClothingItem getClothingItem() {
        return clothingItem;
    }

    public void setClothingItem(ClothingItem clothingItem) {
        this.clothingItem = clothingItem;
        this.notifyDataSetChanged();
    }
}
