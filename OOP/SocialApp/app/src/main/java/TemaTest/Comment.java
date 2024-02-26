package TemaTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;

import static TemaTest.FileHandler.*;

public class Comment implements Likeable {
    private static final FileHandler handler = new FileHandler();

    protected int id;
    protected int postID;
    protected String username;
    protected String text;

    public Comment(String[] args) {
        this.username = getUsername(args);
        if (!args[0].equals("-delete-comment-by-id") && args.length == 5) {
            this.id = getLastID() + 1;
        } else if (args[0].equals("-delete-comment-by-id") && args.length == 4) {
            this.id = getId(args);
        }
    }

    private String getUsername(String[] args) {
        return User.getUsername(args);
    }

    protected String getText(String[] args) throws ArrayIndexOutOfBoundsException {
        String[] param = args[4].split(" '");
        return param[1];
    }

    protected int getPostID(String[] args) throws ArrayIndexOutOfBoundsException {
        String[] param = args[3].split(" ");
        return Integer.parseInt(param[1].replace("'", ""));
    }

    protected int getId(String[] args) throws ArrayIndexOutOfBoundsException {
        String[] param = args[3].split(" '");
        param[1] = param[1].replace("'", "");
        return Integer.parseInt(param[1]);
    }

    private int getCommNo() {
        String line = handler.readKthLine(appStats, 3);
        assert line != null;
        return Integer.parseInt(line.split(": ")[1]);
    }

    private int getLastID() {
        if (handler.getLineNo(commentsLog) == 0) {
            return 0;
        }
        String[] lastLine = Objects.requireNonNull(handler.readKthLine(commentsLog, getCommNo())).split(",");
        return Integer.parseInt(lastLine[0]);
    }

    protected boolean logComment() {
        try {
            handler.writeCommToFile(this.postID, this.id, this.username, this.text);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        try {
            handler.updateAppStats("COMMENTS", 2, false);
        } catch (IOException e) {
            System.out.println("Warning: Did not log in App Stats file!");
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    protected boolean delete() {
        int lastID = getLastID();

        if (lastID == 0 || this.id > lastID) {
            System.out.println("{'status':'error','message':'The identifier was not valid'}");
            return false;
        }

        try {
            if (deleteCommFromLog(this.id)) {
                handler.updateAppStats("COMMENTS", 2, true);
            }
            return true;
        } catch (IOException e) {
            System.out.println("Warning: Was not deleted from App Stats file!");
        }

        return false;
    }

    private boolean deleteCommFromLog(int id) throws IOException {
        return handler.deletePostOrComm(id, commentsLog);
    }

    @Override
    public void like(int id) {
        HashMap<Integer, String> lines;
        lines = handler.getLines(commentsLog);

        for (String line : lines.values()) {
            int currentID = Integer.parseInt(line.split(",")[0]);
            if (currentID == id) {
                int likeNo = Integer.parseInt(line.split(",")[4]);
                likeNo++;

                updateLikeInfo(lines, line, likeNo);
            }
        }
    }

    @Override
    public void unlike(int id) {
        HashMap<Integer, String> lines;
        lines = handler.getLines(commentsLog);

        for (String line : lines.values()) {
            int currentID = Integer.parseInt(line.split(",")[0]);
            if (currentID == id) {
                int likeNo = Integer.parseInt(line.split(",")[4]);
                likeNo--;

                updateLikeInfo(lines, line, likeNo);
                return;
            }
        }
    }

    @Override
    public void updateLikeInfo(HashMap<Integer, String> lines, String line, int likeNo) {
        int lineIndex = lines.values().stream().toList().indexOf(line);
        line = line.replace(line.split(",")[4], Integer.toString(likeNo));
        lines.replace(lineIndex, line);

        try {
            Files.write(Path.of(commentsLog), lines.values());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
