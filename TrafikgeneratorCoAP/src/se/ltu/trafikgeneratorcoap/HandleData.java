package se.ltu.trafikgeneratorcoap;

import se.ltu.trafikgeneratorcoap.send.Sending;
import se.ltu.trafikgeneratorcoap.send.TrafficConfig;
import se.ltu.trafikgeneratorcoap.send.Settings;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;

public class HandleData extends AbstractActivity {  
	  
    private String[] ip;
	private String[] port;
	private String[] seconds;
	private String[] filePath;
	private String[] timeout;
	private String[] random;
	private String[] retransmit;
	private String[] nStart;
	private String[] payloadSize;
	private String[] sleep;
    private TrafficConfig[] config;
    
    private ProgressDialog progressDialog;  
	private Intent intent;
	private int totalConfigs;
    private int indexer = 0;
    private int progressbarUpdate = 0;
    
    private int thisResultType;
 
    @Override  
    public void onCreate(Bundle savedInstanceState)  
    {  
		Log.d("HandleData", "Task Started!");
        super.onCreate(savedInstanceState);  
        
        intent = getIntent();
        thisResultType =	intent.getIntExtra("ResultType", -1);
        timeout = 			intent.getStringArrayExtra("timeout");
        retransmit = 		intent.getStringArrayExtra("retransmit");
        nStart = 			intent.getStringArrayExtra("nStart");
	    payloadSize = 		intent.getStringArrayExtra("payloadSize");
	    port = 				intent.getStringArrayExtra("port");
	    seconds = 			intent.getStringArrayExtra("seconds");
        random = 			intent.getStringArrayExtra("random");
	    filePath = 			intent.getStringArrayExtra("filePath");
	    ip = 				intent.getStringArrayExtra("ip");
	    sleep = 			intent.getStringArrayExtra("sleep");
	    totalConfigs =		intent.getIntExtra("totalConfigs", 0);
	    
	    Log.d("HandleData", "Configs: " + totalConfigs);
	    
	    config = new TrafficConfig[totalConfigs];

	    nextTask(indexer);
    }
    
    private void nextTask(int taskIndex)
    {	    
    	Log.d("HandleData", "Creating config from: " + filePath[taskIndex]);
    	config[taskIndex] = new TrafficConfig(TrafficConfig.fileToString(filePath[taskIndex]));
    	
	    if(timeout[taskIndex] != null)
	    	config[taskIndex].setIntegerSetting(Settings.COAP_ACK_TIMEOUT, parseInt(timeout[taskIndex]));
	    if(retransmit[taskIndex] != null)
	    	config[taskIndex].setIntegerSetting(Settings.COAP_MAX_RETRANSMIT, parseInt(retransmit[taskIndex]));
	    if(nStart[taskIndex] != null)
	    	config[taskIndex].setIntegerSetting(Settings.COAP_NSTART, parseInt(nStart[taskIndex]));
	    if(payloadSize[taskIndex] != null)
	    	config[taskIndex].setIntegerSetting(Settings.TRAFFIC_MESSAGESIZE, parseInt(payloadSize[taskIndex]));
	    if(port[taskIndex] != null)
	    	config[taskIndex].setIntegerSetting(Settings.TEST_TESTPORT, parseInt(port[taskIndex]));
	    if(seconds[taskIndex] != null)
	    	config[taskIndex].setIntegerSetting(Settings.TRAFFIC_MAXSENDTIME, parseInt(seconds[taskIndex]));	 
	    
	    if(random[taskIndex] != null)
	    	config[taskIndex].setDecimalSetting(Settings.COAP_ACK_RANDOM_FACTOR, parseFloat(random[taskIndex]));
    	
	    if(ip[taskIndex] != null)
	    	config[taskIndex].setStringSetting(Settings.TEST_SERVER, ip[taskIndex]);
	    
    	new LoadViewTask().execute();
    }
    
    private Float parseFloat(String s)
    {
    	Log.d("parseFloat","parseFloat: " + s);
		try {
			return Float.parseFloat(s);
		} catch (NumberFormatException e) {
			return null;
		}
    }
    
    private Integer parseInt(String s)
    {
    	Log.d("parseInt","parseInt: " + s);
	    try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return null;
		}
    }
    
    private Long parseLong(String s)
    {
    	Log.d("parseLong","parseLong: " + s);
    	try {
			return Long.parseLong(s);
		} catch (NumberFormatException e) {
			return (long) 1000;
		}
    }
    
    private class LoadViewTask extends AsyncTask<Void, Integer, Void>  
    {  
    	private int processNumber;
    	
        private String getMessage(){
        	ResultType type = ResultType.values()[thisResultType];
        	switch(type) {
    	    	case SEND_DATA:
    	    		return "Sending data... Please wait.";
    	    	case RECEIVE_DATA:
    	    		return "Receiving data... Please wait.";
    	    	default:
    	    		return "Error";
        	}
        }
        
        private void pickType() {
        	ResultType type = ResultType.values()[thisResultType];
        	switch(type) {
		    	case SEND_DATA:
						Sending.sendData(config[this.processNumber], getApplicationContext());
		        	try { ;
					} catch (Exception e1) {
						Log.e("HandleData", "Something went terribly wrong in sendData!");
						//Sending.abort(config[this.processNumber]);
					}
		        	break;
		    	case RECEIVE_DATA:
		        	try {
		        		//Receiving!
						//Sending.sendData(config[this.processNumber], getApplicationContext());
					} catch (Exception e1) {
						Log.e("HandleData", "Something went terribly wrong in receiveData!");
					}
		        	break;
		    	default:
		    		break;
        	}
        }
    	
        @Override  
        protected void onPreExecute()  
        {  
        	this.processNumber = indexer++;
        	Log.d("HandleData", "Creating process nr: " + this.processNumber);
        	if(this.processNumber == 0)
        	{
	            progressDialog = new ProgressDialog(HandleData.this);  
	            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
	            progressDialog.setTitle("Loading..."); 
	            progressDialog.setMessage(getMessage());  
	            progressDialog.setCancelable(true);
	            progressDialog.setOnCancelListener(new OnCancelListener() {
	                @Override
	                public void onCancel(DialogInterface dialog) {
	                    cancel(true);
	                }
	            });
	            progressDialog.setIndeterminate(false);  
	            progressDialog.setMax(totalConfigs);   
	            progressDialog.setProgress(0);  
	            progressDialog.show();
        	}
        }  
  
        //The code to be executed in a background thread.  
        @Override  
        protected Void doInBackground(Void... params)  
        {   
    	    Log.d("HandleData", "IP: " + config[this.processNumber].getStringSetting(Settings.TEST_SERVER));
    	    pickType();
        	publishProgress(++progressbarUpdate);
        	Log.d("HandleData", "End of process nr : " + this.processNumber);
        	if(this.processNumber != (totalConfigs-1))
        	{
        		try { Thread.sleep(parseLong(sleep[this.processNumber])); } catch (InterruptedException e) {}
        		nextTask(this.processNumber + 1);
        	}
        	return null;
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... values)  
        {  
            //set the current progress of the progress dialog  
            progressDialog.setProgress(values[0]);  
        }  
        
        @Override
        protected void onCancelled() {
        	Log.d("HandleData", "Task Canceled!");
        	//Sending.abort(config[this.processNumber]);
            progressDialog.dismiss();
			setResult(RESULT_CANCELED);
			finish();
        }
  
        @Override  
        protected void onPostExecute(Void result)  
        {  
			if(progressbarUpdate == (totalConfigs))
			{
	        	Log.d("HandleData", "Task Done!");
	            progressDialog.dismiss();
				setResult(RESULT_OK);
				finish();
			}
        }  
    }  
}  
