package gg.nodus.gaslight.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.ChatSelectionScreen;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;
import java.net.URISyntaxException;

@Mixin(ChatSelectionScreen.class)
public abstract class MixinChatSelectionScreen extends Screen {

    protected MixinChatSelectionScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void init(CallbackInfo ci) {
        this.addDrawableChild(
                new PressableTextWidget(2, this.height - 10, MinecraftClient.getInstance().textRenderer.getWidth("Gaslight - nodus.gg"), 10, Text.of("§aGaslight - nodus.gg"), (button) -> {
                    try {
                        Util.getOperatingSystem().open(new URI("https://nodus.gg"));
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }, this.textRenderer));
    }

}
