import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame implements Runnable {

    protected Socket socket;
    protected DataInputStream inStream;
    protected DataOutputStream outStrem;
    protected JTextArea outTextArea;
    protected JTextField inTextField;
    protected boolean isOn;

    public Client(String title, Socket s, DataInputStream dis, DataOutputStream dos) {
        super(title);
        outStrem = dos;
        inStream = dis;

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(BorderLayout.CENTER, outTextArea = new JTextArea());
        outTextArea.setEditable(false);
        cp.add(BorderLayout.SOUTH, inTextField = new JTextField());

        inTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    outStrem.writeUTF(inTextField.getText());
                    outStrem.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                inTextField.setText("");
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);//todo
                isOn = false;
                try {
                    outStrem.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setVisible(true);
        inTextField.requestFocus();
        (new Thread(this)).start();
    }

    @Override
    public void run() {
        isOn = true;
        try {
            while (isOn) {
                String line = inStream.readUTF();
                outTextArea.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inTextField.setVisible(false);
            validate();
        }
    }

    public static void main(String[] args) throws IOException {
        String args0 = "localhost";
        String args1 = "8082";

        Socket socket = new Socket(args0, Integer.parseInt(args1));
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            new Client("Chat" + args0 + ":" + args1, socket, dis, dos);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (dos != null) {
                    dos.close();
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
        }
    }
}
