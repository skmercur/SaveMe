package com.hackathon.saveme;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class settingFragments extends Fragment {
    TextView GpsModeText;
    TextView AccModeText;

    public settingFragments() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting_fragments, container, false);
        final SeekBar GpsMode = (SeekBar) view.findViewById(R.id.seekBar);
        SeekBar AccMode = (SeekBar) view.findViewById(R.id.seekBar2);
        GpsModeText = (TextView) view.findViewById(R.id.gpsModeText);
        AccModeText = (TextView) view.findViewById(R.id.AccModeText);
        GpsMode.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0: {
                        GpsModeText.setText("Eco");
                        break;
                    }
                    case 1: {
                        GpsModeText.setText("Balanced");
                        break;
                    }
                    case 2: {
                        GpsModeText.setText("High precision");
                        break;
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        AccMode.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0: {
                        AccModeText.setText("Eco");
                        break;
                    }
                    case 1: {
                        AccModeText.setText("Balanced");
                        break;
                    }
                    case 2: {
                        AccModeText.setText("High precision");
                        break;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return view;
    }

}
