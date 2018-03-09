package my.simple.calculator;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.text.Editable;
import android.view.View;
import android.widget.*;

public class CalcActivity extends Activity {
	Button add, sub, mult, div, but1, but2, but3, but4, but5, but6, but7, but8,	but9, but0, deci, equ, C, CE;
	EditText screen;
	View butVal;
	String operater;
    String display; //this string will later be used to retrieve the contents of the screen object
    Double num1;
    Double num2;
    boolean opIsSet; //boolean to check whether operator is set or not
    boolean onNextPressReset; //checks whether the net calculator press will first clear the screen or not
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calc);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); 
		StrictMode.setThreadPolicy(policy); //required for web service
		//set the declared variables and assign them to the ID's of the views created in graphical design
		screen = (EditText) findViewById(R.id.calcDisplay);
		add = (Button) findViewById(R.id.add);
		sub = (Button) findViewById(R.id.subtract);
		mult = (Button) findViewById(R.id.multiply);
		div = (Button) findViewById(R.id.divide);
		but1 = (Button) findViewById(R.id.Num1);
		but2 = (Button) findViewById(R.id.Num2);
		but3 = (Button) findViewById(R.id.Num3);
		but4 = (Button) findViewById(R.id.Num4);
		but5 = (Button) findViewById(R.id.Num5);
		but6 = (Button) findViewById(R.id.Num6);
		but7 = (Button) findViewById(R.id.Num7);
		but8 = (Button) findViewById(R.id.Num8);
		but9 = (Button) findViewById(R.id.Num9);
		but0 = (Button) findViewById(R.id.Num0);
		deci = (Button) findViewById(R.id.Deci);
		equ = (Button) findViewById(R.id.equals);
		C = (Button) findViewById(R.id.Clear);
		CE = (Button) findViewById(R.id.clearEntry);
		opIsSet = false; //operator is not set yet
		onNextPressReset = false; //do not clear the screen on the next button press yet
	}
	//Massive if statement contained inside this method to determine what's being pressed and what should happen when buttons are pressed
	public void onClick(View v) {
		Button but = (Button) v;	//declare button for the view object clicked
		String butVal = but.getText().toString();	//retrieve the button value
		Editable display = screen.getText();	//retrieve the screen value
		if(v==but1 || v==but2 || v==but3 || v==but4 || v==but5 || v==but6 || v==but7 || v==but8 || v==but9 || v==but0) {
            if(display.toString().equals("0") || onNextPressReset == true){
            	screen.setText(butVal);		//clear the contents of the screen and replace it with the new button event
            	onNextPressReset = false;	//the next press shall not clear the screen now
            }else{
                screen.setText(display + butVal); //concatenate it
            }
        }else if(v == add  || v == sub || v == mult || v == div) {  
        	opIsSet = true; //an operator is pressed so now assign this as true
            onNextPressReset = true; //now the next press should clear the screen
            if(opIsSet == true && operater != null){
            	 num2 = Double.parseDouble((String) screen.getText().toString()); //num1 = checkPassed(num1, operater, num2);  
            	 num1 = Double.parseDouble(calcPHP()); //calculate and set new value asnum1
                 screen.setText(String.valueOf(num1));//display onto screen
                 if(v == add){	//assign the operators
                     operater = "A+"; 
                 }else if(v == sub){
                 	operater = "S";
             	 }else if(v == div){
             		operater = "D";
             	 }else if(v == mult){
             		operater = "M";
             	 }
            }else{
            	num1 = Double.parseDouble((String) screen.getText().toString()); //operator is not set yet so this is the first number                
            	if(v == add){	//assign the operators
                    operater = "A"; 
                }else if(v == sub){
                	operater = "S";
            	}else if(v == div){
            		operater = "D";
            	}else if(v == mult){
            		operater = "M";
            	}
            }
        }else if(v == deci){
            if(display.toString().contains(".")){
                //don't do anything we don't want consecutive dots
            }else{
                screen.setText(display + butVal);// concatenate onto number
            }
        }else if(v == equ){
            onNextPressReset = true; //next number button press should clear the screen
            if(opIsSet == true && operater != null){
            	 num2 = Double.parseDouble((String) screen.getText().toString()); //num1 = checkPassed(num1, operater, num2); //calculate 
            	 num1 = Double.parseDouble(calcPHP()); //calculate via web service the new value will now be assigned num1
                 screen.setText(String.valueOf(num1)); //set the screen value
                 operater = null; //operator is now cleared
                 num2 = null; //num2 is not cleared
            }else{
            	//do nothing since operator isn't set
            }
        }else if(v == C){ 
            screen.setText("0"); //reset everything to its pristine condition
            num1 = null;
            num2 = null;
            operater = null;
            opIsSet = false;
            onNextPressReset = false;
        }else if(v == CE){
        	//clear entry function only clears the most recent entry
			if (operater == null){
				screen.setText("0"); //operator is not set yet so only need to reset screen and empty num1
				num1 = null;
			}else if (operater != null && !display.toString().equals("0")){
				screen.setText("0"); //operator is set so just clear screen and empty num2
		        num2 = null;
		        onNextPressReset = false;
			}
	    }
	}
	//this is my old method used in step 1 and step 2 of the calculator (non web-service)
	public Double checkPassed(Double num1, String operater, Double num2){
        if(operater.equals("A")){
            return num1 + num2;
        }
        if(operater.equals("S")){
            return num1 - num2;
        }
        if(operater.equals("M")){
            return num1 * num2;
        }
        if(operater.equals("D")){
            return num1 / num2;
        }
        return 0.0;
    }
	//(Web service method) This method changes my original operators to match the server operators then retrieves variables from the URL parameters
	public String calcPHP() {
		String response = ""; //Instantiate the response string
		String calcPHP = "http://cs.kent.ac.uk/~iau/calc.php?f=" + operater + "&v1=" + num1.toString() + "&v2=" + num2.toString();
		HttpClient client = new DefaultHttpClient(); //create new client
		HttpGet get = new HttpGet(calcPHP);
		ResponseHandler<String> handler = new BasicResponseHandler();
		try {
			response = client.execute(get, handler); //response handler to return the response
		} catch (Exception e) {
			e.printStackTrace(); // print error into the logCat
		}
		return response; //the returned string value to be parsed as double
	}
}