package net.gotlicked.rsrreforged;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSRReforgedCommon {
    public static final String MOD_ID = "rsrreforged";
    public static final String MOD_NAME = "RSRequestify: Reforged";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    private RSRReforgedCommon() {
    }

    public static void init() {
        RSRReforgedCommon.LOG.info("RSRReforgedCommon has been initialized");
    }
}