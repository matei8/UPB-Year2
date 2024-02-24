package TemaTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface Handler {
    String usersLog = "users.txt";
    String postsLog = "posts.txt";
    String appStats = "appStats.txt";
    String followingLog = "following.txt";
    String followersLog = "followers.txt";
    String commentsLog = "comments.txt";

    Map<Integer, String> appFiles = new HashMap<>();

    HashMap<Integer, String> getLines(String filePath) throws IOException;
    void writeUserToFIle(String username, String password) throws IOException;
    void writeCommToFile(int postID, int id, String username, String text) throws IOException;
    void writePostToFile(int id, String username, String text) throws IOException;
    boolean logFollower(String followerName, String username, String followersLog) throws IOException;
    void updateAppStats(String type, int value, boolean increment) throws IOException;
    String readKthLine(String filePath, int k);
    int getLineNo(String filePath);
    void updateLikeInfo(HashMap<Integer, String> lines, String line, int likeNo);
    void cleanAll();
}
