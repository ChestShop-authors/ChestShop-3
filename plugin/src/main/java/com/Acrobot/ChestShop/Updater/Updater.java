/*
 * Updater for Bukkit.
 *
 * This class provides the means to safely and easily update a plugin, or check to see if it is updated using dev.bukkit.org
 */

package com.Acrobot.ChestShop.Updater;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Check dev.bukkit.org to find updates for a given plugin, and download the updates if needed.
 * <p/>
 * <b>VERY, VERY IMPORTANT</b>: Because there are no standards for adding auto-update toggles in your plugin's config, this system provides NO CHECK WITH YOUR CONFIG to make sure the user has allowed auto-updating.
 * <br>
 * It is a <b>BUKKIT POLICY</b> that you include a boolean value in your config that prevents the auto-updater from running <b>AT ALL</b>.
 * <br>
 * If you fail to include this option in your config, your plugin will be <b>REJECTED</b> when you attempt to submit it to dev.bukkit.org.
 * <p/>
 * An example of a good configuration option would be something similar to 'auto-update: true' - if this value is set to false you may NOT run the auto-updater.
 * <br>
 * If you are unsure about these rules, please read the plugin submission guidelines: http://goo.gl/8iU5l
 *
 * @author Gravity
 * @version 2.1
 */

public final class Updater {

    private Plugin plugin;
    private UpdateType type;
    private String versionName;
    private String versionLink;
    private String versionHash;
    private String versionType;
    private String versionGameVersion;

    private boolean announce; // Whether to announce file downloads

    private URL url; // Connecting to RSS
    private File file; // The plugin's file
    private Thread thread; // Updater thread

    private String id; // Project's Curse ID
    private String apiKey = null; // Modrinth API key
    private static final String TITLE_VALUE = "version_number"; // Gets remote version
    private static final String FILES_VALUE = "files"; // Gets all files associated with that version
    private static final String LINK_VALUE = "url"; // Gets remote file's download link
    private static final String TYPE_VALUE = "version_type"; // Gets remote file's release type
    private static final String VERSION_VALUE = "game_versions"; // Gets remote file's build version
    private static final String QUERY = "/v2/project/%projectid%/version"; // Path to GET
    private static final String HOST = "https://api.modrinth.com"; // Slugs will be appended to this to get to the project's versions

    private static final String USER_AGENT = "Updater v2.1 (by Gravity) - Modified by Phoenix616 for Modrinth";
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+\\.\\d+(?>\\.\\d+)?)"); // Used for locating version numbers in file names
    private static final String[] NO_UPDATE_TAG = { "-DEV", "-PRE", "-SNAPSHOT" }; // If the version number contains one of these, don't update.
    private static final int BYTE_SIZE = 1024; // Used for downloading files
    private final YamlConfiguration config = new YamlConfiguration(); // Config file
    private Updater.UpdateResult result = Updater.UpdateResult.SUCCESS; // Used for determining the outcome of the update process

    /**
     * Gives the developer the result of the update process. Can be obtained by called {@link #getResult()}
     */
    public enum UpdateResult {
        /**
         * The updater found an update, and has readied it to be loaded the next time the server restarts/reloads.
         */
        SUCCESS,
        /**
         * The updater did not find an update, and nothing was downloaded.
         */
        NO_UPDATE,
        /**
         * The server administrator has disabled the updating system.
         */
        DISABLED,
        /**
         * The updater found an update, but was unable to download it.
         */
        FAIL_DOWNLOAD,
        /**
         * For some reason, the updater was unable to contact dev.bukkit.org to download the file.
         */
        FAIL_DBO,
        /**
         * When running the version check, the file on DBO did not contain a recognizable version.
         */
        FAIL_NOVERSION,
        /**
         * The id provided by the plugin running the updater was invalid and doesn't exist on DBO.
         */
        FAIL_BADID,
        /**
         * The server administrator has improperly configured their API key in the configuration.
         */
        FAIL_APIKEY,
        /**
         * The updater found an update, but because of the UpdateType being set to NO_DOWNLOAD, it wasn't downloaded.
         */
        UPDATE_AVAILABLE,
        /**
         * The downloaded file does not match the SHA1 hash sum provided by the api.
         */
        FAIL_HASH,
    }

    /**
     * Allows the developer to specify the type of update that will be run.
     */
    public enum UpdateType {
        /**
         * Run a version check, and then if the file is out of date, download the newest version.
         */
        DEFAULT,
        /**
         * Don't run a version check, just find the latest update and download it.
         */
        NO_VERSION_CHECK,
        /**
         * Get information about the version and the download size, but don't actually download anything.
         */
        NO_DOWNLOAD
    }

    /**
     * Represents the various release types of a file on BukkitDev.
     */
    public enum ReleaseType {
        /**
         * An "alpha" file.
         */
        ALPHA,
        /**
         * A "beta" file.
         */
        BETA,
        /**
         * A "release" file.
         */
        RELEASE
    }

    /**
     * Initialize the updater.
     *
     * @param plugin   The plugin that is checking for an update.
     * @param id       The id of the project.
     * @param file     The file that the plugin is running from, get this by doing this.getFile() from within your main class.
     * @param type     Specify the type of update this will be. See {@link UpdateType}
     * @param announce True if the program should announce the progress of new updates in console.
     */
    public Updater(Plugin plugin, String id, File file, UpdateType type, boolean announce) {
        this.plugin = plugin;
        this.type = type;
        this.announce = announce;
        this.file = file;
        this.id = id;

        final File pluginFile = plugin.getDataFolder().getParentFile();
        final File updaterFile = new File(pluginFile, "Updater");
        final File updaterConfigFile = new File(updaterFile, "config.yml");

        this.config.options().header("This configuration file affects all plugins using the Updater system (version 2+ )" + '\n'
                + "If you wish to use your API key, then you can get it from https://modrinth.com/settings/pats and place it below." + '\n'
                + "Some updating systems will not adhere to the disabled value, but these may be turned off in their plugin's configuration.");
        this.config.addDefault("modrinth-key", "PUT_PAT_HERE");
        this.config.addDefault("disable", false);

        if (!updaterFile.exists()) {
            updaterFile.mkdir();
        }

        boolean createFile = !updaterConfigFile.exists();
        try {
            if (createFile) {
                updaterConfigFile.createNewFile();
                this.config.options().copyDefaults(true);
                this.config.save(updaterConfigFile);
            } else {
                this.config.load(updaterConfigFile);
            }
        } catch (final Exception e) {
            if (createFile) {
                plugin.getLogger().severe("The updater could not create configuration at " + updaterFile.getAbsolutePath());
            } else {
                plugin.getLogger().severe("The updater could not load configuration at " + updaterFile.getAbsolutePath());
            }
            plugin.getLogger().log(Level.SEVERE, null, e);
        }

        if (this.config.getBoolean("disable")) {
            this.result = UpdateResult.DISABLED;
            return;
        }

        String key = this.config.getString("api-key");
        if (key != null && (key.equalsIgnoreCase("PUT_API_KEY_HERE") || key.equals(""))) {
            key = null;
        }

        this.apiKey = key;

        try {
            this.url = new URL(Updater.HOST + Updater.QUERY.replace("%projectid%", id));
        } catch (final MalformedURLException e) {
            plugin.getLogger().log(Level.SEVERE, "The project ID provided for updating, " + id + " is invalid.", e);
            this.result = UpdateResult.FAIL_BADID;
        }

        this.thread = new Thread(new UpdateRunnable());
        this.thread.start();
    }

    /**
     * Get the result of the update process.
     *
     * @return result of the update process.
     * @see UpdateResult
     */
    public Updater.UpdateResult getResult() {
        this.waitForThread();
        return this.result;
    }

    /**
     * Get the latest version's release type.
     *
     * @return latest version's release type.
     * @see ReleaseType
     */
    public ReleaseType getLatestType() {
        this.waitForThread();
        return getLatestTypeInternal();
    }

    /**
     * Get the latest version's release type without waiting for the thread to finish.
     *
     * @return latest version's release type.
     * @see ReleaseType
     */
    private ReleaseType getLatestTypeInternal() {
        if (this.versionType != null) {
            for (ReleaseType type : ReleaseType.values()) {
                if (this.versionType.equals(type.name().toLowerCase(Locale.ROOT))) {
                    return type;
                }
            }
        }
        return null;
    }

    /**
     * Get the latest version's game version (such as "CB 1.2.5-R1.0").
     *
     * @return latest version's game version.
     */
    public String getLatestGameVersion() {
        this.waitForThread();
        return this.versionGameVersion;
    }

    /**
     * Get the latest version's name (such as "Project v1.0").
     *
     * @return latest version's name.
     */
    public String getLatestName() {
        this.waitForThread();
        return this.versionName;
    }

    /**
     * Get the latest version's direct file link.
     *
     * @return latest version's file link.
     */
    public String getLatestFileLink() {
        this.waitForThread();
        return this.versionLink;
    }

    /**
     * As the result of Updater output depends on the thread's completion, it is necessary to wait for the thread to finish
     * before allowing anyone to check the result.
     */
    private void waitForThread() {
        if ((this.thread != null) && this.thread.isAlive()) {
            try {
                this.thread.join();
            } catch (final InterruptedException e) {
                plugin.getLogger().log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * Save an update from dev.bukkit.org into the server's update folder.
     *
     * @param folder the updates folder location.
     * @param file the name of the file to save it as.
     * @param link the url of the file.
     */
    private void saveFile(File folder, String file, String link) {
        if (!folder.exists()) {
            folder.mkdir();
        }
        try {
            // Download the file
            final URL url = new URL(link);
            final int fileLength = url.openConnection().getContentLength();
            final File targetFile = new File(folder, file);
            try (BufferedInputStream in = new BufferedInputStream(url.openStream());
                 FileOutputStream fout = new FileOutputStream(targetFile)) {

                final byte[] data = new byte[Updater.BYTE_SIZE];
                int count;
                if (this.announce) {
                    this.plugin.getLogger().info("About to download a new update: " + this.versionName);
                }
                long downloaded = 0;
                int lastAnnouncePercent = 0;
                while ((count = in.read(data, 0, Updater.BYTE_SIZE)) != -1) {
                    downloaded += count;
                    fout.write(data, 0, count);
                    final int percent = (int) ((downloaded * 100) / fileLength);
                    if (this.announce && lastAnnouncePercent != percent && ((percent % 10) == 0)) {
                        lastAnnouncePercent = percent;
                        this.plugin.getLogger().info("Downloading update: " + percent + "% of " + fileLength + " bytes.");
                    }
                }
                // Check sha1 sum of the downloaded file
                if (this.versionHash != null) {
                    final String fileHash = Files.asByteSource(targetFile).hash(Hashing.sha512()).toString();
                    if (!this.versionHash.equalsIgnoreCase(fileHash)) {
                        this.plugin.getLogger().warning("Downloaded file " + file + " does not match the remote file's SHA-1 hash");
                        this.result = UpdateResult.FAIL_HASH;
                        return;
                    }
                }
                //Just a quick check to make sure we didn't leave any files from last time...
                File[] files = this.plugin.getServer().getUpdateFolderFile().listFiles();
                if (files != null) {
                    for (final File xFile : files) {
                        if (xFile.getName().endsWith(".zip")) {
                            xFile.delete();
                        }
                    }
                }
                // Check to see if it's a zip file, if it is, unzip it.
                final File dFile = new File(folder.getAbsolutePath(), file);
                if (dFile.getName().endsWith(".zip")) {
                    // Unzip
                    this.unzip(dFile.getCanonicalPath());
                }
                if (this.announce) {
                    this.plugin.getLogger().info("Finished updating.");
                }
            }
        } catch (final Exception ex) {
            this.plugin.getLogger().warning("The auto-updater tried to download a new update, but was unsuccessful.");
            this.result = Updater.UpdateResult.FAIL_DOWNLOAD;
        }
    }

    /**
     * Part of Zip-File-Extractor, modified by Gravity for use with Updater.
     *
     * @param file the location of the file to extract.
     */
    private void unzip(String file) {
        final File fSourceZip = new File(file);
        final String zipPath = file.substring(0, file.length() - 4);
        try (ZipFile zipFile = new ZipFile(fSourceZip)) {
            Enumeration<? extends ZipEntry> e = zipFile.entries();
            while (e.hasMoreElements()) {
                ZipEntry entry = e.nextElement();
                File destinationFilePath = new File(zipPath, entry.getName());
                destinationFilePath.getParentFile().mkdirs();
                if (!entry.isDirectory()) {
                    final BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
                    int b;
                    final byte[] buffer = new byte[Updater.BYTE_SIZE];
                    final FileOutputStream fos = new FileOutputStream(destinationFilePath);
                    final BufferedOutputStream bos = new BufferedOutputStream(fos, Updater.BYTE_SIZE);
                    while ((b = bis.read(buffer, 0, Updater.BYTE_SIZE)) != -1) {
                        bos.write(buffer, 0, b);
                    }
                    bos.flush();
                    bos.close();
                    bis.close();
                    final String name = destinationFilePath.getName();
                    if (name.endsWith(".jar") && this.pluginFile(name)) {
                        destinationFilePath.renameTo(new File(this.plugin.getServer().getUpdateFolderFile(),name));
                    }
                }
            }
            zipFile.close();

            // Move any plugin data folders that were included to the right place, Bukkit won't do this for us.
            File[] files = new File(zipPath).listFiles();
            if (files != null) {
                for (final File dFile : files) {
                    if (dFile.isDirectory()) {
                        if (this.pluginFile(dFile.getName())) {
                            final File oFile = new File(this.plugin.getDataFolder().getParent(), dFile.getName()); // Get current dir
                            final File[] contents = oFile.listFiles(); // List of existing files in the current dir
                            final File[] newFiles = dFile.listFiles();
                            if (newFiles != null) {
                                for (final File cFile : newFiles) // Loop through all the files in the new dir
                                {
                                    boolean found = false;
                                    if (contents != null) {
                                        for (final File xFile : contents) // Loop through contents to see if it exists
                                        {
                                            if (xFile.getName().equals(cFile.getName())) {
                                                found = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (!found) {
                                        // Move the new file into the current dir
                                        cFile.renameTo(new File(oFile.getCanonicalFile(), cFile.getName()));
                                    } else {
                                        // This file already exists, so we don't need it anymore.
                                        cFile.delete();
                                    }
                                }
                            }
                        }
                    }
                    dFile.delete();
                }
            }
            new File(zipPath).delete();
            fSourceZip.delete();
        } catch (final IOException e) {
            this.plugin.getLogger().log(Level.SEVERE, "The auto-updater tried to unzip a new update file, but was unsuccessful.", e);
            this.result = Updater.UpdateResult.FAIL_DOWNLOAD;
        }
        new File(file).delete();
    }

    /**
     * Check if the name of a jar is one of the plugins currently installed, used for extracting the correct files out of a zip.
     *
     * @param name a name to check for inside the plugins folder.
     * @return true if a file inside the plugins folder is named this.
     */
    private boolean pluginFile(String name) {
        File[] files = new File("plugins").listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check to see if the program should continue by evaluating whether the plugin is already updated, or shouldn't be updated.
     *
     * @param title the plugin's title.
     * @return true if the version was located and is not the same as the remote's newest.
     */
    private boolean versionCheck(String title) {
        if (this.type != UpdateType.NO_VERSION_CHECK) {
            final String rawLocalVersion = this.plugin.getDescription().getVersion();
            final Matcher localMatcher = Updater.VERSION_PATTERN.matcher(rawLocalVersion);
            final Matcher titleMatcher = Updater.VERSION_PATTERN.matcher(title);
            if (titleMatcher.find() && localMatcher.find()) {
                final String localVersion = localMatcher.group(1); // Get the plugins version number
                final String remoteVersion = titleMatcher.group(1); // Get the newest file's version number

                if (this.hasTag(rawLocalVersion) || !this.shouldUpdate(localVersion, remoteVersion)) {
                    // We already have the latest version, or this build is tagged for no-update
                    this.result = Updater.UpdateResult.NO_UPDATE;
                    return false;
                }
            } else {
                // The file's name did not contain the string 'vVersion', which means it is not a proper plugin file
                this.result = Updater.UpdateResult.FAIL_NOVERSION;
                return false;
            }
        }
        return true;
    }

    /**
     * <b>If you wish to run mathematical versioning checks, edit this method.</b>
     * <p>
     * With default behavior, Updater will NOT verify that a remote version available on BukkitDev
     * which is not this version is indeed an "update".
     * If a version is present on BukkitDev that is not the version that is currently running,
     * Updater will assume that it is a newer version.
     * This is because there is no standard versioning scheme, and creating a calculation that can
     * determine whether a new update is actually an update is sometimes extremely complicated.
     * </p>
     * <p>
     * Updater will call this method from {@link #versionCheck(String)} before deciding whether
     * the remote version is actually an update.
     * If you have a specific versioning scheme with which a mathematical determination can
     * be reliably made to decide whether one version is higher than another, you may
     * revise this method, using the local and remote version parameters, to execute the
     * appropriate check.
     * </p>
     * <p>
     * Returning a value of <b>false</b> will tell the update process that this is NOT a new version.
     * Without revision, this method will always consider a remote version at all different from
     * that of the local version a new update.
     * </p>
     * @param localVersion the current version
     * @param remoteVersion the remote version
     * @return true if Updater should consider the remote version an update, false if not.
     */
    public boolean shouldUpdate(String localVersion, String remoteVersion) {
        if (this.type != Updater.UpdateType.NO_DOWNLOAD && localVersion.contains("DEV") || getLatestTypeInternal() != ReleaseType.RELEASE) {
            return false; //Do not download alphas or betas
        }

        if (localVersion.equalsIgnoreCase(remoteVersion)) {
            return true; //Already the same version
        }

        try {
            int[] localSemanticVersion = parseVersion(localVersion);
            int[] remoteSemanticVersion = parseVersion(remoteVersion);

            for (int i = 0; i < localSemanticVersion.length; i++) {
                if (remoteSemanticVersion.length < i + 1) {
                    return false;
                }
                if (localSemanticVersion[i] < remoteSemanticVersion[i]) {
                    return true;
                } else if (localSemanticVersion[i] > remoteSemanticVersion[i]) {
                    return false;
                }
            }
            return false;
        } catch (NumberFormatException e) {
            this.plugin.getLogger().warning("Invalid version number found: " + localVersion + " or " + remoteVersion);
            return true;
        }
    }

    /**
     * Parse the version number from a string. This expects the version number to be consisting of numbers separated by dots.
     *
     * @param version the version string to parse
     * @return the parsed version number
     * @throws NumberFormatException if the version number is not in the expected format
     */
    private int[] parseVersion(String version) {
        final String[] split = version.split("\\.");
        final int[] semanticVersion = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            semanticVersion[i] = Integer.parseInt(split[i]);
        }
        return semanticVersion;
    }

    /**
     * Evaluate whether the version number is marked showing that it should not be updated by this program.
     *
     * @param version a version number to check for tags in.
     * @return true if updating should be disabled.
     */
    private boolean hasTag(String version) {
        for (final String string : Updater.NO_UPDATE_TAG) {
            if (version.contains(string)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Make a connection to the BukkitDev API and request the newest file's details.
     *
     * @return true if successful.
     */
    private boolean read() {
        try {
            final URLConnection conn = this.url.openConnection();
            conn.setConnectTimeout(5000);

            if (this.apiKey != null) {
                conn.addRequestProperty("Authorization", this.apiKey);
            }
            conn.addRequestProperty("User-Agent", Updater.USER_AGENT);

            conn.setDoOutput(true);

            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final String response = reader.readLine();

            final JSONArray array = (JSONArray) JSONValue.parse(response);

            if (array.size() == 0) {
                this.plugin.getLogger().warning("The updater could not find any files for the project id " + this.id);
                this.result = UpdateResult.FAIL_BADID;
                return false;
            }

            this.versionName = (String) ((JSONObject) array.get(array.size() - 1)).get(Updater.TITLE_VALUE);
            JSONArray versionFiles = (JSONArray) ((JSONObject) array.get(array.size() - 1)).get(Updater.FILES_VALUE);
            for (Object versionFile : versionFiles) {
                JSONObject file = (JSONObject) versionFile;
                if (file.get("primary").equals(true)) {
                    versionLink = (String) file.get(Updater.LINK_VALUE);
                    versionHash = ((JSONObject) file.get("hashes")).get("sha512").toString();
                    break;
                }
            }
            this.versionType = (String) ((JSONObject) array.get(array.size() - 1)).get(Updater.TYPE_VALUE);
            JSONArray gameVersions = (JSONArray) ((JSONObject) array.get(array.size() - 1)).get(Updater.VERSION_VALUE);
            this.versionGameVersion = gameVersions.get(gameVersions.size() - 1).toString();
            return true;
        } catch (final IOException e) {
            if (e.getMessage().contains("HTTP response code: 403")) {
                this.plugin.getLogger().severe("The Modrinth API server rejected the API key provided in plugins/Updater/config.yml");
                this.plugin.getLogger().severe("Please double-check your configuration to ensure it is correct.");
                this.result = UpdateResult.FAIL_APIKEY;
            } else {
                this.plugin.getLogger().severe("The updater could not contact the api.modrinth.com server for updating.");
                this.plugin.getLogger().severe("If you have not recently modified your configuration and this is the first time you are seeing this message, the site may be experiencing temporary downtime.");
                this.result = UpdateResult.FAIL_DBO;
            }
            this.plugin.getLogger().log(Level.SEVERE, null, e);
            return false;
        }
    }

    private class UpdateRunnable implements Runnable {

        @Override
        public void run() {
            if (Updater.this.url != null) {
                // Obtain the results of the project's file feed
                if (Updater.this.read()) {
                    if (Updater.this.versionCheck(Updater.this.versionName)) {
                        if ((Updater.this.versionLink != null) && (Updater.this.type != UpdateType.NO_DOWNLOAD)) {
                            String name = Updater.this.file.getName();
                            // If it's a zip file, it shouldn't be downloaded as the plugin's name
                            if (Updater.this.versionLink.endsWith(".zip")) {
                                final String[] split = Updater.this.versionLink.split("/");
                                name = split[split.length - 1];
                            }
                            Updater.this.saveFile(Updater.this.plugin.getServer().getUpdateFolderFile(), name, Updater.this.versionLink);
                        } else {
                            Updater.this.result = UpdateResult.UPDATE_AVAILABLE;
                        }
                    }
                }
            }
        }
    }
}
