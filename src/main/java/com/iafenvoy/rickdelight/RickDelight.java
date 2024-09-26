package com.iafenvoy.rickdelight;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.IModInfo;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Mod(RickDelight.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RickDelight {
    public static final String MOD_ID = "rick_delight";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<IModInfo> MODS = new ArrayList<>();
    private static final List<String> MATCH = List.of("projectcounts", "projectcount", "projectscounts", "projectscount", "swutm", "swutm1");

    public RickDelight() {
        MODS.addAll(ModList.get().getMods()
                .stream()
                .filter(x -> {
                    Optional<String> optional = x.getConfig().getConfigElement("authors");
                    return optional.map(s -> s.toLowerCase(Locale.ROOT).replace(" ", "")).map(s -> MATCH.stream().anyMatch(s::contains)).orElse(false);
                })
                .toList());
        if (!MODS.isEmpty())
            LOGGER.warn("检测到画饼乐事：{}", build());
    }

    public static String build() {
        return MODS.stream().map(IModInfo::getDisplayName).reduce("", (p, c) -> p + c, (a, b) -> a + b);
    }

    @SubscribeEvent
    public static void onTitleScreen(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof TitleScreen && !MODS.isEmpty())
            Minecraft.getInstance().getToasts().addToast(new SystemToast(SystemToast.SystemToastIds.PERIODIC_NOTIFICATION,
                    Component.literal("你可能是画饼乐事的受害者"),
                    Component.literal("检测到：" + build())));
    }
}
