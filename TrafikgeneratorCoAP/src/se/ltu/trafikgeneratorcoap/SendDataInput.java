package se.ltu.trafikgeneratorcoap;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.net.Inet4Address;
import se.ltu.trafikgeneratorcoap.R;

public class SendDataInput extends AbstractActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_data_input);
		TextView infoField = (TextView) findViewById(R.id.Error);
		infoField.setText("Add a file!");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		ActionBar act = getActionBar();
		act.setDisplayShowHomeEnabled(false);
		act.setDisplayShowTitleEnabled(false);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.send_input, menu);
	    return super.onCreateOptionsMenu(menu);
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
		boolean validIP = false;
		try {
			Inet4Address.getByName(ipString);
			if(!ipString.equals(""))
				ip[totalConfigs] = ipString;
			validIP = true;
		} catch (Exception e) {
			TextView infoField = (TextView) findViewById(R.id.Error);
			infoField.setText("IP-Address not valid");
		}
		
		if (validIP && filePath[totalConfigs] != null) 
		{
			totalConfigs++;
			totalConfigs = Math.max(totalConfigs, 0);
			totalConfigs = Math.min(totalConfigs, length-1);
            addFileDialog(this);
		}
		else if((filePath[totalConfigs] == null) && totalConfigs != 0)
		{
			continueDialog(this);
		}
	}
	
	public void load(View view){
		Intent intent = new Intent(this, AndroidExplorer.class);
		startActivityForResult(intent, ResultType.LOAD_FILE.index());
	}
	
	public void remove(View view){
		if(totalConfigs != 0)
			removeFileDialog(this);
	}
	
	private void addFileDialog(final SendDataInput context){
		final Dialog dialog = new Dialog((Context)this);
        dialog.setContentView(R.layout.dialog_add_file);
        dialog.setTitle("Add file?");
        TextView txt = (TextView) dialog.findViewById(R.id.txt);
        txt.setText("Add another file?");
        	
        Button dialogButtonAddFile = (Button) dialog.findViewById(R.id.AddFile);
        dialogButtonAddFile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
    			TextView infoField = (TextView) findViewById(R.id.Error);
    			infoField.setText("Add another file!");
                dialog.dismiss();
            }
        });
        
        Button dialogButtonContinue = (Button) dialog.findViewById(R.id.Continue);
        dialogButtonContinue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
    		    Intent intent = new Intent(context, SendData.class);
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
    		    startActivityForResult(intent, ResultType.SEND_DATA.index());
            }
        });
        
        dialog.show();
	}
	
	private void continueDialog(final SendDataInput context){
        final Dialog dialog = new Dialog((Context)this);
        dialog.setContentView(R.layout.dialog_continue);
        dialog.setTitle("Are you sure?");
        TextView txt = (TextView) dialog.findViewById(R.id.txt);
        txt.setText("Continue without adding a file?");
        
        Button dialogButtonContinue = (Button) dialog.findViewById(R.id.Continue);
        dialogButtonContinue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
    		    Intent intent = new Intent(context, SendData.class);
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
    		    startActivityForResult(intent, ResultType.SEND_DATA.index());
            }
        });
        
        Button dialogButtonCancel = (Button) dialog.findViewById(R.id.Cancel);
        dialogButtonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
	}
	
	private void removeFileDialog(final SendDataInput context){
        final Dialog dialog = new Dialog((Context)this);
        dialog.setContentView(R.layout.dialog_remove_file);
        dialog.setTitle("Are you sure?");
        TextView txt = (TextView) dialog.findViewById(R.id.txt);
        txt.setText("Remove the latest file added?");
        
        Button dialogButtonRemoveFile = (Button) dialog.findViewById(R.id.RemoveFile);
        dialogButtonRemoveFile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
    			totalConfigs--;
    			totalConfigs = Math.max(totalConfigs, 0);
    			totalConfigs = Math.min(totalConfigs, length-1);
            	
    			TextView infoField = (TextView) findViewById(R.id.Error);
    			infoField.setText(fileName[totalConfigs] + " Removed!");
    			
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
            	probingRate[totalConfigs] = null;
            	dialog.dismiss();
            }
        });
        
        Button dialogButtonCancel = (Button) dialog.findViewById(R.id.Cancel);
        dialogButtonCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
    	if(requestCode == ResultType.SEND_DATA.index())
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

