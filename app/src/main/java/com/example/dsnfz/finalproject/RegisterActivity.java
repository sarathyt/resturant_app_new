package com.example.dsnfz.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class RegisterActivity extends AppCompatActivity {

    private Socket socket;
    private MsgEncoding msgEncoding =new MsgEncoding();
    private MsgDecoding msgDecoding=new MsgDecoding();
    private String response="";
    private boolean isSuccess;
    Customer customer = new Customer();
    EditText etName,etUserId,etPin;
    Button bRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etName = (EditText) findViewById(R.id.NameTextField);
        etUserId = (EditText) findViewById(R.id.userIdTextField);
        etPin = (EditText) findViewById(R.id.pinTextField);


        bRegister = (Button) findViewById(R.id.registerButton);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
//                Intent registrIntent = new Intent(RegisterActivity.this, RegisterActivity.class);
//                RegisterActivity.this.startActivity(registrIntent);
                customer.setCustomerID(etUserId.getText().toString());
                customer.setPassword(etPin.getText().toString());
                customer.setCustomerName(etName.getText().toString());
                if(customer.getCustomerID().substring(0,2).equals("CH")){
                    RegisterActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder adb = new AlertDialog.Builder(RegisterActivity.this);
                            adb.setTitle("Notice");
                            adb.setMessage("Invalid UserID; Should not start as 'CH'");
                            final AlertDialog ad = adb.create();
                            ad.show();
                        }
                    });
                }else {
                    SocketClientSendMsg1Thread socketClientSendMsg1Thread
                            = new SocketClientSendMsg1Thread("192.168.0.8", 8080,
                            msgEncoding.EncodingMsg1("R", customer));
                    socketClientSendMsg1Thread.start();
                }
            }
        });

    }

    private class SocketClientSendMsg1Thread extends Thread{
        String dstAddress;
        int dstPort;

        private String Msg1;

        SocketClientSendMsg1Thread(String addr,int port, String msg1){
            dstAddress=addr;
            dstPort=port;
            this.Msg1 = msg1;
        }

        @Override
        public void run(){

            try {
                socket = MyClientSocket.startClientSocket(dstAddress,dstPort);
                OutputStream outputStream;
                outputStream = socket.getOutputStream();
                outputStream.write(Msg1.getBytes("UTF-8"));
                outputStream.flush();
                response += "send message: " + Msg1 + "\n";

//                RegisterActivity.this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        etUserId.setText(response);
//                    }
//                });

                SocketClientRecvMsg2Thread socketClientRecvMsg2Thread
                        =new SocketClientRecvMsg2Thread();
                socketClientRecvMsg2Thread.start();

    /*
     * notice:
     * inputStream.read() will block if no data return
     */
                /*while ((bytesRead = inputStream.read(buffer)) != '.'){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }*/

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
//                response += "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                RegisterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        AlertDialog.Builder adb = new AlertDialog.Builder(RegisterActivity.this);
                        adb.setTitle("Notice");
                        adb.setMessage("Restaurant is Closed!");
                        final AlertDialog ad = adb.create();
                        ad.show();

                    }
                });
//                response += "IOException: " + e.toString();
            }
        }
    }
    private class SocketClientRecvMsg2Thread extends Thread{
        private String Msg2;
        private String flag;
        SocketClientRecvMsg2Thread(){
            Msg2="";
        }

        public void run(){
            try{
                InputStream inputStream = socket.getInputStream();
                InputStreamReader isr=new InputStreamReader(inputStream);
                BufferedReader br=new BufferedReader(isr);
                Msg2=br.readLine();
//                response+= Msg2+"\n";
                isSuccess=msgDecoding.DecodingMsg2(Msg2);


                flag=msgDecoding.flag;
                if(isSuccess&&flag.equals("R")){
                    response+="\n register success,dear "+customer.getCustomerName();
                    //TODO implement the register success

                }
                else if(isSuccess&&flag.equals("S")){
                    customer.setCustomerName(msgDecoding.Msg2Name);
                    response+="\n sign up success,dear "+customer.getCustomerName();
                    //TODO implement the sign up success

                }
                else if(!isSuccess&&flag.equals("R")){
                    response+="\n register failed, dear "+customer.getCustomerName();
                    //TODO implement the register failed;

                }
                else {
                    customer.setCustomerName(msgDecoding.Msg2Name);
                    response+="\n sign up failed, dear"+customer.getCustomerName();
                    //TODO implement the sign up success;

                }

//                RegisterActivity.this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        etUserId.setText(response);
//                    }
//                });

                if(isSuccess){
                    Intent registerIntent = new Intent(RegisterActivity.this, MenuActivity.class);
                    registerIntent.putExtra("ID",customer.getCustomerID());
                    registerIntent.putExtra("Name",customer.getCustomerName());
                    registerIntent.putExtra("Yes","1");
                    RegisterActivity.this.startActivity(registerIntent);
                }
                else {
//                    Toast.makeText();
                }

            }catch(UnknownHostException e){
                e.printStackTrace();
                response+="UnkonwnHostException: "+e.toString();
            }catch(IOException e){
                e.printStackTrace();
                response+="IOException: "+e.toString();
            }
        }

    }
}
