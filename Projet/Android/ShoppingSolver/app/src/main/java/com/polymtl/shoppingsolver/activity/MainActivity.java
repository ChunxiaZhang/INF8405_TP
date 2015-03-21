package com.polymtl.shoppingsolver.activity;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.polymtl.shoppingsolver.ui.CountFragment;
import com.polymtl.shoppingsolver.ui.DrawerItemAdapter;
import com.polymtl.shoppingsolver.ui.NavDrawerItem;
import com.polymtl.shoppingsolver.ui.PaymentFragment;
import com.polymtl.shoppingsolver.R;

public class MainActivity extends Activity {
	private String[] mNavigationDrawerItemTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	// nav drawer title
	private CharSequence mDrawerTitle;
	// used to store app title
	private CharSequence mTitle;
	
	private String userId;
	private String urlServlet;
	
	private final static int SCANNIN_GREQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent = getIntent();
		userId = intent.getStringExtra("login");
		urlServlet = intent.getStringExtra("urlServlet");
		
		mTitle = mDrawerTitle = getTitle();
		mNavigationDrawerItemTitles = getResources().getStringArray(R.array.nav_drawer_items);
		mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawerList = (ListView)findViewById(R.id.left_drawer);
		NavDrawerItem[] drawerItem = new NavDrawerItem[2];
		drawerItem[0] = new NavDrawerItem(R.drawable.ic_payment,"Shopping");
		drawerItem[1] = new NavDrawerItem(R.drawable.ic_count_detail,"Count Detail");
		DrawerItemAdapter adapter = new DrawerItemAdapter(this,R.layout.listview_item_row,drawerItem);
		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		mDrawerToggle = new ActionBarDrawerToggle(
		        this,
		        mDrawerLayout,
		        R.drawable.ic_drawer, 
		        R.string.drawer_open, 
		        R.string.drawer_close 
		        ) {
		    
		    /** Called when a drawer has settled in a completely closed state. */
		    public void onDrawerClosed(View view) {
		        super.onDrawerClosed(view);
		        getActionBar().setTitle(mTitle);
		    }
		 
		    /** Called when a drawer has settled in a completely open state. */
		    public void onDrawerOpened(View drawerView) {
		        super.onDrawerOpened(drawerView);
		        getActionBar().setTitle(mDrawerTitle);
		    }
		};
		 
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		 
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		if(savedInstanceState == null){
			selectItem(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
		
		if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
        case R.id.action_settings:
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
	}
	
	@Override
	public void setTitle(CharSequence title) {
	    mTitle = title;
	    getActionBar().setTitle(mTitle);
	}
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        selectItem(position);
	    }
	    
	}
	 
	private void selectItem(int position) {
	    Fragment fragment = null;
	    Bundle bundle=new Bundle();
	    switch (position) {
	    case 0:
	        fragment = new PaymentFragment(getApplicationContext());
	        break;
	    case 1:
	        fragment = new CountFragment();
	        break;
	    default:
	        break;
	    }
	    if (fragment != null) {
	    	bundle.putString("userId", userId);
	    	bundle.putString("urlServlet", urlServlet);
	    	fragment.setArguments(bundle);
	    	
	        FragmentManager fragmentManager = getFragmentManager();
	        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
	        mDrawerList.setItemChecked(position, true);
	        mDrawerList.setSelection(position);
	        setTitle(mNavigationDrawerItemTitles[position]);
	        mDrawerLayout.closeDrawer(mDrawerList);
	    } else {
	        Log.e("MainActivity", "Error in creating fragment");
	    }	    
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
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
				Map json = (Map)jsonParser.parse(scanResult, containerFactory);
	            JSONObject jsonObject = new JSONObject(json);
	            Number total = (Number)jsonObject.get("total");
	            TextView tvTotalVal = (TextView)findViewById(R.id.tvTotalVal);
	            tvTotalVal.setText(total.toString());
	            List products = (List)jsonObject.get("products");
				TextView tvScanResult = (TextView)findViewById(R.id.tvProductsVal);
				tvScanResult.setText(products.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Button buttonPay = (Button)findViewById(R.id.buttonPay);
			buttonPay.setEnabled(true);
		}
	}
	
}
