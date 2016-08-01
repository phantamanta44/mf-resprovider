package io.github.phantamanta44.mobafort.mfrp.stat;

import io.github.phantamanta44.mobafort.weaponize.stat.Stats;

public interface IStatted {

	<T extends Number> T getStat(Stats<T> stat);

}
