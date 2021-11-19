package eu.europa.esig.dss.validation.process.bbb.sav;

import eu.europa.esig.dss.detailedreport.jaxb.XmlConstraint;
import eu.europa.esig.dss.detailedreport.jaxb.XmlSAV;
import eu.europa.esig.dss.detailedreport.jaxb.XmlStatus;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlCertificate;
import eu.europa.esig.dss.diagnostic.jaxb.XmlCertificateRef;
import eu.europa.esig.dss.diagnostic.jaxb.XmlFoundCertificates;
import eu.europa.esig.dss.diagnostic.jaxb.XmlIssuerSerial;
import eu.europa.esig.dss.diagnostic.jaxb.XmlRelatedCertificate;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignature;
import eu.europa.esig.dss.enumerations.CertificateRefOrigin;
import eu.europa.esig.dss.policy.jaxb.Level;
import eu.europa.esig.dss.policy.jaxb.LevelConstraint;
import eu.europa.esig.dss.validation.process.bbb.AbstractTestCheck;
import eu.europa.esig.dss.validation.process.bbb.sav.checks.KeyIdentifierMatchCheck;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeyIdentifierMatchCheckTest extends AbstractTestCheck {

    @Test
    public void validTest() throws Exception {
        XmlCertificateRef xmlCertificateRef = new XmlCertificateRef();
        xmlCertificateRef.setOrigin(CertificateRefOrigin.KEY_IDENTIFIER);

        XmlIssuerSerial xmlIssuerSerial = new XmlIssuerSerial();
        xmlIssuerSerial.setMatch(true);
        xmlCertificateRef.setIssuerSerial(xmlIssuerSerial);

        XmlRelatedCertificate xmlRelatedCertificate = new XmlRelatedCertificate();
        xmlRelatedCertificate.setCertificate(new XmlCertificate());
        xmlRelatedCertificate.getCertificateRefs().add(xmlCertificateRef);

        XmlFoundCertificates xmlFoundCertificates = new XmlFoundCertificates();
        xmlFoundCertificates.getRelatedCertificates().add(xmlRelatedCertificate);

        XmlSignature sig = new XmlSignature();
        sig.setFoundCertificates(xmlFoundCertificates);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlSAV result = new XmlSAV();
        KeyIdentifierMatchCheck kimc = new KeyIdentifierMatchCheck(
                i18nProvider, result, new SignatureWrapper(sig), constraint);
        kimc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.OK, constraints.get(0).getStatus());
    }

    @Test
    public void invalidTest() throws Exception {
        XmlCertificateRef xmlCertificateRef = new XmlCertificateRef();
        xmlCertificateRef.setOrigin(CertificateRefOrigin.KEY_IDENTIFIER);

        XmlIssuerSerial xmlIssuerSerial = new XmlIssuerSerial();
        xmlIssuerSerial.setMatch(false);
        xmlCertificateRef.setIssuerSerial(xmlIssuerSerial);

        XmlRelatedCertificate xmlRelatedCertificate = new XmlRelatedCertificate();
        xmlRelatedCertificate.setCertificate(new XmlCertificate());
        xmlRelatedCertificate.getCertificateRefs().add(xmlCertificateRef);

        XmlFoundCertificates xmlFoundCertificates = new XmlFoundCertificates();
        xmlFoundCertificates.getRelatedCertificates().add(xmlRelatedCertificate);

        XmlSignature sig = new XmlSignature();
        sig.setFoundCertificates(xmlFoundCertificates);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlSAV result = new XmlSAV();
        KeyIdentifierMatchCheck kimc = new KeyIdentifierMatchCheck(
                i18nProvider, result, new SignatureWrapper(sig), constraint);
        kimc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.NOT_OK, constraints.get(0).getStatus());
    }

    @Test
    public void issuerSerialNotDefinedTest() throws Exception {
        XmlCertificateRef xmlCertificateRef = new XmlCertificateRef();
        xmlCertificateRef.setOrigin(CertificateRefOrigin.KEY_IDENTIFIER);

        XmlRelatedCertificate xmlRelatedCertificate = new XmlRelatedCertificate();
        xmlRelatedCertificate.setCertificate(new XmlCertificate());
        xmlRelatedCertificate.getCertificateRefs().add(xmlCertificateRef);

        XmlFoundCertificates xmlFoundCertificates = new XmlFoundCertificates();
        xmlFoundCertificates.getRelatedCertificates().add(xmlRelatedCertificate);

        XmlSignature sig = new XmlSignature();
        sig.setFoundCertificates(xmlFoundCertificates);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlSAV result = new XmlSAV();
        KeyIdentifierMatchCheck kimc = new KeyIdentifierMatchCheck(
                i18nProvider, result, new SignatureWrapper(sig), constraint);
        kimc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.NOT_OK, constraints.get(0).getStatus());
    }

}
