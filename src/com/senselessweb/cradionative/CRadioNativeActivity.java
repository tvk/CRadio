package com.senselessweb.cradionative;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.senselessweb.cradionative.radio.Radio;

public class CRadioNativeActivity extends Activity 
{
	
	private Radio radio;
	
    /** 
     * Called when the activity is first created. 
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);
        this.radio = new Radio(this, (TextView) this.findViewById(R.id.display));
    }
    
    /**
     * Is called when any action is performed
     * 
     * @param view The action that triggered this event.
     */
    public void onClick(final View view)
    {
    	switch (view.getId())
    	{
    		case R.id.buttonTogglePlay:
    			this.radio.togglePlayback();
    			break;
    			
    		case R.id.buttonStation1:
    			this.radio.playPreset(1);
    			break;
    		case R.id.buttonStation2:
    			this.radio.playPreset(2);
    			break;
    		case R.id.buttonStation3:
    			this.radio.playPreset(3);
    			break;
    		case R.id.buttonStation4:
    			this.radio.playPreset(4);
    			break;
    		case R.id.buttonStation5:
    			this.radio.playPreset(5);
    			break;
    		case R.id.buttonStation6:
    			this.radio.playPreset(6);
    			break;
    			
    		case R.id.buttonGenreUp:
    			this.radio.nextGenre();
    			break;
    		case R.id.buttonGenreDown:
    			this.radio.previousGenre();
    			break;
    		case R.id.buttonStationUp:
    			this.radio.nextStation();
    			break;
    		case R.id.buttonStationDown:
    			this.radio.previousStation();
    			break;
    			
    	}
    }
}