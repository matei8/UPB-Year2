package TemaTest;

import java.util.HashMap;
import static TemaTest.Handler.appFiles;

public class Validator {
    private static final Validator instance = new Validator();
    private final FileHandler handler = new FileHandler();

    private Validator() {
    }

    public static Validator getInstance() {
        return instance;
    }

    public boolean validLogin(String[] args) {
        if (args.length < 2 || !args[1].contains("-u")) {
            System.out.println("{'status':'error','message':'You need to be authenticated'}");
            return false;
        } else if (args.length < 3 || !args[2].contains("-p")) {
            System.out.println("{'status':'error','message':'You need to be authenticated'}");
            return false;
        }
        return true;
    }

    public boolean postExists(int id) {
        HashMap<Integer, String> lines;
        lines = handler.getLines(appFiles.get(1));

        for (String line : lines.values()) {
            if (Integer.parseInt(line.split(",")[0]) == id) {
                return true;
            }
        }

        return false;
    }

    public boolean userExists(String username) {
        HashMap<Integer, String> lines;
        lines = handler.getLines(appFiles.get(0));

        for (String line : lines.values()) {
            String currentUser = line.split(",")[0];
            if (currentUser.equals(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean validCredentials(String username, String password) {
        HashMap<Integer, String> lines;
        lines = handler.getLines(appFiles.get(0));

        for (String line : lines.values()) {
            String currentUser = line.split(",")[0];
            String currentPassword = line.split(",")[1];

            if (currentUser.equals(username)) {
                return password.equals(currentPassword);
            }
        }

        return false;
    }

    private boolean postAlreadyLiked(User user, int id) {
        HashMap<Integer, String> lines;
        lines = handler.getLines(appFiles.get(0));

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

    private boolean postBelongsToUser(User user, int id) {
        HashMap<Integer, String> lines;
        lines = handler.getLines(appFiles.get(1));

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

    public boolean postValidForLike(User user, int id) {
        return !postAlreadyLiked(user, id) && postBelongsToUser(user, id);
    }

    public boolean postValidForUnlike(User user, int id) {
        return postAlreadyLiked(user, id) && postBelongsToUser(user, id);
    }

    private boolean commentBelongsToUser(String username, int id) {
        HashMap<Integer, String> lines;
        lines = handler.getLines(appFiles.get(5));

        for (String line : lines.values()) {
            int currentID = Integer.parseInt(line.split(",")[0]);
            if (currentID == id) {
                String currentUser = line.split(",")[2];
                if (currentUser.equals(username)) {
                    return false;
                }
                break;
            }
        }
        return true;
    }

    public boolean commentValidForDelete(String username, int id) {
        return commentBelongsToUser(username, id);
    }
}
