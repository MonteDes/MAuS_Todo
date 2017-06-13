import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.SocketHandler;

/**
 * @author Benedikt Michaelis
 */
public class LoginServer {



    public static void main(String args []) {
        String email;
        ArrayList<User> users = new ArrayList<>();
        try {
            Socket con;
            ServerSocket socket = new ServerSocket(1510);

            con = socket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));

            while(true) {
                if(in.readLine().equals(0)){
                    email = in.readLine();

                    users.add(new User(email, in.readLine()));
                    out.write(-1);
                } else {
                    int check = 0;
                    email = in.readLine();

                    for (User e: users){
                        if(e.getEmail().equals(email)){
                            if(e.getPassword().equals(in.readLine()))
                                check = 1;
                            break;
                        }
                    }
                    out.write(check);
                }




            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
