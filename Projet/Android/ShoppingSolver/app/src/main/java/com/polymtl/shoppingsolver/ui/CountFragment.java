package com.polymtl.shoppingsolver.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polymtl.shoppingsolver.R;

public class CountFragment extends Fragment {
	private String urlServlet;
	private String userId;
	private View rootView;
	
    public CountFragment() {
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_count, container, false);
        Bundle bundle=getArguments();
        userId = bundle.getString("userId");
        urlServlet = bundle.getString("urlServlet");
   
        CountDetailTask countDetailTask = new CountDetailTask();
        countDetailTask.execute(userId);
        
        return rootView;
    }
    
	private class CountDetailTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			String countDetail = null;
			try {
				URL url = new URL(urlServlet+"?userID="+params[0]);
				HttpURLConnection urlConn= (HttpURLConnection)url.openConnection();
				urlConn.setConnectTimeout(60000);
				if( urlConn.getResponseCode() == HttpURLConnection.HTTP_OK){
					InputStreamReader in = new InputStreamReader(urlConn.getInputStream());
					BufferedReader br = new BufferedReader(in);
					StringBuilder sb = new StringBuilder();
					String line = null;
					while((line=br.readLine())!=null){
						sb.append(line);
					}
					countDetail = sb.toString();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return countDetail;
		}
		
		@Override
		protected void onPostExecute(String result){
			JSONParser jsonParser = new JSONParser();
	        ContainerFactory containerFactory = new ContainerFactory(){
	            @Override
	            public List creatArrayContainer() {
	              return new LinkedList();
	            }
	            @Override
	            public Map createObjectContainer() {
	              return new LinkedHashMap();
	            }
	        }; 
			try {
				Map json = (Map)jsonParser.parse(result, containerFactory);
	            JSONObject jsonObject = new JSONObject(json);
	            String firstname = (String)jsonObject.get("firstname");
	            String lastname = (String)jsonObject.get("lastname");
	            String balance = (String)jsonObject.get("balance");
	            Double b = Double.parseDouble(balance);
	            DecimalFormat df = new DecimalFormat("#.00");  
	            TextView tvMatricule=(TextView)CountFragment.this.rootView.findViewById(R.id.tvMatriculeVal); 
	            tvMatricule.setText(CountFragment.this.userId);
	            TextView tvFirstName=(TextView)CountFragment.this.rootView.findViewById(R.id.tvFirstNameVal); 
	            tvFirstName.setText(firstname);
	            TextView tvLastName=(TextView)CountFragment.this.rootView.findViewById(R.id.tvLastNameVal); 
	            tvLastName.setText(lastname);
	            TextView tvBalance=(TextView)CountFragment.this.rootView.findViewById(R.id.tvBalanceVal); 
	            tvBalance.setText(df.format(b));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
}
