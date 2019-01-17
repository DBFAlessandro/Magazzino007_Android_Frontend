package com.amsoftware.testrestapplication.service;

public interface TaskActivity
{
    void onPostExecute(MagazzinoService.CALL_NAME callName,Object result);

    void onPreExecute(MagazzinoService.CALL_NAME callName,Object param);

    void onCancelled(MagazzinoService.CALL_NAME restLogin);
}
