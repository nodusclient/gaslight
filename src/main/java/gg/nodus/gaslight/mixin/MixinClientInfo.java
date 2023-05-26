package gg.nodus.gaslight.mixin;

import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbuseReportRequest.ClientInfo.class)
public class MixinClientInfo {

    @Shadow(remap = false)
    public String clientVersion;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    public void init(final String clientVersion, final String locale, final CallbackInfo ci) {
        this.clientVersion = this.clientVersion.replace(" (modded)", "");
    }

}
