package com.example.jjnjs.gamersonline;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileView extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
    }

    public void getImage(){
        Bitmap imageBitmap;
        /*
        byte[] decode = Base64.decode(jsonObject.getString("Image String Name"),0);
        imagebitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        ImageName.setImageDrawable(imagebitmap);
        */

        //return imageBitmap;
    }

    public void showStats(View view) {
    }
}
