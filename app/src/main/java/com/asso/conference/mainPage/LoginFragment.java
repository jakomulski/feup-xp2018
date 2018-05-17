package com.asso.conference.mainPage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asso.conference.HomeActivity;
import com.asso.conference.R;
import com.asso.conference.SignUpActivity;
import com.asso.conference.db.AuthDBModel;
import com.asso.conference.webClient.BookmarkCallback;
import com.asso.conference.webClient.UserService;
import com.asso.conference.webClient.models.AuthModel;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class LoginFragment extends Fragment {

    private EditText usernameView;
    private EditText passwordView;
    private LinearLayout layout;
    private ProgressBar progressBar;

    public UserService userService = UserService.INSTANCE;
    private Button  signInButton;
    private Button signUpButton;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(
                R.layout.activity_login,
                container,
                false);


        createView(view);
        return view;
    }


    private void showProgress(final boolean show) {
        if(show){
            layout.setAlpha(0.4f);
            progressBar.setVisibility(View.VISIBLE);
            signInButton.setClickable(false);
            signUpButton.setClickable(false);
        }
        else{
            layout.setAlpha(1f);
            progressBar.setVisibility(View.GONE);
            signInButton.setClickable(true);
            signUpButton.setClickable(true);
        }
    }



    private void createView(View view){
        layout = (LinearLayout) view.findViewById(R.id.layout);
        progressBar = (ProgressBar) view.findViewById(R.id.login_progress);
        usernameView = (EditText) view.findViewById(R.id.username);
        passwordView = (EditText) view.findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        signInButton = (Button) view.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener((e)->attemptLogin());

        signUpButton = (Button) view.findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(e->{
            Intent intent = new Intent(getContext(), SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin(){
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();
        showProgress(true);
        userService.logIn(username, password, new BookmarkCallback<AuthModel>() {
            @Override
            public void onSuccess(AuthModel value) {
                if(value == null)
                    return;

                if(AuthDBModel.exists()){
                    AuthDBModel authDBModel = AuthDBModel.getFirst();
                    authDBModel.userId = value.id.toString();
                    authDBModel.firstName =value.firstName;
                    authDBModel.key = value.token;
                    authDBModel.lastName = value.lastName;
                    authDBModel.username = value.username;
                    authDBModel.save();
                } else{
                    AuthDBModel authDBModel = new AuthDBModel();
                    authDBModel.userId = value.id.toString();
                    authDBModel.firstName =value.firstName;
                    authDBModel.key = value.token;
                    authDBModel.lastName = value.lastName;
                    authDBModel.username = value.username;
                    authDBModel.save();
                }
                UserService.INSTANCE.createAuthenticatedClient(value.token);
                HomeActivity.loggedIn = true;
                HomeActivity.class.cast(getActivity()).finish();
                HomeActivity.class.cast(getActivity()).startActivity(HomeActivity.class.cast(getActivity()).getIntent());
                showProgress(false);
            }


            @Override
            public void onError(String message) {
                createPopUp("Failure", message);
                showProgress(false);
            }
        });
    }


    // This is the method the pager adapter will use
    // to create a new fragment
    public static Fragment newInstance(){
        LoginFragment f=new LoginFragment();
        return f;
    }

    private void createPopUp(String title, String text){
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(text);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

}