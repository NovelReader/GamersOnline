package com.example.jjnjs.gamersonline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.Console;
import java.util.HashMap;
import java.util.Map;

public class GamersOnline extends AppCompatActivity {

    private JSONObject json;
    private boolean successLogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamers_online);

    }

    public void getStuff(){
        EditText emailIn = (EditText)findViewById(R.id.email);
        TextView passIn = (TextView)findViewById(R.id.pass);
        System.out.println("\n\tUser emaail: " + emailIn.getText().toString());
        System.out.println("\t User pass: " + passIn.getText().toString());
        final String email = emailIn.getText().toString();
        final String password =  passIn.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "Http://18.219.55.178/GamersOnline/Methods/profile.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            //JSONObject jsonObject = new JSONObject(response)
                            json = new JSONObject(response);
                            System.out.println(json.toString(4));
                            Boolean error = json.getBoolean("error");
                            String message = json.getString("Message");
                            if(error.equals(true)){
                                if(message.matches("Logged In")){
                                    String name = json.getString("Name");
                                    String image = json.getString("Image");
                                    String user = json.getString("User");
                                    String currency = json.getString("Currency");
                                }else{
                                    Toast.makeText(GamersOnline.this, message, Toast.LENGTH_LONG).show();
                                }
                            }
                            
                            successLogin = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GamersOnline.this, "Error",  Toast.LENGTH_LONG).show();
                        Toast.makeText(GamersOnline.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Email", email);
                params.put("Password",password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(GamersOnline.this);
        requestQueue.add(stringRequest);
    }

    public void tryLogin(View view) {
        getStuff();
        System.out.println("\tCan login: " + successLogin);
        if(successLogin){
            Intent in = new Intent(this, ProfileView.class);
            try{
                in.putExtra("profileName", json.getString("user_id"));
                startActivity(in);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
