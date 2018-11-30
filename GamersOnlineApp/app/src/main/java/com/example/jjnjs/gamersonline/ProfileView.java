package com.example.jjnjs.gamersonline;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
    JSONObject profileJSON;

    private String profileName = "";
    private String profileId = "";
    private String profileImageRaw = "";
    private String profileCurrency = "";

    private TextView userNameTextView;
    private TextView userCurrencyTextView;
    private ImageView userProfileImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        userNameTextView = findViewById(R.id.userName);
        userNameTextView.setText(getIntent().getStringExtra("profileName"));
        userNameTextView = null;
        if(getIntent().hasExtra("profileImage")){
            userProfileImageView = findViewById(R.id.profileImage);
            Bitmap b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("profileImage"),0,
                    getIntent().getByteArrayExtra("profileImage").length
            );
            userProfileImageView.setImageBitmap(b);
            userProfileImageView = null;
        }
        if(getIntent().hasExtra("playerMoney")){
            userCurrencyTextView = findViewById(R.id.currencyTextView);
            userCurrencyTextView.setText(
                    "Currency: " + getIntent().getStringExtra("playerMoney") + " shekel");
        }
        fetchServerImage();
    }

    public void fetchServerImage(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://18.219.55.178/GamersOnline/Methods/server.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Toast.makeText(GamersOnline.this, "Made IT", Toast.LENGTH_LONG).show();
                            JSONObject json = new JSONObject(response);
                            Boolean hasError = json.getBoolean("error");
                            String message = json.getString("message");
                            if(hasError){
                                Toast.makeText(ProfileView.this, message, Toast.LENGTH_LONG).show();
                            } else {
                                if(json.has("Image")){
                                    byte[] decode = Base64.decode(json.getString("Image"), Base64.DEFAULT);
                                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                                    BitmapDrawable bd = new BitmapDrawable(imageBitmap);
                                    findViewById(R.id.MainLayout).setBackgroundDrawable(bd);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("\n\t---\tError response.\n");
                        System.out.println("\n\t---\t" + error.getMessage() + "\n");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("User", getIntent().getStringExtra("playerId"));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ProfileView.this);
        requestQueue.add(stringRequest);
    }

    public void showStats(View view) {

    }
}
