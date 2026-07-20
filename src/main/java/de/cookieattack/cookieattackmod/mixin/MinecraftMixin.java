package de.cookieattack.cookieattackmod.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import de.cookieattack.cookieattackmod.CookieAttackMod;
import de.cookieattack.cookieattackmod.IconSetter;
import de.cookieattack.cookieattackmod.util.WindowHandleUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements IconSetter {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        CookieAttackMod.applyPendingStartupIcon();
    }

    @Override
    public void setIcon(NativeImage icon) {
        if (icon == null) return;

        if (Util.getPlatform() == Util.OS.OSX) {
            CookieAttackMod.LOGGER.error("sowy but mac is currently not supported :c");
            return;
        }

        Window window = Minecraft.getInstance().getWindow();
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            GLFWImage.Buffer buffer = GLFWImage.malloc(1, memoryStack);
            ByteBuffer byteBuffer = MemoryUtil.memAlloc(icon.getWidth() * icon.getHeight() * 4);
            byteBuffer.asIntBuffer().put(icon.getPixelsABGR());
            buffer.position(0);
            buffer.width(icon.getWidth());
            buffer.height(icon.getHeight());
            buffer.pixels(byteBuffer);

            long handle = WindowHandleUtil.getHandle(window);
            if (handle == 0) {
                MemoryUtil.memFree(byteBuffer);
                return;
            }

            GLFW.glfwSetWindowIcon(handle, buffer.position(0));

            MemoryUtil.memFree(byteBuffer);
            CookieAttackMod.markStartupIconApplied();
        }
    }
}