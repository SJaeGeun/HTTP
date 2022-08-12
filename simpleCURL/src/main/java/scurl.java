import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class scurl {
  static HttpURLConnection con;
  public static void main(String[] args) throws Exception {
    if (hasNotArgs(args)){
      System.out.println("Usage: scurl [option] url\n Options: \n-v\n-H <line>\n-d <data>\n-X <command>\n-L\n-F <name=content>");
    } else {
      for(int i =0; i<args.length; i++) {
        if (args[i].contains("http")) {
          URL url = new URL(args[i]);
          con = (HttpURLConnection) url.openConnection();
        }
      }
      if(args[0].contains("-")) {
        switch (args[0]) {
          case "-v":
            curl_V(con, args);
            break;
          case "-H":
            curl_H(con, args);
            break;
          case "-X":
            String method = args[1];
            if(method.equals("GET") || method.equals("POST"))
              if (args[2].equals("-d")){
                curl_D(con, args);
              } else {
                curl(con, args);
              }
            break;
          case "-F":
            curl_F(con, args);
            break;
        }
      } else {
        curl(con, args);
      }
    }
  }
  public static void curl(HttpURLConnection con, String[] args) throws IOException {
    if(args[0].contains("http")) //curl 호출
      con.setRequestMethod("GET");
    else { //X에 의한 호출
      con.setRequestMethod(args[1]);
    }
    con.getResponseCode();

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine+"\n");
    }
    in.close();

    System.out.println(response.toString());
  }
  public static void curl_V(HttpURLConnection con, String[] args) throws IOException {
    con.setRequestMethod("GET");
    // 헤더출력
    Map<String, List<String>> headerFields = con.getHeaderFields();
    for(List<String> lists:  headerFields.values()){
      for(int i = 0; i< lists.size(); i++){
        System.out.println(lists.get(i));
      }
    }
    System.out.println("\n");

    con.getResponseCode();
    // 헤더출력
    Map<String, List<String>> headers = con.getHeaderFields();
    Set<String> keys = headers.keySet();
    for( String key : keys ){
      String val = con.getHeaderField(key);
      System.out.println(key+" "+val);
    }
    System.out.println(con.getLastModified() );

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine+"\n");
    }
    in.close();

    System.out.println(response.toString());
  }

  public static void curl_H(HttpURLConnection con, String[] args) throws IOException {
    con.setRequestMethod("GET");

    for(int i=0; i<args.length; i++) {
      if (args[i].equals("-H")) {
        String[] property = args[i + 1].split(":");
        con.setRequestProperty(property[0], property[1]);
      }
    }

    con.getResponseCode();

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine+"\n");
    }
    in.close();

    System.out.println(response.toString());
  }

  public static void curl_D(HttpURLConnection con, String[] args) throws IOException {
    con.setRequestMethod(args[1]);

    for(int i=0; i<args.length; i++) {
      if (args[i].equals("-H")) {
        String[] property = args[i + 1].split(":");
        con.setRequestProperty(property[0], property[1]);
      }
    }
    for (int i=0; i<args.length; i++){
      if(args[i].equals("-d")){
        con.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
        writer.write(args[i+1]);
        writer.flush();
        writer.close();
        con.getOutputStream().close();
      }
    }

    con.getResponseCode();

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine+"\n");
    }
    in.close();

    System.out.println(response.toString());
  }

  //구현 못했습니다.
  public static void curl_F(HttpURLConnection con, String[] args) throws IOException {
    con.setRequestMethod("POST");

    String boundary = Long.toHexString(System.currentTimeMillis());
    con.setRequestProperty("Content-Type", "multipart/form-data; boundary=------------------------"+boundary);

    String data = args[1];

    byte[] out = data.getBytes(StandardCharsets.UTF_8);

    con.setDoOutput(true);
    OutputStream stream = con.getOutputStream();
    stream.write(out);

    con.getOutputStream().close();

    con.getResponseCode();

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

//    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(data), "UTF-8"));

    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine+"\n");
    }
    in.close();

    System.out.println(response.toString());
  }

  private static boolean hasNotArgs(String[] args) {
    return args.length == 0;
  }

}