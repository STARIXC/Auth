package com.example.login_signup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private EditText emailTV, passwordTV;
    private Button regBnt;
    private SignInButton signInBtn;
    private ProgressBar progBar;
    private FirebaseAuth mAuth;
    private GoogleApiClient googleApiClient;
    private TextView textView;
    private static final int RC_SIGN_IN =1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        initUI();
        regBnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewUser();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        signInBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent =Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent,RC_SIGN_IN);
            }
        });
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN){
            GoogleSignInResult result =Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()){
            gotoDashboard();

        }else{
            Toast.makeText(getApplicationContext(),"Sign in Canceled",Toast.LENGTH_LONG).show();
        }
    }

    private void gotoDashboard() {
        Intent intent = new Intent(MainActivity.this,Dashboard.class);
        startActivity(intent);
    }

    private void registerNewUser() {
        progBar.setVisibility(View.VISIBLE);

        String email,password;
        email = emailTV.getText().toString();
        password= passwordTV.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(),"Please enter email ...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"Please enter password!",Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Registration successful!",Toast.LENGTH_LONG).show();
                            progBar.setVisibility(View.GONE);

                            Intent intent = new Intent(MainActivity.this,Login.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(getApplicationContext(),"Registration failed, Please try again laster",Toast.LENGTH_LONG).show();
                            progBar.setVisibility(View.GONE);
                        }
                    }
                });




    }
    private void initUI() {
        emailTV= findViewById(R.id.email);
        passwordTV= findViewById(R.id.password);
        regBnt =findViewById(R.id.reg_btn);
        progBar = findViewById(R.id.progressBar);
        signInBtn= findViewById(R.id.google_sign_in_btn);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
