
import java.util.Properties;

public interface NormalForm {

    public boolean verify(Properties props, DBwrapper db, boolean decomp);
    public void decomposite(Properties props, DBwrapper db);
}
