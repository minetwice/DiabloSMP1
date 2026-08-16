Key hudFont = Key.key("diablosmp", "diablo_hud");
Component stoneIcon = Component.text("\uE001").font(hudFont);
Component cooldownBar = Component.text("\uE101\uE101\uE101\uE102\uE102").font(hudFont);
Component finalHud = Component.empty().append(stoneIcon).append(Component.space()).append(cooldownBar).append(Component.text(" 7.2s", NamedTextColor.GRAY));
player.sendActionBar(finalHud);
