package TemaTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class User extends FileHandler implements Likeable {
    protected String username;
    protected String password;

    protected User(String[] args) {
        this.username = getUsername(args);
        this.password = getPassword(args);
    }

    protected boolean logUser() {
        try {
            writeUserToFIle(this.username, this.password);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        try {
            updateAppStats("USERS", 0, false);
        } catch (IOException e) {
            System.out.println("Warning: Did not log in App Stats file!");
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    protected boolean userExists() {
        return checkUser(this.username);
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
            return logFollower(followerName, this.username, followersLog);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    protected boolean follow(String usernameToFollow) {
        try {
            if (!logFollower(this.username, usernameToFollow, followingLog)) {
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
        List<String> lines;

        try {
            lines = getLines(log);
        } catch (IOException e) {
            return false;
        }

        for (int i = 0; i < lines.size(); i++) {
            String currentUser = lines.get(i).split(",")[0];

            if (currentUser.equals(user)) {
                int beginIndex = currentUser.length() + 1;
                String[] following = lines.get(i).substring(beginIndex).split(",");

                lines.set(i, currentUser + ",");
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
                    lines.set(i, lines.get(i).concat(String.valueOf(update)));
                    break;
                }
            }
        }

        if (found) {
            Files.write(file.toPath(), lines);
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
        List<String> lines;

        try {
            lines = getLines(file.getPath());
        } catch (IOException e) {
            return null;
        }

        for (String line : lines) {
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
        List<String> lines;
        try {
            lines = getLines(usersLog);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        for (String line : lines) {
            String currentUser = line.split(",")[0];
            if (currentUser.equals(this.username)) {
                int lineIndex = lines.indexOf(line);
                line = line.concat("," + id);
                lines.set(lineIndex, line);

                try {
                    Files.write(Path.of(usersLog), lines);
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
        List<String> lines;
        try {
            lines = getLines(usersLog);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        for (String line : lines) {
            String currentUser = line.split(",")[0];
            if (currentUser.equals(this.username)) {
                int lineIndex = lines.indexOf(line);
                List<String> lineAsList = new ArrayList<>(List.of(line.split(",")));
                lineAsList.remove(id);
                lines.set(lineIndex, lineAsList.toString());

                try {
                    Files.write(Path.of(usersLog), lines);
                    return;
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    return;
                }
            }
        }
    }
}
