package gitlet;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;

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
    public static HashMap<String, String> addFileMap = new HashMap<>(); // key 为fileName, value 为File 的sha1
    public static HashMap<String, String> rmFileMap = new HashMap<>(); // key 为fileName, value 为File 的sha1
    public static final File UTILS_DIR = join(GITLET_DIR, "utils");
    public static Commit head = initialCommit;//Head指向的是当前节点（即使有两个分支也会有一个Head) // 每次commit后都需要更新head
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        BLOBS_DIR.mkdir();
        createForCommit(COMMITS_DIR, initialCommit);
        saveForAddFileMap();
        saveForRmFileMap();
        saveForHead();

    }


    /**
     * 1.
     * 将当前版本的文件复制一份，添加到暂存区中
     * 2.
     * 如果一个已经暂存的文件再次被添加，那么它在暂存区中的内容会被新的内容覆盖,原来的暂存的文件在stage的备份删除
     * 3.
     * 如果当前工作目录中的文件内容与当前提交中的版本完全相同，则不应将其暂存添加
     * 4.
     *  如果该文件已在暂存区中，应将其从暂存区移除（这种情况可能发生在文件被修改、添加到暂存区后又改回原样的情况下）
     * 5.
     * 如果该文件之前被标记为删除（见 `gitlet rm`），那么这次 `add` 操作应使其不再被标记删除
     * 6.如果args.length == 1,即没有指明文件名，直接退出,已经在Main类处理了
     * 7.如果文件不存在，则输出File does not exist.然后退出
     * 实现方式
     * 第一种情况A:
     * 如果Head有这个filename
     *      如果head的这个文件的sha1 value与文件的sha1 value相同：
     *             如果filename 在 rmFileMap中，则把他从rmFileMap中删除，其他不做
     *             如果filename 不在rmFileMap 中
     *                      如果暂存区有这个filename,则把他从暂存区中删除
     *                      如果暂存区没有这个filename,则不做改变，也不把它放到暂存区中
     *      如果head的这个文件的sha1value 与文件的sha1 value不同：
     *             如果暂存区有这个filename
     *                      如果两个sha1value相同，则不做改变
     *                      如果两个sha1value不同，则把原来的暂存区的删除，把新的放到暂存区中
     *             如果暂存区没有这个filename，则把他放到暂存区中（case1）
     * 第二种情况B:
     * 如果Head没有这个filename
     *      如果暂存区中有这个filename：
     *             如果两个sha1value相同，则不做改变
     *             如果两个sha1value不同，则把原来的暂存区的删除，把新的放到暂存区中
     *      如果暂存区没有这个filename，把他放到暂存区中（case1）
     *
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
        loadForHead();
        loadForRmFileMap();
        Commit originalCommit = head;
        HashMap<String, String> originalCommitMap = originalCommit.getMap();
        if (originalCommitMap.containsKey(fileName)) {
            //如果Head有这个filename
            if (originalCommitMap.get(fileName).equals(sha1ForFile(file))) {
                //如果head的这个文件的sha1 value与文件的sha1 value相同
                if (rmFileMap.containsKey(fileName)) {
                    //如果filename 在 rmFileMap中，则把他从rmFileMap中删除，其他不做
                    rmFileMap.remove(fileName);
                } else {
                    //如果filename 不在rmFileMap中
                    if (addFileMap.containsKey(fileName)) {
                        //如果暂存区有这个filename,则把他从暂存区中删除
                        String originalSha1Value = addFileMap.get(fileName);
                        addFileMap.remove(fileName);
                        join(join(STAGS_DIR, originalSha1Value.substring(0, 2)), originalSha1Value).delete();
                        join(STAGS_DIR, originalSha1Value.substring(0, 2)).delete();
                    } else {
                        //如果暂存区没有这个filename,则不做改变，也不把它放到暂存区中

                    }
                }
            } else {
                //如果head的这个文件的sha1value 与文件的sha1 value不同
                if (addFileMap.containsKey(fileName)) {
                    //如果暂存区有这个filename
                    if (addFileMap.get(fileName).equals(sha1ForFile(file))) {
                        //如果两个sha1value相同，则不做改变
                    } else {
                        //如果两个sha1value不同，则把原来的暂存区的删除，把新的放到暂存区中
                        String originalSha1Value = addFileMap.get(fileName);
                        join(join(STAGS_DIR, originalSha1Value.substring(0, 2)), originalSha1Value).delete();
                        join(STAGS_DIR, originalSha1Value.substring(0, 2)).delete();
                        addFileMap.remove(fileName);
                        addFileMap.put(fileName, sha1ForFile(file));
                        pushFileToStag(file);

                    }
                } else {
                    //如果暂存区没有这个filename，则把他放到暂存区中（case1）
                    addFileMap.put(fileName, sha1ForFile(file));
                    pushFileToStag(file);
                }

            }
        } else {
            //如果Head没有这个filename
            if (addFileMap.containsKey(fileName)) {
                //如果暂存区中有这个filename
                if (addFileMap.get(fileName).equals(sha1ForFile(file))) {
                    //如果两个sha1value相同，则不做改变
                } else {
                    //如果两个sha1value不同，则把原来的暂存区的删除，把新的放到暂存区中
                    String originalSha1Value = addFileMap.get(fileName);
                    join(join(STAGS_DIR, originalSha1Value.substring(0, 2)), originalSha1Value).delete();
                    join(STAGS_DIR, originalSha1Value.substring(0, 2)).delete();
                    addFileMap.remove(fileName);
                    addFileMap.put(fileName, sha1ForFile(file));
                    pushFileToStag(file);
                }
            } else {
                //如果暂存区没有这个filename，把他放到暂存区中（case1）
                addFileMap.put(fileName, sha1ForFile(file));
                pushFileToStag(file);
            }
        }
        saveForRmFileMap();
        saveForHead();
        saveForAddFileMap();


    }

    /**
     * 1.如果该文件已被加入暂存区，则将其从暂存区移除(不需要测试）
     *
     * 2.如果当前提交已经追踪了某个文件，而用户执行了rm命令，那么这个文件会被标记为移除，即他将不再被当前提交追踪，
     * 并且会被从工作目录中删除（如果之前已经手动删除了，就直接标记为移除，不再被当前提交追踪，不需要从工作目录中删除，
     * 这里只需要一个判断条件就好了，但测试还是两个文件）
     *
     * 3.如果文件既未加入暂存区也未被当前提交追踪，输出：`No reason to remove the file.`
     * @param fileName
     */
    public static void rm(String fileName) {
        //注意顺序，首先需要验证第一个
        loadForAddFileMap();
        loadForRmFileMap();
        loadForHead();
        if (addFileMap.get(fileName) != null) {
            addFileMap.remove(fileName);
            saveForAddFileMap();
            System.exit(0);
        }
        Commit originalCommit = head;
        String originalCommitSha1 = sha1ForObject(originalCommit);
        HashMap<String, String> originalCommitMap = originalCommit.getMap();
        if (!originalCommitMap.containsKey(fileName)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        } else {
            rmFileMap.put(fileName, originalCommitMap.get(fileName));
            if (!join(CWD, fileName).exists()) {

            } else {
                join(CWD, fileName).delete();
            }
        }
        saveForRmFileMap();



    }

    /**
     * 1.如果消息为空，输出：`Please enter a commit message.(已经在main处理了）
     * 2.如果没有文件被暂存，且没有文件被rm，则输出：`No changes added to the commit.`
     * 3.默认提交内容 = 父提交内容 + 本次添加/移除的内容更新
     * 注意：
     * 1.提交后暂存区会被清空
     * 2.用head指向当前新的提交（并且需要save)
     * @param message
     */
    public static void commit(String message) {
        loadForAddFileMap();
        loadForRmFileMap();
        if (addFileMap.size() == 0 && rmFileMap.size() == 0) {
            System.out.println("No changes added to the commit");
            System.exit(0);
        }
        loadForHead();
        Commit originalCommit = head;
        String originalCommitSha1 = sha1ForObject(originalCommit);
        HashMap<String, String> originalCommitMap = originalCommit.getMap();
        HashMap<String, String> newCommitMap = new HashMap<>();
        newCommitMap.putAll(originalCommitMap);
        for (String s : rmFileMap.keySet()) {
            newCommitMap.remove(s);
        }
        newCommitMap.putAll(addFileMap);
        for (String key : newCommitMap.keySet()) {
            String sha1File = newCommitMap.get(key);
            pushFileToBlob(sha1File);
        }
        Commit newCommit = new Commit(message, Instant.now(), originalCommitSha1, null, newCommitMap);
        head = newCommit;
        saveForHead();
        Utils.saveForCommit(newCommit);
        addFileMap = new HashMap<>();
        rmFileMap  = new HashMap<>();
        saveForRmFileMap();
        saveForAddFileMap();
        deleteDirectory(STAGS_DIR);
        STAGS_DIR.mkdir();

    }
    public static void log() {
        loadForHead();
        Commit temp = head;
        while (!sha1ForObject(temp).equals(sha1ForObject(initialCommit))) {
            //即只有一个分支
            if (temp.getParentSha2() == null) {
                showCommitForBasicLog(temp);
                Commit parentCommit = getCommit(temp.getParentSha1());
                temp = parentCommit;
            } else {
                //有两个分支即底下需要做的 TODO:
            }

        }
        //输出的是initialCommit的信息
        showCommitForBasicLog(initialCommit);

    }

    /**
     * Usages:
     * java gitlet.Main checkout -- [file name]
     * java gitlet.Main checkout [commit id] -- [file name]
     * java gitlet.Main checkout [branch name]
     * 这里只需要完成第一种和第二种，第三种以后再做TODO:
     * 第一种：
     * 从当前分支的最新提交（即 HEAD）中取出该文件的版本，并放入工作目录中，覆盖当前已有的同名文件（如果有）。注意这个新文件不会被加入暂存区
     * 第二种：
     * 从指定的提交中取出该文件的版本，并放入工作目录中，同样会覆盖已有的文件（如果有），新文件也不会被加入暂存区
     * 第三种：
     * 从指定分支的最新提交中，取出其中的所有文件并放入工作目录，覆盖已有的版本，然后
     * 1.当前分支变为这个指定的分支（即head指向这个分支）
     * 2.如果当前分支中追踪的某些文件在目标分支中不存在，这些文件会被删除
     * 3.暂存区会被清空，除非你checkout的就是当前分支本身（见失败情况）
     */
    public static void checkout(String[] args) {
        loadForHead();
        if (args.length == 2) {
            //第三种情况
        } else if (args.length == 3) {
            //第一种情况
            if (head.getMap().containsKey(args[2])) {
                //当前分支中有该文件
                String sha1ValueForFile = head.getMap().get(args[2]);
                Utils.writeContents(join(CWD, args[2]), readContents(join(join(BLOBS_DIR, sha1ValueForFile.substring(0, 2)), sha1ValueForFile)));
            } else {
                //当前分支中没有该文件
                error("File does not exist in that commit.");
            }
        } else if (args.length == 4) {
            //第二种情况
            String commitId = args[1];
            Commit temp = head;
            while (!temp.equals(initialCommit)) {
                if (sha1ForObject(temp).equals(commitId)) {
                    break;
                }
                Commit parentCommit = getCommit(temp.getParentSha1());
                temp = parentCommit;
            }
            if (temp.equals(initialCommit)) {
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
            if (temp.getMap().containsKey(args[3])) {
                String sha1ValueForFile = temp.getMap().get(args[3]);
                Utils.writeContents(join(CWD, args[3]), readContents(join(join(BLOBS_DIR, sha1ValueForFile.substring(0, 2)), sha1ValueForFile)));
            } else {
                error("File does not exist in that commit.");
            }

        }
    }


}
