package com.example.clothingapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clothingapp.R;
import com.example.clothingapp.adapters.ClothingItemImageAdapter;
import com.example.clothingapp.data.ClothesSize;
import com.example.clothingapp.data.ClothingItem;
import com.example.clothingapp.data.Gender;
import com.example.clothingapp.util.CartManager;
import com.example.clothingapp.util.SavedManager;
import com.google.android.material.chip.Chip;

import org.w3c.dom.Text;

import java.util.List;

public class ClothingItemActivity extends AppCompatActivity {

    public static final String INTENT_CLOTHING_ITEM_KEY = "clothingItem";
    public static final String TRANSITION_SHARED_IMAGE_NAME = "clothingItemImage";

    private static final float DOT_UNSELECTED_SP = 14.0f;
    private static final float DOT_SELECTED_SP = 20.0f;

    private static class ViewHolder {
        private final ViewPager2 images;
        private final TextView price;
        private final TextView title;
        private final TextView description;
        private final LinearLayout dotsLayout, scrollLayout;
        private final ImageButton addToCartButton;
        private final Chip saveChip;

        public ViewHolder(Activity activity) {
            this.images = activity.findViewById(R.id.clothing_item_pager);
            this.price = activity.findViewById(R.id.clothing_item_price);
            this.title = activity.findViewById(R.id.clothing_item_title);
            this.description = activity.findViewById(R.id.clothing_item_description);
            this.addToCartButton = activity.findViewById(R.id.clothing_item_cart_button);
            this.saveChip = activity.findViewById(R.id.clothing_item_save_button);

            this.scrollLayout = activity.findViewById(R.id.clothing_item_scroll_layout);

            this.dotsLayout = activity.findViewById(R.id.clothing_item_dots_layout);
            this.dotsLayout.removeAllViews();
        }
    }

    private ViewHolder vh;
    private ClothingItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing_item);

        vh = new ViewHolder(this);

        var intent = getIntent();
        item = (ClothingItem) intent.getSerializableExtra(INTENT_CLOTHING_ITEM_KEY);

        if(item == null) {
            item = new ClothingItem("NOTHING",
                                -1.0f,
                                    ClothesSize.MEDIUM,
                                    List.of(),
                          "No clothing item passed to activity",
                            "NULL",
                                     Gender.MALE);
            // Warn if no provider is sent
            Log.w(getPackageName(), "No ClothingItem passed to ClothingItemActivity");
        }

        final var displayMetrics = getResources().getDisplayMetrics();

        vh.title.setText(item.getName());
        vh.price.setText(String.format("$ %.2f", item.getPrice()));
        vh.description.setText(Html.fromHtml(item.getDescription(), Html.FROM_HTML_SEPARATOR_LINE_BREAK_BLOCKQUOTE));

        vh.images.setAdapter(new ClothingItemImageAdapter(this, item));
        var margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, displayMetrics);
        vh.images.setPageTransformer(new MarginPageTransformer((int) margin));
        initDots(item);
        vh.images.registerOnPageChangeCallback(new ImageChangedCallback());

        // Button callbacks
        vh.addToCartButton.setOnClickListener(v -> this.onCartClicked(v));

        // Save button
        vh.saveChip.setOnCheckedChangeListener((buttonView, isChecked) -> this.onSaveChecked(buttonView, isChecked));
        vh.saveChip.setChecked(SavedManager.isSaved(item));
    }

    private void onSaveChecked(CompoundButton button, boolean isChecked) {
        if(isChecked) {
            SavedManager.addToSaved(item);
        } else {
            SavedManager.removeFromSaved(item);
        }
    }

    private void onCartClicked(View v) {
        Toast.makeText(this, "Added item to cart", Toast.LENGTH_SHORT).show();
        CartManager.addToCart(item);
    }


    private void initDots(ClothingItem item) {
        vh.dotsLayout.removeAllViews();

        var imageResourceIds = item.getImageUrls();

        for(var ignored : imageResourceIds) {
            vh.dotsLayout.addView(createDot());
        }
    }

    private TextView createDot() {
        TextView tv = new TextView(this);

        var params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        params.setMargins(2, 0, 2, 0);

        tv.setLayoutParams(params);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tv.setText(Html.fromHtml("&#8226;", Html.FROM_HTML_MODE_COMPACT));

        return tv;
    }

    private TextView selectDot(TextView tv) {
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, DOT_SELECTED_SP);
        return tv;
    }

    private class ImageChangedCallback extends ViewPager2.OnPageChangeCallback {

        private int lastSelectedIndex = 0;

        ImageChangedCallback() {
            if(vh.dotsLayout.getChildCount() > 0) {
                selectDot((TextView) vh.dotsLayout.getChildAt(0));
            }
        }

        @Override
        public void onPageSelected(int position) {
            final TextView prev = (TextView) vh.dotsLayout.getChildAt(lastSelectedIndex);
            final TextView curr = (TextView) vh.dotsLayout.getChildAt(position);

            // Setup animation
            var anim = ValueAnimator.ofFloat(0, DOT_SELECTED_SP - DOT_UNSELECTED_SP);
            anim.setDuration(150); // milliseconds
            anim.addUpdateListener(x -> {
                prev.setTextSize(TypedValue.COMPLEX_UNIT_SP, DOT_SELECTED_SP - (Float) x.getAnimatedValue());
                curr.setTextSize(TypedValue.COMPLEX_UNIT_SP, DOT_UNSELECTED_SP + (Float) x.getAnimatedValue());
            });
            anim.start();

            lastSelectedIndex = position;
        }
    }
}