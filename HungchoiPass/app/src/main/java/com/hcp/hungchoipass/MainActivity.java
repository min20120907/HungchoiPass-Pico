package com.hcp.hungchoipass;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.UUID;
public class MainActivity extends Activity  {

  Button btnOn, btnOff;
  TextView txtArduino, txtString, txtStringLength, sensorView0,sensorView5, sensorView1, sensorView2, sensorView3, sensorView4;
  Handler bluetoothIn;

  final int handlerState = 0;        				 //used to identify handler message
  private BluetoothAdapter btAdapter = null;
  private BluetoothSocket btSocket = null;
  private StringBuilder recDataString = new StringBuilder();

  private ConnectedThread mConnectedThread;
/*    HttpClient client;
    HttpGet get;
    HttpResponse response;
    String _url = "http://120.126.84.27/update_current.php?";
    String result;
    StringBuilder stringBuf;
    HttpEntity resEntity;*/
  // SPP UUID service - this should work for most devices
  private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

  // String for MAC address
  private static String address;
    Main2Activity db =new Main2Activity(this);
    Handler customHandler = new Handler();

    Runnable updateTimerThread = new Runnable() {//counting duration

        @Override

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis()-startTime;

            updateTime = timeSwapBuff+timeInMilliseconds;

            int secs=(int)(updateTime/1000);

            int mins=secs/60;
            int hours=mins/60;
            secs%=60;

            int milliseconds=(int)(updateTime%1000);

            sensorView5.setText(getResources().getString(R.string.time_spent)+"\n"+hours+":"+mins+":"+String.format("%02d",secs)+":"

                    +String.format("%03d",milliseconds));

            customHandler.postDelayed(this,0);



        }

    };




    long startTime=0L,timeInMilliseconds=0L,timeSwapBuff=0L,updateTime=0L;

@Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);
    startTime = SystemClock.uptimeMillis();



    customHandler.postDelayed(updateTimerThread,0);




    //Link the buttons and textViews to respective views

    getActionBar().setLogo(R.drawable.ic_launcher);
    getActionBar().setDisplayUseLogoEnabled(true);
    getActionBar().setHomeButtonEnabled(true);
    getActionBar().setDisplayHomeAsUpEnabled(true);
    txtString = (TextView) findViewById(R.id.txtString);
    txtStringLength = (TextView) findViewById(R.id.testView1);
    sensorView0 = (TextView) findViewById(R.id.sensorView0);
    sensorView1 = (TextView) findViewById(R.id.sensorView1);
    sensorView2 = (TextView) findViewById(R.id.sensorView2);
    sensorView3 = (TextView) findViewById(R.id.sensorView3);
    sensorView4 = (TextView) findViewById(R.id.sensorView4);
    sensorView5 = (TextView) findViewById(R.id.sensorView5);
    bluetoothIn = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == handlerState) {										//if message is what we want
            	String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                recDataString.append(readMessage);      								//keep appending to string until ~
                int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                if (endOfLineIndex > 0) {                                           // make sure there data before ~
                    String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                //    txtString.setText("Data Received = " + dataInPrint);
                    int dataLength = dataInPrint.length();							//get length of data received
                 //   txtStringLength.setText("String Length = " + String.valueOf(dataLength));
                    NumberFormat formatter ;
                    formatter = new DecimalFormat(".00");
                    NumberFormat formatter1 ;
                    formatter1 = new DecimalFormat(".000000");
                    NumberFormat formatter2;
                    formatter2 = new DecimalFormat("0.00000000");


                    if (recDataString.charAt(0) == '#')								//if it starts with # we know it is what we are looking for
                    {
                        String current = recDataString.substring(1,dataLength-9);             //get sensor value from string between indices 1-5
                        String cardnumber = recDataString.substring(Math.max(1, dataInPrint.length() - 8)).replaceAll("[~]","");            //same again...
                    Cursor mcursor = db.getCurrent(cardnumber);

                    int numRows = mcursor.getCount();
                    float i = 0;
                    if (numRows > 0)
                    {
                        mcursor.moveToFirst();
                        while (numRows>0) // or for loop
                        {
                            String strName = mcursor.getString(0);
                            i = mcursor.getFloat(0);
                            numRows--;
                        }
                    }
                    float current2= Float.parseFloat(current)+i;
/*                        stringBuf = new StringBuilder(_url);
                        stringBuf.append("cardnumber="+cardnumber+"&cardbalance="+formatter1.format(current2/3600*0.00000714)+"&");
                        get = new HttpGet(stringBuf.toString());
                        try {
                            response = client.execute(get);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        resEntity = response.getEntity();*/
                    db.updateInfo(cardnumber,Float.toString(current2));
                    	sensorView0.setText(getResources().getString(R.string.cardnumber) +"\n"+ cardnumber );	//update the textviews with sensor values
                    	sensorView1.setText(getResources().getString(R.string.current) + "\n"+current + "mA");

                        if(current2>0){
                            sensorView2.setText(getResources().getString(R.string.electricity)+"\n" + formatter.format(current2/3600) +"mAh");
                        }else{
                            sensorView2.setText(getResources().getString(R.string.electricity)+"\n" +"0.00 mAh");
                        }
                        sensorView3.setText(getResources().getString(R.string.cardbalance)+"\n"+formatter1.format(current2/3600*0.00000714));
                        sensorView4.setText(getResources().getString(R.string.gain)+"\n"+formatter2.format(Float.parseFloat(current)/3600*0.00000714));
                    mcursor.close();
                    }

                    recDataString.delete(0, recDataString.length()); 					//clear all string data
                   // strIncom =" ";
                    dataInPrint = " ";
                }
            }
        }
    };

    btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
    checkBTState();



  }


  private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

      return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
      //creates secure outgoing connecetion with BT device using UUID
  }

  @Override
  public void onResume() {
    super.onResume();

    //Get MAC address from DeviceListActivity via intent
    Intent intent = getIntent();
      //create device and set the MAC address

    //Get the MAC address from the DeviceListActivty via EXTRA
    address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
      BluetoothDevice device = btAdapter.getRemoteDevice(address);
    //create device and set the MAC address
      try {
          btSocket = createBluetoothSocket(device);
      } catch (IOException e) {
          Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
      }

    // Establish the Bluetooth socket connection.
    try
    {
      btSocket.connect();
    } catch (IOException e) {
      try
      {
        btSocket.close();
      } catch (IOException e2)
      {
    	//insert code to deal with this
      }
    }
    mConnectedThread = new ConnectedThread(btSocket);
    mConnectedThread.start();

    //I send a character when resuming.beginning transmission to check device is connected
    //If it is not an exception will be thrown in the write method and finish() will be called
    mConnectedThread.write("x");
  }

  @Override
  public void onPause()
  {
    super.onPause();
    try
    {
    //Don't leave Bluetooth sockets open when leaving activity
      btSocket.close();
    } catch (IOException e2) {
    	//insert code to deal with this
    }
  }

 //Checks that the Android device Bluetooth is available and prompts to be turned on if off
  private void checkBTState() {

    if(btAdapter==null) {
    	Toast.makeText(getBaseContext(), R.string.notsupport, Toast.LENGTH_LONG).show();
    } else {
      if (btAdapter.isEnabled()) {
      } else {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 1);
      }
    }
  }

  //create new class for connect thread
  private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
            	//Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;


            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
            	//if you cannot write, close the application
            	Toast.makeText(getBaseContext(), R.string.connect_failed, Toast.LENGTH_LONG).show();
            	finish();

              }
        	}
    	}
}

