package com.doctor.app.doctorapp.Presenter;

import android.content.Context;

public interface ILoginPresenter {

    void validateLogin(String email, String passworrd, Context context);
    void validateRegister(String email,String password,String name,String lastname,String phone,String speciality,Context context);
    void onDestroy();
}
