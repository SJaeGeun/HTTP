import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class server{
  static ServerSocket serverSocket = null;
  static Socket socket;
  static InetAddress inetAddress = null;
  public static void main(String[] args) throws IOException {
    if (hasNotArgs(args)){
      System.out.println("Usage: server [option] [hostname] [port]\n Options: \n -l  <port>");
    }
    else{
      if (Objects.equals(args[0], "-l")) {
        int portNum = Integer.parseInt(args[1]);

        try {
          serverSocket = new ServerSocket(portNum);
        } catch (IOException e) {
          System.out.println("Port 값이 올바르지 않습니다.");
        }
        socket = serverSocket.accept();
      } else{
        try{
          client(args);
        } catch (IOException ignored){}
      }
    ReceiveThread receiveThread = new ReceiveThread();
    receiveThread.setSocket(socket);

    SendThread sendThread = new SendThread();
    sendThread.setSocket(socket);

    sendThread.start();
    receiveThread.start();

    }
  }
  private static void client(String[] args) throws IOException {
    inetAddress = InetAddress.getByName(args[0]);
    int port = Integer.parseInt(args[1]);
    socket = new Socket(inetAddress, port);
  }
  private static boolean hasNotArgs(String[] args) {
    return args.length == 0;
  }
  
}

class SendThread extends Thread{

  Socket socket;
  @Override
  public void run(){
    super.run();
    try{
      BufferedReader tmpbuf = new BufferedReader(new InputStreamReader(System.in));
      PrintWriter sendWriter = new PrintWriter(socket.getOutputStream());
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
    socket = _socket;
  }
}

class ReceiveThread extends Thread{
  Socket socket;

  @Override
  public void run(){
    super.run();

    try{
      BufferedReader tmpbuf = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      String receiveString;

      while(true){
        receiveString = tmpbuf.readLine();

        if(receiveString == null){
          System.out.println("상대방 연결이 끊겼습니다.");
          break;
        } else{
          System.out.println("상대방 : " + receiveString);
        }
      }
      tmpbuf.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setSocket(Socket _Socket){
    socket = _Socket;
  }
}