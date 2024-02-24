package TemaTest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class FileHandler implements Handler {
    static {
        appFiles.put(0, usersLog);
        appFiles.put(1, postsLog);
        appFiles.put(2, appStats);
        appFiles.put(3, followingLog);
        appFiles.put(4, followersLog);
        appFiles.put(5, commentsLog);
    }

    public HashMap<Integer, String> getLines(String filePath) {
        FileReader fileReader;
        BufferedReader reader;

        try {
            fileReader = new FileReader(filePath);
            reader = new BufferedReader(fileReader);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        HashMap<Integer, String> map = new HashMap<>();
        String line;

        try {
            line = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int i = 0;

        while (line != null) {
            map.put(i, line);
            try {
                line = reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            i++;
        }

        try {
            reader.close();
            fileReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    public void writeUserToFIle(String username, String password) throws IOException {
        HashMap<Integer, String> lines;
        lines = getLines(usersLog);

        lines.put(lines.size(), username + "," + password);
        Files.write(Path.of(usersLog), lines.values());
    }

    public void writePostToFile(int id, String username, String text) throws IOException {
        HashMap<Integer, String> lines;
        lines = getLines(postsLog);

        lines.put(lines.size(), id + "," + username + "," + text + "," + 0);
        Files.write(Path.of(postsLog), lines.values());
    }

    public void writeCommToFile(int postID, int comID, String username, String text) throws IOException {
        HashMap<Integer, String> lines;
        lines = getLines(commentsLog);

        lines.put(lines.size(), comID + "," + postID + "," + username + "," + text);
        Files.write(Path.of(commentsLog), lines.values());
    }

    public boolean logFollower(String follower, String usernameToFollow, String file) throws IOException {
        File logFile = new File(file);
        HashMap<Integer, String> lines;
        lines = getLines(file);

        if (logFile.length() != 0 && logFile.length() != 1) {
            for (int i = 0; i < lines.size(); i++) {
                String currentLine = lines.get(i);
                String currentUsername = currentLine.split(",")[0];

                if (currentUsername.equals(follower)) {
                    if (currentLine.contains(usernameToFollow)) {
                        return false;
                    }

                    currentLine = currentLine.concat("," + usernameToFollow);
                    lines.replace(i, currentLine);
                    Files.write(logFile.toPath(), lines.values());
                    return true;
                }
            }
        }

        lines.put(lines.size(), follower + "," + usernameToFollow);
        Files.write(logFile.toPath(), lines.values());
        return true;
    }

    private void cleanFile(String fileName) throws IOException {
        PrintWriter file = new PrintWriter(fileName);
        file.print("");
        file.close();
    }

    public void cleanAll() {
        for (String appFile : appFiles.values()) {
            try {
                cleanFile(appFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void updateAppStats(String fieldToUpdate, int line, boolean remove) throws IOException {
        File appStatsFile = new File(appStats);
        HashMap<Integer, String> lines;

        if (appStatsFile.length() == 0) {
            setupAppStats();
        }

        lines = getLines(appStats);

        String stat = lines.get(line).split(": ")[1];
        int number;

        if (remove) {
            number = Integer.parseInt(stat) - 1;
        } else {
            number = Integer.parseInt(stat) + 1;
        }

        lines.replace(line, fieldToUpdate + ": " + number);
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
            System.out.println("Warning: Could not create App Stats file!");
        }
    }

    public String readKthLine(String file, int k) {
        HashMap<Integer, String> lines;
        lines = getLines(file);
        return lines.get(k - 1);
    }

    public int getLineNo(String file) {
        HashMap<Integer, String> lines;
        lines = getLines(file);
        return lines.size();
    }
    public void updateLikeInfo(HashMap<Integer, String> lines, String line, int likeNo) {
        int lineIndex = lines.values().stream().toList().indexOf(line);
        line = line.replace(line.split(",")[3], Integer.toString(likeNo));
        lines.replace(lineIndex, line);

        try {
            Files.write(Path.of(postsLog), lines.values());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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
