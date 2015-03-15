package com.polymtl.jiajing.tp2_localisationmap.util;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Created by Zoe on 15-03-06.
 */
public class GetGsmCellLocation {
    String mcc;  //Mobile Country Code
    String mnc;  //mobile network code
    String cellid; //Cell ID
    String lac;  //Location Area Code

    Boolean error;
    String strURLSent;
    String GetOpenCellID_fullresult;

    String latitude;
    String longitude;

    public Boolean isError(){
        return error;
    }

    public void setMcc(String value){
        mcc = value;
    }

    public void setMnc(String value){
        mnc = value;
    }

    public void setCallID(int value){
        cellid = String.valueOf(value);
    }

    public void setCallLac(int value){
        lac = String.valueOf(value);
    }

    public String getLocation(){
        return(latitude + " : " + longitude);
    }

    public LatLng getLocationLatLng(){

        return new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

    }

    public void groupURLSent(){
        //String key = "59c6c861-b14d-4be2-8e3f-8b7e91d9a9db";
        String key = "59c6c861b14d4be28e3f8b7e91d9a9db";
       // http://www.opencellid.org/cell/get?key=59c6c861b14d4be28e3f8b7e91d9a9dbf&mcc=250&mnc=99&cellid=29518&lac=0&fmt=txt
       /* strURLSent =
                "http://www.opencellid.org/cell/get?key=" + key
                        +"&mcc=" + mcc
                        +"&mnc=" + mnc
                        +"&cellid=" + cellid
                        +"&lac=" + lac
                        +"&fmt=txt";*/
        strURLSent =
                "http://www.opencellid.org/cell/get?key=" + key
                        +"&mcc=" + mcc
                        +"&mnc=" + mnc
                        +"&cellid=" + cellid
                        +"&lac=" + lac;
        Log.i("strURLSent", strURLSent);
    }

    public String getstrURLSent(){
        return strURLSent;
    }

    public String getGetOpenCellID_fullresult(){
        return GetOpenCellID_fullresult;
    }

    public void GetOpenCellID() throws Exception {
        groupURLSent();
        HttpClient client = new DefaultHttpClient(); //initial HttpClient object
        Log.i("HttpClient", "create");
        HttpGet request = new HttpGet(strURLSent); //create HttpGet object, put url as parameter
        Log.i("HttpGet", "create");
        HttpResponse response = client.execute(request); //use execute to send request and get HttpResponse object
        Log.i("HttpResponse", "create");
        GetOpenCellID_fullresult = EntityUtils.toString(response.getEntity()); //use getEntity to get response information

        Log.i("GetOpenCellIDfullresult", GetOpenCellID_fullresult);
        spliteResult();
    }

    private void spliteResult(){
        if(GetOpenCellID_fullresult.equalsIgnoreCase("err")){
            error = true;
        }else{
            error = false;
            String[] tResult = GetOpenCellID_fullresult.split(",");
            latitude = tResult[0];
            longitude = tResult[1];
        }


    }
}
