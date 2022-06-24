package gg.nodus.fakereport.mixin;

import com.mojang.authlib.minecraft.report.AbuseReport;
import com.mojang.authlib.minecraft.report.ReportChatMessage;
import com.mojang.authlib.minecraft.report.ReportEvidence;
import com.mojang.authlib.minecraft.report.ReportedEntity;
import gg.nodus.fakereport.Fakereport;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.message.ChatMessageSigner;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.security.SignatureException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Mixin(AbuseReport.class)
public class MixinAbuseReport {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(String type, String opinionComments, String reason, ReportEvidence evidence, ReportedEntity reportedEntity, Instant createdTime, CallbackInfo ci) {
        List<ReportChatMessage> modifiedMessages = new ArrayList<>();

        for (var addedMessage : Fakereport.context) {
            var signer = new ChatMessageSigner(MinecraftClient.getInstance().player.getUuid(), Instant.now().minus(ChronoUnit.MINUTES.getDuration()), NetworkEncryptionUtils.SecureRandomUtil.nextLong());
            var otherSigner = MinecraftClient.getInstance().getProfileKeys().getSigner();
            try {
                var signature = signer.sign(otherSigner, addedMessage);
                var sig = Base64.getEncoder().encodeToString(signature.saltSignature().signature());
                var json = Text.Serializer.toSortedJsonString(Text.of(addedMessage));
                var newMessage = new ReportChatMessage(MinecraftClient.getInstance().player.getUuid(), signature.timestamp(), signature.saltSignature().salt(), sig, json, null, false);
                modifiedMessages.add(0, newMessage);
            } catch (SignatureException e) {
                throw new RuntimeException(e);
            }
        }

        for (ReportChatMessage message : evidence.messages) {
            if (message.messageReported) {
                modifiedMessages.add(message);
            }
        }
        evidence.messages = modifiedMessages;
    }

}
