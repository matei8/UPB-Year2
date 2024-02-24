package TemaTest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileHandler {
    static final String usersLog = "users.txt";
    static final String postsLog = "posts.txt";
    static final String appStats = "appStats.txt";
    static final String followingLog = "following.txt";
    static final String followersLog = "followers.txt";
    static final String commentsLog = "comments.txt";

    static final List<String> appFiles = List.of(new String[]{usersLog, postsLog, appStats,
            followingLog, followersLog, commentsLog});

    protected static List<String> getLines(String filePath) throws IOException {
        File file = new File(filePath);
        return Files.readAllLines(file.toPath());
    }

    protected static boolean checkUser(String username) {
        List<String> lines;

        try {
            lines = getLines(usersLog);
        } catch (IOException e) {
            return false;
        }

        for (String line : lines) {
            String currentUser = line.split(",")[0];
            if (currentUser.equals(username)) {
                return true;
            }
        }
        return false;
    }

    static boolean validPasswordAndUser(String username, String password) {
        List<String> lines;

        try {
            lines = getLines(usersLog);
        } catch (IOException e) {
            return false;
        }

        for (String line : lines) {
            String currentUser = line.split(",")[0];
            String currentPasswd = line.split(",")[1];
            if (currentUser.equals(username)) {
                return password.equals(currentPasswd);
            }
        }

        return false;
    }

    static void writeUserToFIle(String username, String password) throws IOException {
        List<String> lines;

        try {
            lines = getLines(usersLog);
        } catch (IOException e) {
            return;
        }

        lines.add(username + "," + password);
        Files.write(Path.of(usersLog), lines);
    }

    static void writePostToFile(int id, String username, String text) throws IOException {
        List<String> lines;

        try {
            lines = getLines(postsLog);
        } catch (IOException e) {
            return;
        }

        lines.add(id + "," + username + "," + text + "," + 0);
        Files.write(Path.of(postsLog), lines);
    }

    static void writeCommToFile(int postID, int comID, String username, String text) throws IOException {
        List<String> lines;

        try {
            lines = getLines(commentsLog);
        } catch (IOException e) {
            return;
        }

        lines.add(comID + "," + postID +"," + username + "," + text + "," + 0);
        Files.write(Path.of(commentsLog), lines);
    }

    static boolean logFollower(String follower, String usernameToFollow, String file) throws IOException {
        File logFile = new File(file);
        List<String> lines;

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
                    lines.set(i, currentLine);
                    Files.write(logFile.toPath(), lines);
                    return true;
                }
            }
        }

        lines.add(follower + "," + usernameToFollow);
        Files.write(logFile.toPath(), lines);
        return true;
    }

    private static void cleanFile(String file) {
        try {
            PrintWriter users = new PrintWriter(file);
            users.print("");
            users.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void cleanAll() {
        for (String appFile : appFiles) {
            cleanFile(appFile);
        }
    }

    protected void updateAppStats(String fieldToUpdate, int line, boolean remove) throws IOException {
        File appStatsFile = new File(appStats);
        List<String> lines;

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

        lines.set(line, fieldToUpdate + ": " + number);
        Files.write(appStatsFile.toPath(), lines);
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

    static String readKthLine(String file, int k) {
        List<String> lines;
        try {
            lines = getLines(file);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return lines.get(k - 1);
    }

    static int getLineNo(String file) {
        List<String> lines;
        try {
            lines = getLines(file);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return 0;
        }
        return lines.size();
    }

    protected boolean postExists(int id) {
        List<String> lines;
        try {
            lines = getLines(postsLog);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        for (String line : lines) {
            if (Integer.parseInt(line.split(",")[0]) == id) {
                return true;
            }
        }
        return false;
    }

    protected boolean postValidForLike(User user, int id) {
        return !postAlreadyLiked(user, id) && postBelongsToUser(user, id);
    }

    protected boolean postValidForUnlike(User user, int id) {
        return postAlreadyLiked(user, id) && postBelongsToUser(user, id);
    }

    private boolean postBelongsToUser(User user, int id) {
        List<String> lines;
        try {
            lines = getLines(postsLog);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        for (String line : lines) {
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
        List<String> lines;

        try {
            lines = getLines(usersLog);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        int usernameLen = user.username.length();
        int passwordLen = user.password.length();
        int beginIndex = usernameLen + passwordLen + 1;

        for (String line : lines) {
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

    static boolean deletePostOrComm(int id, String commentsLog, List<String> lines) {
        File comments = new File(commentsLog);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int currentID = Integer.parseInt(line.split(",")[0]);
            if (currentID == id) {
                lines.remove(i);

                try {
                    Files.write(comments.toPath(), lines);
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
