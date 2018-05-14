package com.asso.conference.mainPage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.asso.conference.R;
import com.asso.conference.common.Consumer;
import com.asso.conference.common.Wrapper;
import com.asso.conference.webClient.models.UserModel;

public class UserPageFragment extends Fragment {

    TextView userData;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(
                R.layout.view_user,
                container,
                false);

        userData = (TextView) view.findViewById(R.id.userData);

        return view;
    }

    // This is the method the pager adapter will use
    // to create a new fragment
    public static Fragment newInstance(Wrapper<Consumer<UserModel>> userModelConsumer){
        UserPageFragment f=new UserPageFragment();
        userModelConsumer.setValue(userModel->{
            f.userData.setText(userModel.username);
            f.userData.append("\n"+userModel.firstName);
            f.userData.append("\n"+userModel.lastName);
        });
        return f;
    }

}