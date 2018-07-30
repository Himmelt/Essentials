package com.earth2me.essentials.signs;

import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;


public class SignDisposal extends EssentialsSign {
    public SignDisposal() {
        super("Disposal");
    }

    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) {
        return onSignInteract(sign, player, username, (com.earth2me.essentials.IEssentials) ess);
    }

    protected boolean onSignInteract(final ISign sign, final User player, final String username, final com.earth2me.essentials.IEssentials ess) {
        player.getBase().openInventory(ess.getServer().createInventory(player.getBase(), 36, "Disposal"));
        return true;
    }
}
