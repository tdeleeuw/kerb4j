package com.kerb4j.spnego;

import com.kerb4j.Kerb4JException;

public abstract class SpnegoToken {

    // Default max size as 65K
    public static int TOKEN_MAX_SIZE = 66560;

    protected byte[] mechanismToken;
    protected byte[] mechanismList;
    protected String mechanism;

    public static SpnegoToken parse(byte[] token) throws Kerb4JException {
        SpnegoToken spnegoToken = null;

        if(token.length <= 0)
            throw new Kerb4JException("spnego.token.empty", null, null);

        switch (token[0]) {
        case (byte)0x60:
            spnegoToken = new SpnegoInitToken(token);
            break;
        case (byte)0xa1:
            spnegoToken = new SpnegoTargToken(token);
            break;
        default:
            spnegoToken = null;
            Object[] args = new Object[]{token[0]};
            throw new Kerb4JException("spnego.token.invalid", args, null);
        }

        return spnegoToken;
    }

    public byte[] getMechanismToken() {
        return mechanismToken;
    }

    public byte[] getMechanismList() {
        return mechanismList;
    }

    public String getMechanism() {
        return mechanism;
    }

}
