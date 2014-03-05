package se.ltu.trafikgeneratorcoap;

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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void sendData(View view) {
		Intent intent = new Intent(this, SendDataInput.class);
		startActivityForResult(intent, ResultType.SEND_DATA.index());
	}
	
	public void recieveData(View view) {
		/*Intent intent = new Intent(this, ReceiveDataInput.class);
		startActivityForResult(intent, ResultType.RECEIVE_DATA.index());*/
	}
	
	public void exit(View view){
        finish();
        System.exit(0);
	}
		
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	if(requestCode == ResultType.SEND_DATA.index())
    	{
    		if(resultCode == RESULT_OK)
    		{
    			Log.d("Main", "Sending done!");
                // create a Dialog component
                final Dialog dialog = new Dialog((Context)this);
 
                //tell the Dialog to use the dialog.xml as it's layout description
                dialog.setContentView(R.layout.dialog_ok);
                dialog.setTitle("Sending to Server");
 
                TextView txt = (TextView) dialog.findViewById(R.id.txt);
 
                //xxx change this to something useful
                txt.setText("Packets : 10/10 \n" +
                			"Time : 10 sec");
                	
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
    	
    	if(requestCode == ResultType.RECEIVE_DATA.index())
    	{
    		if(resultCode == RESULT_OK)
    		{
    			Log.d("Main", "Receiving done!");
                // create a Dialog component
                final Dialog dialog = new Dialog((Context)this);
 
                //tell the Dialog to use the dialog.xml as it's layout description
                dialog.setContentView(R.layout.dialog_ok);
                dialog.setTitle("Receiving from Server");
 
                TextView txt = (TextView) dialog.findViewById(R.id.txt);
 
                //xxx change this to something useful
                txt.setText("Packets : 10/10 \n" +
                			"Time : 10 sec");
                	
                Button dialogButton = (Button) dialog.findViewById(R.id.AddFile);
 
                dialogButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
    		}
    	}
    }
}

