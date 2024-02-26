package TemaTest;

import java.util.HashMap;

public interface Likeable {
    void like(int id);
    void unlike(int id);
    void updateLikeInfo(HashMap<Integer, String> lines, String line, int likeNo);
}
