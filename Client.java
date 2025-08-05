import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;


import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Client extends JFrame {

    Socket socket;
    BufferedReader br;
    PrintWriter out;

    //Declare Component
    private JLabel heading=new JLabel("Client Area");
    private JTextArea messageArea=new JTextArea();
    private JTextField messageInput=new JTextField();
    private Font font= new Font("Roboto",Font.PLAIN,20);

    // Constructor
    public Client()
    {
        try {
            System.out.println("Sending request to server.");
            socket=new Socket("127.0.0.1",777);
            System.out.println("Connection done");
             br= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out= new PrintWriter(socket.getOutputStream());

            createGUI();
            handlEvent();
            startReading();
            startWriting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //handle Event 
    private void handlEvent(){
        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
               
            }

            @Override
            public void keyPressed(KeyEvent e) {
               
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode()==10) {
                   String contentToSend=messageInput.getText();
                   messageArea.append("Me: "+contentToSend+"\n");
                   out.println(contentToSend);
                   
                   out.flush();
                   messageInput.setText("");
                   messageInput.requestFocus();
                }
                

            }
            
        });
    }

    //GUI Code here
    private void createGUI()
    {
        this.setTitle("Client Messager[End]");
        this.setSize(600,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //coding for component
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setIcon(new ImageIcon("Message.jpg"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);
        //fram ka layout set karen gy
        this.setLayout(new BorderLayout());

        //adding the compents to frame
        this.add(heading,BorderLayout.NORTH);
        this.add(messageArea,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);

        this.setVisible(true);
    }

    //startReading method
    public void startReading()
    {
        //thread: read karke deta rahega
        Runnable r1=()->{
            try {
                System.out.println("Reader started");
            while (true) {
                String msg= br.readLine();
                if (msg.equals("exit")) {
                    
                    System.out.println("Server Terminated the chat..");
                    JOptionPane.showMessageDialog(this, "Sever terminated the chat" );
                    messageInput.setEnabled(false);;
                    socket.close();
                    break;
                }
                //System.out.println("Server: "+msg);

                messageArea.append("Server: "+msg+"\n");
            }
            } catch (Exception e) {
                System.out.println("Connection is closed");
            }
            
        };
        new Thread(r1).start();
    }
    //startWriting Method
    public void startWriting()
    {
        // thread: data user sy lega and send karega client tak
        Runnable r2 =()->{
            
            try {
                System.out.println("Writer started....");
                while (!socket.isClosed()) {
                    BufferedReader br1=new BufferedReader(new InputStreamReader(System.in));
                    String content=br1.readLine();
                    out.println(content);
                    out.flush();
                    if (content.equals("exit")) {


                        socket.close();
                        break;
                    }
            
                }
                
            } catch (Exception e) {
                System.out.println("Connection is closed");
            }
        };
        new Thread(r2).start();
    }
    public static void main(String[] args) {
        
        System.out.println("This is a client...");
        new Client();
    }
}
