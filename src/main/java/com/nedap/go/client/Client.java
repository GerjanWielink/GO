package com.nedap.go.client;

import com.nedap.go.server.Logger;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main (String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8000);
            (new InHandler(socket)).start();
            (new OutHandler(socket)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class InHandler extends  Thread {
    private Socket socket;

    public InHandler (Socket socket) {
        this.socket = socket;
    }

    public void run () {
        String inbound;

        try (BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            while ((inbound = inStream.readLine()) != null) {
                Logger.log(inbound);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

class OutHandler extends Thread {
    private Socket socket;

    public OutHandler(Socket socket) {
        this.socket = socket;
    }

    public void run () {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (true) {
                String line = readString("");

                writer.write(line);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            //
        }
    }

    static public String readString(String tekst) {
        System.out.print(tekst);
        String antw = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            antw = in.readLine();
        } catch (IOException e) {
        }

        return (antw == null) ? "" : antw;
    }
}