package TemaTest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileHandler implements Handler {
    public static final String usersLog = "users.txt";
    public static final String postsLog = "posts.txt";
    public static final String appStats = "appStats.txt";
    public static final String followingLog = "following.txt";
    public static final String followersLog = "followers.txt";
    public static final String commentsLog = "comments.txt";

    public static final Map<Integer, String> appFiles = new HashMap<>();

    static {
        appFiles.put(0, usersLog);
        appFiles.put(1, postsLog);
        appFiles.put(2, appStats);
        appFiles.put(3, followingLog);
        appFiles.put(4, followersLog);
        appFiles.put(5, commentsLog);
    }

    public HashMap<Integer, String> getLines(String filePath) throws IOException {
        FileReader fileReader = new FileReader(filePath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        HashMap<Integer, String> map = new HashMap<>();
        String line;
        int i = 0;

        while ((line = bufferedReader.readLine()) != null) {
            map.put(i, line);
            i++;
        }

        return map;
    }

    public boolean checkUser(String username) {
        HashMap<Integer, String> lines;

        try {
            lines = getLines(usersLog);
        } catch (IOException e) {
            return false;
        }

        for (String line : lines.values()) {
            String currentUser = line.split(",")[0];
            if (currentUser.equals(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean validPasswordAndUser(String username, String password) {
        HashMap<Integer, String> lines;

        try {
            lines = getLines(usersLog);
        } catch (IOException e) {
            return false;
        }

        for (String line : lines.values()) {
            String currentUser = line.split(",")[0];
            String currentPasswd = line.split(",")[1];
            if (currentUser.equals(username)) {
                return password.equals(currentPasswd);
            }
        }

        return false;
    }

    public void writeUserToFIle(String username, String password) throws IOException {
        HashMap<Integer, String> lines;

        try {
            lines = getLines(usersLog);
        } catch (IOException e) {
            return;
        }

        lines.put(lines.size(), username + "," + password);
        Files.write(Path.of(usersLog), lines.values());
    }

    public void writePostToFile(int id, String username, String text) throws IOException {
        HashMap<Integer, String> lines;

        try {
            lines = getLines(postsLog);
        } catch (IOException e) {
            return;
        }

        lines.put(lines.size(), id + "," + username + "," + text + "," + 0);
        Files.write(Path.of(postsLog), lines.values());
    }

    public void writeCommToFile(int postID, int comID, String username, String text) throws IOException {
        HashMap<Integer, String> lines;

        try {
            lines = getLines(commentsLog);
        } catch (IOException e) {
            return;
        }

        lines.put(lines.size(), comID + "," + postID + "," + username + "," + text);
        Files.write(Path.of(commentsLog), lines.values());
    }

    public boolean logFollower(String follower, String usernameToFollow, String file) throws IOException {
        File logFile = new File(file);
        HashMap<Integer, String> lines;

        try {
            lines = getLines(file);
        } catch (IOException e) {
            return false;
        }

        if (logFile.length() != 0 && logFile.length() != 1) {
            for (int i = 0; i < lines.size(); i++) {
                String currentLine = lines.get(i);
                String currentUsername = currentLine.split(",")[0];

                if (currentUsername.equals(follower)) {
                    if (currentLine.contains(usernameToFollow)) {
                        return false;
                    }

                    currentLine = currentLine.concat("," + usernameToFollow);
                    lines.put(i, currentLine);
                    Files.write(logFile.toPath(), lines.values());
                    return true;
                }
            }
        }

        lines.put(lines.size(), follower + "," + usernameToFollow);
        Files.write(logFile.toPath(), lines.values());
        return true;
    }

    private void cleanFile(String file) {
        try {
            PrintWriter users = new PrintWriter(file);
            users.print("");
            users.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void cleanAll() {
        for (String appFile : appFiles.values()) {
            cleanFile(appFile);
        }
    }

    public void updateAppStats(String fieldToUpdate, int line, boolean remove) throws IOException {
        File appStatsFile = new File(appStats);
        HashMap<Integer, String> lines;

        if (appStatsFile.length() == 0) {
            setupAppStats();
        }

        try {
            lines = getLines(appStats);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        String stat = lines.get(line).split(": ")[1];
        int number;

        if (remove) {
            number = Integer.parseInt(stat) - 1;
        } else {
            number = Integer.parseInt(stat) + 1;
        }

        lines.put(line, fieldToUpdate + ": " + number);
        Files.write(appStatsFile.toPath(), lines.values());
    }

    private void setupAppStats() {
        try (FileWriter fw = new FileWriter(appStats, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println("USERS: 0");
            out.println("POSTS: 0");
            out.println("COMMENTS: 0");
        } catch (IOException ignored) {

        }
    }

    public String readKthLine(String file, int k) {
        HashMap<Integer, String> lines;

        try {
            lines = getLines(file);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }

        return lines.get(k - 1);
    }

    public int getLineNo(String file) {
        HashMap<Integer, String> lines;

        try {
            lines = getLines(file);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return 0;
        }

        return lines.size();
    }

    public boolean postExists(int id) {
        HashMap<Integer, String> lines;

        try {
            lines = getLines(postsLog);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        for (String line : lines.values()) {
            if (Integer.parseInt(line.split(",")[0]) == id) {
                return true;
            }
        }

        return false;
    }

    public boolean postValidForLike(User user, int id) {
        return !postAlreadyLiked(user, id) && postBelongsToUser(user, id);
    }

    public boolean postValidForUnlike(User user, int id) {
        return postAlreadyLiked(user, id) && postBelongsToUser(user, id);
    }

    private boolean postBelongsToUser(User user, int id) {
        HashMap<Integer, String> lines;

        try {
            lines = getLines(postsLog);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        for (String line : lines.values()) {
            int currentID = Integer.parseInt(line.split(",")[0]);
            if (currentID == id) {
                String currentUser = line.split(",")[1];
                if (currentUser.equals(user.username)) {
                    return false;
                }
                break;
            }
        }
        return true;
    }

    private boolean postAlreadyLiked(User user, int id) {
        HashMap<Integer, String> lines;

        try {
            lines = getLines(usersLog);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        int usernameLen = user.username.length();
        int passwordLen = user.password.length();
        int beginIndex = usernameLen + passwordLen + 1;

        for (String line : lines.values()) {
            String currentUser = line.split(",")[0];
            if (currentUser.equals(user.username)) {
                if (beginIndex != line.length()) {
                    beginIndex++;
                } else {
                    return false;
                }

                String[] likedPosts = line.substring(beginIndex).split(",");

                for (String postID : likedPosts) {
                    if (Integer.parseInt(postID) == id) {
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }

    public boolean deletePostOrComm(int id, String commentsLog, HashMap<Integer, String> lines) {
        File comments = new File(commentsLog);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int currentID = Integer.parseInt(line.split(",")[0]);
            if (currentID == id) {
                lines.remove(i);

                try {
                    Files.write(comments.toPath(), lines.values());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    return false;
                }
                return true;
            }
        }
        return false;
    }
}
