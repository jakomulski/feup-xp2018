package com.asso.conference.mainPage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.asso.conference.HomeActivity;
import com.asso.conference.R;
import com.asso.conference.db.AuthDBModel;
import com.asso.conference.webClient.BookmarkCallback;
import com.asso.conference.webClient.UserService;
import com.asso.conference.webClient.models.AuthModel;

public class LoginFragment extends Fragment {

    private EditText usernameView;
    private EditText passwordView;

    public UserService userService = UserService.INSTANCE;

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

    private void createView(View view){
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

        Button signInButton = (Button) view.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener((e)->attemptLogin());

    }

    private void attemptLogin(){
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();

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
            }


            @Override
            public void onError() {

            }
        });
    }

    // This is the method the pager adapter will use
    // to create a new fragment
    public static Fragment newInstance(){
        LoginFragment f=new LoginFragment();
        return f;
    }

}