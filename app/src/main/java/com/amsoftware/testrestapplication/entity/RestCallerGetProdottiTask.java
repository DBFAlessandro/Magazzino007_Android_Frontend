package com.amsoftware.testrestapplication.entity;

import android.os.AsyncTask;
import android.util.Log;

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

public class RestCallerGetProdottiTask extends AsyncTask<String, Void, ResponseEntity<ProdottiEntity[]>>
{
    private final String AUTH_TOKEN;
    private final String AUTH_HEADER;
    private final TaskActivity activity;
    public RestCallerGetProdottiTask(TaskActivity activity,String authHeader, String authToken)
    {
        this.activity = activity;
        AUTH_HEADER    = authHeader;
        AUTH_TOKEN     = authToken;
    }

    @Override
    protected void onPreExecute()
    {
        activity.onPreExecute(MagazzinoService.CALL_NAME.REST_PRODOTTI,null);
    }
    @Override
    protected ResponseEntity<ProdottiEntity[]> doInBackground(String... params)
    {
        ResponseEntity<ProdottiEntity[]> prodottiResponse = null;

        try
        {

            final String url = params[0];

            RestTemplate restTemplate = MagazzinoService.getRestCaller(MagazzinoService.REST_MODE.HTTPS_UNCHECKED);

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            if(AUTH_TOKEN !=null)
            {
                HttpHeaders headers = new HttpHeaders();
                headers.add(AUTH_HEADER, AUTH_TOKEN);
                HttpEntity httpHeadersOnlyEntity = new HttpEntity(headers);

                prodottiResponse = restTemplate.exchange(url, HttpMethod.GET, httpHeadersOnlyEntity, ProdottiEntity[].class);
            }
            else
            {
                prodottiResponse = restTemplate.exchange(url, HttpMethod.GET, null, ProdottiEntity[].class);
            }

        }
        catch (HttpClientErrorException re)
        {
            Log.e("MainActivity", re.getMessage(), re.getMostSpecificCause());
            prodottiResponse = new ResponseEntity<ProdottiEntity[]>(re.getStatusCode());
        }
        catch (Exception e)
        {
            Log.e("MainActivity", e.getMessage(), e);
            prodottiResponse = new ResponseEntity<ProdottiEntity[]>(HttpStatus.SEE_OTHER);
        }

        return prodottiResponse;
    }

    @Override
    protected void onPostExecute(final ResponseEntity<ProdottiEntity[]> success)
    {
         activity.onPostExecute(MagazzinoService.CALL_NAME.REST_PRODOTTI, success);
    }

    @Override
    protected void onCancelled()
    {
        activity.onCancelled(MagazzinoService.CALL_NAME.REST_PRODOTTI);
    }
}


