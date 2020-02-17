package com.aei.dosbook;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aei.dosbook.Utils.MyApp;
import com.aei.dosbook.Utils.Verification;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PhoneVerificationFragment extends Fragment {

    private static final String PHONE_NUMBER_REGEX = "05[0-9]{8}";

    private Context cntx;
    private OnFragmentInteractionListener mListener;
    private EditText phoneInput;

    private String phoneNumber;
    private Button okButton;
    public PhoneVerificationFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_phone_verification, container, false);
        Pattern r = Pattern.compile(PHONE_NUMBER_REGEX);
        phoneInput = view.findViewById(R.id.verifacation_phone_input);
        okButton = view.findViewById(R.id.verifacation_ok_button);
        phoneInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Matcher matcher = r.matcher(phoneInput.getText());
                okButton.setEnabled(matcher.find());
            }
        });
        okButton.setOnClickListener(e->{
//            Matcher matcher = r.matcher(phoneInput.getText());
//            if(!matcher.find()) {
//                Toast.makeText(cntx,"Illegal Phone Number!",Toast.LENGTH_SHORT).show();
//                return;
//            }
            phoneNumber = "+972" + phoneInput.getText().toString();
            MyApp.getPrefs().putString("phone",phoneNumber);
            mListener.onStartVerification(phoneNumber);
        });

        return view;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        cntx = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onStartVerification(String phoneNumber);
    }
}
