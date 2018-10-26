package com.doctor.app.doctorapp.View;

public interface ILoginView {

    void showProgress();
    void hideProgress();
    void setEmailLoginError();
    void setPasswordLoginError();
    void navigatetoMain();
    void showAlertLogin(String message);
    void showAlertRegister(String message);
}
