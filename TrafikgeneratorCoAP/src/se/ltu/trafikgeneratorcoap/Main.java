package se.ltu.trafikgeneratorcoap;

import java.io.IOException;

import se.ltu.trafikgeneratorcoap.R;
import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class Main extends AbstractActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		try {
			Runtime.getRuntime().exec("su ; echo \"hej\"");
		} catch (IOException e) {
			Log.e("Main", "Could not grant SU.");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void sendData(View view) {
		Intent intent = new Intent(this, InputData.class);
	    intent.putExtra("ResultType", ResultType.SEND_DATA.ordinal());
		startActivityForResult(intent, ResultType.SEND_DATA.ordinal());
	}
	
	public void recieveData(View view) {
		Intent intent = new Intent(this, InputData.class);
	    intent.putExtra("ResultType", ResultType.RECEIVE_DATA.ordinal());
		startActivityForResult(intent, ResultType.RECEIVE_DATA.ordinal());
	}
	
	public void installTCPDump(View view) {
		Intent intent = new Intent(this, AndroidExplorer.class);
		startActivityForResult(intent, ResultType.LOAD_FILE.ordinal());
	}
	
	public void uninstallTCPDump(View view) {
		
	}
	
	public void checkSU(View view) {
		
	}
	
	public void exit(View view){
        finish();
        System.exit(0);
	}
	
	private void install(String string) {
		Log.d("Main", string);
	}
		
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	if(requestCode == ResultType.LOAD_FILE.ordinal())
    	{
    		if(resultCode == RESULT_OK)
    		{
    			install(data.getStringExtra("path"));
    		}
    	}
    	if(requestCode == ResultType.SEND_DATA.ordinal())
    	{
    		if(resultCode == RESULT_OK)
    		{
    			Log.d("Main", "Sending done!");
                final Dialog dialog = new Dialog((Context)this);
 
                dialog.setContentView(R.layout.dialog_ok);
                dialog.setTitle("Sending to Server");
 
                TextView txt = (TextView) dialog.findViewById(R.id.txt);
 
                //xxx change this to something useful
                txt.setText("Sending done!");
                	
                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButton);
 
                dialogButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
    		}
    		if(resultCode == RESULT_CANCELED)
    		{
    			Log.d("Main", "Task Canceled!");
    		}
    	}
    	
    	if(requestCode == ResultType.RECEIVE_DATA.ordinal())
    	{
    		if(resultCode == RESULT_OK)
    		{
    			Log.d("Main", "Receiving done!");
                final Dialog dialog = new Dialog((Context)this);
 
                dialog.setContentView(R.layout.dialog_ok);
                dialog.setTitle("Receiving from Server");
 
                TextView txt = (TextView) dialog.findViewById(R.id.txt);
 
                //xxx change this to something useful
                txt.setText("Receiving done!");
                	
                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButton);
 
                dialogButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
    		}
    		if(resultCode == RESULT_CANCELED)
    		{
    			Log.d("Main", "Task Canceled!");
    		}
    	}
    }
}

