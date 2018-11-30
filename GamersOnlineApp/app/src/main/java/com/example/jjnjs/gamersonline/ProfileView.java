package com.example.jjnjs.gamersonline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileView extends AppCompatActivity{
    public static ArrayList<Item> inventory = new ArrayList<Item>();

    private TextView userNameTextView;
    private TextView userCurrencyTextView;
    private ImageView userProfileImageView;
    private TextView personalWealthTextView;
    private TextView serverWealthTextView;
    private ListView inventoryListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        fetchServerImage();


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

        fetchInventory();
    }

    public void fetchServerImage(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://18.219.55.178/GamersOnline/Methods/server.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Toast.makeText(ProfileView.this, "Made IT", Toast.LENGTH_LONG).show();
                            JSONObject json = new JSONObject(response);
                            if(json.has("image")){
                                byte[] decode = Base64.decode(json.getString("image"), Base64.DEFAULT);
                                Bitmap imageBitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                                BitmapDrawable bd = new BitmapDrawable(imageBitmap);
                                findViewById(R.id.MainLayout).setBackgroundDrawable(bd);
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

    public void fetchInventory(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://18.219.55.178/GamersOnline/Methods/items.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Toast.makeText(GamersOnline.this, "Made IT", Toast.LENGTH_LONG).show();
                            JSONObject json = new JSONObject(response);
                            if(json.has("name")){
                                JSONArray names = json.getJSONArray("name");
                                JSONArray numbers = json.getJSONArray("number");
                                JSONArray costs = json.getJSONArray("cost");
                                JSONArray descriptions = json.getJSONArray("description");
                                JSONArray images = json.getJSONArray("image");

                                for(int i = 0; i < names.length(); i++){

                                    Item newItem = new Item(names.getString(i), numbers.getInt(i),
                                            costs.getInt(i), descriptions.getString(i),
                                            images.getString(i));
                                    System.out.println(String.format("\t---Item Name: %s", newItem.name));
                                    System.out.println(String.format("\t---Orig Name: %s", names.getString(i)));
                                    inventory.add(newItem);
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

    private void updateInventoryListView(){

    }

    public void showStats(View view) {

    }
}

class Item{
    public String name;
    public int number;
    public int cost;
    public String description;
    public Bitmap image;

    public Item(String name, int number, int cost, String description, String imageText) {
        this.name = name;
        this.number = number;
        this.cost = cost;
        this.description = description;
        byte[] decode = Base64.decode(imageText, Base64.DEFAULT);
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        decode = null;
        this.image = imageBitmap;
    }

    @Override
    public String toString(){
        return String.format("Item: %s\tAmount: %d", name, number);
    }
}