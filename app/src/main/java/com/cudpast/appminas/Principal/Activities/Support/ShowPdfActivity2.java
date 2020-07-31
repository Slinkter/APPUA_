package com.cudpast.appminas.Principal.Activities.Support;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.cudpast.appminas.Common.Common;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.cudpast.appminas.R;

import java.io.File;

public class ShowPdfActivity2 extends AppCompatActivity {

    private PDFView pdfView2;
    private File file;
    public static final String folderpdf = "/arsi21.pdf";
    public static final String TAG = ShowPdfActivity2.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pdf2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });


        pdfView2 = findViewById(R.id.pdfView2);
        init();


    }

    private void init() {
        try {
            file = new File(Environment.getExternalStorageDirectory(), folderpdf);
            Log.e(TAG, "file archivo  " + file.toString());
            pdfView2.fromFile(file)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .enableAntialiasing(true)
                    .load();


            //    sendEmail2();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendEmail() {
        Log.e(TAG, "sendEmail()  2 ");
        File root = Environment.getExternalStorageDirectory();
        String filelocation = root.getAbsolutePath() + folderpdf;
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        String message = "Documento Generado por " + Common.currentUser.getReg_name();
        intent.putExtra(Intent.EXTRA_SUBJECT, "Unidades ARSI : " + Common.unidadTrabajoSelected.getAliasUT() + "\n Saludos");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + filelocation));
        intent.putExtra(Intent.EXTRA_TEXT, message);
        String currentusermail = Common.currentUser.getReg_email();
        Log.e(TAG, "currentusermail  : " + currentusermail);
        intent.setData(Uri.parse("mailto:" + currentusermail));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.e(TAG, "sendEmail 2  -->  filelocation " + filelocation);
        startActivity(intent);
    }
}