package efb;

import com.soybeany.sfile.data.IIndex;
import com.soybeany.sfile.loader.SFileRange;

import java.util.HashMap;
import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class EFBIndex implements IIndex<EFBIndex> {
    public final Map<String, long[]> time = new HashMap<String, long[]>();
    public final Map<String, SFileRange> thread = new HashMap<String, SFileRange>();
    public final Map<String, SFileRange> user = new HashMap<String, SFileRange>();
    public final Map<String, SFileRange> url = new HashMap<String, SFileRange>();

    @Override
    public EFBIndex copy() {
        EFBIndex index = new EFBIndex();
        index.time.putAll(time);
        index.thread.putAll(thread);
        index.user.putAll(user);
        index.url.putAll(url);
        return index;
    }
}
