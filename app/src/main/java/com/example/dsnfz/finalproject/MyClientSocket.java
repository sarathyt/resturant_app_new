package com.example.dsnfz.finalproject;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by dsnfz on 2017/12/1.
 */

public class MyClientSocket extends Socket{
    private static String Ip="10.0.2.15";
    private static int Port=8080;
    private static MyClientSocket socket;

    private MyClientSocket(String ip, int port) throws IOException{
        super(ip,port);
        Ip=ip;
        Port=port;
    }

    public static MyClientSocket startClientSocket(String ip,int port) throws IOException{
        Ip=ip;
        Port=port;
        socket=new MyClientSocket(Ip,Port);
        return socket;
    }

    public static MyClientSocket getClientSocket() throws IOException{
        if(socket==null){
            socket=new MyClientSocket(Ip,Port);
        }
        return socket;
    }
}
