package com.doctor.app.doctorapp.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.doctor.app.doctorapp.Model.DoctorInfo;
import com.doctor.app.doctorapp.Presenter.LoginPresenter;
import com.doctor.app.doctorapp.R;
import com.doctor.app.doctorapp.Utilitario.Common;
import com.doctor.app.doctorapp.View.ILoginView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity implements ILoginView {

    private Button btnSign,btnRegister;
    private RelativeLayout layout_login;
    private View mProgressView;
    private View mLoginFormView;
    private LoginPresenter loginPresenter;
    private EditText edtEmaillogin,edtPasswordlogin;
    private EditText edtEmailregister,edtPasswordregister,edtNameregister,edtLastnameregister,edtPhoneregister,edtSpecialityregister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginPresenter = new LoginPresenter(this);
        btnSign = findViewById(R.id.btnSingIn_login);
        btnRegister = findViewById(R.id.btnRegister_login);
        layout_login = findViewById(R.id.layout_login);
        mLoginFormView = findViewById(R.id.scrollv_login);
        mProgressView = findViewById(R.id.pgb_login);

        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });

        btnSign.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });
    }

    private void showLoginDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.login_title));
        dialog.setMessage(getString(R.string.login_message));

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View login_layout = layoutInflater.inflate(R.layout.layout_login_doctor,null);

        edtEmaillogin = login_layout.findViewById(R.id.txtEmail_doctor);
        edtPasswordlogin = login_layout.findViewById(R.id.txtPassword_doctor);
        dialog.setView(login_layout);

        dialog.setPositiveButton(getString(R.string.login_accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
                loginPresenter.validateLogin(edtEmaillogin.getText().toString(),edtPasswordlogin.getText().toString(),LoginActivity.this);
            }
        });
        dialog.setNegativeButton(getString(R.string.login_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    private void showRegisterDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.register_title));
        dialog.setMessage(getString(R.string.register_message));

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View register_layout = layoutInflater.inflate(R.layout.layout_register_doctor,null);

        edtEmailregister = register_layout.findViewById(R.id.txtEmail_doctor);
        edtPasswordregister = register_layout.findViewById(R.id.txtPassword_doctor);
        edtNameregister = register_layout.findViewById(R.id.txtName_doctor);
        edtLastnameregister= register_layout.findViewById(R.id.txtLastName_doctor);
        edtPhoneregister = register_layout.findViewById(R.id.txtPhone_doctor);

        dialog.setView(register_layout);
        dialog.setPositiveButton(getString(R.string.register_accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
                loginPresenter.validateRegister(edtEmailregister.getText().toString(),
                        edtPasswordregister.getText().toString(),
                        edtNameregister.getText().toString(),
                        edtLastnameregister.getText().toString(),
                        edtPhoneregister.getText().toString(),"NONE",
                        LoginActivity.this);
            }
        });
        dialog.setNegativeButton(getString(R.string.register_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    @Override
    public void showProgress() {
        showProgress(true);
    }

    @Override
    public void hideProgress() {
        showProgress(false);
    }

    @Override
    public void setEmailLoginError() {
        edtEmaillogin.setError(getString(R.string.error_invalid_email));
        edtEmaillogin.requestFocus();
    }

    @Override
    public void setPasswordLoginError() {
        edtPasswordlogin.setError(getString(R.string.error_invalid_password));
        edtPasswordlogin.requestFocus();
    }

    @Override
    public void navigatetoMain() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void showAlertLogin(String message) {
        Snackbar.make(layout_login,message,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showAlertRegister(String message) {
        Snackbar.make(layout_login,message,Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginPresenter.onDestroy();
    }
}

