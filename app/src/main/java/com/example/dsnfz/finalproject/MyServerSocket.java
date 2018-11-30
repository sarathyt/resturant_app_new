package com.example.dsnfz.finalproject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by dsnfz on 2017/12/1.
 */

public class MyServerSocket extends ServerSocket{
    private static int socketPort=8080;
    private static MyServerSocket serversocket=null;
    private static Socket MySocket=null;

    private MyServerSocket(int Port) throws IOException{
        super(Port);
        socketPort=Port;
    }

    public static MyServerSocket getServerSocket() throws IOException{
        if(serversocket==null){
            serversocket=new MyServerSocket(socketPort);
        }
        return serversocket;
    }

    public void setSocketPort(int Port)throws IOException{
        socketPort=Port;
        serversocket=new MyServerSocket(socketPort);
    }

    public int getSocketPort(){
        return socketPort;
    }

    public static void setMySocket(Socket socket){
        MySocket=socket;
    }

    public static Socket getMySocket(){
        return MySocket;
    }

}
