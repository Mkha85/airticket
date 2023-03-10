package com.logarithm.airticket.flightticketbook;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.logarithm.airticket.flightticketbook.ModelClass.Profile.Profile;
import com.logarithm.airticket.flightticketbook.ParametersClass.Credentials;
import com.logarithm.airticket.flightticketbook.RestAPI.APIClient;
import com.logarithm.airticket.flightticketbook.RestAPI.APIInterface;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.logarithm.airticket.flightticketbook.Login.TOKEN_ID;


public class LoginAdmin extends AppCompatActivity {


    public static String  TOKEN_ID_ADMIN=null;
    TextView edt_username,edt_pass,register;
    Button btn_login;
    AlertDialog alertDialog;

    SharedPreferences pref ;
    SharedPreferences.Editor editor ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edt_username=findViewById(R.id.edt_email);
        edt_pass=findViewById(R.id.edt_pass);
        btn_login=findViewById(R.id.btn_book);
        register=findViewById(R.id.registerView);
        Log.i("ACT ","LOGIN ADMIN");

          pref = getApplicationContext().getSharedPreferences("cred", 0); // 0 - for private mode
       editor = pref.edit();


        if(pref.getString("TOKEN_ID_ADMIN", null)!=null)
        {
            TOKEN_ID_ADMIN=pref.getString("TOKEN_ID_ADMIN",null);
            startActivity(new Intent(getApplicationContext(),AdminDashboard.class));
            finish();
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegisterAdmin.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog = new SpotsDialog(LoginAdmin.this);
                alertDialog.setMessage("Logging In... ");
                alertDialog.show();

                if (edt_pass.getText().length() > 0 && edt_username.getText().length() > 0) {

                    //   Credentials credentials=new Credentials(editTextUserId.getText().toString(),editTextPassword.getText().toString());
                    Credentials credentials = new Credentials(edt_username.getText().toString(), edt_pass.getText().toString());
                    final APIInterface apiService = APIClient.getClient().create(APIInterface.class);
                    Call<com.logarithm.airticket.flightticketbook.ModelClass.Login> call2 = apiService.loginAdmin(credentials);
                    call2.enqueue(new Callback<com.logarithm.airticket.flightticketbook.ModelClass.Login>() {
                        @Override
                        public void onResponse(Call<com.logarithm.airticket.flightticketbook.ModelClass.Login> call, Response<com.logarithm.airticket.flightticketbook.ModelClass.Login> response) {
                            try {
                                alertDialog.dismiss();
                                if (response.body().getSuccess()) {
                                    TOKEN_ID_ADMIN=response.body().getToken();
                                    editor.putString("TOKEN_ID_ADMIN",TOKEN_ID_ADMIN);
                                    editor.commit();
                                    alertDialog = new SpotsDialog(LoginAdmin.this);
                                    alertDialog.setMessage("Getting Profile... ");
                                    alertDialog.show();

                                    final APIInterface apiService1 = APIClient.getClient().create(APIInterface.class);
                                    Call<Profile> call3 = apiService1.getProfile(TOKEN_ID_ADMIN);
                                    call3.enqueue(new Callback<Profile>() {
                                        @Override
                                        public void onResponse(Call<Profile> call, Response<Profile> response) {
                                            try {

                                                alertDialog.dismiss();
                                                Login.EMAIL=response.body().getEmail();


                                                startActivity(new Intent(getApplicationContext(),AdminDashboard.class));
                                                finish();

                                                //   alertDialog.dismiss();

                                            } catch (Exception e) {
                                                alertDialog.dismiss();
                                                Toast.makeText(LoginAdmin.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();
//                                            alertDialog.dismiss();

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Profile> call, Throwable t) {
                                            Toast.makeText(LoginAdmin.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                                            alertDialog.dismiss();

                                        }
                                    });
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginAdmin.this,"Invalid Credentials !", Toast.LENGTH_SHORT).show();
                                }
                                //   alertDialog.dismiss();

                            } catch (Exception e) {
                                Toast.makeText(LoginAdmin.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
//                            alertDialog.dismiss();

                            }
                        }

                        @Override
                        public void onFailure(Call<com.logarithm.airticket.flightticketbook.ModelClass.Login> call, Throwable t) {
                            Toast.makeText(LoginAdmin.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                            alertDialog.dismiss();

                        }
                    });
                } else {
                    alertDialog.dismiss();
                    Toast.makeText(LoginAdmin.this, "Fields cannot be blank !", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
