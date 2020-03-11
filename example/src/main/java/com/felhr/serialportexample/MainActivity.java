package com.felhr.serialportexample;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import java.lang.ref.WeakReference;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
        public int sender = 0;



    /*
         * ESTABLECEMOS LA CONEXION USB
         */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


     // Definición de Variables.
    private UsbService usbService;
    private TextView display;
    private EditText editText;
    private MyHandler mHandler;

    // Lista de comandos para ejecutar cada una de las acciones en los nodos.

    private byte RELAY_TURN_SWITCH_ON = (byte) 0xA1; // Encender Nodo Rele
    private byte RELAY_TURN_SWITCH_OFF = (byte) 0xA2; // Apagar Nodo Rele
    private byte RELAY_READ_VOLTAJE_CURRENT = (byte) 0xA6; // Leer corriente nodo rele
    private byte LOGGER_READ_ALL= (byte) 0xD4; // Lectura RMS Nodo RMS
    private byte SENSOR_TURN_SW1_ON =(byte) 0xb6; // Encender Salida1 transistor nodo asen
    private byte SENSOR_TURN_SW1_OFF =(byte) 0xb7; // Apagar salida1 transistor nodo Asn
    private byte SENSOR_TURN_SW2_ON =(byte) 0xb8; // Encender salida2 transistor nodo Asen
    private byte SENSOR_TURN_SW2_OFF =(byte) 0xb9; // Apagar salida2 transistor Noso Asen
    private byte SENSOR_TURN_OPTO1_ON =(byte) 0xba; // Encender Salida1 Optocoplada Nodo Asen
    private byte SENSOR_TURN_OPTO1_OFF =(byte) 0xbb; // Apagar Salida1 Optocoplada Nodo Asen
    private byte SENSOR_TURN_OPTO2_ON =(byte) 0xbc; //  Encender Salida2 Optocoplada Nodo Asen
    private byte SENSOR_TURN_OPTO2_OFF =(byte) 0xbd; // Apagar Salida2 Optocoplada Nodo Asen
    private byte SENSOR_READ_ALL =(byte) 0xc1; // Lectura de sensores Noso Asen
    private byte RS485_READ_ALL =(byte) 0xa9; // Lectura de comunicacion 485



    private boolean relayState = false;
    private boolean swstate = false;




    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

     /// **** Instancia para  ejecutar cada acción de lectura en botones ** ////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new MyHandler(this);

        display = (TextView) findViewById(R.id.textView1);
        editText = (EditText) findViewById(R.id.editText1);
        Button sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("")) {
                    String data = editText.getText().toString();
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        //display.append(data);
                        usbService.write(data.getBytes());

                    }
                }
            }
        });

        // (byte) 0x7e, (byte)0x2, (byte)0x01, RELAY_TURN_SWITCH_ON};
        /// El 0x7e llama a los Nodos,
        // 0x02 establece el No de Byte a enviar,
        // 0x01 Establece el No de nodo a recibir, Seguido por la acción programada


        Button bt =  (Button)findViewById(R.id.button1);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;
                if (relayState) {
                    data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x01, RELAY_TURN_SWITCH_ON};   /// El 0x7e llama a los nodos, 0x02 establece el No de Byte a enviar, 0x01 Establece el No de nodo a recibir, seguido por la acción programada
                    relayState = false;
                } else {
                    data = new byte[]{(byte) 0x7e, (byte)0x2, (byte)0x01, RELAY_TURN_SWITCH_OFF};
                    relayState = true;
                }

                usbService.write(data);
            }
        });

        Button bt2 =  (Button)findViewById(R.id.button2);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;
                if (relayState) {
                    data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x02, RELAY_TURN_SWITCH_ON};
                    relayState = false;
                } else {
                    data = new byte[]{(byte) 0x7e, (byte)0x2, (byte)0x02, RELAY_TURN_SWITCH_OFF};
                    relayState = true;
                }

                usbService.write(data);
            }
        });

        Button bt3 =  (Button)findViewById(R.id.button3);
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;
                if (relayState) {
                    data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x03, RELAY_TURN_SWITCH_ON};
                    relayState = false;
                } else {
                    data = new byte[]{(byte) 0x7e, (byte)0x2, (byte)0x03, RELAY_TURN_SWITCH_OFF};
                    relayState = true;
                }

                usbService.write(data);
            }
        });

        Button bt4 =  (Button)findViewById(R.id.button4);
        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;
                if (relayState) {
                    data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x04, RELAY_TURN_SWITCH_ON};
                    relayState = false;
                } else {
                    data = new byte[]{(byte) 0x7e, (byte)0x2, (byte)0x04, RELAY_TURN_SWITCH_OFF};
                    relayState = true;
                }

                usbService.write(data);
            }
        });


        Button bt5 =  (Button)findViewById(R.id.button5);
        bt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;
                if (relayState) {
                    data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x05, RELAY_TURN_SWITCH_ON};
                    relayState = false;
                } else {
                    data = new byte[]{(byte) 0x7e, (byte)0x2, (byte)0x05, RELAY_TURN_SWITCH_OFF};
                    relayState = true;
                }

                usbService.write(data);
            }
        });

        Button bt6 =  (Button)findViewById(R.id.button6);
        bt6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;
                data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x01, RELAY_READ_VOLTAJE_CURRENT};
                usbService.write(data);
            }
        });

        Button bt7 =  (Button)findViewById(R.id.button7);
        bt7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;

                data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x02, RELAY_READ_VOLTAJE_CURRENT};
                usbService.write(data);
            }
        });

        Button bt8 =  (Button)findViewById(R.id.button8);
        bt8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;

                data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x03, RELAY_READ_VOLTAJE_CURRENT};
                usbService.write(data);
            }
        });

        Button bt9 =  (Button)findViewById(R.id.button9);
        bt9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;

                data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x04, RELAY_READ_VOLTAJE_CURRENT};
                usbService.write(data);
            }
        });

        Button bt10 =  (Button)findViewById(R.id.button10);
        bt10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;

                data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x05, RELAY_READ_VOLTAJE_CURRENT};
                usbService.write(data);
            }
        });


        Button bt11 =  (Button)findViewById(R.id.button11);
        bt11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;

                data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x06, LOGGER_READ_ALL};
                usbService.write(data);
            }
        });

        Button bt12 =  (Button)findViewById(R.id.button12);
        bt12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;

                data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x07, LOGGER_READ_ALL};
                usbService.write(data);
            }
        });

        Button bt13 =  (Button)findViewById(R.id.button13);
        bt13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;

                data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x08, SENSOR_READ_ALL};
                usbService.write(data);
            }
        });

        Button bt14 =  (Button)findViewById(R.id.button14);
        bt14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;
                if (swstate) {
                    data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x08, SENSOR_TURN_SW1_ON};
                    swstate = false;
                } else {
                    data = new byte[]{(byte) 0x7e, (byte)0x2, (byte)0x08, SENSOR_TURN_SW1_OFF};
                    swstate = true;
                }

                usbService.write(data);
            }
        });

        Button bt15 =  (Button)findViewById(R.id.button15);
        bt15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;
                if (swstate) {
                    data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x08, SENSOR_TURN_SW2_ON};
                    swstate = false;
                } else {
                    data = new byte[]{(byte) 0x7e, (byte)0x2, (byte)0x08, SENSOR_TURN_SW2_OFF};
                    swstate = true;
                }

                usbService.write(data);
            }
        });

        Button bt16 =  (Button)findViewById(R.id.button16);
        bt16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;
                if (swstate) {
                    data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x08, SENSOR_TURN_OPTO1_ON};
                    swstate = false;
                } else {
                    data = new byte[]{(byte) 0x7e, (byte)0x2, (byte)0x08, SENSOR_TURN_OPTO1_OFF};
                    swstate = true;
                }

                usbService.write(data);
            }
        });

        Button bt17 =  (Button)findViewById(R.id.button17);
        bt17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;
                if (swstate) {
                    data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x08, SENSOR_TURN_OPTO2_ON};
                    swstate = false;
                } else {
                    data = new byte[]{(byte) 0x7e, (byte)0x2, (byte)0x08, SENSOR_TURN_OPTO2_OFF};
                    swstate = true;
                }

                usbService.write(data);
            }
        });

        Button bt18 =  (Button)findViewById(R.id.button18);
        bt18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;

                data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x09, SENSOR_READ_ALL};
                usbService.write(data);
            }
        });

        Button bt19 =  (Button)findViewById(R.id.button19);
        bt19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;
                if (swstate) {
                    data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x09, SENSOR_TURN_SW1_ON};
                    swstate = false;
                } else {
                    data = new byte[]{(byte) 0x7e, (byte)0x2, (byte)0x09, SENSOR_TURN_SW1_OFF};
                    swstate = true;
                }

                usbService.write(data);
            }
        });

        Button bt20 =  (Button)findViewById(R.id.button20);
        bt20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;
                if (swstate) {
                    data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x09, SENSOR_TURN_SW2_ON};
                    swstate = false;
                } else {
                    data = new byte[]{(byte) 0x7e, (byte)0x2, (byte)0x09, SENSOR_TURN_SW2_OFF};
                    swstate = true;
                }

                usbService.write(data);
            }
        });

        Button bt21 =  (Button)findViewById(R.id.button21);
        bt21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;
                if (swstate) {
                    data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x09, SENSOR_TURN_OPTO1_ON};
                    swstate = false;
                } else {
                    data = new byte[]{(byte) 0x7e, (byte)0x2, (byte)0x09, SENSOR_TURN_OPTO1_OFF};
                    swstate = true;
                }

                usbService.write(data);
            }
        });

        Button bt22 =  (Button)findViewById(R.id.button22);
        bt22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;
                if (swstate) {
                    data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0x09, SENSOR_TURN_OPTO2_ON};
                    swstate = false;
                } else {
                    data = new byte[]{(byte) 0x7e, (byte)0x2, (byte)0x09, SENSOR_TURN_OPTO2_OFF};
                    swstate = true;
                }

                usbService.write(data);
            }
        });

        Button bt23 =  (Button)findViewById(R.id.button23);
        bt23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;

                data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0xa, RS485_READ_ALL};
                usbService.write(data);
            }
        });

        Button bt24 =  (Button)findViewById(R.id.button24);
        bt24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;

                data = new byte[]{ (byte) 0x7e, (byte)0x2, (byte)0xb, RS485_READ_ALL};
                usbService.write(data);
            }
        });


    }

    // *****  Fin de la instancia *** ///
    @Override
    public void onResume() {
        super.onResume();
       // setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
      //  unregisterReceiver(mUsbReceiver);
     //   unbindService(usbConnection);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;

//                    mActivity.get().display.setText("data:"+data+"; n"+n);
                    if (data != null) {
                        if (data.length() > 0) {
                            String arrayString[] = data.split(",");
                             mActivity.get().display.setText("data:" + data + ";");
                        }
                        break;
                    }
            }
        }
    }
}