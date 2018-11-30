package com.example.dsnfz.finalproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Iterator;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ChefActivity extends Activity {

    TextView orderIDView,orderAvailable,orderTime;
    TextView orderItemView;
    Button buttonPackaging,buttonFoodReady;
    String message = "";
    ServerSocket serverSocket;
    public KitchenManagement kitchen=KitchenManagement.getKitchen();
    public Customer cus=new Customer();
    Order order=new Order();
    public MsgDecoding MD=new MsgDecoding();
    public String items="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_order_to_chef);
        orderIDView = (TextView) findViewById(R.id.labelSetOrderID);
        orderItemView = (TextView) findViewById(R.id.labelSetOrderItem);
        orderAvailable= (TextView) findViewById(R.id.textFieldAvailability);
        orderTime= (TextView) findViewById(R.id.labelSetOrderTime);
        buttonPackaging=(Button)findViewById(R.id.btnStatus);

        buttonPackaging.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                if(buttonPackaging.getText().equals("packaging")){
                    SocketServerSendMsg6Thread socketServerSendMsg6Thread
                        =new SocketServerSendMsg6Thread();
                    socketServerSendMsg6Thread.start();
                    buttonPackaging.setText("foodready");
                }
                else if(buttonPackaging.getText().equals("foodready")){
                    SocketServerSendMsg7Thread socketServerSendMsg7Thread
                            =new SocketServerSendMsg7Thread();
                    socketServerSendMsg7Thread.start();
                    buttonPackaging.setText("packaging");
                }
                else
                    ;
            }
        });
        UploadInventoryListThread uploadInventoryListThread=new UploadInventoryListThread();
        uploadInventoryListThread.start();

        ShowOldestOrderThread showOldestOrderThread=new ShowOldestOrderThread();
        showOldestOrderThread.start();



        Thread socketServerRecvMsg1Thread = new Thread(new SocketServerRecvMsg1Thread());
        socketServerRecvMsg1Thread.start();

        //infoip.setText(getIpAddress());


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class ShowOldestOrderThread extends Thread{
        public void run(){
            Order oldest=kitchen.getOrderList().getOldestOrder();
            ChefActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    orderIDView.setText("There is no order in the list");
                    orderItemView.setText("");
                    orderAvailable.setText("");
                    orderTime.setText("");
                }
            });
            while(true) {
                if(oldest==null&&kitchen.getOrderList().getOldestOrder()==null)continue;
                if(kitchen.getOrderList().getOldestOrder()!=null&&oldest!=null
                        &&oldest.equals(kitchen.getOrderList().getOldestOrder()))
                    continue;
                oldest=kitchen.getOrderList().getOldestOrder();

//                if (oldest==null) {
//                if(oldest==kitchen.getOrderList().getOldestOrder())continue;
//                oldest=kitchen.getOrderList().getOldestOrder();
                if (oldest == null) {
                    ChefActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            orderIDView.setText("There is no order in the list");
                            orderItemView.setText("");
                            orderAvailable.setText("");
                            orderTime.setText("");
                        }
                    });
                    oldest = kitchen.getOrderList().getOldestOrder();
                    continue;
                }else if (oldest!=null) {
                    Iterator it = oldest.getOrderItems().iterator();
                    items = "";
                    while (it.hasNext()) {
                        OrderItem p = (OrderItem) it.next();
                        items += "    " + p.getName() + "     " + p.getQuantiy() + "\n";
                    }
                    ChefActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            orderIDView.setText(order.getOrderID());
                            orderItemView.setText(items);
                            orderAvailable.setText(order.getOrderAvailable());
                            orderTime.setText((order.getOrderTime().getMonth() + 1) + "/"
                                    + order.getOrderTime().getDate() + "/"
                                    + (order.getOrderTime().getYear() + 1900));
                        }
                    });
                    oldest = kitchen.getOrderList().getOldestOrder();
                }else
                    ;
            }

        }
    }



    private class UploadInventoryListThread extends Thread{
        public void run(){
            while(true){
                try{
                    kitchen.uploadInventoryList(getResources().openRawResource(
                            R.raw.inventorylist));
                    message+="\n the inventory is upload:"+kitchen.getInventoryItems()
                            .getInventoryItemByName("burger").getQuantity();
                    sleep(600000);
                }catch (Exception e){
                    message+=e.toString();
                }
                /*ChefActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                    }
                });*/
            }
        }
    }

    /**
     * thread dealing with receiving the Msg1 from client, and this thread establish
     * the socket connection
     */

    private class SocketServerRecvMsg1Thread extends Thread {

        private String Msg1="";
        private String flag="";
        SocketServerSendMsg2Thread socketServerSendMsg2Thread;

        @Override
        public void run() {
            try {
                serverSocket = MyServerSocket.getServerSocket();
                /*ChefActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        info.setText("I'm a chief waiting here: "
                                + serverSocket.getLocalPort());
                    }
                });*/
                while(true) {
                    Socket socket = serverSocket.accept();
                    MyServerSocket.setMySocket(socket);
                    InputStream inputStream = socket.getInputStream();
                    InputStreamReader isr=new InputStreamReader(inputStream);
                    BufferedReader br=new BufferedReader(isr);
                    Msg1=br.readLine();
                    message += Msg1+"\n";

                    cus=MD.DecodingMsg1(Msg1);
                    if(kitchen.getCustomerList().size()==0)
                        message+="fuck";
                    //else
                        //message += "\n" +kitchen.customerList.getCustomerByID(cus.getCustomerID())
                    // .getCustomerID();
                      //  ;
                    if((flag=(MD.flag)).equals("R")){
                        if(kitchen.registerCustomer(cus)){
                           // message+="register Success";
                            socketServerSendMsg2Thread = new SocketServerSendMsg2Thread(
                                    flag, "success");
                        }
                        else {
                           // message+="\nregister Failed\n";
                            socketServerSendMsg2Thread = new SocketServerSendMsg2Thread(
                                    flag, "fail");
                        }

                    }
                    if(flag.equals("S")){
                       if( kitchen.verifyCustomer(cus)){
                            //message+="Sign up success";
                           cus.setCustomerName(kitchen.getCustomerList()
                                   .getCustomerByID(cus.getCustomerID()).getCustomerName());
                           socketServerSendMsg2Thread = new SocketServerSendMsg2Thread(
                                   flag, "success");
                        }
                        else {
                           //message+="\nsign up Failed\n";
                           cus.setCustomerName(kitchen.getCustomerList()
                                   .getCustomerByID(cus.getCustomerID()).getCustomerName());
                           socketServerSendMsg2Thread = new SocketServerSendMsg2Thread(
                                   flag, "fail");
                       }
                    }
                    /*ChefActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                        }
                    });*/

                    socketServerSendMsg2Thread.start();
                }

            } catch (IOException e) {
                /*message+=e.toString();
                /*ChefActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                    }
                });*/
                //e.printStackTrace();
            }
        }

    }

    private class SocketServerSendMsg2Thread extends Thread {

        private Socket hostThreadSocket;
        private String Msg2="";
        private String flag="";
        private String isSuccess="";
        int cnt;

        SocketServerSendMsg2Thread(String flag, String isSuccess) {
            hostThreadSocket = MyServerSocket.getMySocket();
            this.flag=flag;
            this.isSuccess=isSuccess;
            Msg2=flag+","+cus.getCustomerName()+","+isSuccess+"\n";
        }

        @Override
        public void run() {
            OutputStream outputStream;
            final String msgReply = "Msg2:" + Msg2;
            String notification;

            try {
                outputStream = hostThreadSocket.getOutputStream();
                outputStream.write(Msg2.getBytes("UTF-8"));
                outputStream.flush();

                //message += "replayed: " + msgReply + "\n";

                /*ChefActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                    }
                });*/

            } catch (IOException e) {
                e.printStackTrace();
                //message += "Something wrong! " + e.toString() + "\n";
            }

            /*ChefActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                }
            });*/
            SocketServerRecvMsg3Thread socketServerRecvMsg3Thread
                    =new SocketServerRecvMsg3Thread();
            socketServerRecvMsg3Thread.start();
        }

    }

    private class SocketServerRecvMsg3Thread extends Thread{
        private Socket hostThreadSocket;
        private String Msg3="";

        SocketServerRecvMsg3Thread(){
            hostThreadSocket=MyServerSocket.getMySocket();
        }
        @Override
        public void run(){

           try{
               InputStream inputStream = hostThreadSocket.getInputStream();
               InputStreamReader isr=new InputStreamReader(inputStream);
               BufferedReader br=new BufferedReader(isr);
               Msg3=br.readLine();
               order=MD.DecodingMsg3(Msg3);

               //message +="\n"+Msg3;
               //message +="order info: "+order.getCustomerID()+","+order.getOrderID()+","+order.getOrderTime().ge;

               //kitchen.uploadInventoryList(getResources().openRawResource(R.raw.inventorylist));

               //message+="fuck: "+kitchen.getInventoryItems().getInventoryItemByName("burger").getQuantity();
               String availableItem=kitchen.CheckOrderItem(order);
               //message="\n"+availableItem;

               order=MD.DecodingItemAvailable(availableItem,order);

               order.setSocket(hostThreadSocket);

               if(order.getOrderAvailable().equals("Not Available"));
               else kitchen.addOrder(order);
               //message+="\n"+order.getOrderAvailable();
               //message=""+order.getOrderItems().;
               /*ChefActivity.this.runOnUiThread(new Runnable() {

                   @Override
                   public void run() {
                   }
               });*/

               SocketServerSendMsg4Thread socketServerSendMsg4Thread
                       =new SocketServerSendMsg4Thread();
               socketServerSendMsg4Thread.start();


           }catch (IOException e){
               e.printStackTrace();
           }

        }
    }

    private class SocketServerSendMsg4Thread extends Thread{
        private Socket hostThreadSocket;
        private String Msg4="";

        SocketServerSendMsg4Thread(){
            hostThreadSocket=MyServerSocket.getMySocket();
        }

        public void run(){
            OutputStream outputStream;

            try {
                Msg4 +=order.getOrderAvailable();
                if(Msg4.equals("PA")){
                    Iterator it=order.getOrderItems().iterator();
                    while(it.hasNext()){
                        OrderItem p=(OrderItem) it.next();
                        Msg4+=","+p.getName()+","+p.getQuantiy();
                    }
                }
                Msg4+="\n";
                outputStream = hostThreadSocket.getOutputStream();
                outputStream.write(Msg4.getBytes("UTF-8"));
                outputStream.flush();

                //message += "replayed: " + Msg4 + "\n";

                /*ChefActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                    }
                });*/
                SocketServerRecvMsg5Thread socketServerRecvMsg5Thread
                        =new SocketServerRecvMsg5Thread();
                socketServerRecvMsg5Thread.start();

            } catch (IOException e) {
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }

            /*ChefActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                }
            });*/
        }
    }

    private class SocketServerRecvMsg5Thread extends Thread{
        private Socket hostThreadSocket;
        private String Msg5;

        public SocketServerRecvMsg5Thread(){
            hostThreadSocket=MyServerSocket.getMySocket();
            Msg5="";
        }

        @Override
        public void run() {
            try {
                InputStream inputStream = hostThreadSocket.getInputStream();
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(isr);
                Msg5 = br.readLine();
                if(Msg5.equals("T"))
                    kitchen.addOrder(order);
                //message += "\n" + Msg5+","+kitchen.getOrderList().getSize();
                /*ChefActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                    }
                });*/

                /*SocketServerSendMsg6Thread socketServerSendMsg6Thread
                        =new SocketServerSendMsg6Thread();
                socketServerSendMsg6Thread.start();*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SocketServerSendMsg6Thread extends Thread{
        private Socket hostThreadSocket;
        private String Msg6;

        public SocketServerSendMsg6Thread(){
            hostThreadSocket=kitchen.getOrderList().getOldestOrder().getSocket();
            Msg6="packaging\n";
        }

        @Override
        public void run(){
            OutputStream outputStream;
            try{
                outputStream = hostThreadSocket.getOutputStream();
                outputStream.write(Msg6.getBytes("UTF-8"));
                outputStream.flush();

                //message+="\nMsg6 "+Msg6;
                /*ChefActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                    }
                });*/
                //SocketServerSendMsg7Thread socketServerSendMsg7Thread
                //        =new SocketServerSendMsg7Thread();
                //socketServerSendMsg7Thread.start();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private class SocketServerSendMsg7Thread extends Thread{
        private Socket hostThreadSocket;
        private String Msg7;

        public SocketServerSendMsg7Thread(){
            hostThreadSocket=kitchen.getOrderList().getOldestOrder().getSocket();
            Msg7="FoodReady\n";
        }

        @Override
        public void run(){
            OutputStream outputStream;
            try{
                outputStream = hostThreadSocket.getOutputStream();
                outputStream.write(Msg7.getBytes("UTF-8"));
                outputStream.flush();

                //message+="\nMsg7 "+Msg7;
                /*ChefActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                    }
                });*/
                kitchen.deleteOldestOrder();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (!inetAddress.isLoopbackAddress()) {
                        ip = "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip = " Something Wrong getting ip! " + e.toString() + "\n";
        }

        return ip;
    }


}
