package com.polymtl.jiajing.projet_shoppingsolver.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.polymtl.jiajing.projet_shoppingsolver.R;

public class LoginActivity extends Activity {
    private EditText login;
    private EditText password;
    //private String urlServlet = "http://192.168.0.104:8080/HttpServeur/ServletMobile";
    private String urlServlet = "http://132.207.220.98:8080/HttpServeur/ServletMobile";
    private LoginTask loginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (EditText)this.findViewById(R.id.editText_login);
        password = (EditText)this.findViewById(R.id.editText_password);

        Button connexionButton = (Button)this.findViewById(R.id.button_Connextion);
        connexionButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String strLogin = login.getText().toString();
                String strPassword = password.getText().toString();
                if(strLogin.length()==0||strPassword.length()==0){
                    Toast.makeText(getApplicationContext(), "Please enter login and password", Toast.LENGTH_SHORT).show();
                }else{
                    //TODO to decomment
//                    loginTask = new LoginTask();
//                    loginTask.execute(strLogin,strPassword);

                    Intent i = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    protected void onStop(){
        if (loginTask != null)
        {
            loginTask.cancel(true);
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoginTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            Boolean loginSuccess = false;
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
                        Map json = (Map)jsonParser.parse(sb.toString(), containerFactory);
                        JSONObject jsonObject = new JSONObject(json);
                        String password = (String)jsonObject.get("password");
                        if(password.equals(params[1])){
                            loginSuccess = true;
                        }
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return loginSuccess;
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(result){
                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                i.putExtra("login", login.getText().toString());
                i.putExtra("urlServlet",urlServlet);
                startActivity(i);
            }else{
                Toast.makeText(getApplicationContext(), "Login or password incorrect", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
