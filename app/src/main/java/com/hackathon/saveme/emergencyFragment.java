package com.hackathon.saveme;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class emergencyFragment extends Fragment {

    FloatingActionButton emergencyButton;

    public emergencyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_emergency, container, false);
        emergencyButton = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        emergencyButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "Long pressed", Toast.LENGTH_LONG).show();
                Intent emergencyActiviyIntent = new Intent(view.getContext(), emergencyActivity.class);
                startActivity(emergencyActiviyIntent);
                return true;
            }
        });
        return view;
    }

}
