package com.kerb4j.spnego;

import com.kerb4j.Kerb4JException;
import org.apache.kerby.kerberos.kerb.KrbCodec;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.type.ap.ApReq;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERApplicationSpecific;
import org.bouncycastle.asn1.DERObjectIdentifier;

import javax.security.auth.kerberos.KerberosKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static com.kerb4j.spnego.SpnegoConstants.KERBEROS_MECHANISM;

/**
 * https://tools.ietf.org/html/rfc1964
 *
 *    Per RFC-1508, Appendix B, the initial context establishment token
 will be enclosed within framing as follows:

 InitialContextToken ::=
 [APPLICATION 0] IMPLICIT SEQUENCE {
 thisMech        MechType
 -- MechType is OBJECT IDENTIFIER
 -- representing "Kerberos V5"
 innerContextToken ANY DEFINED BY thisMech
 -- contents mechanism-specific;
 -- ASN.1 usage within innerContextToken
 -- is not required
 }
 */
public class SpnegoKerberosMechToken {

    private ApReq apRequest;

    public SpnegoKerberosMechToken(byte[] token) throws Kerb4JException {
        this(token, null);
    }

    public SpnegoKerberosMechToken(byte[] token, KerberosKey[] keys) throws Kerb4JException {

        if(token.length <= 0)
            throw new Kerb4JException("kerberos.token.empty", null, null);

        try {
            ASN1InputStream stream = new ASN1InputStream(new ByteArrayInputStream(token));
            DERApplicationSpecific derToken = DecodingUtil.as(DERApplicationSpecific.class, stream);
            if(derToken == null || !derToken.isConstructed())
                throw new Kerb4JException("kerberos.token.malformed", null, null);
            stream.close();

            stream = new ASN1InputStream(new ByteArrayInputStream(derToken.getContents()));
            ASN1ObjectIdentifier kerberosOid = DecodingUtil.as(ASN1ObjectIdentifier.class, stream);
            if(!kerberosOid.getId().equals(KERBEROS_MECHANISM))
                throw new Kerb4JException("kerberos.token.invalid", null, null);

            int read = 0;
            int readLow = stream.read() & 0xff;
            int readHigh = stream.read() & 0xff;
            read = (readHigh << 8) + readLow;
            if(read != 0x01)
                throw new Kerb4JException("kerberos.token.malformed", null, null);

            DERApplicationSpecific krbToken = DecodingUtil.as(DERApplicationSpecific.class, stream);
            if(krbToken == null || !krbToken.isConstructed())
                throw new Kerb4JException("kerberos.token.malformed", null, null);

            stream.close();

            apRequest = KrbCodec.decode(krbToken.getEncoded(), ApReq.class);

        } catch(IOException e) {
            throw new Kerb4JException("kerberos.token.malformed", null, e);
        } catch (KrbException e) {
            throw new Kerb4JException("kerberos.token.malformed", null, e);
        }
    }

    public ApReq getApRequest() {
        return apRequest;
    }

}