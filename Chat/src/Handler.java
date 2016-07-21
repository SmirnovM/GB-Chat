import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Handler extends Thread{

    protected Socket socket;
    protected DataInputStream inStream;
    protected DataOutputStream outStream;
    protected boolean isOn;

    protected static List<Handler> handlers = Collections.synchronizedList(new ArrayList<Handler>());

    public Handler(Socket s) throws IOException {
        socket = s;
        inStream = new DataInputStream(new BufferedInputStream(s.getInputStream()));
        outStream = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
    }

    public Handler() {

    }

    public void run() {
        isOn = true;
        try {
            handlers.add(this);
            while (isOn) {
                String msg = inStream.readUTF();
                broadcast(msg);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            handlers.remove(this);
            try{
                outStream.close();
            }catch (IOException ex) {
                ex.printStackTrace();
            }
            try{
                socket.close();
            }catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected static void broadcast(String message) {
        synchronized (handlers) {
            Iterator<Handler> it = handlers.iterator();
            while (it.hasNext()) {
                Handler c = it.next();
                try {
                    synchronized (c.outStream) {
                        c.outStream.writeUTF(message);
                    }
                    c.outStream.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    c.isOn = false;
                }
            }
        }
    }
}
