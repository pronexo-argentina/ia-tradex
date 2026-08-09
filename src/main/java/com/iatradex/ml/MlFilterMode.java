package com.iatradex.ml;

public enum MlFilterMode {
    DISABLED("Desactivado"),
    INFORMATIVE("Informativo"),
    CONFIRMATION("Confirmación");

    private final String displayName;

    MlFilterMode(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
