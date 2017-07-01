package de.thb.fbi.maus.bm.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import de.thb.fbi.maus.bm.login.accessor.CRUDAccessor;
import de.thb.fbi.maus.bm.login.accessor.CredentialsManager;

import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private boolean emailCheck = false, passwCheck = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ressource besorgen
        final Button loginButton = (Button) findViewById(R.id.button);
        final Button signUpButton = (Button) findViewById(R.id.signUp_button);
        final EditText email = (EditText) findViewById(R.id.editText);
        final EditText password = (EditText) findViewById(R.id.editText1);
        final TextView wrongEmail = (TextView) findViewById(R.id.emailError);
        final TextView wrongPassword = (TextView) findViewById(R.id.passwordError);
        final TextView wrongLoginData = (TextView) findViewById(R.id.wrongLoginData);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final TextView progressBarText = (TextView) findViewById(R.id.myTextProgress);

        new AsyncTask<Void, Void, Void>(){
            CRUDAccessor accessor;
            @Override
            protected Void doInBackground(Void... voids) {
                accessor = new CRUDAccessor(CRUDAccessor.getBaseURL());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(accessor == null) {
                    Todos.online = false;
                    startActivity(new Intent(MainActivity.this, Todos.class));
                    Toast.makeText(MainActivity.this, R.string.webservice_not_available, Toast.LENGTH_LONG).show();
                } else {
                    Todos.online = true;
                }
            }
        }.execute();

        // check syntax of email
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // hide information that entered credentials are wrong
                wrongLoginData.setVisibility(View.GONE);

                // check the syntax
                if (!Patterns.EMAIL_ADDRESS.matcher(s).matches() && s.length() > 0) {
                    wrongEmail.setVisibility(View.VISIBLE);
                    emailCheck = false;

                }

                if (Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    wrongEmail.setVisibility(View.GONE);
                    emailCheck = true;
                }


                loginButton.setEnabled(emailCheck && passwCheck);
                signUpButton.setEnabled(emailCheck && passwCheck);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // check password length
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // hide information that credentials are wrong
                wrongLoginData.setVisibility(View.GONE);

                // check password length
                if (s.length() < 6 && s.length() > 0) {
                    wrongPassword.setVisibility(View.VISIBLE);
                    passwCheck = false;
                }
                if (s.length() == 6) {
                    wrongPassword.setVisibility(View.GONE);
                    passwCheck = true;
                }


                loginButton.setEnabled(emailCheck && passwCheck);
                signUpButton.setEnabled(emailCheck && passwCheck);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loginButton.setBackgroundColor(getResources().getColor(R.color.lightBlue));

        // check credentials on click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(email.getText()) && !TextUtils.isEmpty(password.getText()) &&
                        wrongEmail.getVisibility() == View.GONE && wrongPassword.getVisibility() == View.GONE) {
                    new ProgressSync().execute();
                }
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SignUpSync().execute();
            }
        });

        progressBar.setTransitionName("Checking Login Data");
        progressBar.setBackgroundColor(this.getColor(R.color.lightGrey));
        progressBar.setScrollBarFadeDuration(3000);

        progressBarText.setBackgroundColor(getColor(R.color.lightGrey));
    }

    private class ProgressSync extends AsyncTask<Void, Void, Void> {
        private boolean check = false, connection = false;
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final TextView progressBarText = (TextView) findViewById(R.id.myTextProgress);
        final EditText email = (EditText) findViewById(R.id.editText);
        final EditText password = (EditText) findViewById(R.id.editText1);
        final TextView wrongLoginData = (TextView) findViewById(R.id.wrongLoginData);
        final Toast toast = Toast.makeText(MainActivity.this, R.string.no_connection_loginServer_check, Toast.LENGTH_LONG);

        @Override
        protected Void doInBackground(Void... voids) {
            CredentialsManager credentialsManager = new CredentialsManager(4300, "54.202.56.214");
            Socket con = credentialsManager.establishConnection();
            if(con != null) {

                connection = true;
                if (credentialsManager.checkCredentials(con, email.getText().toString(), password.getText().toString())) {
                    startActivity(new Intent(MainActivity.this, Todos.class));
                } else {
                    check = true;
                }
            } else {

                startActivity(new Intent(MainActivity.this, Todos.class));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(!connection)
                toast.show();
            if(check)
                wrongLoginData.setVisibility(View.VISIBLE);

            if(connection) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    System.err.print(e.getMessage());
                }
            }

            progressBar.setVisibility(View.INVISIBLE);
            progressBarText.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBarText.setVisibility(View.VISIBLE);
        }
    }


    private class SignUpSync extends AsyncTask<Void, Void, Void> {
        private boolean connection = false;
        final EditText email = (EditText) findViewById(R.id.editText);
        final EditText password = (EditText) findViewById(R.id.editText1);
        final Toast toast = Toast.makeText(MainActivity.this, R.string.no_connection_loginServer_signup, Toast.LENGTH_LONG);

        @Override
        protected Void doInBackground(Void... voids) {
            CredentialsManager credentialsManager = new CredentialsManager(4300, "54.202.56.214");
            Socket con = credentialsManager.establishConnection();

            if(con != null) {
                connection = true;
                credentialsManager.addCredentials(con, email.getText().toString(), password.getText().toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            toast.show();
        }
    }

}