package com.asso.conference.mainPage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.asso.conference.HomeActivity;
import com.asso.conference.R;
import com.asso.conference.common.Consumer;
import com.asso.conference.common.Wrapper;
import com.asso.conference.db.AuthDBModel;
import com.asso.conference.webClient.BookmarkCallback;
import com.asso.conference.webClient.UserService;
import com.asso.conference.webClient.models.AuthModel;
import com.asso.conference.webClient.models.UserModel;

public class UserPageFragment extends Fragment {

    AuthDBModel authDBModel;
    TextView userData;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(
                R.layout.view_user,
                container,
                false);

        userData = (TextView) view.findViewById(R.id.userData);


        if(userData != null){
            userData.setText(""+authDBModel.username);
            userData.append("\n"+authDBModel.firstName);
            userData.append("\n"+authDBModel.lastName);
        }


        Button logoutButton = (Button) view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener((e)->attemptLogout());


        return view;
    }

    private void attemptLogout(){
        AuthDBModel authDBModel = AuthDBModel.getFirst();
        authDBModel.key = "";
        authDBModel.save();
        HomeActivity.class.cast(getActivity()).finish();
        HomeActivity.class.cast(getActivity()).startActivity(HomeActivity.class.cast(getActivity()).getIntent());
    }

    public static Fragment newInstance(){
        UserPageFragment f=new UserPageFragment();

        if(AuthDBModel.exists()) {
            f.authDBModel = AuthDBModel.getFirst();
        }

        return f;
    }
}