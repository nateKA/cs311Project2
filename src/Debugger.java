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

    public void printf(String str, Object...objs){
        System.out.printf(str+"\n",objs);
        out.printf(str+"\n",objs);
    }
    public void close(){
        out.close();
    }
}
