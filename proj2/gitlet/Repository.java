package gitlet;

import java.io.File;
import java.time.Instant;
import java.util.HashMap;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author syx
 */
public class Repository {

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final Commit initialCommit = new Commit("initial commit", Instant.ofEpochSecond(0), null, null, new HashMap<>());
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File STAGS_DIR = join(GITLET_DIR, "stages");
    public static final HashMap<String, String> addFileMap = new HashMap<>(); // key 为fileName, value 为File 的sha1
    public static final HashMap<String, String> rmFileMap = new HashMap<>(); // key 为fileName, value 为File 的sha1
    public static final File UTILS_DIR = join(GITLET_DIR, "utils");
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        createForCommit(COMMITS_DIR, initialCommit);
        saveForAddFileMap();
        saveForRmFileMap();
    }


    /**
     * 1.
     * 将当前版本的文件复制一份，添加到暂存区中
     * 2.
     * 如果一个已经暂存的文件再次被添加，那么它在暂存区中的内容会被新的内容覆盖,原来的暂存的文件在stage的备份删除
     * 3.
     * 如果当前工作目录中的文件内容与当前提交中的版本完全相同，则不应将其暂存添加(needed to be done in commit) TODO:
     * 4.
     *  如果该文件已在暂存区中，应将其从暂存区移除（这种情况可能发生在文件被修改、添加到暂存区后又改回原样的情况下）（needed to be done in commit) TODO:
     * 5.
     * 如果该文件之前被标记为删除（见 `gitlet rm`），那么这次 `add` 操作应使其不再被标记删除 （needed to be done in rm) TODO:
     * 6.如果args.length == 1,即没有指明文件名，直接退出,已经在Main类处理了
     * 7.如果文件不存在，则输出File does not exist.然后退出
     * @param fileName
     */
    public static void add(String fileName) {
        //case 7
        File file = join(CWD, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        loadForAddFileMap();
        if (addFileMap.containsKey(fileName)) {
            //case 2 这个文件已经被添加过了，如果sha1相同则不做改变，如果sha1不同则删除原来的在stage的文件，并pushFileToStag新文件，并修改addFileMap
            if (sha1ForFile(join(CWD, fileName)).equals(addFileMap.get(fileName))) {
                System.exit(0);
            } else {
                //删除原来的在stage的文件
                String sha1OriginalFile = addFileMap.get(fileName);
                join(join(STAGS_DIR, sha1OriginalFile.substring(0, 2)), sha1OriginalFile).delete();
                pushFileToStag(file);
                addFileMap.put(fileName, sha1ForFile(file));
                saveForAddFileMap();
            }
        } else {
            //这个文件没有被添加过，因此直接加入到addFileMap并且pushFileToStag(case 1)
            addFileMap.put(fileName, sha1ForFile(file));
            pushFileToStag(file);
            saveForAddFileMap();
        }



    }

    /**
     * 1.如果该文件已被加入暂存区，则将其从暂存区移除(不需要测试）
     *
     * 2.如果当前提交已经追踪了某个文件，而用户执行了rm命令，那么这个文件会被标记为移除，即他将不再被当前提交追踪，
     * 并且会被从工作目录中删除（如果之前已经手动删除了，就直接标记为移除，不再被当前提交追踪，不需要从工作目录中删除，
     * 这里只需要一个判断条件就好了，但测试还是两个文件）TODO:
     *
     * 3.如果文件既未加入暂存区也未被当前提交追踪，输出：`No reason to remove the file.` TODO:
     * @param fileName
     */
    public static void rm(String fileName) {
        loadForAddFileMap();
        if (addFileMap.get(fileName) != null) {
            addFileMap.remove(fileName);
        }
        saveForAddFileMap();
    }

    /**
     * 1.如果消息为空，输出：`Please enter a commit message.(已经在main处理了）
     * 2.如果没有文件被暂存，且没有文件被rm，则输出：`No changes added to the commit.`
     * 3.
     * @param message
     */
    public static void commit(String message) {
        loadForAddFileMap();
        loadForRmFileMap();
        if (addFileMap.size() == 0 && rmFileMap.size() == 0) {
            System.out.println("No changes added to the commit");
            System.exit(0);
        }
    }

}
