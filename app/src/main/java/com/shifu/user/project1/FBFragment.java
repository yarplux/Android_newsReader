package com.shifu.user.project1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class FBFragment extends Fragment{

    private final List<String> permissions = Arrays.asList("user_status", "public_profile");
    private TextView loginText;
    private CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;

    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_fb, container, false);

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton =  v.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        loginButton.setReadPermissions(permissions);

        loginText = v.findViewById(R.id.login_text);

        boolean loggedOut = AccessToken.getCurrentAccessToken() == null;
        if(!loggedOut) {
            Log.d("Logged", "as " + Profile.getCurrentProfile().getName());
            getUserProfile(AccessToken.getCurrentAccessToken());
        }

        accessTokenTracker = (new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    Log.d("Logged Out", "It works!");
                    loginText.setText("");
                    accessTokenTracker.stopTracking();
                }
            }
        });

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
                Log.d("Logged:", loggedIn+"");
                getUserProfile(AccessToken.getCurrentAccessToken());
                accessTokenTracker.startTracking();
            }

            @Override
            public void onCancel() { }

            @Override
            public void onError(FacebookException error) { }

        });
        return v;
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("Logged As:", object.toString());
                        try {
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String login = getResources().getString(R.string.fb_login);
                            loginText.setText(getResources().getString(R.string.login_welcome, login, first_name, last_name));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

}
