package com.example.zipfile;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;

import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    Button btn_download, btn_unzip, btn_view;
    DownloadManager downloadManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_download = findViewById(R.id.btn_download);
        btn_unzip = findViewById(R.id.btn_unzip);
        btn_view = findViewById(R.id.btn_view);



        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permissions,PERMISSION_STORAGE_CODE);
                    }
                    else{
                        startDownloading();
                    }
                }
                else{

                    startDownloading();
                }


            }
        });

        btn_unzip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String filePath = String.valueOf(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)+"/zip_2MB.zip");
//                Toast.makeText(MainActivity.this,filePath,Toast.LENGTH_LONG).show();


                String zipFile = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS)+"/MyZipFile/zip_2MB.zip"; //your zip file location
                String unzipLocation = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS) + "/MyZipFile/unzippedtestNew/"; // destination folder location
                DecompressFast df= new DecompressFast(zipFile, unzipLocation);
                df.unzip();
                Toast.makeText(MainActivity.this,"unzip completed : "+unzipLocation,Toast.LENGTH_LONG).show();



            }
        });

        btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                startActivity(view);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_STORAGE_CODE:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    startDownloading();
                }
                else{
                    Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void startDownloading(){
        File direct = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS)+"/MyZipFile");

        if (!direct.exists()) {
            direct.mkdirs();
        }
        downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        Uri url = Uri.parse("https:/file-examples.com/wp-content/uploads/2017/02/zip_2MB.zip");
        DownloadManager.Request request = new DownloadManager.Request(url);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir("/Documents/MyZipFile", "zip_2MB.zip");
        request.allowScanningByMediaScanner();
        Long reference =  downloadManager.enqueue(request);
        Toast.makeText(MainActivity.this,"File downloading to "+Environment.getExternalStorageDirectory()+"/Documents/MyZipFile",Toast.LENGTH_LONG).show();
    }

}
