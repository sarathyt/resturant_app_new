package com.example.dsnfz.finalproject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.UnknownHostException;


public class ClientCommunicationActivity extends Activity {

    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;
    String response="";
    public MyClientSocket socket = null;

    public Customer cus=new Customer();
    public MsgDecoding MD=new MsgDecoding();
    public Order order=new Order();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientcommunication);

        editTextAddress = (EditText)findViewById(R.id.address);
        editTextPort = (EditText)findViewById(R.id.port);
        buttonConnect = (Button)findViewById(R.id.connect);
        buttonClear = (Button)findViewById(R.id.clear);
        textResponse = (TextView)findViewById(R.id.response);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);

        buttonClear.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                textResponse.setText("");

            }});
    }

    View.OnClickListener buttonConnectOnClickListener =
            new View.OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    /*MyClientTask myClientTask = new MyClientTask(
                            editTextAddress.getText().toString(),
                            Integer.parseInt(editTextPort.getText().toString()));
                    myClientTask.execute();*/
                    SocketClientSendMsg1Thread myThread
                            =new SocketClientSendMsg1Thread(editTextAddress.getText().toString(),
                                Integer.parseInt(editTextPort.getText().toString()), "");
                    myThread.start();
                }};

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
                String msgReply = "S,322,2333\n";
                outputStream = socket.getOutputStream();
                outputStream.write(msgReply.getBytes("UTF-8"));
                outputStream.flush();
                response += "send message: " + msgReply + "\n";

                ClientCommunicationActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textResponse.setText(response);
                    }
                });

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
                response += "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response += "IOException: " + e.toString();
            }
        }
    }

    private class SocketClientRecvMsg2Thread extends Thread{
        private String Msg2;
        private String flag;
        private boolean isSuccess;
        SocketClientRecvMsg2Thread(){
            Msg2="";
        }

        public void run(){
            try{
                InputStream inputStream = socket.getInputStream();
                InputStreamReader isr=new InputStreamReader(inputStream);
                BufferedReader br=new BufferedReader(isr);
                Msg2=br.readLine();
                response+= Msg2+"\n";
                isSuccess=MD.DecodingMsg2(Msg2);


                flag=MD.flag;
                if(isSuccess&&flag.equals("R")){
                    response+="\n register success,dear "+cus.getCustomerName();
                    //TODO implement the register success

                }
                else if(isSuccess&&flag.equals("S")){
                    cus.setCustomerName(MD.Msg2Name);
                    response+="\n sign in success,dear "+cus.getCustomerName();
                    //TODO implement the sign up success

                }
                else if(!isSuccess&&flag.equals("R")){
                    response+="\n register failed, dear "+cus.getCustomerName();
                    //TODO implement the register failed;

                }
                else {
                    cus.setCustomerName(MD.Msg2Name);
                    response+="\n sign up success, dear"+cus.getCustomerName();
                    //TODO implement the sign up success;

                }

                ClientCommunicationActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textResponse.setText(response);
                    }
                });



                SocketClientSendMsg3Thread socketClientSendMsg3Thread
                        =new SocketClientSendMsg3Thread();
                socketClientSendMsg3Thread.start();

            }catch(UnknownHostException e){
                e.printStackTrace();
                response+="UnkonwnHostException: "+e.toString();
            }catch(IOException e){
                e.printStackTrace();
                response+="IOException: "+e.toString();
            }
        }

    }

    private class SocketClientSendMsg3Thread extends Thread{
        private String Msg3="1234,1000,322,10/11/2017,burger,30,chicken,10\n";

        SocketClientSendMsg3Thread(){
            ;
        }

        public void run(){
            try{
                OutputStream outputStream=socket.getOutputStream();
                outputStream.write(Msg3.getBytes("UTF-8"));
                outputStream.flush();
                response += "send message: " + Msg3 + "\n";

                ClientCommunicationActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textResponse.setText(response);
                    }
                });

                SocketClientRecvMsg4Thread socketClientRecvMsg4Thread
                        =new SocketClientRecvMsg4Thread();
                socketClientRecvMsg4Thread.start();

            }catch(UnknownHostException e){
                e.printStackTrace();
                response+="UnkonwnHostException: "+e.toString();
            }catch(IOException e){
                e.printStackTrace();
                response+="IOException: "+e.toString();
            }

        }
    }

    private class SocketClientRecvMsg4Thread extends Thread{
        private String Msg4;

        SocketClientRecvMsg4Thread(){
            Msg4="";
        }

        public void run(){
            try{
                InputStream inputStream = socket.getInputStream();
                InputStreamReader isr=new InputStreamReader(inputStream);
                BufferedReader br=new BufferedReader(isr);
                Msg4=br.readLine();
                response+= Msg4;
                order=MD.DecodingMsg4(Msg4,order);

                response+=order.getOrderAvailable()+"\n";
                ClientCommunicationActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textResponse.setText(response);
                    }
                });
                SocketClientSendMsg5Thread socketClientSendMsg5Thread
                        =new SocketClientSendMsg5Thread();
                socketClientSendMsg5Thread.start();

            }catch(UnknownHostException e){
                e.printStackTrace();
                response+="UnkonwnHostException: "+e.toString();
            }catch(IOException e){
                e.printStackTrace();
                response+="IOException: "+e.toString();
            }/*finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }*/
        }
    }

    private class SocketClientSendMsg5Thread extends Thread{
        private String Msg5="T\n";

        SocketClientSendMsg5Thread(){
            ;
        }

        @Override
        public void run(){
            try{
                OutputStream outputStream=socket.getOutputStream();
                outputStream.write(Msg5.getBytes("UTF-8"));
                outputStream.flush();

                response+= "\nreply:"+Msg5+"\n";

                ClientCommunicationActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textResponse.setText(response);
                    }
                });

                SocketClientRecvMsg6Thread socketClientRecvMsg6Thread
                        =new SocketClientRecvMsg6Thread();
                socketClientRecvMsg6Thread.start();
            }catch(UnknownHostException e){
                e.printStackTrace();
                response+="UnkonwnHostException: "+e.toString();
            }catch(IOException e){
                e.printStackTrace();
                response+="IOException: "+e.toString();
            }
        }
    }

    private class SocketClientRecvMsg6Thread extends Thread{
        private String Msg6="";

        SocketClientRecvMsg6Thread(){;}

        @Override
        public void run(){
            try{
                InputStream inputStream = socket.getInputStream();
                InputStreamReader isr=new InputStreamReader(inputStream);
                BufferedReader br=new BufferedReader(isr);
                Msg6=br.readLine();
                response+="\nMsg6 "+Msg6;
                ClientCommunicationActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textResponse.setText(response);
                    }
                });

                SocketClientRecvMsg7Thread socketClientRecvMsg7Thread
                        =new SocketClientRecvMsg7Thread();
                socketClientRecvMsg7Thread.start();
            }catch(UnknownHostException e){
                e.printStackTrace();
                response+="UnkonwnHostException: "+e.toString();
            }catch(IOException e){
                e.printStackTrace();
                response+="IOException: "+e.toString();
            }
        }
    }

    private class SocketClientRecvMsg7Thread extends Thread{
        private String Msg7="";

        SocketClientRecvMsg7Thread(){;}

        @Override
        public void run(){
            try{
                InputStream inputStream = socket.getInputStream();
                InputStreamReader isr=new InputStreamReader(inputStream);
                BufferedReader br=new BufferedReader(isr);
                Msg7=br.readLine();
                response+="\nMsg7 "+Msg7;
                ClientCommunicationActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        textResponse.setText(response);
                    }
                });
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