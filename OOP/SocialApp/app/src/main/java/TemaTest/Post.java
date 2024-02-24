package TemaTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import static TemaTest.FileHandler.*;

public class Post implements Likeable {
    private static final FileHandler handler = new FileHandler();

    private int id;
    protected String text;
    protected String username;

    protected Post(String[] args) {
        this.username = getUsername(args);

        if (!args[0].equals("-delete-post-by-id") && args.length == 4) {
            this.text = getText(args);
            this.id = getLastID() + 1;
        } else if (args[0].equals("-delete-post-by-id") && args.length == 4) {
            this.id = getId(args);
        }
    }

    private int getPostsNo() {
        String line = handler.readKthLine(appStats, 2);
        assert line != null;
        return Integer.parseInt(line.split(": ")[1]);
    }

    private int getLastID() {
        if (handler.getLineNo(postsLog) == 0) {
            return 0;
        }
        String[] lastLine = Objects.requireNonNull(handler.readKthLine(postsLog, getPostsNo())).split(",");
        return Integer.parseInt(lastLine[0]);
    }

    protected boolean delete() {
        if (getLastID() == 0 || this.id > getLastID()) {
            System.out.println("{'status':'error','message':'The identifier was not valid'}");
            return false;
        }

        try {
            if (deletePostFromLog(this.id)) {
                handler.updateAppStats("POSTS", 1, true);
            }
            return true;
        } catch (IOException e) {
            System.out.println("Warning: Did not got deleted from App Stats file!");
        }

        return false;
    }

    private boolean deletePostFromLog(int id) throws IOException {
        return handler.deletePostOrComm(id, postsLog, handler.getLines(postsLog));
    }

    protected boolean logPost() {
        try {
            handler.writePostToFile(this.id, this.username, this.text);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        try {
            handler.updateAppStats("POSTS", 1, false);
        } catch (IOException e) {
            System.out.println("Warning: Did not log in App Stats file!");
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    private String getUsername(String[] args) {
        return User.getUsername(args);
    }

    private String getText(String[] args) {
        String[] param = args[3].split(" '");
        return param[1];
    }

    protected int getId(String[] args) throws ArrayIndexOutOfBoundsException {
        String[] param = args[3].split(" '");
        param[1] = param[1].replace("'", "");
        return Integer.parseInt(param[1]);
    }

    @Override
    public void like(int id) {
        HashMap<Integer, String> lines;
        lines = handler.getLines(postsLog);

        for (String line : lines.values()) {
            int currentID = Integer.parseInt(line.split(",")[0]);
            if (currentID == id) {
                int likeNo = Integer.parseInt(line.split(",")[3]);
                likeNo++;

                handler.updateLikeInfo(lines, line, likeNo);
            }
        }
    }

    @Override
    public void unlike(int id) {
        HashMap<Integer, String> lines;
        lines = handler.getLines(postsLog);

        for (String line : lines.values()) {
            int currentID = Integer.parseInt(line.split(",")[0]);
            if (currentID == id) {
                int likeNo = Integer.parseInt(line.split(",")[3]);
                likeNo--;

                handler.updateLikeInfo(lines, line, likeNo);
                return;
            }
        }
    }
}
