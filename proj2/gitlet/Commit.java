package gitlet;


import java.io.Serializable;
import java.time.Instant;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;
import java.util.TreeMap;

/** Represents a gitlet commit object.
 *
 *  does at a high level.
 *
 *  @author syx
 */
public class Commit implements Serializable {
    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private String message;
    private Instant commit_date;
    private String parentSha1;
    private String parentSha2;
    private HashMap<String, String> map; // Key 是文件名， Value 是文件对应的 Sha1

    public String getMessage() {
        return message;
    }

    public Instant getCommit_date() {
        return commit_date;
    }

    public Commit(String message, Instant commit_date, String parentSha1, String parentSha2, HashMap<String, String> map) {
        this.message = message;
        this.commit_date = commit_date;
        this.parentSha1 = parentSha1;
        this.parentSha2 = parentSha2;
        this.map = map;
    }

    public HashMap<String, String> getMap() {
        return map;
    }

    public String getParentSha1() {
        return parentSha1;
    }

    public String getParentSha2() {
        return parentSha2;
    }

    @Override
    public String toString() {
        TreeMap<String, String> sortedMap = new TreeMap<>(map);
        return "Commit{" +
                "message='" + message + '\'' +
                ", commit_date=" + commit_date +
                ", parentSha1='" + parentSha1 + '\'' +
                ", parentSha2='" + parentSha2 + '\'' +
                ", sortedmap=" + sortedMap +
                '}';
    }
}
