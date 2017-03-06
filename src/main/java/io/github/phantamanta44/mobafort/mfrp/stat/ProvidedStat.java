package io.github.phantamanta44.mobafort.mfrp.stat;

import io.github.phantamanta44.mobafort.weaponize.stat.Stats;

public class ProvidedStat<T extends Number> {

    public final Stats<T> stat;
    public final Number value;
    public final int src;
    public final ReduceType type;

    public ProvidedStat(Stats<T> stat, Number val, int source, ReduceType type) {
        this.stat = stat;
        this.value = val;
        this.src = source;
        this.type = type;
    }

    public enum ReduceType {

        ADD, PERC, MULT

    }

}
