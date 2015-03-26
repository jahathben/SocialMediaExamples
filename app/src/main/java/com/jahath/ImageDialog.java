package com.jahath;

/**
 * Created by Jahath Bennett on 3/11/2015.
 */

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import compenland.pictureviewer.R;

public class ImageDialog extends Dialog{

	public ImageView mDialog;
	String url;
	Activity b;

	public ImageDialog(Activity a, String url) {
		super(a);
		b = a;
		this.url = url;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		mDialog = (ImageView)findViewById(R.id.image);

        Glide.with(getContext())
                .load(url)
                .crossFade()
                .into(mDialog);
		mDialog.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		} );
	}
}
