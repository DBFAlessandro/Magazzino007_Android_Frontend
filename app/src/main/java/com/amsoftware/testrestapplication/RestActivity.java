package com.amsoftware.testrestapplication;

//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amsoftware.testrestapplication.entity.LoginResponse;
import com.amsoftware.testrestapplication.entity.ProdottiEntity;
import com.amsoftware.testrestapplication.entity.RestCallerGetProdottiTask;
import com.amsoftware.testrestapplication.entity.RestCallerPostLoginTask;
import com.amsoftware.testrestapplication.service.MagazzinoService;
import com.amsoftware.testrestapplication.service.TaskActivity;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import static android.support.v7.widget.LinearLayoutCompat.HORIZONTAL;


/**
 * A login screen that offers login via email/password.
 */
public class RestActivity extends AppCompatActivity implements TaskActivity
{

    public class Token
    {
        String mToken;

        public Token(String token)
        {
            mToken = token;
        }

        public String getToken()
        {
            return mToken;
        }

    }

    private static final String REMEMBER_ME = "remember-me";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private RestCallerPostLoginTask mAuthTask = null;
    private RestCallerGetProdottiTask mRestCallerTask = null;
    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
  //  private View mProgressView;
    private ProgressDialog mProgressView;
    private View mLoginFormView;
    private Button mLogInButton;
    private boolean mResumedOnly;

    private Token mStoredToken;
    private boolean mRememberMe;
    private View mLoginCheckBox;
    private View mProdottiButton;
    private View mUserView;
    private View mPassView;
    @Override
    protected void onResume()
    {
        super.onResume();

        setUIReferences();

        if(mResumedOnly && mStoredToken != null)
        {
            //try to login with current token and refresh it (like remember me...)
            //if token is invalid it negate the login as usual showing the same login page
            //checkbox for refresh (if not use another service to check header only, (don't pass for credential filter))
            //like done for logout
            //if logged show logout

            setLoginControls(true);
            attemptTokenLogin();
        }

        mResumedOnly = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        MagazzinoService.configureLocale(this,getBaseContext());

        setContentView(R.layout.activity_login);

        setTitle(MagazzinoService.getMainActivityTitle(this));

        mResumedOnly = false;


        // Set up the login form.
        // set the references on construction
        setUIReferences();
        //set listeners!!!
        mPasswordView.setOnEditorActionListener
                (
            new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL)
                {

                    attemptUsernamePasswordLogin();

                    return true;
                }
                return false;
            }
        });



        mLogInButton.setOnClickListener
                (
                        new OnClickListener()
                         {
                           @Override
                           public void onClick(View view)
                            {
                                attemptUsernamePasswordLogin();
                            }
                        }
                );
        mProdottiButton.setOnClickListener
                (
                        new OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                attemptGetProdotti(mStoredToken!=null ? mStoredToken.getToken() : null);
                            }
                        }
                );
        mLoginFormView = findViewById(R.id.login_form);

        if(mStoredToken == null && savedInstanceState!=null)
        {
            String stateSavedToken    = savedInstanceState.getString(MagazzinoService.getAuthenticationHeader(this));
            boolean rememberMe = savedInstanceState.getBoolean(REMEMBER_ME);

            if(stateSavedToken!=null)
            {
                mStoredToken = new Token(stateSavedToken);
                mRememberMe  = rememberMe;

                setLoginControls(true);

                return;
            }
        }

        setLoginControls(false);
    }

    private void setUIReferences()
    {
        mUsernameView   = findViewById(R.id.email);
        mPasswordView   = findViewById(R.id.password);
        mUserView       = findViewById(R.id.username_container);
        mPassView       = findViewById(R.id.password_container);
        mLoginCheckBox  = findViewById(R.id.checkbox_rememberme);
        mProdottiButton = findViewById(R.id.button_prodotti);
        mLogInButton    = findViewById(R.id.email_sign_in_button);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptUsernamePasswordLogin()
    {
        if (mAuthTask != null)
        {
            return;
        }

      if(mLogInButton.getText().equals(getResources().getString(R.string.logout)))
      {
          mAuthTask = new RestCallerPostLoginTask(this,mStoredToken,MagazzinoService.getAuthenticationHeader(this),true);
          mAuthTask.execute(MagazzinoService.getLogoutService(this));
          return;
      }
        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email    = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password))
        {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView   = mPasswordView;
            cancel      = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email))
        {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }
        else if (!isUsernameOrEmailValid(email))
        {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return;
        }

        mAuthTask = new RestCallerPostLoginTask(this,email, password);
        mAuthTask.execute(MagazzinoService.getLoginService(this));

    }
    private void attemptTokenLogin()
    {
        if (mAuthTask != null)
        {
            return;
        }

        mAuthTask = new RestCallerPostLoginTask(this,mStoredToken,MagazzinoService.getAuthenticationHeader(this),false);

        mAuthTask.execute(mRememberMe ? MagazzinoService.getLoginService(this) : MagazzinoService.getLoginCheckService(this));

    }



    @Override
    public void onPreExecute(MagazzinoService.CALL_NAME callName, Object param)
    {
        switch(callName)
        {

            case REST_PRODOTTI:
            {
                showProgress(RestActivity.this.getResources().getString(R.string.prodotti),RestActivity.this.getResources().getString(R.string.caricamento));
            }
            break;
            case REST_LOGIN:
            {
                showProgress((Boolean)param? RestActivity.this.getResources().getString(R.string.logout) : RestActivity.this.getResources().getString(R.string.login),RestActivity.this.getResources().getString(R.string.caricamento));
            }
            break;
            default: break;
        }
    }
    @Override
    public void onPostExecute(MagazzinoService.CALL_NAME callName, Object result) {
        switch(callName)
        {

            case REST_PRODOTTI:
            {
                mRestCallerTask = null;
                hideProgress();
                HttpStatus status = ((ResponseEntity<ProdottiEntity[]>) result).getStatusCode();
                if (status == HttpStatus.OK) {

                    Intent request = new Intent(this, ProdottiView.class);
                    request.putExtra(MagazzinoService.CALL_NAME.REST_PRODOTTI.toString(), (ProdottiEntity[]) ((ResponseEntity<ProdottiEntity[]>) result).getBody());
                    if(mStoredToken!=null)
                    {
                        request.putExtra(MagazzinoService.getAuthenticationHeader(this),mStoredToken.getToken());
                    }

                    startActivity(request);
                }
                else
                {
                    showHttpError(status);
                }
                break;
            }
            case REST_LOGIN:
            {
                mAuthTask = null;
                hideProgress();

                HttpStatus status = ((ResponseEntity<LoginResponse>) result).getStatusCode();

                if (status == HttpStatus.OK)
                {
                    LoginResponse loginResponse = ((ResponseEntity<LoginResponse>) result).getBody();

                    if(loginResponse == null)
                    {
                        setLoginControls(true);
                        return;
                    }

                    if(loginResponse.getStatus())
                    {
                        RestActivity.Token respToken = new RestActivity.Token(loginResponse.getIdToken());
                        mStoredToken    = respToken;
                    }

                    setLoginControls(loginResponse.getStatus());
                }
                else
                {

                    showHttpError(status);

                    setLoginControls(false);

                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mUsernameView.setError(getString(R.string.error_invalid_email));
                    mUsernameView.requestFocus();
                }
            }
            break;
            default: break;
        }
    }
    @Override
    public void onCancelled(MagazzinoService.CALL_NAME callName)
    {
            switch(callName)
            {
                case REST_LOGIN:
                {
                   mAuthTask = null;

                }
                break;
                case REST_PRODOTTI:
                {
                    mRestCallerTask = null;
                }
                break;
                default:
                 break;
            }
        hideProgress();
    }


    private void attemptGetProdotti(String authToken)
    {
        if (mRestCallerTask != null)
        {
            return;
        }

        mRestCallerTask = new RestCallerGetProdottiTask(this,MagazzinoService.getAuthenticationHeader(this),authToken);
        mRestCallerTask.execute(MagazzinoService.getProdottiService(this));

    }

    private boolean isUsernameOrEmailValid(String usernameOrEmail)
    {
        //TODO: Replace this with your own logic
      //  return email.contains("@");
        return usernameOrEmail.length() > MagazzinoService.getUsernameOrEmailMinLength(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if(mStoredToken != null)
        {
            outState.putString(MagazzinoService.getAuthenticationHeader(this), mStoredToken.getToken());
            outState.putBoolean(REMEMBER_ME, mRememberMe);
        }
    }

    private boolean isPasswordValid(String password)
    {
        //TODO: Replace this with your own logic
        return password.length() > MagazzinoService.getPasswordMinLength(this);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(String taskName,String processString)
    {
        mProgressView = ProgressDialog.show(this, taskName, processString, true);
        mLoginFormView.setVisibility(View.INVISIBLE);

    }

    private void hideProgress()
    {

        mProgressView.dismiss();
        mLoginFormView.setVisibility(View.VISIBLE);

    }

    private void setLoginControls(boolean isLogged)
    {
        if(isLogged)
        {
            mPasswordView.setVisibility(View.GONE);
            mUsernameView.setVisibility(View.GONE);
            mPassView.setVisibility(View.GONE);
            mUserView.setVisibility(View.GONE);
            mLoginCheckBox.setVisibility(View.GONE);
            mProdottiButton.setVisibility(View.VISIBLE);
            mLogInButton.setText(R.string.logout);
            return;
        }

        mPasswordView.setVisibility(View.VISIBLE);
        mUsernameView.setVisibility(View.VISIBLE);
        mPassView.setVisibility(View.VISIBLE);
        mUserView.setVisibility(View.VISIBLE);
        mLoginCheckBox.setVisibility(View.VISIBLE);
        mProdottiButton.setVisibility(View.GONE);
        mLogInButton.setText(R.string.login);
    }

    private void showHttpError(HttpStatus status) {
        Toast toast = Toast.makeText(this, MagazzinoService.getErrorServicePrefix(this) + status.toString() + " " + status.getReasonPhrase(), Toast.LENGTH_SHORT);
        toast.setGravity(HORIZONTAL, 0, 0);
        toast.show();
    }

    @Override
    public void onBackPressed()
    {
        //pass some parameters back
        super.onBackPressed();
    }

    public void onCheckboxRememberMeClicked(View view)
    {
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_rememberme:
                if (checked)
                {
                    mRememberMe = true;
                }
            else
                {
                    mRememberMe = false;
                }
                break;

           default:
            break;
        }
    }



}

