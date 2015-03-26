package com.jahath;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import compenland.pictureviewer.R;

/**
 * Created by Jahath Bennett on 3/11/2015.
 */
public class CustomGrid extends ArrayAdapter<String> {

    private Context mContext;
    private ArrayList<String> urlList;
    LayoutInflater inflater;


    public CustomGrid(Context c,  ArrayList<String> urls) {
        super(c, R.layout.adapter_view, urls);
        mContext = c;
        urlList = urls;
        Log.d("array size", String.valueOf(urls.size()));
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        Log.d("count called",String.valueOf(urlList.size()));
        return urlList.size();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewGroup.LayoutParams lp;
         if (view == null)
        {
            lp = new ViewGroup.LayoutParams(0, 0);
            view = inflater.inflate(R.layout.adapter_view, viewGroup, false);

        } else {
            lp =  view.getLayoutParams();
         }
        ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
        int size = 0;
        if(i % 3 == 0)
            size = 400;
        else
            size = 250;

        lp.width = size;
        lp.height = size;
        view.setLayoutParams(lp);

        Picasso.with(mContext)
                .load(urlList.get(i))
                .resize(size,size)
                .centerCrop()
                .into(imageView);

        imageView.setOnDragListener(new MyDragListener());
        imageView.setTag(urlList.get(i));
        return view;
    }

    class MyDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(Color.RED);
                    v.setPadding(4,4,4,4);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(Color.TRANSPARENT);
                    v.setPadding(0,0,0,0);
                    break;
                case DragEvent.ACTION_DROP:
                    v.setBackgroundColor(Color.TRANSPARENT);
                    v.setPadding(0,0,0,0);
                    // Dropped, redraw
                    View view = (View) event.getLocalState(); //dragged image
                    ImageView container = (ImageView) v; //image dropped onto
                    String sourceUrl = view.getTag().toString();
                    String containerUrl = container.getTag().toString();

                    int insertIndex = urlList.indexOf(containerUrl);
                    urlList.remove(sourceUrl);
                    urlList.add(insertIndex, sourceUrl);

                    notifyDataSetChanged();

                    break;
                case DragEvent.ACTION_DRAG_ENDED:

                default:
                    break;
            }
            return true;
        }
    }
}
