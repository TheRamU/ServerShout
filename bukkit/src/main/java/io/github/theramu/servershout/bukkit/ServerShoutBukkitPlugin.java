package io.github.theramu.servershout.bukkit;

import io.github.theramu.dependencyloader.DependencyLoader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author TheRamU
 * @since 2024/8/22 5:05
 */
public class ServerShoutBukkitPlugin extends JavaPlugin {

    private static ServerShoutBukkitPlugin instance;
    private ServerShoutBukkitApi api;
    private boolean loaded = false;

    public static ServerShoutBukkitPlugin getInstance() {
        return instance;
    }

    public static ServerShoutBukkitApi getAPI() {
        return instance.api;
    }

    @Override
    public void onLoad() {
        String[] dependencies = {
                "org.jetbrains.kotlin:kotlin-stdlib:1.9.21",
                "org.slf4j:slf4j-api:2.0.16",
                "org.yaml:snakeyaml:2.0",
                "com.mysql:mysql-connector-j:9.0.0",
                "com.zaxxer:HikariCP:4.0.3",
                "net.kyori:adventure-api:4.17.0",
                "net.kyori:adventure-key:4.17.0",
                "net.kyori:adventure-text-serializer-json:4.17.0",
                "net.kyori:adventure-text-serializer-gson:4.17.0",
                "net.kyori:adventure-text-serializer-legacy:4.17.0",
                "net.kyori:examination-api:1.3.0",
                "net.kyori:option:1.0.0"
        };
        loaded = new DependencyLoader(getLogger()).loadDependencies(dependencies);
        if (!loaded) {
            Bukkit.getPluginManager().disablePlugin(this);
            throw new RuntimeException("Failed to load dependencies");
        }
    }

    @Override
    public void onEnable() {
        if (!loaded) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        instance = this;
        api = new ServerShoutBukkitApi(this);
        api.onEnable();
    }

    @Override
    public void onDisable() {
        if (api != null) api.onDisable();
    }
}