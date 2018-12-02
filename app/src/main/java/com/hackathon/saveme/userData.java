package com.hackathon.saveme;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.FontsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class userData extends AppCompatActivity {
    String filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        Button imagePick = (Button) findViewById(R.id.button);
        Button saveBtn = (Button) findViewById(R.id.button2);
        final EditText userFullname = (EditText) findViewById(R.id.editText);
        final EditText userAge = (EditText) findViewById(R.id.editText2);
        final EditText userWeight = (EditText) findViewById(R.id.editText3);
        imagePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);


            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File path = getFilesDir();
                File file = new File(path, "userdata");
                if (!file.exists()) {
                    try {
                        OutputStream outputStream = new FileOutputStream(file);
                        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                        writer.write(userFullname.getText().toString() + "\n");
                        writer.write(userAge.getText().toString() + "\n");
                        writer.write(userWeight.getText().toString() + "\n");
                        writer.write(filepath + "\n");
                        writer.close();
                        Intent main = new Intent(userData.this, MainActivity.class);
                        startActivity(main);
                        finish();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
}
