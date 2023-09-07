package eu.europa.esig.dss.asic.xades.validation.evidencerecord;

import eu.europa.esig.dss.diagnostic.CertificateRefWrapper;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.FoundCertificatesProxy;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDigestMatcher;
import eu.europa.esig.dss.enumerations.CertificateRefOrigin;
import eu.europa.esig.dss.enumerations.TimestampType;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.spi.SignatureCertificateSource;
import eu.europa.esig.dss.utils.Utils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ASiCEWithXAdESLevelLTWithEvidenceRecordMultiDataValidationTest extends AbstractASiCEWithXAdESWithEvidenceRecordTestValidation {

    @Override
    protected DSSDocument getSignedDocument() {
        return new FileDocument("src/test/resources/validation/evidencerecord/xades-lt-with-er-multi-data.sce");
    }

    @Override
    protected int getNumberOfExpectedEvidenceScopes() {
        return 4; // the signatures001.xml document + 3 signed documents
    }

    @Override
    protected void verifyCertificateSourceData(SignatureCertificateSource certificateSource, FoundCertificatesProxy foundCertificates) {
        // skip (time-stamp contains multiple sign-cert refs)
    }

    @Override
    protected void checkTimestamp(DiagnosticData diagnosticData, TimestampWrapper timestampWrapper) {
        assertNotNull(timestampWrapper.getProductionTime());
        assertTrue(timestampWrapper.isMessageImprintDataFound());
        assertTrue(timestampWrapper.isMessageImprintDataIntact());
        assertTrue(timestampWrapper.isSignatureIntact());
        assertTrue(timestampWrapper.isSignatureValid());

        List<XmlDigestMatcher> digestMatchers = timestampWrapper.getDigestMatchers();
        for (XmlDigestMatcher xmlDigestMatcher : digestMatchers) {
            assertTrue(xmlDigestMatcher.isDataFound());
            assertTrue(xmlDigestMatcher.isDataIntact());
        }
        if (TimestampType.ARCHIVE_TIMESTAMP.equals(timestampWrapper.getType())) {
            assertNotNull(timestampWrapper.getArchiveTimestampType());
        }

        assertTrue(timestampWrapper.isSigningCertificateIdentified());
        assertTrue(timestampWrapper.isSigningCertificateReferencePresent());

        if (timestampWrapper.isTSAGeneralNamePresent()) {
            assertTrue(timestampWrapper.isTSAGeneralNameMatch());
            assertTrue(timestampWrapper.isTSAGeneralNameOrderMatch());
        }

        CertificateRefWrapper signingCertificateReference = timestampWrapper.getSigningCertificateReference();
        assertNotNull(signingCertificateReference);
        assertTrue(signingCertificateReference.isDigestValuePresent());
        assertTrue(signingCertificateReference.isDigestValueMatch());
        if (signingCertificateReference.isIssuerSerialPresent()) {
            assertTrue(signingCertificateReference.isIssuerSerialMatch());
        }

        CertificateWrapper signingCertificate = timestampWrapper.getSigningCertificate();
        assertNotNull(signingCertificate);
        String signingCertificateId = signingCertificate.getId();
        String certificateDN = diagnosticData.getCertificateDN(signingCertificateId);
        String certificateSerialNumber = diagnosticData.getCertificateSerialNumber(signingCertificateId);
        assertEquals(signingCertificate.getCertificateDN(), certificateDN);
        assertEquals(signingCertificate.getSerialNumber(), certificateSerialNumber);

        assertTrue(Utils.isCollectionEmpty(timestampWrapper.foundCertificates()
                .getOrphanCertificatesByRefOrigin(CertificateRefOrigin.SIGNING_CERTIFICATE)));

        assertTrue(Utils.isCollectionNotEmpty(timestampWrapper.getTimestampedObjects()));

        if (timestampWrapper.getType().isContentTimestamp() || timestampWrapper.getType().isArchivalTimestamp() ||
                timestampWrapper.getType().isDocumentTimestamp() || timestampWrapper.getType().isContainerTimestamp()) {
            assertTrue(Utils.isCollectionNotEmpty(timestampWrapper.getTimestampScopes()));
        } else if (timestampWrapper.getType().isEvidenceRecordTimestamp()) {
            assertTrue(Utils.isCollectionNotEmpty(timestampWrapper.getTimestampScopes()));
        } else {
            assertFalse(Utils.isCollectionNotEmpty(timestampWrapper.getTimestampScopes()));
        }
    }

}
