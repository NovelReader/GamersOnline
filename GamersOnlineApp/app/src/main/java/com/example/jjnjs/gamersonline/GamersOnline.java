package com.example.jjnjs.gamersonline;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
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

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.util.HashMap;
import java.util.Map;

public class GamersOnline extends AppCompatActivity {

    private boolean buttonPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamers_online);

        //Code for debugging only
        EditText emailIn = (EditText)findViewById(R.id.email);
        TextView passIn = (TextView)findViewById(R.id.pass);
        emailIn.setText("user1@user.com");
        passIn.setText("user1");
        //end Code for debugging
    }

    public void getStuff(){
        System.out.println("\n---\tCalled getStuff()\n");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://18.219.55.178/GamersOnline/Methods/profile.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //Toast.makeText(GamersOnline.this, "Made IT", Toast.LENGTH_LONG).show();
                            JSONObject json = new JSONObject(response);
                            System.out.println(json.toString(4));
                            Boolean hasError = json.getBoolean("error");
                            String message = json.getString("message");
                            if(!hasError){
                                if(message.matches("Logged In.")) {
                                    login(json.getString("User"), json.getString("Name"),
                                            json.getString("Image") ,json.getString("Currency"));
                                }
                                else{
                                    Toast.makeText(GamersOnline.this, message, Toast.LENGTH_LONG).show();
                                }
                                json = null;
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
            protected Map<String, String> getParams() throws AuthFailureError {
                EditText emailIn = (EditText)findViewById(R.id.email);
                TextView passIn = (TextView)findViewById(R.id.pass);
                Map<String, String> params = new HashMap<>();
                System.out.println("\tUser emaail: " + emailIn.getText().toString());
                System.out.println("\t User pass: " + passIn.getText().toString());
                params.put("Email", emailIn.getText().toString());
                params.put("Password", passIn.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(GamersOnline.this);
        requestQueue.add(stringRequest);
        buttonPressed = false;
    }

    //Function tied to the sign in button
    public void tryLogin(View view) {
        getStuff();
    }

    public void login(String userID,String userName,String imageInText,String userCurrency){
        if(buttonPressed) return;
        buttonPressed = true;
        Intent in = new Intent(this, ProfileView.class);
        byte[] decode = Base64.decode(imageInText, Base64.DEFAULT);
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
        in.putExtra("playerId", userID)
                .putExtra("profileName", userName)
                .putExtra("playerMoney", userCurrency)
                .putExtra("profileImage", bs.toByteArray());

        startActivity(in);
    }
}
