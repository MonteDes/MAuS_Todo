package de.thb.fbi.maus.bm.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ressource besorgen
        final Button loginButton = (Button)findViewById(R.id.button);
        final EditText email = (EditText)findViewById(R.id.editText);
        final EditText password = (EditText)findViewById(R.id.editText1);
        final TextView wrongEmail = (TextView)findViewById(R.id.emailError);
        final TextView wrongPassword = (TextView)findViewById(R.id.passwordError);
        final TextView wrongLoginData = (TextView)findViewById(R.id.wrongLoginData);
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        final TextView progressBarText = (TextView)findViewById(R.id.myTextProgress);

        // Ueberpruefen, ob email syntaktisch korrekt
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Info, dass Logindaten falsch sind, ausblenden
                wrongLoginData.setVisibility(View.GONE);

                // Faelle fuer Email Syntax behandeln

                if(!Patterns.EMAIL_ADDRESS.matcher(s).matches() && s.length() > 0)
                    wrongEmail.setVisibility(View.VISIBLE);

                if(Patterns.EMAIL_ADDRESS.matcher(s).matches())
                    wrongEmail.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Ueberpruefen, ob Passwort Laenge korrekt
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Info, dass Logindaten falsch sind, ausblenden
                wrongLoginData.setVisibility(View.GONE);

                //Passwort Syntax Uberpruefen

                if(s.length() < 6 && s.length() > 0)
                    wrongPassword.setVisibility(View.VISIBLE);
                if(s.length() == 6)
                    wrongPassword.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loginButton.setBackgroundColor(getResources().getColor(R.color.lightBlue));

        // Daten bei Klick des Buttons ueberpruefen
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(email.getText()) && !TextUtils.isEmpty(password.getText()) &&
                        wrongEmail.getVisibility() == View.GONE && wrongPassword.getVisibility() == View.GONE) {
                    new ProgressSync().execute();
                }
            }
        });

        progressBar.setTransitionName("Checking Login Data");
        progressBar.setBackgroundColor(getResources().getColor(R.color.lightGrey));
        progressBar.setScrollBarFadeDuration(3000);

        progressBarText.setBackgroundColor(getResources().getColor(R.color.lightGrey));
    }
private class ProgressSync extends AsyncTask<Void, Void, Void>{
    final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
    final TextView progressBarText = (TextView)findViewById(R.id.myTextProgress);
    final EditText email = (EditText)findViewById(R.id.editText);
    final EditText password = (EditText)findViewById(R.id.editText1);
    final TextView wrongLoginData = (TextView)findViewById(R.id.wrongLoginData);

    @Override
    protected Void doInBackground(Void... voids) {

        return null;
    }
    @Override
    protected void onPostExecute(Void result){

        try {
            Thread.sleep(3000);
        }catch (InterruptedException e){
            System.err.print(e.getMessage());
        }
        if (email.getText().toString().equals("falsch@falsch.de") && password.getText().toString().equals("999999")){
            wrongLoginData.setVisibility(View.VISIBLE);
        }else{
            startActivity(new Intent(MainActivity.this, Todos.class));
        }
        progressBar.setVisibility(View.INVISIBLE);
        progressBarText.setVisibility(View.INVISIBLE);
    }
    @Override
    protected void onPreExecute(){
        progressBar.setVisibility(View.VISIBLE);
        progressBarText.setVisibility(View.VISIBLE);
    }
}
}

