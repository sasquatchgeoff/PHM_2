package capstone.se491_phm.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import capstone.se491_phm.R;
import capstone.se491_phm.common.util.BackgroundWorker;

/**
 * Created by Advait on 22-10-2016.
 */

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);



        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    // Register Button Click event
        public void onRegister(View view) {
            String name = inputFullName.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String type="register";

            if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    /*
                    registerUser(name, email, password);
                    A method that takes user i/p from here and connect with database
                     */

                BackgroundWorker bLogin= new BackgroundWorker(this);
                bLogin.execute(type, email ,password, name);


              //  Intent i = new Intent(getApplicationContext(),
                //        MainActivity.class);
             //   startActivity(i);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Please enter your details!", Toast.LENGTH_LONG)
                        .show();
            }
        }

}
