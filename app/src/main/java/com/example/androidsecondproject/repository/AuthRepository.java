package com.example.androidsecondproject.repository;

import android.app.Activity;
import android.content.Context;


import androidx.annotation.NonNull;

import com.example.androidsecondproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class AuthRepository {
    private FirebaseAuth mAuth;
    private Context mContext;
    private RepoRegisterInterface mRegisterLister;
    private RepoLoginInterface mLoginLister;
    private static AuthRepository authRepository;

    public static AuthRepository getInstance(Context context){
        if(authRepository==null)
            return new AuthRepository(context);
        return authRepository;
    }

    private AuthRepository(Context context) {
        mAuth=FirebaseAuth.getInstance();
        this.mContext=context;
    }
    public boolean checkIfAuth(){
        FirebaseUser firebaseUser= mAuth.getCurrentUser();
        if(firebaseUser!=null) {
            return true;
        }
        else{
            return false;
        }
    }
    public void registerUser(String email,String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity)mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if(mRegisterLister!=null) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                mRegisterLister.onSuccessRegister(user.getUid());
                            }

                        } else {
                            userRegisterFailed(task);
                        }
                    }
                });
    }

    private void userRegisterFailed(Task<AuthResult> task){
        if(mRegisterLister!=null) {
            try {
                throw task.getException();
            } catch (FirebaseAuthWeakPasswordException e) {
                mRegisterLister.onFailedRegister(mContext.getString(R.string.password_length_error));
            } catch (FirebaseAuthInvalidCredentialsException e) {
                mRegisterLister.onFailedRegister(mContext.getString(R.string.invalid_credentials));
            } catch (FirebaseAuthUserCollisionException e) {
                mRegisterLister.onFailedRegister(mContext.getString(R.string.user_already_exist));
            } catch (Exception e) {
                mRegisterLister.onFailedRegister(mContext.getString(R.string.error));
            }
        }

    }

    public void loginUser(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity)mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (mLoginLister != null) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                mLoginLister.onSuccessLogin(user.getUid());
                            } else {
                                mLoginLister.onFailedLogin("Incorrect Credentials");
                            }
                        }
                    }
                });
    }
    public void logoutUser(){
        mAuth.signOut();
    }

    public void setRegisterLister(RepoRegisterInterface repoRegisterInterface){
        this.mRegisterLister =repoRegisterInterface;
    }
    public void setLoginLister(RepoLoginInterface repoLoginInterface){
        this.mLoginLister =repoLoginInterface;
    }

    public String getCurrentUserUid() {
        return mAuth.getCurrentUser().getUid();
    }

    public String getCurrentUserEmail() {
       return mAuth.getCurrentUser().getEmail();
    }

    public interface RepoRegisterInterface{
        void onSuccessRegister(String uid);
        void onFailedRegister(String error);
    }

    public interface RepoLoginInterface{
        void onSuccessLogin(String uid);
        void onFailedLogin(String error);
    }
}