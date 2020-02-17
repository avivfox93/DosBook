package com.aei.dosbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.aei.dosbook.Entities.MyUserProfile;
import com.aei.dosbook.Entities.UserProfile;
import com.aei.dosbook.Utils.MyApp;
import com.aei.dosbook.Utils.Verification;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity implements
        PhoneVerificationFragment.OnFragmentInteractionListener,
        CodeVerificationFragment.OnFragmentInteractionListener,
        Executor{

    private static final int REQUEST_CODE_ALL_PERMISSIONS = 15;

    private PhoneVerificationFragment phoneVerificationFragment;
    private CodeVerificationFragment codeVerificationFragment;
    private String verificationId = "";
    private FirebaseAuth mAuth;
    private Dialog loadingDialog;

    void setVerificationId(String verificationId){
        this.verificationId = verificationId;
    }

    public void getPermissions(){
        // Check whether this app has write external storage permission or not.
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int writeLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        ArrayList<String> permissions = new ArrayList<>();
        if(writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(writeLocationPermission != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissions.size() > 0)
            ActivityCompat.requestPermissions(this,
                    permissions.toArray(new String[0]),REQUEST_CODE_ALL_PERMISSIONS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        phoneVerificationFragment = new PhoneVerificationFragment();
        codeVerificationFragment = new CodeVerificationFragment();
        showFragment(phoneVerificationFragment);
        getPermissions();
        Verification.loadToken();
        String token = Verification.getToken();
        mAuth = FirebaseAuth.getInstance();
        loadingDialog = MyApp.getLoadingView(this);
        if(!token.isEmpty())
            onTokenReady(token);
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.verifacation_fragment, fragment);
        transaction.commit();
    }

    private void startMainActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startRegisterActivity(){
        Intent intent = new Intent(this,RegistrationActivity.class);
        intent.putExtra("phoneNumber",phoneVerificationFragment.getPhoneNumber());
        startActivity(intent);
        finish();
    }

    @Override
    public void onStartVerification(String phoneNumber){
        final VerificationActivity a = this;
        loadingDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                (Executor) this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        onVerComplete(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        loadingDialog.dismiss();
                        e.printStackTrace();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken code){
                        loadingDialog.dismiss();
                        showFragment(codeVerificationFragment);
                        a.setVerificationId(verificationId);
                    }
                });
    }


    void onVerComplete(PhoneAuthCredential credential){
        signInWithPhoneAuthCredential(credential);
    }

    @Override
    public void onStartCodeVerification(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        onVerComplete(credential);
    }

    @Override
    public void execute(Runnable command) {
        this.runOnUiThread(command);
    }

    private void onTokenReady(String token){
        loadingDialog.show();
        Verification.verifyToken(token,(err, result) -> {
            loadingDialog.dismiss();
            if(err) {
                Log.e("Verification","error!!! " + result.toString());
                return;
            }
            Log.e("JSON",result.toString());
            try {
                MyUserProfile myUserProfile = new Gson().fromJson(result.getString("user"),MyUserProfile.class);
                Verification.setToken(myUserProfile.getUuid());
                Verification.saveToken();
                MyApp.setMyUserProfile(myUserProfile);
            }catch (JSONException e){
                e.printStackTrace();
            }
            if(result.optBoolean("new_user")){
                Toast.makeText(this,"New User",Toast.LENGTH_SHORT).show();
                startRegisterActivity();
            }else
                startMainActivity();
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("FIREBASE", "signInWithCredential:success");

                        FirebaseUser user = task.getResult().getUser();
                        user.getIdToken(true).addOnSuccessListener(getTokenResult -> {
                            String token = getTokenResult.getToken();
                            Verification.setToken(token);
                            Verification.saveToken();
                            onTokenReady(token);
                        });
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w("FIREBASE", "signInWithCredential:failure", task.getException());
                        loadingDialog.dismiss();
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                });
    }
}
