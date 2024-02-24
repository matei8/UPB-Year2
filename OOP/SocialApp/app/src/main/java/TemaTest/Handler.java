package TemaTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public interface Handler {
    HashMap<Integer, String> getLines(String filePath) throws IOException;
    boolean checkUser(String username);
    boolean validPasswordAndUser(String username, String password);
    boolean logFollower(String followerName, String username, String followersLog) throws IOException;
    void writeUserToFIle(String username, String password) throws IOException;
    void writeCommToFile(int postID, int id, String username, String text) throws IOException;
    void writePostToFile(int id, String username, String text) throws IOException;
    void updateAppStats(String type, int value, boolean increment) throws IOException;
    String readKthLine(String filePath, int k) throws IOException;
    int getLineNo(String filePath) throws IOException;
    void cleanAll();
    boolean postExists(int id);
    boolean postValidForLike(User user, int id);
    boolean postValidForUnlike(User user, int id);
}
