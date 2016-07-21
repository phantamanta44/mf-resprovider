package io.github.phantamanta44.mobafort.mfrp.status;

public interface IStatus {

	String getId();

	String getName();

	String getDescription();

	boolean isBuff();

	default long getDuration() {
		return -1L;
	}

	default int getMaxStacks() {
		return 1;
	}

}
