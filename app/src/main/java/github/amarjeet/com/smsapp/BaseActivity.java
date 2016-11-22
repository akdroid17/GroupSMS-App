package github.amarjeet.com.smsapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Dharmraj on 7/26/2016.
 */
public class BaseActivity extends AppCompatActivity{

    public SessionManager sessionManager;
    public ApplicationController app;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = ApplicationController.getInstance();
        sessionManager = app.sessionManager;
    }
}
