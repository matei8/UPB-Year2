package TemaTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static TemaTest.FileHandler.*;

public class User implements Likeable {
    private static final FileHandler handler = new FileHandler();

    protected String username;
    protected String password;

    protected User(String[] args) {
        this.username = getUsername(args);
        this.password = getPassword(args);
    }

    protected boolean logUser() {
        try {
            handler.writeUserToFIle(this.username, this.password);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        try {
            handler.updateAppStats("USERS", 0, false);
        } catch (IOException e) {
            System.out.println("Warning: Did not log in App Stats file!");
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }


    protected static String getUsername(String[] args) {
        String[] param = args[1].split(" ");
        return param[1];
    }

    protected static String getPassword(String[] args) {
        String[] param = args[2].split(" ");
        return param[1];
    }

    protected String get4thArg(String[] args) {
        String[] param = args[3].split(" ");
        return param[1];
    }

    protected boolean addFollower(String followerName) {
        try {
            return handler.logFollower(followerName, this.username, followersLog);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    protected boolean follow(String usernameToFollow) {
        try {
            if (!handler.logFollower(this.username, usernameToFollow, followingLog)) {
                System.out.println("{'status':'error','message':'The username to follow was not valid'}");
                return false;
            }
        }catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    protected boolean unfollow(String user, String usernameToUnfollow, String log) throws IOException {
        File file = new File(log);
        boolean found = false;

        HashMap<Integer, String> lines;
        lines = handler.getLines(log);

        for (int i = 0; i < lines.size(); i++) {
            String currentUser = lines.get(i).split(",")[0];

            if (currentUser.equals(user)) {
                int beginIndex = currentUser.length() + 1;
                String[] following = lines.get(i).substring(beginIndex).split(",");

                lines.replace(i, currentUser + ",");
                StringBuilder update = new StringBuilder();

                for (String s : following) {
                    if (!s.equals(usernameToUnfollow)) {
                        update.append(s).append(",");
                    } else {
                        found = true;
                    }
                }

                if (update.length() >= 2) {
                    update.delete(update.length() - 2, update.length());
                }

                if (found) {
                    lines.replace(i, lines.get(i).concat(String.valueOf(update)));
                    break;
                }
            }
        }

        if (found) {
            Files.write(file.toPath(), lines.values());
            return true;
        }

        System.out.println("{'status':'error','message':'The username to unfollow was not valid'}");
        return false;
    }

    protected String[] getFollowers(String user) {
        File file = new File(followersLog);
        return getFollowsInfo(user, file);
    }

    protected String[] getFollowing(String user) {
        File file = new File(followingLog);
        return getFollowsInfo(user, file);
    }

    private String[] getFollowsInfo(String user, File file) {
        String[] following = null;
        HashMap<Integer, String> lines;
        lines = handler.getLines(file.getPath());

        for (String line : lines.values()) {
            String currentUser = line.split(",")[0];
            if (currentUser.equals(user)) {
                int beginIndex = currentUser.length() + 1;
                following = line.substring(beginIndex).split(",");
                break;
            }
        }

        return following;
    }

    @Override
    public void like(int id) {
        HashMap<Integer, String> lines;
        lines = handler.getLines(usersLog);

        for (String line : lines.values()) {
            String currentUser = line.split(",")[0];
            if (currentUser.equals(this.username)) {
                int lineIndex = lines.values().stream().toList().indexOf(line);
                line = line.concat("," + id);
                lines.replace(lineIndex, line);

                try {
                    Files.write(Path.of(usersLog), lines.values());
                    return;
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }
        }
    }

    @Override
    public void unlike(int id) {
        HashMap<Integer, String> lines;
        lines = handler.getLines(usersLog);

        for (String line : lines.values()) {
            String currentUser = line.split(",")[0];
            if (currentUser.equals(this.username)) {
                int lineIndex = lines.values().stream().toList().indexOf(line);
                List<String> lineAsList = new ArrayList<>(List.of(line.split(",")));
                lineAsList.remove(id);
                lines.replace(lineIndex, lineAsList.toString());

                try {
                    Files.write(Path.of(usersLog), lines.values());
                    return;
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }
        }
    }
}
