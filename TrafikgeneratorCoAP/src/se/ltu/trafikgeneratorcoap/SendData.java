package se.ltu.trafikgeneratorcoap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import se.ltu.trafikgeneratorcoap.send.Sending;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.ProgressDialog;
import android.content.Intent;

public class SendData extends AbstractActivity {  
    //A ProgressDialog object  
    private ProgressDialog progressDialog;  
  
	private String ip;
	private int port;
	private int seconds;
	private String fileName = "";
	private int timeout;
	private float random;
	private int retransmitt;
	private int nStart;
	private float probingRate;
	private int payloadSize;
	
	private Intent intent;
	
	FileOutputStream outputStream;
	FileInputStream inputStream;
    
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState)  
    {  
        super.onCreate(savedInstanceState);  
  
        intent = getIntent();
        timeout = parseInt("timeout");
        random = parseFloat("random");
        retransmitt = parseInt("retransmitt");
        nStart = parseInt("nstart");
	    probingRate = parseFloat("probingrate");
	    payloadSize = parseInt("payloadsize");
	    port = parseInt("port");
	    seconds = parseInt("time");
	    
	    fileName = intent.getStringExtra("filename");
	    ip = intent.getStringExtra("ip");
	    
        //Initialize a LoadViewTask object and call the execute() method  
        new LoadViewTask().execute();         
    } 
    
    private float parseFloat(String s)
    {
	    try {
			float floatReturn = Float.parseFloat(intent.getStringExtra(s));
			return floatReturn;
		} catch (NumberFormatException e) {
			return 0.0f;
		}
    }
    
    private int parseInt(String s)
    {
	    try {
			int intReturn = Integer.parseInt(intent.getStringExtra(s));
			return intReturn;
		} catch (NumberFormatException e) {
			return 0;
		}
    }
  
    //To use the AsyncTask, it must be subclassed  
    private class LoadViewTask extends AsyncTask<Void, Integer, Void>  
    {  
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {  
            //Create a new progress dialog  
            progressDialog = new ProgressDialog(SendData.this);  
            //Set the progress dialog to display a horizontal progress bar  
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
            //Set the dialog title to 'Loading...'  
            progressDialog.setTitle("Loading...");  
            //Set the dialog message 
            progressDialog.setMessage("Sending data... Please wait.");  
            //This dialog can't be canceled by pressing the back key  
            progressDialog.setCancelable(true);  
            //This dialog isn't indeterminate  
            progressDialog.setIndeterminate(false);  
            //The maximum number of items is 100  
            progressDialog.setMax(10000);  
            //Set the current progress to zero  
            progressDialog.setProgress(0);  
            //Display the progress dialog  
            progressDialog.show();  
        }  
  
        //The code to be executed in a background thread.  
        @Override  
        protected Void doInBackground(Void... params)  
        {   
        	System.out.println("asdf " + ip);
        	Sending.sendData("");
        	return null;  
        }  
  
        //Update the progress  
        @Override  
        protected void onProgressUpdate(Integer... values)  
        {  
            //set the current progress of the progress dialog  
            progressDialog.setProgress(values[0]);  
        }  
  
        //after executing the code in the thread  
        @Override  
        protected void onPostExecute(Void result)  
        {  
            //close the progress dialog  
            progressDialog.dismiss();  
            //initialize the View
			setResult(RESULT_OK);
			finish();
        }  
    }  
}  
