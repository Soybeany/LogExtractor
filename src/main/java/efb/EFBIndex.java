package efb;

import com.soybeany.sfile.data.IIndex;
import com.soybeany.sfile.loader.SFileRange;
import com.soybeany.std.data.Index;

import java.util.HashMap;
import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class EFBIndex implements IIndex<EFBIndex> {
    public final Index index;
    public final Map<String, SFileRange> user = new HashMap<String, SFileRange>();

    public EFBIndex() {
        this(new Index());
    }

    private EFBIndex(Index index) {
        this.index = index;
    }

    @Override
    public EFBIndex copy() {
        EFBIndex efbIndex = new EFBIndex(index.copy());
        efbIndex.user.putAll(user);
        return efbIndex;
    }
}
