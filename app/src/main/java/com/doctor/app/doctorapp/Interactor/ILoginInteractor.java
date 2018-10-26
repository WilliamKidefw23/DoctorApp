package com.doctor.app.doctorapp.Interactor;

import android.content.Context;

public interface ILoginInteractor {

    interface onLoginFinishedListener{
        void onEmailLoginError();
        void onPasswordLoginError();
        void onSuccessLogin();
        void onFailureLogin(String message);
    }

    interface onRegisterFinishedListener{
        void onFailureRegister(String message);
        void onSuccessRegister(String message);
    }

    void onLogin(String email, String password , onLoginFinishedListener listener, Context context);
    void onRegister(String email,String password,String name,String lastname,String phone,String speciality, onRegisterFinishedListener listener,Context context);
}
