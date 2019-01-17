package com.amsoftware.testrestapplication.entity;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.amsoftware.testrestapplication.ProdottiView;
import com.amsoftware.testrestapplication.R;
import com.amsoftware.testrestapplication.RestActivity;
import com.amsoftware.testrestapplication.service.MagazzinoService;
import com.amsoftware.testrestapplication.service.TaskActivity;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class RestCallerPostLoginTask extends AsyncTask<String, Void, ResponseEntity<LoginResponse>>
    {

        private final String mUsernameOrEmail;
        private final String mPassword;
        private final String mAuthorizationHeader;
        private final boolean mLogout;
        private TaskActivity taskActivity;

        public RestCallerPostLoginTask(TaskActivity tActivity, String email, String password)
        {
            taskActivity = tActivity;
            mUsernameOrEmail = email;
            mPassword = password;
            mAuthorizationHeader = null;
            mLogout = false;
        }

        public RestCallerPostLoginTask(TaskActivity tActivity,RestActivity.Token token, String authorizationHeader, boolean logout)
        {
            taskActivity = tActivity;
            mUsernameOrEmail = token.getToken();
            mPassword  = "";
            mAuthorizationHeader = authorizationHeader;
            mLogout = logout;
        }
        @Override
        protected void onPreExecute()
        {
            taskActivity.onPreExecute(MagazzinoService.CALL_NAME.REST_LOGIN,mLogout);

        }

        @Override
        protected ResponseEntity<LoginResponse> doInBackground(String... params)
        {

            ResponseEntity<LoginResponse> result = null;

            try
            {
                final String url = params[0];

                RestTemplate restTemplate = MagazzinoService.getRestCaller(MagazzinoService.REST_MODE.HTTPS_UNCHECKED);

                if(mLogout)
                {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(mAuthorizationHeader, mUsernameOrEmail);
                    HttpEntity token = new HttpEntity<>(null,headers);
                    result = restTemplate.postForEntity(url,token,LoginResponse.class);
                    Log.i("MainActivity", result.getStatusCode().toString());
                }
                else {

                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.setPassword(mPassword);
                    loginRequest.setUsernameOrEmail(mUsernameOrEmail);

                    if (mAuthorizationHeader == null)
                    {

                        HttpEntity<LoginRequest> requestLoginBody = new HttpEntity<>(loginRequest);
                        result = restTemplate.postForEntity(url, requestLoginBody, LoginResponse.class);
                        Log.i("MainActivity", result.getStatusCode().toString());

                    } else {

                        HttpHeaders headers = new HttpHeaders();
                        headers.add(mAuthorizationHeader, mUsernameOrEmail);

                        HttpEntity<LoginRequest> requestLoginBody = new HttpEntity<>(loginRequest, headers);

                        result = restTemplate.postForEntity(url, requestLoginBody, LoginResponse.class);
                        Log.i("MainActivity", result.getStatusCode().toString());
                    }
                }
            }
            catch (HttpClientErrorException re)
            {
                Log.e("MainActivity", re.getMessage(), re);
                result = new ResponseEntity<LoginResponse>(re.getStatusCode());
            }
            catch (Exception e)
            {
                Log.e("MainActivity", e.getMessage(), e);
                result = new ResponseEntity<LoginResponse>(HttpStatus.SEE_OTHER);
            }

            return result;

        }

        @Override
        protected void onPostExecute(final ResponseEntity<LoginResponse> success)
        {
            taskActivity.onPostExecute(MagazzinoService.CALL_NAME.REST_LOGIN,success);

        }

        @Override
        protected void onCancelled()
        {

            taskActivity.onCancelled(MagazzinoService.CALL_NAME.REST_LOGIN);
        }
    }


