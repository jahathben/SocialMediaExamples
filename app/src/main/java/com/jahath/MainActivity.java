package com.jahath;

/**
 * Created by Jahath Bennett on 3/11/2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import compenland.pictureviewer.R;


public class MainActivity extends Activity implements AbsListView.OnScrollListener{
	JSONObject json;
	String streamUrl;
	JSONCall caller;
    ListView grid;
    private ProgressDialog progressDialog;
    ArrayList<String> urlArray = new ArrayList<String>();
    CustomGrid adapter;
    int lastLoadPosition;

    @Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_view);
	}

    @Override
	protected void onResume(){
		super.onResume();
        streamUrl = ApplicationData.APIURL+"/tags/selfie/media/recent?client_id="+ApplicationData.CLIENTID;
        grid = (ListView) findViewById(R.id.dynamic_grid);
        adapter = new CustomGrid(MainActivity.this, urlArray);
        grid.setAdapter(adapter);
        setupConnection();
        grid.setOnScrollListener(this);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ImageDialog dialog = new ImageDialog(MainActivity.this, view.getTag().toString());
                           dialog.show();

            }
        });

        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClipData data = ClipData.newPlainText("", "");
                            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                return false;
            }
        });

	}

    // Initialization - check for network connection, and call JSON
    public void setupConnection(){
        caller = new JSONCall();
        if(isNetworkAvailable()){
            caller.execute();
        }else
        {
            Toast.makeText(this, "Network Unavailable, Please connect to network and try again", Toast.LENGTH_LONG).show();
        }
    }

    //check for network connection
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void displayImages(){
        JSONArray pictures;
		try {
			pictures = json.getJSONArray(ApplicationData.TAG_DATA);
			for(int i = 0; i < pictures.length(); i++){
				JSONObject c = pictures.getJSONObject(i);
                JSONObject image = c.getJSONObject(ApplicationData.TAG_IMAGES);
                JSONObject regPic = image.getJSONObject(ApplicationData.TAG_HIGHRESOLUTION);
                urlArray.add(regPic.getString("url"));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
        adapter.notifyDataSetChanged();
	}

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        if(absListView.getLastVisiblePosition() == (i3 - 1)){
            progressDialog.dismiss();
            lastLoadPosition = absListView.getLastVisiblePosition();
            setupConnection();
        }
    }

    //Aysnchronous call for the JSON objects
    protected class JSONCall extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Loading pictures ...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONParser parser = new JSONParser();
			json = parser.getJSON(streamUrl);
			return null;
		}

	    public void onPostExecute(Void cha){
	    	displayImages();
            grid.smoothScrollToPosition(lastLoadPosition);
            progressDialog.dismiss();
	    }
	 }
}
