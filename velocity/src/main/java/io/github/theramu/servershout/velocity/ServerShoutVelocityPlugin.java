package io.github.theramu.servershout.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import io.github.theramu.servershout.common.BuildConstants;
import io.github.theramu.dependencyloader.DependencyLoader;
import org.slf4j.Logger;

import java.nio.file.Path;

/**
 * @author TheRamU
 * @since 2024/8/20 22:47
 */
@Plugin(
        id = "servershout",
        name = "ServerShout",
        version = BuildConstants.VERSION,
        authors = {"TheRamU"},
        url = "https://github.com/TheRamU/ServerShout"
)
public class ServerShoutVelocityPlugin {

    @Inject
    private ProxyServer proxyServer;
    @Inject
    private Logger logger;
    @Inject
    @DataDirectory
    private Path dataDirectory;
    private static ServerShoutVelocityPlugin instance;
    private ServerShoutVelocityApi api;

    public static ServerShoutVelocityPlugin getInstance() {
        return instance;
    }

    public static ServerShoutVelocityApi getAPI() {
        return instance.api;
    }

    public static String getVersion() {
        return BuildConstants.VERSION;
    }

    public static Logger getLogger() {
        return instance.logger;
    }

    public static ProxyServer getProxy() {
        return instance.proxyServer;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        String[] dependencies = new String[]{
                "org.jetbrains.kotlin:kotlin-stdlib:1.9.21",
                "org.slf4j:slf4j-api:2.0.16",
                "org.yaml:snakeyaml:2.0",
                "com.mysql:mysql-connector-j:9.2.0",
                "com.zaxxer:HikariCP:5.1.0"
        };
        boolean loaded = new DependencyLoader(java.util.logging.Logger.getLogger("ServerShout")).loadDependencies(dependencies);
        if (!loaded) {
            throw new RuntimeException("Failed to load dependencies");
        }
        instance = this;
        api = new ServerShoutVelocityApi(this, proxyServer, dataDirectory);
        proxyServer.getEventManager().register(this, api);
        api.onEnable();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (api != null) api.onDisable();
    }
}
