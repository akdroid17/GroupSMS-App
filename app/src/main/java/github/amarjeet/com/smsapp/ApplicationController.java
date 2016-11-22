package github.amarjeet.com.smsapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by Dharmraj on 7/26/2016.
 */
public class ApplicationController extends Application {

    private static ApplicationController instance;
    public SessionManager sessionManager;

    @Override
    public void onCreate() {
        instance = this;
        sessionManager = new SessionManager(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(getApplicationContext(), "PpZ5d0qUJdgkYmv3xT7W0F4YYLrX3aRYkKQXrLJR", "H9Oqwgj9pLV7TSQvOWgE709r4LEj5cDmODJ8Dbrb");  //for parse initialization
        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
        parseInstallation.saveInBackground();
        super.onCreate();
    }


    public static synchronized ApplicationController getInstance(){
        return instance;
    }
}
