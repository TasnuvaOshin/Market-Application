package com.joytechnologies.market;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.joytechnologies.market.Market.Market_Home_Fragment;

public class MainActivity extends AppCompatActivity {

    private Market_Home_Fragment market_home_fragment;
    private EditText editText;
    private String SearchText;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        market_home_fragment = new Market_Home_Fragment();
        /*
        if permission granted then we will go the map activity
         */

        SetFragment(market_home_fragment);

        //edit text for the search

        editText = findViewById(R.id.et_search);
        imageButton = findViewById(R.id.ib_search);
        //this is for the button now if the user press the button
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Run The Search TAsk
                RunSearchTask();
            }
        });


        //this is for the keyboard

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                    RunSearchTask();
                    return true;
                } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                    RunSearchTask();
                    return true;


                }
                return false;
            }
        });
    }

    private void RunSearchTask() {
        SearchText = editText.getText().toString();

        if (!SearchText.isEmpty()) {
            Toast.makeText(this, "we will going to do some action", Toast.LENGTH_SHORT).show();
            //now we will call our API to get the data
        } else {

            Toast.makeText(this, "Please Enter Some Keyword Ex: dress", Toast.LENGTH_SHORT).show();
        }

    }


    private void SetFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack("my_fragment").commit();

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }


}
