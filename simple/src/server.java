import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class server extends Thread{
  static ServerSocket serverSocket = null;
  static Socket c_socket;
  static InetAddress inetAddress = null;
  public static void main(String[] args) throws IOException {
    if (hasNotArgs(args)){
      System.out.println("Usage: server [option] [hostname] [port]\n Options: \n -l  <port>");
    }
    else{
      if(Objects.equals(args[0], "-1")) {
        int portNum = Integer.parseInt(args[1]);

        try {
          serverSocket = new ServerSocket(portNum);
        } catch (IOException e) {
          System.out.println("Port 값이 올바르지 않습니다.");
        }
        c_socket = serverSocket.accept();
      } else{
        try{
          client(args);
        } catch (IOException ignored){}
      }
      RecieveThread recieveThread = new RecieveThread();
      recieveThread.setSocket(c_socket);

      SendThread sendThread = new SendThread();
      sendThread.setSocket(c_socket);

      recieveThread.start();
      sendThread.start();


    }
  }
  private static void client(String[] args) throws IOException {
    inetAddress = InetAddress.getByName(args[0]);
    int port = Integer.parseInt(args[1]);
    c_socket = new Socket(inetAddress, port);
  }
  private static boolean hasNotArgs(String[] args) {
    return args.length == 0;
  }

  @Override
  public void run(){
    super.run();
    try {
      BufferedReader tmpbuf = new BufferedReader(new InputStreamReader(System.in));
      BufferedReader tmpbuf2 = new BufferedReader(new InputStreamReader(c_socket.getInputStream()));
      PrintWriter sendWriter = new PrintWriter(c_socket.getOutputStream());
      String sendString;
      String receiveString;
      while (true){
        receiveString = tmpbuf2.readLine();
        System.out.println(receiveString);
        sendString = tmpbuf.readLine();
        sendWriter.println(sendString);
        sendWriter.flush();
      }
    }catch (IOException e){}
  }
}

class SendThread extends Thread{

  private Socket m_Socket;
  @Override
  public void run(){
    super.run();
    try{
      BufferedReader tmpbuf = new BufferedReader(new InputStreamReader(System.in));
      PrintWriter sendWriter = new PrintWriter(m_Socket.getOutputStream());
      String sendString;

      while(true){
        sendString = tmpbuf.readLine();

        sendWriter.println(sendString);
        sendWriter.flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setSocket(Socket _socket){
    m_Socket = _socket;
  }
}

class RecieveThread extends Thread{
  private Socket m_Socket;

  @Override
  public void run(){
    super.run();

    try{
      BufferedReader tmpbuf = new BufferedReader(new InputStreamReader(System.in));

      String recieveString;

      while(true){
        recieveString = tmpbuf.readLine();

        if(recieveString == null){
          System.out.println("상대방 연결이 끊겼습니다.");
          break;
        } else{
          System.out.println("상대방 : " + recieveString);
        }
      }
      tmpbuf.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setSocket(Socket _Socket){
    m_Socket = _Socket;
  }
}