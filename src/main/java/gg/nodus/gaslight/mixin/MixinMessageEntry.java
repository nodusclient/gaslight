package gg.nodus.gaslight.mixin;

import gg.nodus.gaslight.Gaslight;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.ChatSelectionScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatSelectionScreen.SelectionListWidget.MessageEntry.class)
public abstract class MixinMessageEntry {

    @Final
    @Shadow
    private boolean fromReportedPlayer;

    private int buttonHovered = -1;
    @Shadow
    @Final
    private int index;

    @Shadow
    public abstract boolean isSelected();

    @Shadow
    protected abstract boolean toggle();

    @ModifyArg(method = "render", index = 5, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawableHelper;drawTextWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)V"))
    public int redirect(final int x) {
        if (Gaslight.REMOVED_MESSAGE_INDEXES.contains(this.index)) {
            return 0xFFFF0000;
        }
        if (fromReportedPlayer) {
            return 0xFFFFFFFF;
        }
        return -1593835521;
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(final double mouseX, final double mouseY, final int button, final CallbackInfoReturnable<Boolean> cir) {
        if (button == 0) {
            if (buttonHovered == 2) {
                if (!Gaslight.REMOVED_MESSAGE_INDEXES.remove(this.index)) {
                    Gaslight.REMOVED_MESSAGE_INDEXES.add(this.index);
                }
                if (this.isSelected()) {
                    this.toggle();
                }
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                cir.setReturnValue(true);
            } else {
                if (Gaslight.REMOVED_MESSAGE_INDEXES.contains(this.index)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(final MatrixStack matrices, final int index, final int y, final int x, final int entryWidth, final int entryHeight, final int mouseX, final int mouseY, final boolean hovered, final float tickDelta, final CallbackInfo ci) {
        if (mouseY > y + entryHeight || y > mouseY) {
            return;
        }

        final int deleteX = x + entryWidth + 2;
        drawButton(matrices, deleteX, y, entryHeight, "X", true);

        if (isMouseOver(deleteX, y, entryHeight, mouseX, mouseY)) {
            buttonHovered = 2;
        } else {
            buttonHovered = -1;
        }
    }

    private boolean isMouseOver(final int x, final int y, final int size, final int mouseX, final int mouseY) {
        return mouseX >= x && mouseX <= x + size + 1 && mouseY > y && mouseY < y + size;
    }

    private void drawButton(final MatrixStack matrices, final int x, final int y, final int size, final String text, final boolean enabled) {
        Screen.fill(matrices, x, y, x + size + 1, y + size, 0xFFCCCCCC);
        Screen.fill(matrices, x + 1, y + 1, x + size, y + size - 1, 0xFF6E6E6E);
        Screen.drawCenteredTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, text, x + (size / 2) + 1, y + (size / 2) - (MinecraftClient.getInstance().textRenderer.fontHeight / 2), 0xFFFFFFFF);
        if (!enabled) {
            Screen.fill(matrices, x, y, x + size + 1, y + size, 0x99000000);
        }
    }

}
