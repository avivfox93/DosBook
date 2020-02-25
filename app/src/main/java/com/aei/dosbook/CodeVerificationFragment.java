package com.aei.dosbook;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.fragment.app.Fragment;


public class CodeVerificationFragment extends Fragment {

    private Context cntx;
    private OnFragmentInteractionListener mListener;
    private EditText codeInput;
    private TextView timeText;
    private Button okButton;
    private long time;
    private boolean running;
    public CodeVerificationFragment() {resetTimer();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_code_verification, container, false);
        codeInput = view.findViewById(R.id.verifacation_code_input);
        okButton = view.findViewById(R.id.verifacation_code_ok_button);
        timeText = view.findViewById(R.id.verifacation_code_time);
        okButton.setOnClickListener(e->{
            if(codeInput.getText().toString().length() != 6){
                Toast.makeText(cntx,"Illegal Code!",Toast.LENGTH_SHORT).show();
                return;
            }
            mListener.onStartCodeVerification(codeInput.getText().toString());
        });
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(!running)
                    return;
                timeText.setText(String.format(Locale.ENGLISH,"%d",time));
                if(--time > 0)
                    handler.postDelayed(this,1000);
                else
                    try {
                        mListener.onVerificationTimeOut();
                    }catch (Exception e){e.printStackTrace();}
            }
        });
        return view;
    }

    public void resetTimer(){
        running = true;
        time = VerificationActivity.VERIFICATION_TIMEOUT;
    }

    public void stop(){
        running = false;
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
        void onStartCodeVerification(String code);
        void onVerificationTimeOut();
    }
}
