package io.github.phantamanta44.mobafort.mfrp.status;

import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.Map;

public enum CrowdControl {

    BLIND(true, false, true, true),
    TAUNT(false, false, false, true),
    SILENCE(true, true, false, true),
    ROOT(false, true, true, true),
    STUN(false, false, false, true),
    SLOW(true, true, true, true),
    ENTANGLE(false, false, true, true);

    public final boolean canMove, canAuto, canCast, canCleanse;

    CrowdControl(boolean canMove, boolean canAuto, boolean canCast, boolean canCleanse) {
        this.canMove = canMove;
        this.canAuto = canAuto;
        this.canCast = canCast;
        this.canCleanse = canCleanse;
    }

    public static EnumSet<CrowdControl> getCCEffects(Player player) {
        EnumSet<CrowdControl> set = EnumSet.noneOf(CrowdControl.class);
        StatusTracker.getStatus(player).stream()
                .map(Map.Entry::getKey)
                .filter(s -> s instanceof ICCStatus)
                .map(s -> (ICCStatus)s)
                .forEach(s -> set.add(s.getCrowdControl(player, StatusTracker.getStacks(player, s.getId()))));
        return set;
    }

    public static CCSnapshot getControlState(Player player) {
        CCSnapshot state = new CCSnapshot();
        getCCEffects(player).forEach(e -> {
            state.moveImpaired |= !e.canMove;
            state.autoImpaired |= !e.canAuto;
            state.castImpaired |= !e.canCast;
        });
        return state;
    }

    public static void cleanse(Player player) {
        StatusTracker.getStatus(player).removeIf(e ->
            e.getKey() instanceof ICCStatus && ((ICCStatus)e.getKey()).getCrowdControl(player, e.getValue()).canCleanse
        );
    }

    public static class CCSnapshot {

        private boolean moveImpaired, autoImpaired, castImpaired;

        private CCSnapshot() {
            this.moveImpaired = this.autoImpaired = this.castImpaired = false;
        }

        public boolean isMoveImpaired() {
            return moveImpaired;
        }

        public boolean isAutoImpaired() {
            return autoImpaired;
        }

        public boolean isCastImpaired() {
            return castImpaired;
        }

    }


}
