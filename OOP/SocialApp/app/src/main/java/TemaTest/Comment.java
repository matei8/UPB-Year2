package TemaTest;

import java.io.IOException;
import java.util.Objects;

public class Comment extends FileHandler implements Likeable {
    protected int id;
    protected int postID;
    protected String username;
    protected String text;

    public Comment(String[] args) {
        this.username = getUsername(args);
        if (!args[0].equals("-delete-comment-by-id") && args.length == 4) {
            this.id = getLastID() + 1;
        } else if (args[0].equals("-delete-comment-by-id") && args.length == 4) {
            this.id = getId(args);
        }
    }

    private String getUsername(String[] args) {
        String[] param = args[1].split(" ");
        return param[1];
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
        String line = readKthLine(appStats, 3);
        assert line != null;
        return Integer.parseInt(line.split(": ")[1]);
    }

    private int getLastID() {
        if (getLineNo(commentsLog) == 0) {
            return 0;
        }
        String[] lastLine = Objects.requireNonNull(readKthLine(commentsLog, getCommNo())).split(",");
        return Integer.parseInt(lastLine[0]);
    }

    protected boolean logComment() {
        try {
            writeCommToFile(this.postID, this.id, this.username, this.text);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        try {
            updateAppStats("COMMENTS", 2, false);
        } catch (IOException e) {
            System.out.println("Warning: Did not log in App Stats file!");
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    protected boolean delete() {
        if (getLastID() == 0 || this.id > getLastID()) {
            System.out.println("{'status':'error','message':'The identifier was not valid'}");
            return false;
        }

        try {
            if (deleteCommFromLog(this.id)) {
                updateAppStats("COMMENTS", 2, true);
            }
            return true;
        } catch (IOException e) {
            System.out.println("Warning: Did not got deleted from App Stats file!");
        }


        return false;
    }

    private boolean deleteCommFromLog(int id) throws IOException {
        return deletePostOrComm(id, commentsLog, getLines(commentsLog));
    }

    @Override
    public void like(int id) {
    }

    @Override
    public void unlike(int id) {

    }
}
