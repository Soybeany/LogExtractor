package efb;

import com.soybeany.sfile.data.SFileRange;
import com.soybeany.std.data.Index;

import java.util.HashMap;
import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class EFBIndex extends Index {
    public final Map<String, SFileRange> user = new HashMap<String, SFileRange>();

    public EFBIndex copy(EFBIndex index) {
        super.copy(index);
        index.user.putAll(user);
        return index;
    }
}
