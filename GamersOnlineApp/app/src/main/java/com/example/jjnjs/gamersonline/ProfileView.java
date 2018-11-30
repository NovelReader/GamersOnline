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

    private TextView currentHealth;
    private TextView currentMP;
    private TextView currentStrength;
    private TextView currentSpeed;
    private TextView currentIntelligence;
    private TextView currentWisdom;

    private TextView maxHealth;
    private TextView maxMP;
    private TextView maxStrength;
    private TextView maxSpeed;
    private TextView maxIntelligence;
    private TextView maxWisdom;





    private ListView inventoryListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        fetchServerImage();


        userNameTextView = findViewById(R.id.userName);
        userNameTextView.setText(getIntent().getStringExtra("profileName"));
        userNameTextView = null;

        personalWealthTextView = (TextView) findViewById(R.id.personalWealth);
        serverWealthTextView = (TextView) findViewById(R.id.serverWealth);

        currentHealth = (TextView) findViewById(R.id.currentHealth);
        currentMP = (TextView) findViewById(R.id.currentMp);
        currentStrength = (TextView) findViewById(R.id.currentStrength);
        currentSpeed = (TextView) findViewById(R.id.currentSpeed);
        currentIntelligence = (TextView) findViewById(R.id.currentIntelligence);
        currentWisdom = (TextView) findViewById(R.id.currentWisdom);

        maxHealth = (TextView) findViewById(R.id.maxHealth);
        maxMP = (TextView) findViewById(R.id.maxMp);
        maxStrength = (TextView) findViewById(R.id.maxStrength);
        maxSpeed = (TextView) findViewById(R.id.maxSpeed);
        maxIntelligence = (TextView) findViewById(R.id.maxIntelligence);
        maxWisdom = (TextView) findViewById(R.id.maxWisdom);


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
        getPersonalWealth();
        getServerWealth();
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
                                JSONArray ids = json.getJSONArray("id");
                                JSONArray names = json.getJSONArray("name");
                                JSONArray numbers = json.getJSONArray("number");
                                JSONArray costs = json.getJSONArray("cost");
                                JSONArray descriptions = json.getJSONArray("description");
                                JSONArray images = json.getJSONArray("image");

                                for(int i = 0; i < names.length(); i++){

                                    Item newItem = new Item(ids.getInt(i),names.getString(i), numbers.getInt(i),
                                            costs.getInt(i), descriptions.getString(i),
                                            images.getString(i));
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

    public void UseItem(View view){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://18.219.55.178/GamersOnline/Methods/items.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                            View v = findViewById(R.id.currencyTextView);
                            showStats(v);
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
                params.put("ItemId", getIntent().getStringExtra("itemId"));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ProfileView.this);
        requestQueue.add(stringRequest);
    }

    public void showStats(View view) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://18.219.55.178/GamersOnline/Methods/getstats.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Toast.makeText(ProfileView.this, "Made IT", Toast.LENGTH_LONG).show();
                            JSONObject json = new JSONObject(response);
                            if(json.has("current_health")){
                                currentHealth.setText("Current Health: " + json.getString("current_health"));
                                maxHealth.setText("Max Health: " + json.getString("max_health"));
                                currentMP.setText("Current MP: "+json.getString("current_mp"));
                                maxMP.setText("Max MP: "+json.getString("max_mp"));
                                currentStrength.setText("Current Strength: " + json.getString("current_strength"));
                                maxStrength.setText("Max Strength: " + json.getString("max_strength"));
                                currentSpeed.setText("Current Speed: " + json.getString("current_speed"));
                                maxSpeed.setText("Max Speed: " + json.getString("max_speed"));
                                currentIntelligence.setText("Current Intelligence: " + json.getString("current_intelligence"));
                                maxIntelligence.setText("Max Intelligence: " + json.getString("max_intelligence"));
                                currentWisdom.setText("Current Wisdom: " + json.getString("current_wisdom"));
                                maxWisdom.setText("Max Wisdom: " + json.getString("max_wisdom"));
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

    public void getPersonalWealth(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://18.219.55.178/GamersOnline/Methods/selfwealth.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Toast.makeText(ProfileView.this, "Made IT", Toast.LENGTH_LONG).show();
                            JSONObject json = new JSONObject(response);
                            if(json.has("Wealth")){
                                personalWealthTextView.append(" " + json.getString("Wealth"));
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

    public void getServerWealth(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://18.219.55.178/GamersOnline/Methods/allwealth.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Toast.makeText(GamersOnline.this, "Made IT", Toast.LENGTH_LONG).show();
                            JSONObject json = new JSONObject(response);
                            if(json.has("Wealth")){
                                serverWealthTextView.append(" " + json.getString("Wealth"));
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
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ProfileView.this);
        requestQueue.add(stringRequest);
    }
}

class Item{
    public int id;
    public String name;
    public int number;
    public int cost;
    public String description;
    public Bitmap image;

    public Item(int id ,String name, int number, int cost, String description, String imageText) {
        this.id = id;
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
        return String.format("Item: %sn\n\tAmount: %d\n\tDescription: %s", name, number, description);
    }
}