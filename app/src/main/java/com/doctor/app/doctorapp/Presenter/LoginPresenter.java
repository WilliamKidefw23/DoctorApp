package com.doctor.app.doctorapp.Presenter;

import android.content.Context;

import com.doctor.app.doctorapp.Interactor.ILoginInteractor;
import com.doctor.app.doctorapp.Interactor.LoginInteractor;
import com.doctor.app.doctorapp.View.ILoginView;

public class LoginPresenter implements ILoginPresenter,
        ILoginInteractor.onLoginFinishedListener,
        ILoginInteractor.onRegisterFinishedListener{

    private ILoginView iLoginView;
    private ILoginInteractor iLoginInteractor;

    public LoginPresenter(ILoginView view){
        this.iLoginView = view;
        iLoginInteractor = new LoginInteractor();
    }

    @Override
    public void validateLogin(String email, String password, Context context) {
        if(iLoginView!=null){
            iLoginView.showProgress();
            iLoginInteractor.onLogin(email,password,this,context);
        }
    }

    @Override
    public void validateRegister(String email, String password, String name, String lastname, String phone,String speciality, Context context) {
        if(iLoginView!=null){
            iLoginInteractor.onRegister(email,password,name,lastname,phone,speciality,this,context);
        }
    }

    @Override
    public void onDestroy() {
        if(iLoginView!=null){
            iLoginView = null;
        }
    }

    @Override
    public void onEmailLoginError() {
        if(iLoginView!=null){
            iLoginView.hideProgress();
            iLoginView.setEmailLoginError();
        }
    }

    @Override
    public void onPasswordLoginError() {
        if(iLoginView!=null){
            iLoginView.hideProgress();
            iLoginView.setPasswordLoginError();
        }
    }

    @Override
    public void onSuccessLogin() {
        if(iLoginView!=null){
            iLoginView.hideProgress();
            iLoginView.navigatetoMain();
        }
    }

    @Override
    public void onFailureLogin(String message) {
        if(iLoginView!=null){
            iLoginView.hideProgress();
            iLoginView.showAlertLogin(message);
        }
    }

    @Override
    public void onFailureRegister(String message) {
        if(iLoginView!=null){
            iLoginView.showAlertRegister(message);
        }
    }

    @Override
    public void onSuccessRegister(String message) {
        if(iLoginView!=null){
            iLoginView.showAlertRegister(message);
        }
    }
}
