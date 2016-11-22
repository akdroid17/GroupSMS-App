package github.amarjeet.com.smsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class SignUpActivity extends BaseActivity {

    public EditText mName,mEmail,mPassword;
    public Button mBtnSignUp;
    public TextView mErrMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mName = (EditText) findViewById(R.id.et_name);
        mEmail = (EditText) findViewById(R.id.et_email);
        mPassword = (EditText)findViewById(R.id.et_password);
        mBtnSignUp = (Button) findViewById(R.id.btn_sign_up);
        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
        mErrMsg = (TextView) findViewById(R.id.err);
    }

    // method for signUp
    private void signUp(){
        final String name = mName.getText().toString();
        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();
        if(name.length() == 0 || email.length() == 0 ||
                password.length() == 0 ){
            mErrMsg.setText("Please fill all the fields");
            return;
        }

        ParseUser parseUser = new ParseUser();
        parseUser.put("name", name);
        parseUser.setUsername(email);
        parseUser.setPassword(password);
        parseUser.setEmail(email);
        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null)
                {
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    mErrMsg.setText("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

    }
}
