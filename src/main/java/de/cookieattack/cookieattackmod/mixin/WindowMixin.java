package de.cookieattack.cookieattackmod.mixin;

import com.mojang.blaze3d.platform.Window;
import de.cookieattack.cookieattackmod.util.WindowHandleUtil;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {

    private static final String CUSTOM_TITLE = "CookieAttack 6";

    @Inject(method = "setTitle", at = @At("HEAD"), cancellable = true)
    private void onSetTitle(String title, CallbackInfo ci) {
        if (Util.getPlatform() == Util.OS.WINDOWS) {
            long handle = WindowHandleUtil.getHandle((Window) (Object) this);
            if (handle != 0) {
                GLFW.glfwSetWindowTitle(handle, CUSTOM_TITLE);
                ci.cancel();
            }
        }
    }
}