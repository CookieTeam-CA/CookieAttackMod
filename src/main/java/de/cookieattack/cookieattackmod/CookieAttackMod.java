package de.cookieattack.cookieattackmod;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CookieAttackMod implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("CookieAttackMod");
    private static boolean startupIconApplied = false;
    private static byte[] pendingStartupIconBytes = null;

    @Override
    public void onInitializeClient() {
        applyStartupIcon();
    }

    public static void applyStartupIcon() {
        if (startupIconApplied) return;

        try (InputStream inputStream = CookieAttackMod.class.getResourceAsStream("/assets/icon.png")) {
            if (inputStream == null) {
                LOGGER.error("Could not find icon.");
                return;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }

            pendingStartupIconBytes = outputStream.toByteArray();
            applyPendingStartupIcon();
        } catch (IOException e) {
            LOGGER.error("Could not load icon", e);
        }
    }

    public static void applyPendingStartupIcon() {
        if (startupIconApplied || pendingStartupIconBytes == null) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.getWindow() == null) {
            return;
        }

        try (NativeImage image = NativeImage.read(pendingStartupIconBytes)) {
            ((IconSetter) minecraft).setIcon(image);
        } catch (IOException e) {
            LOGGER.error("Could not set icon", e);
        }
    }

    public static void markStartupIconApplied() {
        startupIconApplied = true;
        pendingStartupIconBytes = null;
    }
}