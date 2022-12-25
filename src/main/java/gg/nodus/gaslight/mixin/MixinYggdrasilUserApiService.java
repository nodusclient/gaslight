package gg.nodus.gaslight.mixin;

import com.mojang.authlib.minecraft.client.ObjectMapper;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;

@Mixin(YggdrasilUserApiService.class)
public class MixinYggdrasilUserApiService {

    @Inject(method = "reportAbuse", at = @At("HEAD"), cancellable = true, remap = false)
    public void reportAbuse(final AbuseReportRequest request, final CallbackInfo ci) {
        /*final ObjectMapper objectMapper = ObjectMapper.create();
        final String string = objectMapper.writeValueAsString(request);
        System.out.println(string);
        try {
            Files.writeString(MinecraftClient.getInstance().runDirectory.toPath().resolve("gaslight_report" + System.currentTimeMillis() + ".json"), string);
        } catch (final IOException e) {
            ci.cancel();
            throw new RuntimeException(e);
        }
        ci.cancel();*/
    }

}
