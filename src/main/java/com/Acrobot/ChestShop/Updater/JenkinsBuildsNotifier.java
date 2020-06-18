package com.Acrobot.ChestShop.Updater;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

public class JenkinsBuildsNotifier implements Runnable {
    private final Plugin plugin;

    private final File configFile;
    private final FileConfiguration config;

    private int build;
    private URL apiUrl;

    public JenkinsBuildsNotifier(Plugin plugin, String jenkinsJobUrl) {
        this.plugin = plugin;

        configFile = new File(plugin.getDataFolder(), "jenkinsBuildsNotifier.yml");
        if (configFile.exists()) {
            config = YamlConfiguration.loadConfiguration(configFile);
        } else {
            config = new YamlConfiguration();
            config.addDefault("disabled", false);
            config.addDefault("jenkins-job-url-override", "");
            config.options().copyDefaults(true);
            saveConfig();
        }

        if (config.getBoolean("disabled")) {
            return;
        }

        build = getBuildNumber();
        if (build < 1) {
            plugin.getLogger().log(Level.WARNING, "Unable to parse plugin build from version string! (" + plugin.getDescription().getVersion() + ")");
            return;
        }

        String jenkinsJobUrlOverride = config.getString("jenkins-job-url-override");
        if (jenkinsJobUrlOverride != null && !jenkinsJobUrlOverride.isEmpty()) {
            jenkinsJobUrl = jenkinsJobUrlOverride;
        }

        try {
            apiUrl = new URL(jenkinsJobUrl + "api/json");
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this);
        } catch (MalformedURLException e) {
            plugin.getLogger().log(Level.WARNING, "Can not check for new dev builds as " + jenkinsJobUrl + "api/json is not a valid url!", e);
        }
    }

    private void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Error while saving " + configFile.getName() + "! " + e.getMessage());
        }
    }

    private int getBuildNumber() {
        String tag = "(build ";
        String versionStr = plugin.getDescription().getVersion();
        int start = versionStr.indexOf(tag);
        int end = versionStr.indexOf(')', start);
        if (start > 0 && end > start) {
            try {
                return Integer.parseInt(versionStr.substring(start + tag.length(), end));
            } catch (NumberFormatException ignored) {}
        }
        return -1;
    }

    @Override
    public void run() {
        try {
            JsonObject responseJson = queryJson();
            if (responseJson.has("lastStableBuild") && responseJson.get("lastStableBuild").isJsonObject()) {
                JsonObject lastStable = responseJson.getAsJsonObject("lastStableBuild");
                int lastStableBuild = lastStable.get("number").getAsInt();
                if (lastStableBuild > build && config.getInt("last-announced-build", 0) < lastStableBuild) {
                    String url = lastStable.get("url").getAsString();
                    plugin.getLogger().log(Level.INFO, "A new development build is available for testing: " + url);

                    config.set("last-announced-build", lastStableBuild);
                    saveConfig();
                }
            }

        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error while trying to query Jenkins API", e);
        }
    }

    private JsonObject queryJson() throws IOException {
        HttpURLConnection con = (HttpURLConnection) apiUrl.openConnection();
        con.setRequestProperty("User-Agent", plugin.getName() + " " + plugin.getDescription().getVersion() + " Jenkins Builds Notifier");
        con.setRequestMethod("GET");
        StringBuilder msg = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line;
        while((line = in.readLine()) != null) {
            if(msg.length() != 0) {
                msg.append("\n");
            }
            msg.append(line);
        }
        in.close();

        return new JsonParser().parse(msg.toString()).getAsJsonObject();
    }
}
