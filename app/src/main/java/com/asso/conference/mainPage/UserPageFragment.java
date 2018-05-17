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
import com.asso.conference.webClient.models.UserModel;

public class UserPageFragment extends Fragment {

    AuthDBModel authDBModel;
    TextView userData;
    Button logOutButton;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(
                R.layout.view_user,
                container,
                false);

        userData = (TextView) view.findViewById(R.id.userData);
        logOutButton = (Button) view.findViewById(R.id.log_out_button);

        logOutButton.setOnClickListener(e->{
            AuthDBModel authDBModel = AuthDBModel.getFirst();
            authDBModel.key = "";
            authDBModel.save();
            HomeActivity.class.cast(getActivity()).finish();
            HomeActivity.class.cast(getActivity()).startActivity(HomeActivity.class.cast(getActivity()).getIntent());
        });

        if(userData != null){
            userData.setText(""+authDBModel.username);
            userData.append("\n"+authDBModel.firstName);
            userData.append("\n"+authDBModel.lastName);
        }


        return view;
    }

    // This is the method the pager adapter will use
    // to create a new fragment
    public static Fragment newInstance(){
        UserPageFragment f=new UserPageFragment();

        if(AuthDBModel.exists()) {
            f.authDBModel = AuthDBModel.getFirst();
//            f.userData.setText(""+authDBModel.username);
//            f.userData.append("\n"+authDBModel.firstName);
//            f.userData.append("\n"+authDBModel.lastName);
        }


//        userModelConsumer.setValue(userModel->{
//            f.userData.setText(userModel.username);
//            f.userData.append("\n"+userModel.firstName);
//            f.userData.append("\n"+userModel.lastName);
//        });
        return f;
    }

}