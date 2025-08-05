import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Server extends JFrame
{
    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    private JLabel heading = new JLabel("Server Side");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto",Font.PLAIN,20);
    public Server()
    {
        try {
            server = new ServerSocket(777);
            
            System.out.println("Server is ready to accept connection");
            System.out.println("Waitig");
            socket=server.accept();
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
    private void handlEvent(){
        messageInput.addKeyListener(new KeyListener()
        {

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
                    messageInput.setText(" ");
                    messageInput.requestFocus();
                    

               }
            }

        });
    }
    public void createGUI()
    {
        this.setTitle("Server Messager [End]");
        this.setSize(600,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setIcon(new ImageIcon("Message.jpg"));
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        this.setLayout(new BorderLayout());

        this.add(heading,BorderLayout.NORTH);
        this.add(messageArea,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);

        this.setVisible(true);

        
    }
    public void startReading()
    {
        //thread: read karke deta rahega
        Runnable r1=()->{
            
            
            try {
                System.out.println("Reader started");
           
                while (true) {
                    String msg= br.readLine();
                    if (msg.equals("exit")) {

                        System.out.println("Cliet Terminated the chat..");
                        socket.close();
                        break;
                    }
                    // System.out.println("Client: "+msg);
                    messageArea.append("Server: "+msg+"\n");
                }
            } catch (Exception e) {
                System.out.println("Connection is closed");
            }
            
        };
        new Thread(r1).start();
    }
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
    public static void main (String [] args) {
        System.out.println("Thi server is going to start..");
        new Server();
    }
}