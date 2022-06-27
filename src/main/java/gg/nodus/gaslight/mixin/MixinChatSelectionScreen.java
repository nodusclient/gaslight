package gg.nodus.gaslight.mixin;

import com.google.common.util.concurrent.Runnables;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.abusereport.ChatSelectionScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.abusereport.AbuseReporter;
import net.minecraft.client.network.chat.ReceivedMessage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.message.ChatMessageSigner;
import net.minecraft.network.message.MessageSignature;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Mixin(ChatSelectionScreen.class)
public abstract class MixinChatSelectionScreen extends Screen {

    @Shadow @Final private AbuseReporter reporter;

    @Shadow @Nullable private ChatSelectionScreen.SelectionListWidget selectionList;
    private TextFieldWidget newMessageField;

    protected MixinChatSelectionScreen(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void init(CallbackInfo ci) {
        var width = (MinecraftClient.getInstance().getWindow().getScaledWidth() - MinecraftClient.getInstance().textRenderer.getWidth(Text.translatable("gui.chatSelection.title"))) / 2 - 20;
        newMessageField = new TextFieldWidget(this.textRenderer, 4, 10, width - 32, 20, Text.of(""));
        newMessageField.setMaxLength(256);
        newMessageField.setTextFieldFocused(true);
        this.addSelectableChild(newMessageField);
        this.addDrawableChild(new ButtonWidget(width - 25, 10, 30, 20, Text.of("Add"), (button) -> {
            if (newMessageField.getText().isBlank()) {
                return;
            }
            var signer = new ChatMessageSigner(MinecraftClient.getInstance().player.getUuid(), Instant.now().minus(ChronoUnit.MINUTES.getDuration()), NetworkEncryptionUtils.SecureRandomUtil.nextLong());
            var otherSigner = MinecraftClient.getInstance().getProfileKeys().getSigner();
            try {
                var signature = signer.sign(otherSigner, Text.of(newMessageField.getText()));
                var message = new ReceivedMessage.ChatMessage(
                        MinecraftClient.getInstance().player.getGameProfile(),
                        MinecraftClient.getInstance().player.getName(),
                        new SignedMessage(Text.of(newMessageField.getText()),
                                new MessageSignature(
                                        MinecraftClient.getInstance().player.getUuid(),
                                        Instant.now(),
                                        new NetworkEncryptionUtils.SignatureData(signature.saltSignature().salt(), signature.saltSignature().signature())
                                ),
                                Optional.empty()
                        )
                );
                this.selectionList.addMessage(0, message);
                this.reporter.chatLog().add(message);
                var parent = ((AccessorChatReportScreen) ((AccessorMixinChatSelectionScreen) MinecraftClient.getInstance().currentScreen).getParent());
                MinecraftClient.getInstance().setScreen(new ChatSelectionScreen((Screen) parent, parent.getAbuseReporter(), parent.getReport(), (report) -> {
                    parent.setReport(report);
                    parent.onChange();
                }));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
        this.addDrawableChild(
        new PressableTextWidget(2, this.height - 10, MinecraftClient.getInstance().textRenderer.getWidth("Gaslight - nodus.gg"), 10, Text.of("§aGaslight - nodus.gg"), (button) -> {
            try {
                Util.getOperatingSystem().open(new URI("https://nodus.gg"));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }, this.textRenderer));
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        newMessageField.render(matrices, mouseX, mouseY, delta);
    }

}
