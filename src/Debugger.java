import java.io.PrintWriter;

public class Debugger {
    PrintWriter out;
    public Debugger(String fileName){
        try{
            out = new PrintWriter(fileName);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void println(String str){

        System.out.println(str);
        out.println(str);
    }
    public void close(){
        out.close();
    }
}
