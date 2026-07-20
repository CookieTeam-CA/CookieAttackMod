package de.cookieattack.cookieattackmod.util;

import com.mojang.blaze3d.platform.Window;
import de.cookieattack.cookieattackmod.CookieAttackMod;

import java.lang.reflect.Field;

public class WindowHandleUtil {

    private static Field handleField = null;

    static {
        try {
            try {
                handleField = Window.class.getDeclaredField("window");
                handleField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                try {
                    handleField = Window.class.getDeclaredField("handle");
                    handleField.setAccessible(true);
                } catch (NoSuchFieldException e2) {
                    for (Field field : Window.class.getDeclaredFields()) {
                        if (field.getType() == long.class) {
                            field.setAccessible(true);
                            if (handleField == null) handleField = field;
                        }
                    }
                    if (handleField == null) {
                        CookieAttackMod.LOGGER.error("Could not find any GLFW window handle field");
                    }
                }
            }
        } catch (Exception e) {
            CookieAttackMod.LOGGER.error("Failed to initialize WindowHandleUtil", e);
        }
    }

    public static long getHandle(Window window) {
        if (handleField == null || window == null) return 0;
        try {
            return handleField.getLong(window);
        } catch (IllegalAccessException e) {
            CookieAttackMod.LOGGER.error("Failed to get window handle", e);
            return 0;
        }
    }
}