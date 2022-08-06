package gg.nodus.gaslight.mixin;

import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(YggdrasilUserApiService.class)
public class MixinYggdrasilUserApiService {

    @Inject(method = "reportAbuse", at = @At("HEAD"), cancellable = true, remap = false)
    public void reportAbuse(AbuseReportRequest request, CallbackInfo ci) {
        /*var objectMapper = ObjectMapper.create();
        var string = objectMapper.writeValueAsString(request);
        System.out.println(string);
        ci.cancel();*/
    }

}
