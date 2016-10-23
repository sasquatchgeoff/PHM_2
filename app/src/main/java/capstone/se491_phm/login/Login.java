package capstone.se491_phm.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import capstone.se491_phm.R;

/**
 * Created by Tahani on 10/22/16.
 */

public class Login extends AppCompatActivity {

    EditText userName, PassWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_interface);

        userName =(EditText) findViewById(R.id.editText);
        PassWord= (EditText) findViewById(R.id.editText2);


    }

    public void onLogin(View view){
        String username=userName.getText().toString();
        String password=PassWord.getText().toString();
        String type ="login";

        BackgroundWorker bLogin= new BackgroundWorker(this);
        bLogin.execute(type, username,password);

    }
}
