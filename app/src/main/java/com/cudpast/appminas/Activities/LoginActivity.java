package com.cudpast.appminas.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cudpast.appminas.Common.Common;
import com.cudpast.appminas.Model.User;
import com.cudpast.appminas.Principal.UnidadesActivity;
import com.cudpast.appminas.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, TextWatcher {


    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;
    private TextInputLayout log_email_layout, log_password_layout;
    private TextInputEditText log_email, log_password;
    private ProgressDialog mDialog;
    private FirebaseDatabase database;
    public static final String TAG = LoginActivity.class.getSimpleName();

    //Check
    private CheckBox checkbox;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static final String PREF_NAME = "prefs";
    public static final String KEY_REMEMBER = "remeber";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        //
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        //
        log_email_layout = findViewById(R.id.log_email_layout);
        log_password_layout = findViewById(R.id.log_password_layout);
        log_email = findViewById(R.id.log_email);
        log_password = findViewById(R.id.log_password);
        //
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        //
        btnLogin.setOnClickListener(v -> initLogin());
        //
        btnRegister.setOnClickListener(v -> initRegistro());
        // CheckBox
        checkbox_user();

    }

    private void checkbox_user() {
        checkbox = (CheckBox) findViewById(R.id.checkbox_rem);
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean(KEY_REMEMBER, false)) {
            checkbox.setChecked(true);
        } else {
            checkbox.setChecked(false);
        }
        //
        log_email.setText(sharedPreferences.getString(KEY_USERNAME, ""));
        log_password.setText(sharedPreferences.getString(KEY_PASSWORD, ""));

        log_email.addTextChangedListener(this);
        log_password.addTextChangedListener(this);

        checkbox.setOnCheckedChangeListener(this);
    }


    //CheckBox
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        checkChange();
    }

    //CheckBox
    private void checkChange() {
        if (checkbox.isChecked()) {
            editor.putBoolean(KEY_REMEMBER, true);
            editor.putString(KEY_USERNAME, log_email.getText().toString().trim());
            editor.putString(KEY_PASSWORD, log_password.getText().toString().trim());
            editor.apply();
        } else {
            editor.putBoolean(KEY_REMEMBER, true);
            editor.remove(KEY_USERNAME);
            editor.remove(KEY_PASSWORD);
            editor.apply();
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        checkChange();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Common.currentUser != null) {
            updateUI(Common.currentUser);
        }
    }

    // End CheckBox
    // Login
    private void initLogin() {
        if (submitForm()) {
            mDialog = new ProgressDialog(LoginActivity.this);
            mDialog.setMessage("Autenticado ...");
            mDialog.show();
            //
            String email = log_email.getText().toString();
            String password = log_password.getText().toString();
            //
            mAuth
                    .signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        //--->
                        String user_uid = authResult.getUser().getUid();
                        DatabaseReference ref_db_user = database.getReference(Common.db_user);
                        ref_db_user
                                .child(user_uid)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // --> Obtener datos del usuario y settear
                                        User mUser = dataSnapshot.getValue(User.class);
                                        //
                                        if (mUser == null) {
                                            updateUI(null);
                                        } else {
                                            Common.currentUser = mUser;
                                            updateUI(Common.currentUser);
                                        }
                                        mDialog.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        updateUI(null);
                                        mDialog.dismiss();
                                        Log.e(TAG, "databaseError : " + databaseError.getMessage());
                                    }
                                });
                        //<---
                    })
                    .addOnFailureListener(e -> {
                        //--->
                        if (e.getMessage().equalsIgnoreCase("The email address is badly formatted")) {
                            Toast.makeText(LoginActivity.this, "Formato invalido", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        } else if (e.getMessage().equalsIgnoreCase("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                            Toast.makeText(LoginActivity.this, "usuario no registrador", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        } else {
                            Toast.makeText(LoginActivity.this, "Usuario no existe ", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                        //<---
                    });
        }
    }

    // End Login
    private void initRegistro() {
        Intent internt_register = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(internt_register);
        finish();
    }

    private void updateUI(User user) {

        if (user == null) {
            Toast.makeText(this, "usuario no registrador", Toast.LENGTH_SHORT).show();

        } else {
            Intent intent_login = new Intent(LoginActivity.this, UnidadesActivity.class);
            startActivity(intent_login);

            Toast.makeText(this, "Bievenid@\n" + user.getReg_name(), Toast.LENGTH_SHORT).show();
            if (mAuth.getCurrentUser().isEmailVerified()) {
                Log.e(TAG, "correo verificado ");
            } else {
                Log.e(TAG, "no verificado ");
            }
        }
    }

    //Validacion
    private boolean checkEmail() {
        if (log_email.getText().toString().trim().isEmpty()) {
            log_email_layout.setError("Debes ingresar tu correo");
            log_email_layout.requestFocus();
            return false;
        } else {
            log_email_layout.setError(null);
        }
        return true;
    }

    private boolean checkPassword() {
        if (log_password.getText().toString().trim().isEmpty()) {
            log_password_layout.setError("Debes ingresar tu contraseña");
            log_password_layout.requestFocus();
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

    public void btn_recovery_password(View view) {
        displayRecoveryPassowrd();
    }

    // *********************************************************
    //. Recovery Username & Password

    private void displayRecoveryPassowrd() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setTitle("Recuperar Contraseña");
        alertDialog.setMessage("Escriba su correo");

        LayoutInflater inflater = LayoutInflater.from(LoginActivity.this);
        View forgot_pwd_layout = inflater.inflate(R.layout.layout_forgot_pwd, null);

        TextInputEditText editEmail = forgot_pwd_layout.findViewById(R.id.edtEmailForgot);
        alertDialog.setView(forgot_pwd_layout);

        alertDialog
                .setPositiveButton("ENVIAR", (dialogInterface, i) ->
                        mAuth
                                .sendPasswordResetEmail(editEmail.getText().toString().trim())
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Gracias , revise su correo", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "isSuccessful");
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Error , intenta nuevamente ", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "FAIL");
                                    }
                                    dialogInterface.dismiss();
                                })
                                .addOnFailureListener(e -> {
                                    dialogInterface.dismiss();
                                    Log.e(TAG, e.getMessage());
                                    Toast.makeText(LoginActivity.this, "Su correo no esta registrado", Toast.LENGTH_SHORT).show();

                                }));


        alertDialog.show();
    }

}
