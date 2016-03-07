package com.wordgridgame.wordgridgame;

import android.bluetooth.BluetoothSocket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;

/**
 * Created by lyf on 2016/2/21.
 */
public class BluetoothConnectManager extends Thread {
    protected static BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public BluetoothConnectManager(BluetoothSocket socket){
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public BluetoothConnectManager(){
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = mmSocket.getInputStream();
            tmpOut = mmSocket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }



    public void sendData(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    public byte[] readData(){
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                return buffer;

            } catch (IOException e) {
                System.out.println(e.toString());
                return null;
            }
        }
    }

    public void sendObject(Object o){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(mmOutStream);
            oos.writeObject(o);
            oos.flush();
        }catch (Exception ee){
            System.out.println("Serialization Save Error : "+ee.toString());

        }
    }

    public Object readObject(){
        try{
            ObjectInputStream ois=new ObjectInputStream(mmInStream);
            Object o=ois.readObject();
            return o;

        }catch (Exception ee){
            return null;}
    }

    public byte[] serialize(Board board) throws IOException {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ObjectOutputStream o = new ObjectOutputStream(b);
            o.writeObject(board);
            return b.toByteArray();
        }catch (Exception ee){
            return null;}
    }
    //AbstractMessage was actually the message type I used, but feel free to choose your own type
    public static Board deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try {
            ByteArrayInputStream b = new ByteArrayInputStream(bytes);
            ObjectInputStream o = new ObjectInputStream(b);
            return (Board) o.readObject();
        }catch (Exception ee){
            return null;}
    }
    public static byte[] intToBytes( final int i ) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }



}
