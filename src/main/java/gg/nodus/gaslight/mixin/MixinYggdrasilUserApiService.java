package gg.nodus.gaslight.mixin;

import com.mojang.authlib.minecraft.report.ReportChatMessage;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import gg.nodus.gaslight.Gaslight;
import gg.nodus.gaslight.TimestampCalculationHack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.message.ChatMessageSigner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.security.SignatureException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Mixin(YggdrasilUserApiService.class)
public class MixinYggdrasilUserApiService {

    @Inject(method = "reportAbuse", at = @At("HEAD"), cancellable = true, remap = false)
    public void reportAbuse(AbuseReportRequest request, CallbackInfo ci) {
        var index = 0;
        List<ReportChatMessage> messages = new ArrayList<>();
        for (ReportChatMessage message : request.report.evidence.messages) {
            if (!Gaslight.removedIndexes.contains(index)) {
                messages.add(message);
            }
            index++;
        }

        var random = new Random();
        var tm = getReportedTimestamp(messages, 1);
        Instant minimum = messages.get(0).messageReported ? messages.get(0).timestamp : messages.stream().sorted(Comparator.comparing(o -> o.timestamp)).findFirst().get().timestamp;
        Instant maximum = tm.time();
        var wordCount = tm.words();

        var timestamps = 1;
        ReportChatMessage lastMessage = messages.get(0);

        for (int i = 0; i < messages.size(); i++) {
            ReportChatMessage message = messages.get(i);
            if (message.messageReported) {
                minimum = message.timestamp;
                var tm2 = getReportedTimestamp(messages, ++timestamps);
                wordCount = tm2.words();
                maximum = tm2.time();
                lastMessage = message;
            } else if (canRewrite(message)) {
                var totalTimeDelta = ((double) calculateTimeWindowNs(maximum, minimum) * random.nextDouble(0.6, 0.9));
                var msgWordCount = (long) message.message.split(" ").length;
                var msgtimeDelta = totalTimeDelta * ((double) msgWordCount / wordCount);

                Instant x;
                if (i == 0) {
                    x = minimum.plus(Duration.ofNanos((long) msgtimeDelta));
                } else {
                    x = lastMessage.timestamp.plus(Duration.ofNanos((long) msgtimeDelta));
                }

                message = resignMessage(message, x);
                messages.set(i, message);
                lastMessage = message;
            }
        }

        request.report.evidence.messages = messages;

        REMOVE_BEFORE_PUBLISHING(request, ci);
    }

    private void REMOVE_BEFORE_PUBLISHING(AbuseReportRequest request, CallbackInfo ci) {
        System.out.println("Abuse report caught - REMOVE BEFORE PUBLISHING");
        System.out.println("UUID: " + request.id);
        System.out.println("Client Version: " + request.clientInfo.clientVersion);
        System.out.println("Server Address: " + request.thirdPartyServerInfo.address);
        System.out.println("Reason: " + request.report.reason);
        System.out.println("Opinion comments: " + request.report.opinionComments);
        System.out.println("Type: " + request.report.type);
        System.out.println("Reported profile id: " + request.report.reportedEntity.profileId);
        System.out.println("Created time: " + request.report.createdTime);
        System.out.println("Evidence: ");
        for (var message : request.report.evidence.messages) {
            System.out.println("Message: " + message.message);
            System.out.println("  Timestamp: " + message.timestamp);
            System.out.println("  Sender profile id: " + message.profileId);
            System.out.println("  Reported: " + message.messageReported);
        }

        ci.cancel();
    }

    private TimestampCalculationHack getReportedTimestamp(List<ReportChatMessage> messages, int count) {
        int words = 0;
        for (ReportChatMessage message : messages) {
            if (message.messageReported) {
                count--;
                if (count == 0) {
                    return new TimestampCalculationHack(message.timestamp, words);
                }
            } else if (canRewrite(message)) {
                words += message.message.split(" ").length;
            }
        }
        return new TimestampCalculationHack(null, words);
    }

    private boolean canRewrite(ReportChatMessage message) {
        return message.profileId.equals(MinecraftClient.getInstance().player.getUuid());
    }

    private ReportChatMessage resignMessage(ReportChatMessage message, Instant at) {
        var uuid = MinecraftClient.getInstance().player.getUuid();
        var signer = new ChatMessageSigner(uuid, at, message.salt);
        var otherSigner = MinecraftClient.getInstance().getProfileKeys().getSigner();
        try {
            var signature = signer.sign(otherSigner, message.message);
            var sig = Base64.getEncoder().encodeToString(signature.saltSignature().signature());
            return new ReportChatMessage(uuid, signature.timestamp(), signature.saltSignature().salt(), sig, message.message, message.overriddenMessage, false);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    private long calculateTimeWindowNs(Instant maximum, Instant minimum) {
        if (minimum == null || maximum == null) {
            return Duration.ofSeconds(30).toNanos();
        } else {
            return (maximum.getEpochSecond() * Duration.ofSeconds(1).toNanos() + maximum.getNano()) - (minimum.getEpochSecond() * Duration.ofSeconds(1).toNanos() + minimum.getNano());
        }
    }
}
