import java.util.ArrayList;

/**
 * Created by Yuting on 2/22/2017.
 */
public class StrUtils {
    public static String join(ArrayList<String> al){
        return al.toString().replaceAll("(^\\[)|(\\]$)", "");
    }
}
