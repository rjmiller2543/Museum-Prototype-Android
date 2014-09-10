package com.example.robertmiller.museum_prototype_android;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by robertmiller on 9/9/14.
 */
public class ArtistImageFragment extends Fragment {
    private String title;
    private int page;
    private View selfView;

    public static ImageFragment newInstance(int page, String title) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        imageFragment.setArguments(args);
        return imageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstances) {
        //View view = inflater.inflate(R.layout.image_fragment,container,false);
        selfView = inflater.inflate(R.layout.image_fragment,container,false);
        TextView titleText = (TextView)selfView.findViewById(R.id.artistImageFragText);
        titleText.setText(page + " -- " + title);
        //titleText.setTextColor(0xFFFF00);
        ImageView imageView = (ImageView)selfView.findViewById(R.id.artistImageFragImageView);
        imageView.setBackgroundColor(0x0000FF);
        return selfView;
    }


    public void setImageView(Bitmap bmp) {
        ImageView imageView = (ImageView)selfView.findViewById(R.id.artistImageFragImageView);
        imageView.setImageBitmap(bmp);
    }
}
