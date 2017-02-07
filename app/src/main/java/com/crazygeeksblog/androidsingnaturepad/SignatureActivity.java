package com.crazygeeksblog.androidsingnaturepad;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class SignatureActivity extends AppCompatActivity {

    public  static final int STORAGE_PERMISSION_CODE= 1001;
    //Declare private variables
    private SignaturePad objSignaturePad;
    private Button btnSaveSignature, btnClearSignature;
    private String signaturePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        // Initialize private variables
        objSignaturePad = (SignaturePad)findViewById(R.id.objSignaturePad);
        btnSaveSignature = (Button)findViewById(R.id.btnSaveSignature);
        btnClearSignature = (Button)findViewById(R.id.btnClearSignature);

        // If the android version is greater than lollipop
        // ask for run time permission
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requestStoragePermission();
        }

        // At the starting disable the buttons
        enableDisableButtons(false);

        objSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                // code to handle the onStartSigning event
            }

            @Override
            public void onSigned() {
                // code to handle the onSigned event
                enableDisableButtons(true);
            }

            @Override
            public void onClear() {
                // code to handle the onClear event
                enableDisableButtons(false);

            }
        });
    }

    public void OnSaveSignatureClick(View v) {
        //write code for saving the signature as image
        Bitmap bitmapSignature = objSignaturePad.getSignatureBitmap();
        // Create image from bitmap and store it in memory
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapSignature.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
        Random rand = new Random();
        int randomValue = rand.nextInt(9999);
        File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() +
                "/"+String.valueOf(randomValue) + "capturedsignature.jpg");
        try {
            if (file.createNewFile()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();

            signaturePath = file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(SignatureActivity.this, "Signature Saved", Toast.LENGTH_SHORT).show();
    }

    public void OnClearSignatureClick(View v) {
        objSignaturePad.clear();
    }


    public void OnViewSignatureClick(View v) {
        Intent i=new Intent(this,ViewSignatureActivity.class);
        i.putExtra("SignaturePath", signaturePath);
        startActivity(i);
    }

    private void   enableDisableButtons(boolean enableButton)
    {
        btnSaveSignature.setEnabled(enableButton);
        btnClearSignature.setEnabled(enableButton);
    }

    //Requesting permission
    private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == STORAGE_PERMISSION_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Toast.makeText(this,"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(this,"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }

}
