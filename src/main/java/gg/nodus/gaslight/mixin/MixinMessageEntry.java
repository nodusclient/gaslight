package gg.nodus.gaslight.mixin;

import gg.nodus.gaslight.Gaslight;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.abusereport.ChatSelectionScreen;
import net.minecraft.client.network.chat.ChatLogImpl;
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

    @Shadow
    @Final
    private int index;

    @Shadow
    public abstract boolean isSelected();

    @Shadow
    protected abstract boolean toggle();

    private int buttonHovered = -1;

    @ModifyArg(method = "render", index = 5, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawableHelper;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)V"))
    public int redirect(int x) {
        if (Gaslight.removedIndexes.contains(index)) {
            return 0xFFFF0000;
        }
        if (fromReportedPlayer) {
            return 0xFFFFFFFF;
        }
        return -1593835521;
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button == 0) {
            switch (buttonHovered) {
                case 0 -> {
                    if (index == 0) {
                        return;
                    }
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

                    swapMessages(index, index - 1);
                    refreshScreen();
                    cir.setReturnValue(true);
                }
                case 1 -> {
                    var chatLog = (AccessorChatLogImplMixin) ((AccessorMixinChatSelectionScreen) MinecraftClient.getInstance().currentScreen).getReporter().chatLog();
                    if (index == ((ChatLogImpl) chatLog).getMaxIndex()) {
                        return;
                    }
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

                    swapMessages(index, index + 1);
                    refreshScreen();
                    cir.setReturnValue(true);
                }
                case 2 -> {
                    if (!Gaslight.removedIndexes.remove(index)) {
                        Gaslight.removedIndexes.add(index);
                    }
                    if (this.isSelected()) {
                        this.toggle();
                    }
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    cir.setReturnValue(true);
                }
                default -> {
                    if (Gaslight.removedIndexes.contains(index)) {
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }

    private void swapMessages(int index, int index2) {
        var chatLog = (AccessorChatLogImplMixin) ((AccessorMixinChatSelectionScreen) MinecraftClient.getInstance().currentScreen).getReporter().chatLog();
        var message = chatLog.getMessages()[index];
        chatLog.getMessages()[index] = chatLog.getMessages()[index2];
        chatLog.getMessages()[index2] = message;

        if (Gaslight.removedIndexes.contains(index) && !Gaslight.removedIndexes.contains(index2)) {
            Gaslight.removedIndexes.remove(index);
            Gaslight.removedIndexes.add(index2);
        } else if (Gaslight.removedIndexes.contains(index2)) {
            Gaslight.removedIndexes.remove(index2);
            Gaslight.removedIndexes.add(index);
        }
    }

    private void refreshScreen() {
        var parent = ((AccessorChatReportScreen) ((AccessorMixinChatSelectionScreen) MinecraftClient.getInstance().currentScreen).getParent());
        MinecraftClient.getInstance().setScreen(new ChatSelectionScreen((Screen) parent, parent.getAbuseReporter(), parent.getReport(), (report) -> {
            parent.setReport(report);
            parent.onChange();
        }));
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        if (mouseY > y + entryHeight || y > mouseY) {
            return;
        }

        boolean canReorder = ((AccessorMixinChatSelectionScreen) MinecraftClient.getInstance().currentScreen).getReporter().chatLog().get(this.index).isSentFrom(MinecraftClient.getInstance().player.getUuid());
        int moveUpX = x + entryWidth;
        drawButton(matrices, moveUpX, y, entryHeight, "↑", canReorder);

        int moveDownX = x + entryWidth + entryHeight + 3;
        drawButton(matrices, moveDownX, y, entryHeight, "↓", canReorder);

        int deleteX = x + entryWidth + entryHeight * 2 + 6;
        drawButton(matrices, deleteX, y, entryHeight, "X", true);

        if (isMouseOver(moveUpX, y, entryHeight, mouseX, mouseY) && canReorder) {
            buttonHovered = 0;
        } else if (isMouseOver(moveDownX, y, entryHeight, mouseX, mouseY) && canReorder) {
            buttonHovered = 1;
        } else if (isMouseOver(deleteX, y, entryHeight, mouseX, mouseY)) {
            buttonHovered = 2;
        } else {
            buttonHovered = -1;
        }
    }

    private boolean isMouseOver(int x, int y, int size, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + size + 1 && mouseY > y && mouseY < y + size;
    }

    private void drawButton(MatrixStack matrices, int x, int y, int size, String text, boolean enabled) {
        Screen.fill(matrices, x, y, x + size + 1, y + size, 0xFFCCCCCC);
        Screen.fill(matrices, x + 1, y + 1, x + size, y + size - 1, 0xFF6E6E6E);
        Screen.drawCenteredText(matrices, MinecraftClient.getInstance().textRenderer, text, x + (size / 2) + 1, y + (size / 2) - (MinecraftClient.getInstance().textRenderer.fontHeight / 2), 0xFFFFFFFF);
        if (!enabled) {
            Screen.fill(matrices, x, y, x + size + 1, y + size, 0x99000000);
        }
    }

}
