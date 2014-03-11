package se.ltu.trafikgeneratorcoap;

import android.os.Bundle;
import android.app.ActionBar;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import java.net.Inet4Address;
import se.ltu.trafikgeneratorcoap.R;

public class InputData extends AbstractActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data_input);
		Intent type = getIntent();
		thisResultType = type.getIntExtra("ResultType", -1);		
		Intent intent = new Intent(this, AndroidExplorer.class);
		startActivityForResult(intent, ResultType.LOAD_FILE.ordinal());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		ActionBar act = getActionBar();
		act.setDisplayShowHomeEnabled(false);
		act.setDisplayShowTitleEnabled(false);
		getMenuInflater().inflate(R.menu.input, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("InputData", "Options!");
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.AddFile:
	            addFile();
	            return true;
	        case R.id.RemoveFile:
	        	removeFile();
	            return true;
	        case R.id.Continue:
	        	next();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private final int maxConfigs = 10;
	private int totalConfigs = 0;
	
	private int thisResultType;
	
	private String[] fileName = 	new String[maxConfigs];
	private String[] filePath = 	new String[maxConfigs];
	private String[] ip = 			new String[maxConfigs];
	
	private String[] timeout = 		new String[maxConfigs];
	private String[] retransmit = 	new String[maxConfigs];		
	private String[] nStart = 		new String[maxConfigs];	
	private String[] payloadSize = 	new String[maxConfigs];	
	private String[] port = 		new String[maxConfigs];	
	private String[] seconds = 		new String[maxConfigs];
	private String[] sleep = 		new String[maxConfigs];
	
	private String[] random = 		new String[maxConfigs];	
	
	private void next(){
		Log.d("InputData", "Next");
		if(addFieldsToLists() && filePath[totalConfigs] != null){
			//Add one more, since we began counting at 0
			totalConfigs++;
			totalConfigs = Math.max(totalConfigs, 0);
			totalConfigs = Math.min(totalConfigs, maxConfigs-1);
		    Intent intent = new Intent(this, HandleData.class);
		    intent.putExtra("timeout", timeout);
		    intent.putExtra("random", random);
		    intent.putExtra("retransmit", retransmit);
		    intent.putExtra("nStart", nStart);
		    intent.putExtra("payloadSize", payloadSize);
		    intent.putExtra("filePath", filePath);
		    intent.putExtra("port", port);
			intent.putExtra("seconds", seconds);
		    intent.putExtra("ip", ip);
		    intent.putExtra("sleep", sleep);
		    intent.putExtra("totalConfigs", totalConfigs);
		    intent.putExtra("ResultType", thisResultType);
		    startActivityForResult(intent, thisResultType);
		}
		else if(filePath[totalConfigs] == null){
			TextView infoField = (TextView) findViewById(R.id.Error);
			infoField.setText("Add a file please!");
		}
	}
	
	private void removeFile(){
		Log.d("InputData", "RemoveFile");
		if(filePath[totalConfigs] != null){
			TextView infoField = (TextView) findViewById(R.id.Error);
			infoField.setText("Removed #" + (totalConfigs + 1) + ": "  + fileName[totalConfigs]);
			fileName[totalConfigs] = null;
			filePath[totalConfigs] = null;
			ip[totalConfigs] = null;
			timeout[totalConfigs] = null;
			retransmit[totalConfigs] = null;		
			nStart[totalConfigs] = null;
			payloadSize[totalConfigs] = null;
			port[totalConfigs] = null;
			seconds[totalConfigs] = null;
			sleep[totalConfigs] = null;
			random[totalConfigs] = null;
			totalConfigs--;
			totalConfigs = Math.max(totalConfigs, 0);
			totalConfigs = Math.min(totalConfigs, maxConfigs-1);
		}
	}
	
	private void addFile(){
		Log.d("InputData", "AddFile");
		
		if(filePath[totalConfigs] == null)
		{
			Intent intent = new Intent(this, AndroidExplorer.class);
			startActivityForResult(intent, ResultType.LOAD_FILE.ordinal());
		}
		else if (addFieldsToLists()) 
		{
			totalConfigs++;
			totalConfigs = Math.max(totalConfigs, 0);
			totalConfigs = Math.min(totalConfigs, maxConfigs-1);
			Intent intent = new Intent(this, AndroidExplorer.class);
			startActivityForResult(intent, ResultType.LOAD_FILE.ordinal());
		}
	}
	
	private boolean addFieldsToLists(){
		Log.d("InputData", "AddFields");
		EditText portField = (EditText) findViewById(R.id.Port);
		String portString = portField.getText().toString();
		if (!portString.equals(""))
			port[totalConfigs] = portString;
	    
		EditText timeField = (EditText) findViewById(R.id.Time);
		String timeString = timeField.getText().toString();
		if (!timeString.equals(""))
			seconds[totalConfigs] = timeString;
		
		EditText timeoutField = (EditText) findViewById(R.id.timeout);
		String timeoutString = timeoutField.getText().toString();
		if(!timeoutString.equals(""))
			timeout[totalConfigs] = timeoutString;
		
		EditText randomField = (EditText) findViewById(R.id.random);
		String randomString = randomField.getText().toString();
		if(!randomString.equals(""))
			random[totalConfigs] = randomString;
		
		EditText retransmittField = (EditText) findViewById(R.id.retransmitt);
		String retransmittString = retransmittField.getText().toString();
		if(!retransmittString.equals(""))
			retransmit[totalConfigs] = retransmittString;
		
		EditText nstartField = (EditText) findViewById(R.id.nStart);
		String nstartString = nstartField.getText().toString();
		if(!nstartString.equals(""))
			nStart[totalConfigs] = nstartString;
		
		EditText payloadsizeField = (EditText) findViewById(R.id.payloadSize);
		String payloadsizeString = payloadsizeField.getText().toString();
		if(!payloadsizeString.equals(""))
			payloadSize[totalConfigs] = payloadsizeString;
		
		EditText sleepField = (EditText) findViewById(R.id.sleep);
		String sleepString = sleepField.getText().toString();
		if(!sleepString.equals(""))
			sleep[totalConfigs] = sleepString;
	
		EditText ipField = (EditText) findViewById(R.id.IPAddress);
		String ipString = ipField.getText().toString();
		try {
			Inet4Address.getByName(ipString);
			if(!ipString.equals(""))
				ip[totalConfigs] = ipString;
			return true;
		} catch (Exception e) {
			TextView infoField = (TextView) findViewById(R.id.Error);
			infoField.setText("IP-Address not valid");
			return false;
		}
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == ResultType.LOAD_FILE.ordinal())
    	{
    		if(resultCode == RESULT_OK)
    		{
    			filePath[totalConfigs] = data.getStringExtra("path");
    			fileName[totalConfigs] = data.getStringExtra("name");
    			TextView infoField = (TextView) findViewById(R.id.Error);
    			infoField.setText("Config file #" + (totalConfigs + 1) + ": "  + fileName[totalConfigs]);
    		}
    		if(resultCode == RESULT_CANCELED)
    		{
    			
    		}
    	}
    	if(requestCode == thisResultType)
    	{
    		if(resultCode == RESULT_OK)
    		{
    			setResult(RESULT_OK);
    			finish();
    		}
    		if(resultCode == RESULT_CANCELED)
    		{
    			setResult(RESULT_CANCELED);
    			finish();
    		}
    	}
    }
}
