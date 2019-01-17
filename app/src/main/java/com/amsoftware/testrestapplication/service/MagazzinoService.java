package com.amsoftware.testrestapplication.service;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.Nullable;

import com.amsoftware.testrestapplication.ProdottiView;
import com.amsoftware.testrestapplication.R;
import com.amsoftware.testrestapplication.RestActivity;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

public class MagazzinoService
{


    public static String getInfoEmptyProducts(Context context)
    {
        return context.getResources().getString(R.string.infoemptyproducts);
    }

    public static int getUsernameOrEmailMinLength(Context context)
    {
        return context.getResources().getInteger(R.integer.user_length);
    }

    public static int getPasswordMinLength(Context context)
    {
        return context.getResources().getInteger(R.integer.password_length);
    }

    public static String getMainActivityTitle(Context context)
    {
        return context.getResources().getString(R.string.app_name) + ":" + context.getResources().getString(R.string.login);
    }

    public static String getProdottiActivityTitle(Context context)
    {
        return context.getResources().getString(R.string.app_name) + ":" + context.getResources().getString(R.string.prodotti);
    }

    public enum REST_MODE
    {
        HTTP,
        HTTPS,
        HTTPS_UNCHECKED
    }
    public enum CALL_NAME
    {
        REST_PRODOTTI,
        REST_LOGIN
    }
    public static RestTemplate getRestCaller(REST_MODE restMode) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        RestTemplate restTemplate = null;

        switch(restMode)

        {
            case HTTPS_UNCHECKED:

            final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            SSLContextBuilder builder = new SSLContextBuilder();

            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());

            X509HostnameVerifier verifier = new X509HostnameVerifier() {
                @Override
                public boolean verify(String host, SSLSession session) {
                    return true;
                }

                @Override
                public void verify(String host, SSLSocket ssl) throws IOException {

                }

                @Override
                public void verify(String host, X509Certificate cert) throws SSLException {

                }

                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {

                }
            };

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(), verifier);
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            requestFactory.setHttpClient(httpclient);

            //only if https self signed
            restTemplate = new RestTemplate(requestFactory);
            break;

            default:
                restTemplate = new RestTemplate();
                break;
        }
        return restTemplate;
    }

     public static Map<String,Locale> mLocales;

     static
     {
         mLocales = new Hashtable<>();

     }

     public static String getProdottiService(Context context)
     {
         final String GET_PRODOTTI = isDebug(context) ? getProdottiServiceUrl(context) : getProdottiServiceUrl(context);
         return GET_PRODOTTI;
     }
    public static String getLoginService(Context context)
    {
        final String POST_LOGIN = isDebug(context) ? getLoginServiceUrl(context) : getLoginServiceUrl(context);
        return POST_LOGIN;
    }
    public static String getLoginCheckService(Context context)
    {
        final String POST_LOGIN_CHECK = isDebug(context) ? getLoginCheckServiceUrl(context) : getLoginCheckServiceUrl(context);
        return POST_LOGIN_CHECK;
    }
    public static String getLogoutService(Context context)
    {
        final String POST_LOGOUT = isDebug(context) ? getLogoutServiceUrl(context) : getLogoutServiceUrl(context);
        return POST_LOGOUT;
    }
     public static String[] getProdottiHeaders(Context context)
     {
          final String[] prodotti_headers =
                  {
                     context.getResources().getString(R.string.prodotti_id),
                     context.getResources().getString(R.string.prodotti_nome),
                     context.getResources().getString(R.string.prodotti_descrizione),
                     context.getResources().getString(R.string.prodotti_quantita),
                     context.getResources().getString(R.string.prodotti_prezzo)
                  };
           return prodotti_headers;
     }

    @Nullable
    public static Locale getLocale(Context context)
    {
        boolean forceLocale = Boolean.parseBoolean(context.getResources().getString(R.string.force_lang));
        if(!forceLocale)
        {
            return null;
        }
        String locale       = context.getResources().getString(R.string.language);
        Locale loc          = null;

        if(!mLocales.containsKey(locale))
        {
            int index = locale.indexOf('_');
            String l = locale.substring(0,index);
            String L = locale.substring(index+1,locale.length());
            loc = new Locale(l,L);
            mLocales.put(locale,loc);
        }

        loc = mLocales.get(locale);
        return loc;
    }

    public static void configureLocale(Locale locale,Context baseContext)
    {
        Locale.setDefault(locale);

        Configuration config = baseContext.getResources().getConfiguration();
        config.locale = locale;
        baseContext.getResources().updateConfiguration(config, baseContext.getResources().getDisplayMetrics());
    }
    public static void configureLocale(Context context,Context baseContext)
    {
        Locale locale = getLocale(context);
        if(locale == null)
        {
            return;
        }

      configureLocale(locale,baseContext);

    }
    public static String formatCurrency(String text, Locale locale)
    {
        if(locale==null)
        {
            return NumberFormat.getCurrencyInstance().format(Float.parseFloat(text));
        }
        return NumberFormat.getCurrencyInstance(locale).format(Float.parseFloat(text));
    }

    public static String getErrorServicePrefix(Context context)
    {
        return context.getResources().getString(R.string.errorserviceprefix);
    }

    public static String getAuthenticationHeader(Context context)
    {
        return context.getResources().getString(R.string.authenticationheader);
    }

    public static String getProdottiServiceUrl(Context context)
    {
        return getProtocol(context) + context.getResources().getString(R.string.service_prodotti);
    }

    public static String getLoginServiceUrl(Context context)
    {
        return getProtocol(context) + context.getResources().getString(R.string.service_login);
    }
    public static String getLoginCheckServiceUrl(Context context)
    {
        return getProtocol(context) + context.getResources().getString(R.string.service_login_check);
    }

    public static String getLogoutServiceUrl(Context context)
    {
        return getProtocol(context) + context.getResources().getString(R.string.service_logout);
    }

    public static String getProtocol(Context context)
    {
        String protocol = isDebug(context) ? context.getResources().getString(R.string.protocol_std_d) + context.getResources().getString(R.string.root) : context.getResources().getString(R.string.protocol_std_r) + context.getResources().getString(R.string.root);
        if (useSecure(context))
        {
            protocol = isDebug(context) ? context.getResources().getString(R.string.protocol_sec_d) + context.getResources().getString(R.string.root) : context.getResources().getString(R.string.protocol_sec_r) + context.getResources().getString(R.string.root);
        }
        return protocol;
    }


    public static Boolean useSecure(Context context)
    {
        return Boolean.parseBoolean(context.getResources().getString(R.string.secure));
    }

    public static Boolean isDebug(Context context)
    {
        return Boolean.parseBoolean(context.getResources().getString(R.string.debug));
    }
}
