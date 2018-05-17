package com.asso.conference;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asso.conference.db.AuthDBModel;
import com.asso.conference.webClient.BookmarkCallback;
import com.asso.conference.webClient.UserService;
import com.asso.conference.webClient.models.AuthModel;
import com.asso.conference.webClient.models.UserModel;

public class SignUpActivity extends AppCompatActivity {
    private EditText usernameView;
    private EditText passwordView;
    private EditText repeatPasswordView;
    private EditText firstName;
    private EditText lastName;
    private Button signUpButton;
    private LinearLayout layout;
    private ProgressBar progressBar;

    public UserService userService = UserService.INSTANCE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        createView();
    }

    private void createView(){
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
        layout = (LinearLayout) findViewById(R.id.layout);
        usernameView = (EditText) findViewById(R.id.username);
        firstName = (EditText) findViewById(R.id.firstname);
        lastName = (EditText) findViewById(R.id.lastname);

        passwordView = (EditText) findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    //attemptLogin();
                    return true;
                }
                return false;
            }
        });
        repeatPasswordView = (EditText) findViewById(R.id.repeatpassword);


        signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(e->{
            attemptSignUp();
        });
    }

    private void showProgress(final boolean show) {

        usernameView.setClickable(show);
        passwordView.setClickable(show);
        repeatPasswordView.setClickable(show);
        firstName.setClickable(show);
        lastName.setClickable(show);
        signUpButton.setClickable(show);

        if(show){
            layout.setAlpha(0.4f);
            progressBar.setVisibility(View.VISIBLE);
        }
        else{
            layout.setAlpha(1f);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void attemptSignUp(){
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();
        String rPassword = repeatPasswordView.getText().toString();
        String firstname = firstName.getText().toString();
        String lastname = lastName.getText().toString();

        boolean correntLength = username.length() > 3 && password.length() > 3 && rPassword.length() > 3 && firstname.length() > 3 && lastname.length() > 3;

        if(!correntLength){
            createPopUp("Invalid", "Value should have more than three characters");
            return;
        }
        if(!password.equals(rPassword)){
            createPopUp("Invalid", "Passwords aren't the same");
            return;
        }

        showProgress(true);
        UserModel user = new UserModel();
        user.firstName = firstname;
        user.lastName = lastname;
        user.username = username;
        user.password = password;

        userService.signUp(user, new BookmarkCallback<AuthModel>() {
            @Override
            public void onSuccess(AuthModel value) {
                if(value == null)
                    return;

                showProgress(false);
                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }


            @Override
            public void onError(String message) {
                createPopUp("Failure", message);
                showProgress(false);
            }
        });
    }

    private void createPopUp(String title, String text){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
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
