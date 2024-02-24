package TemaTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Post extends FileHandler implements Likeable {
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
        String line = readKthLine(appStats, 2);
        assert line != null;
        return Integer.parseInt(line.split(": ")[1]);
    }

    private int getLastID() {
        if (getLineNo(postsLog) == 0) {
            return 0;
        }
        String[] lastLine = Objects.requireNonNull(readKthLine(postsLog, getPostsNo())).split(",");
        return Integer.parseInt(lastLine[0]);
    }

    protected boolean delete() {
        if (getLastID() == 0 || this.id > getLastID()) {
            System.out.println("{'status':'error','message':'The identifier was not valid'}");
            return false;
        }

        try {
            if (deletePostFromLog(this.id)) {
                updateAppStats("POSTS", 1, true);
            }
            return true;
        } catch (IOException e) {
            System.out.println("Warning: Did not got deleted from App Stats file!");
        }

        return false;
    }

    private boolean deletePostFromLog(int id) throws IOException {
        return deletePostOrComm(id, postsLog, getLines(postsLog));
    }

    protected boolean logPost() {
        try {
            writePostToFile(this.id, this.username, this.text);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        try {
            updateAppStats("POSTS", 1, false);
        } catch (IOException e) {
            System.out.println("Warning: Did not log in App Stats file!");
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    private String getUsername(String[] args) {
        String[] param = args[1].split(" ");
        return param[1];
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
        List<String> lines;
        try {
            lines = getLines(postsLog);
        } catch (IOException e) {
            return;
        }

        for (String line : lines) {
            int currentID = Integer.parseInt(line.split(",")[0]);
            if (currentID == id) {
                int likeNo = Integer.parseInt(line.split(",")[3]);
                likeNo++;

                changeLikeInfo(lines, line, likeNo);
            }
        }
    }

    @Override
    public void unlike(int id) {
        List<String> lines;
        try {
            lines = getLines(postsLog);
        } catch (IOException e) {
            return;
        }

        for (String line : lines) {
            int currentID = Integer.parseInt(line.split(",")[0]);
            if (currentID == id) {
                int likeNo = Integer.parseInt(line.split(",")[3]);
                likeNo--;

                changeLikeInfo(lines, line, likeNo);
                return;
            }
        }
    }

    private void changeLikeInfo(List<String> lines, String line, int likeNo) {
        int lineIndex = lines.indexOf(line);
        line = line.replace(line.split(",")[3], Integer.toString(likeNo));
        lines.set(lineIndex, line);

        try {
            Files.write(Path.of(postsLog), lines);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
