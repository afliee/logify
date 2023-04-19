package com.example.logify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.logify.R;

public class ViewPagerAdapter extends PagerAdapter {
    Context context;

    int images[] = {
            R.drawable.first_slide,
            R.drawable.second_slide,
            R.drawable.thirt_slide
    };

    int titles[] = {
            R.string.title_1,
            R.string.title_2,
            R.string.title_3
    };

    int decriptions[] = {
            R.string.description_1,
            R.string.description_2,
            R.string.description_3
    };

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slider_layout, container, false);

        ImageView imageView = view.findViewById(R.id.imageSlider);
        TextView title = view.findViewById(R.id.tvTitle);
        TextView description = view.findViewById(R.id.tvDescription);

        imageView.setImageResource(images[position]);
        title.setText(titles[position]);
        description.setText(decriptions[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
