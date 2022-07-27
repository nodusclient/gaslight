package gg.nodus.gaslight.mixin;

import com.mojang.authlib.minecraft.report.ReportChatMessageBody;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import gg.nodus.gaslight.Gaslight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.ByteBuffer;

@Mixin(YggdrasilUserApiService.class)
public class MixinYggdrasilUserApiService {

    private static String toHex(ByteBuffer bb) {
        if (bb == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        while (bb.hasRemaining()) {
            sb.append(String.format("%02X", bb.get()));
        }
        return sb.toString();
    }

    @Inject(method = "reportAbuse", at = @At("HEAD"), cancellable = true, remap = false)
    public void reportAbuse(AbuseReportRequest request, CallbackInfo ci) {
        for (var message : request.report.evidence.messages) {
            if (message.body != null) {
                if (Gaslight.removedMessages.contains(message.body.message.plain)) {
                    message.body = null;
                }
            }
        }

        //REMOVE_BEFORE_PUBLISHING(request, ci);
    }

    private void REMOVE_BEFORE_PUBLISHING(AbuseReportRequest request, CallbackInfo ci) {
        System.out.println("##########################################################################################");
        System.out.println("Abuse report caught - REMOVE BEFORE PUBLISHING");
        System.out.println("UUID: " + request.id);
        System.out.println("Client Version: " + request.clientInfo.clientVersion);
        System.out.println("Server Address: " + request.thirdPartyServerInfo.address);
        System.out.println("Reason: " + request.report.reason);
        System.out.println("Opinion comments: " + request.report.opinionComments);
        System.out.println("Reported profile id: " + request.report.reportedEntity.profileId);
        System.out.println("Created time: " + request.report.createdTime);
        System.out.println("Evidence: ");
        for (var message : request.report.evidence.messages) {
            System.out.println("===================================================================");
            if (message.body != null) {
                System.out.println("Message:");
                System.out.println("  Body:");
                System.out.println("    Decorated message: " + message.body.message.decorated);
                System.out.println("    Plain message: " + message.body.message.plain);
                System.out.println("    Timestamp: " + message.body.timestamp.toString());
                System.out.println("    Last seen:");
                for (ReportChatMessageBody.LastSeenSignature sig : message.body.lastSeenSignatures) {
                    System.out.println("      Last seen from " + sig.profileId + " sig: " + toHex(sig.lastSignature));
                }
            } else {
                System.out.println("Message body is null");
            }
            System.out.println("  Header:");
            System.out.println("    Profile id: " + message.header.profileId);
            System.out.println("    Hash of body: " + toHex(message.header.hashOfBody));
            System.out.println("    Signature: " + toHex(message.header.signature));
            System.out.println("    Previous Signature: " + toHex(message.header.signatureOfPreviousHeader));
            System.out.println("  Reported: " + message.messageReported);
            System.out.println("  Overriden message: " + message.overriddenMessage);
        }

        ci.cancel();
    }

}
