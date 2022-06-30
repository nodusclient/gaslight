package gg.nodus.gaslight.mixin;

import net.minecraft.client.gui.screen.report.ChatSelectionScreen;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntryListWidget.class)
public abstract class MixinSelectionListWidget<E extends EntryListWidget.Entry<E>> {

    @Shadow
    protected int top;

    @Shadow
    protected int headerHeight;
    @Shadow
    @Final
    protected int itemHeight;

    @Shadow
    public abstract double getScrollAmount();

    @Shadow
    protected abstract int getEntryCount();

    @Shadow
    public abstract List<E> children();

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "getEntryAtPosition", at = @At("HEAD"), cancellable = true)
    public void getEntryAtPosition(double x, double y, CallbackInfoReturnable<E> cir) {
        if (this.getClass() == (Class<?>) ChatSelectionScreen.SelectionListWidget.class) {
            int m = MathHelper.floor(y - (double) this.top) - this.headerHeight + (int) this.getScrollAmount() - 4;
            int n = m / this.itemHeight;
            cir.setReturnValue(n >= 0 && m >= 0 && n < this.getEntryCount() ? this.children().get(n) : null);
        }

    }

}
