package com.hackathon.saveme;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {

    ArrayList<String> userDataCollected = new ArrayList<String>();
    ImageView userProfileImage;

    private void readUserData(Context context) {
        File path = context.getFilesDir();
        File file = new File(path, "userdata");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                userDataCollected.add(line);

            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        TextView userFullName = (TextView) view.findViewById(R.id.textView7);
        TextView allergies = (TextView) view.findViewById(R.id.textView11);


        readUserData(getActivity());
        userFullName.setText(userDataCollected.get(0));
        allergies.setText(userDataCollected.get(2));

        userProfileImage = (ImageView) view.findViewById(R.id.imageView2);

        new GetImageFromServers(userProfileImage).execute(userDataCollected.get(3));

        // Inflate the layout for this fragment
        return view;
    }

    private class GetImageFromServers extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public GetImageFromServers(ImageView imageView) {
            this.imageView = imageView;

        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String imageURL = strings[0];
            Bitmap b = null;
            try {
                InputStream in = new URL(imageURL).openStream();
                b = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return b;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }

}

