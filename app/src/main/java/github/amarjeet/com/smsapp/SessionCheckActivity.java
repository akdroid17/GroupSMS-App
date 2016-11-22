package github.amarjeet.com.smsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.parse.ParseInstallation;
import com.parse.ParseUser;

/**
 * Created by Dharmraj on 7/26/2016.
 */
public class SessionCheckActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userSession();
    }

    public void userSession(){

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null) {
            ParseInstallation currentInstall = ParseInstallation.getCurrentInstallation();
            currentInstall.put("user", currentUser);
            currentInstall.put("userEmail", currentUser.getUsername());
            currentInstall.saveInBackground();

            Intent i = new Intent(SessionCheckActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            startActivity(new Intent(SessionCheckActivity.this, LoginActivity.class));
            finish();
        }

    }
}
