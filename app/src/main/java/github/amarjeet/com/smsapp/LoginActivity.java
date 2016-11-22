package github.amarjeet.com.smsapp;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class LoginActivity extends BaseActivity {

    public EditText username,password;
    public Button  btnLogin,btnNewUser;
    public TextView mErr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        btnLogin = (Button)findViewById(R.id.btn_login);
        btnNewUser = (Button) findViewById(R.id.btn_new_account);
        mErr = (TextView) findViewById(R.id.err);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parseUserLogin();
            }
        });

        btnNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

    }


    private void parseUserLogin(){
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();

        if (username.length() == 0 || password.length() == 0){
            mErr.setText("Please complete the fields");
            return;
        }

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null && e == null) {
                    ParseInstallation currentInstall = ParseInstallation.getCurrentInstallation();
                    currentInstall.put("user",parseUser);
                    currentInstall.put("userEmail", parseUser.getUsername());
                    currentInstall.saveInBackground();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                } else {
                    mErr.setText("Error: " + e.getMessage());
                }
            }
        });
    }
}
