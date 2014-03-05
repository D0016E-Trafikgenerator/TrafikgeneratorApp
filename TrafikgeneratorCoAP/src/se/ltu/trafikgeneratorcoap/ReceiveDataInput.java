package se.ltu.trafikgeneratorcoap;

import java.net.Inet4Address;

import se.ltu.trafikgeneratorcoap.R;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ReceiveDataInput extends AbstractActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receive_data_input);
		TextView infoField = (TextView) findViewById(R.id.Error);
		infoField.setText("Add a file!");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recieve_input, menu);
		return true;
	}
	
	private int length = 10;
	private int totalConfigs = 0;
	
	private String[] fileName = 	new String[length];
	private String[] filePath = 	new String[length];
	private String[] ip = 			new String[length];
	
	private String[] timeout = 		new String[length];
	private String[] retransmit = 	new String[length];		
	private String[] nStart = 		new String[length];	
	private String[] payloadSize = 	new String[length];	
	private String[] port = 		new String[length];	
	private String[] seconds = 		new String[length];
	private String[] sleep = 		new String[length];
	
	private String[] random = 		new String[length];	
	private String[] probingRate = 	new String[length];
	
	public void next(View view){
		//Port Field
		EditText portField = (EditText) findViewById(R.id.Port);
		String portString = portField.getText().toString();
		if (!portString.equals(""))
			port[totalConfigs] = portString;
	    
		//Time Field
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
		
		EditText probingrateField = (EditText) findViewById(R.id.probingRate);
		String probingrateString = probingrateField.getText().toString();
		if(!probingrateString.equals(""))
			probingRate[totalConfigs] = probingrateString;
		
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
		//Check if IP-Address is valid
		boolean validIP = false;
		try {
			Inet4Address.getByName(ipString);
			ip[totalConfigs] = ipString;
			validIP = true;
		} catch (Exception e) {
			TextView infoField = (TextView) findViewById(R.id.Error);
			infoField.setText("IP-Address not valid");
		}
		
        final ReceiveDataInput th = this;
	
		if (validIP && !(filePath[totalConfigs] == null)) 
		{
			totalConfigs++;
			totalConfigs = Math.max(totalConfigs, 0);
			totalConfigs = Math.min(totalConfigs, length-1);
			
            final Dialog dialog = new Dialog((Context)this);
            dialog.setContentView(R.layout.dialog_yesno);
            dialog.setTitle("More files?");
            TextView txt = (TextView) dialog.findViewById(R.id.txt);
            txt.setText("Do you want to add another file?");
            	
            Button dialogButtonYes = (Button) dialog.findViewById(R.id.AddFile);
            dialogButtonYes.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
        			TextView infoField = (TextView) findViewById(R.id.Error);
        			infoField.setText("Add another file!");
                    dialog.dismiss();
                }
            });
            
            Button dialogButtonNo = (Button) dialog.findViewById(R.id.Cont);
            dialogButtonNo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
        		    Intent intent = new Intent(th, SendData.class);
        		    intent.putExtra("timeout", timeout);
        		    intent.putExtra("random", random);
        		    intent.putExtra("retransmit", retransmit);
        		    intent.putExtra("nStart", nStart);
        		    intent.putExtra("probingRate", probingRate);
        		    intent.putExtra("payloadSize", payloadSize);
        		    intent.putExtra("filePath", filePath);
        		    intent.putExtra("port", port);
        			intent.putExtra("seconds", seconds);
        		    intent.putExtra("ip", ip);
        		    intent.putExtra("sleep", sleep);
        		    intent.putExtra("totalConfigs", totalConfigs);
                    dialog.dismiss();
        		    startActivityForResult(intent, ResultType.RECEIVE_DATA.index());
                }
            });
            
            dialog.show();
		}
		else if((filePath[totalConfigs] == null))
		{
            final Dialog dialog = new Dialog((Context)this);
            dialog.setContentView(R.layout.dialog_yesno);
            dialog.setTitle("Are you sure?");
            TextView txt = (TextView) dialog.findViewById(R.id.txt);
            txt.setText("Do you want to continue without adding a file?");
            
            Button dialogButtonYes = (Button) dialog.findViewById(R.id.AddFile);
            dialogButtonYes.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
        		    Intent intent = new Intent(th, SendData.class);
        		    intent.putExtra("timeout", timeout);
        		    intent.putExtra("random", random);
        		    intent.putExtra("retransmit", retransmit);
        		    intent.putExtra("nStart", nStart);
        		    intent.putExtra("probingRate", probingRate);
        		    intent.putExtra("payloadSize", payloadSize);
        		    intent.putExtra("filePath", filePath);
        		    intent.putExtra("port", port);
        			intent.putExtra("seconds", seconds);
        		    intent.putExtra("ip", ip);
        		    intent.putExtra("sleep", sleep);
        		    intent.putExtra("totalConfigs", totalConfigs);
                    dialog.dismiss();
        		    startActivityForResult(intent, ResultType.RECEIVE_DATA.index());
                }
            });
            
            Button dialogButtonNo = (Button) dialog.findViewById(R.id.Cont);
            dialogButtonNo.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            
		}
		validIP = false;
	}
	
	public void load(View view){
		Intent intent = new Intent(this, AndroidExplorer.class);
		startActivityForResult(intent, ResultType.LOAD_FILE.index());
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	if(requestCode == ResultType.LOAD_FILE.index())
    	{
    		if(resultCode == RESULT_OK)
    		{
    			filePath[totalConfigs] = data.getStringExtra("path");
    			fileName[totalConfigs] = data.getStringExtra("name");
    			TextView infoField = (TextView) findViewById(R.id.Error);
    			infoField.setText("Config file:  " + fileName[totalConfigs]);
    		}
    	}
    	if(requestCode == ResultType.RECEIVE_DATA.index())
    	{
    		if(resultCode == RESULT_OK)
    		{
    			setResult(RESULT_OK);
    			finish();
    		}
    	}
    }
}
