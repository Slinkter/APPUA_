package com.cudpast.appminas.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Model.User;
import com.cudpast.appminas.Principal.MainActivity;
import com.cudpast.appminas.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;
    private TextInputLayout log_email_layout, log_password_layout;
    private TextInputEditText log_email, log_password;
    private ProgressDialog mDialog;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        //
        mAuth = FirebaseAuth.getInstance();
        log_email_layout = findViewById(R.id.log_email_layout);
        log_password_layout = findViewById(R.id.log_password_layout);
        log_email = findViewById(R.id.log_email);
        log_password = findViewById(R.id.log_password);


        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        //
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initLogin();
            }
        });
        //
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRegistro();
            }
        });
        //


    }

    private void initLogin() {

        if (submitForm()) {
            mDialog = new ProgressDialog(LoginActivity.this);
            mDialog.setMessage("Ingresando ...");
            mDialog.show();
            //
            String email = log_email.getText().toString();
            String password = log_password.getText().toString();
            //
            mAuth
                    .signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            String user_uid = authResult.getUser().getUid();
                            DatabaseReference ref_db_user = database.getReference(Common.db_user);
                            ref_db_user
                                    .child(user_uid)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            User mUser = dataSnapshot.getValue(User.class);
                                            if (mUser == null) {
                                                Log.e(TAG, "usuario no registrador");
                                                mDialog.dismiss();
                                            } else {
                                                Log.e(TAG, "user_uid" + mUser.getUid());
                                                Log.e(TAG, "currentUser : " + Common.currentUser.getReg_name());
                                                Intent intent_login = new Intent(LoginActivity.this, MainActivity.class);
                                                intent_login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent_login);
                                                finish();
                                                mDialog.dismiss();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.e(TAG, "databaseError : " + databaseError.getMessage());
                                            mDialog.dismiss();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            if (e.getMessage().equalsIgnoreCase("The email address is badly formatted")) {
                                Toast.makeText(LoginActivity.this, "Formato invalido", Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            } else if (e.getMessage().equalsIgnoreCase("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                Toast.makeText(LoginActivity.this, "usuario no registrador", Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            } else {
                                Toast.makeText(LoginActivity.this, "Usuario no existe", Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }


                        }
                    });
        }
    }

    private void initRegistro() {
        Intent internt_register = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(internt_register);
        finish();
    }

    //Validacion
    private boolean checkEmail() {
        if (log_email.getText().toString().trim().isEmpty()) {
            log_email_layout.setError("Ingrese su correo");
            return false;
        } else {
            log_email_layout.setError(null);
        }
        return true;
    }

    private boolean checkPassword() {
        if (log_password.getText().toString().trim().isEmpty()) {
            log_password_layout.setError("Ingrese su contraseña");
            return false;
        } else {
            log_password_layout.setError(null);
        }
        return true;
    }

    private boolean submitForm() {

        if (!checkEmail()) {
            return false;
        }

        if (!checkPassword()) {
            return false;
        }
        return true;
    }


}
