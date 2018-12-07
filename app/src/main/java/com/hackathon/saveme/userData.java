package com.hackathon.saveme;

import android.app.Activity;
import android.app.Person;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.FontsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

public class userData extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String filepath;
    LoginButton loginButton;
    CallbackManager callbackManager;
    LoginManager loginManager;
    String message,message1;
    String desease, Allergy;
    private String name,email,img;
    private  String android_id;
    private static final String EMAIL = "email";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),Settings.Secure.ANDROID_ID);
        Spinner dropdown = (Spinner) findViewById(R.id.spinner1);
        Spinner dropdown2 = (Spinner) findViewById(R.id.spinner2);
        String[] items = new String[]{"Diabete", "Tension", "Vertige"};
        String[] items2 = new String[]{"Pollen", "Lactose", "Strawberries"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);
        dropdown2.setAdapter(adapter2);
        dropdown2.setOnItemSelectedListener(this);

        final EditText userWeight = (EditText) findViewById(R.id.editText3);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_btn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, 235);
            }
        });

        loginButton = (LoginButton) findViewById(R.id.fbButton);
        loginButton.setReadPermissions("email", "public_profile");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String userid = loginResult.getAccessToken().getUserId();
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            displayUserInfo(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "first_name, last_name, email, id");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        LocalBroadcastManager.getInstance(this).registerReceiver(positionReciver, new IntentFilter("position"));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //google sign-In

        if (requestCode == 235) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount acct = result.getSignInAccount();

            File path = getFilesDir();
            File file = new File(path, "userdata");
            if (!file.exists()) {
                try {
                    OutputStream outputStream = new FileOutputStream(file);
                    OutputStreamWriter writer = new OutputStreamWriter(outputStream);
name = acct.getGivenName() + " " + acct.getFamilyName();
email = acct.getEmail();
img = acct.getPhotoUrl().toString();
                    writer.write(acct.getGivenName() + " " + acct.getFamilyName() + "\n");
                    writer.write(acct.getEmail() + "\n");
                    writer.write(Allergy + "\n");
                    writer.write(acct.getPhotoUrl().toString() + "\n");
                    writer.close();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("name", name);
                                jsonObject.put("email", email);
                                jsonObject.put("allergy", Allergy);
                                jsonObject.put("lat", "0");
                                jsonObject.put("lon","0");
                                jsonObject.put("id",android_id);
                                jsonObject.put("image", new String(Base64.encode(img.getBytes(), Base64.DEFAULT)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String dataF = jsonObject.toString();

                            byte[] encodedJson = Base64.encode(dataF.getBytes(), Base64.DEFAULT);
                            String url = null;
                            try {
                                url = "http://192.168.1.61/newuser?json=" + URLEncoder.encode(dataF, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            Log.d("sending to ", url);
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("Response : ", response);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("Error", error.getMessage());
                                }
                            });

                            queue.add(stringRequest);

                        }

                    }).start();

                    Intent main = new Intent(userData.this, MainActivity.class);
                    startActivity(main);
                    finish();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Intent main = new Intent(userData.this, MainActivity.class);
                startActivity(main);
                finish();
            }

        }

        //end


        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (requestCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                filepath = getPath(selectedImage);
                //Continue doing stuff save and send

            }
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);

    }

    public void displayUserInfo(JSONObject object) throws JSONException {
        String first, last, email;
        first = object.getString("first_name");
        email = object.getString("email");
        Log.d("user", email);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Allergy = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private BroadcastReceiver positionReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            message = intent.getStringExtra("lon");
            message1 = intent.getStringExtra("lat");
            Log.d("Lat", message);
            Log.d("lon", message1);



        }
    };
}
