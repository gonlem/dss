/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.pdf;

import java.util.List;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.pades.PAdESCommonParameters;
import eu.europa.esig.dss.pades.SignatureFieldParameters;
import eu.europa.esig.dss.pades.validation.PdfRevision;

/**
 * The usage of this interface permits the user to choose the underlying PDF library used to create PDF signatures.
 *
 */
public interface PDFSignatureService {
	
	/**
	 * Returns the digest value of a PDF document
	 *
	 * @param toSignDocument
	 *            the document to be signed
	 * @param parameters
	 *            the signature/timestamp parameters
	 * @return the digest value
	 * @throws DSSException
	 *             if an error occurred
	 */
	byte[] digest(final DSSDocument toSignDocument, final PAdESCommonParameters parameters);

	/**
	 * Signs a PDF document
	 *
	 * @param pdfData
	 *            the pdf document
	 * @param signatureValue
	 *            the signature value
	 * @param parameters
	 *            the signature/timestamp parameters
	 * @throws DSSException
	 *             if an error occurred
	 */
	DSSDocument sign(final DSSDocument pdfData, final byte[] signatureValue, final PAdESCommonParameters parameters);

	/**
	 * Retrieves revisions from a PDF document
	 * 
	 * @param document
	 *            the document to extract revisions from
	 * @param pwd
	 *            the password protection phrase used to encrypt the PDF document
	 *            use 'null' value for not an encrypted document
	 * @return list of extracted {@link PdfRevision}s
	 */
	List<PdfRevision> getRevisions(final DSSDocument document, final String pwd);

	/**
	 * This method adds the DSS dictionary (Baseline-LT)
	 * 
	 * @param document
	 *            the document to be extended
	 * @param callbacks
	 *            the callbacks to retrieve the revocation data,...
	 * @return the pdf document with the added dss dictionary
	 * 
	 * @throws DSSException
	 *             if an error occurred
	 */
	DSSDocument addDssDictionary(DSSDocument document, List<DSSDictionaryCallback> callbacks);

	/**
	 * This method adds the DSS dictionary (Baseline-LT)
	 * 
	 * @param document
	 *            the document to be extended
	 * @param callbacks
	 *            the callbacks to retrieve the revocation data,...
	 * @param pwd
	 *            the password protection used to create the encrypted document
	 * @return the pdf document with the added dss dictionary
	 * 
	 * @throws DSSException
	 *             if an error occurred
	 */
	DSSDocument addDssDictionary(DSSDocument document, List<DSSDictionaryCallback> callbacks, final String pwd);

	/**
	 * This method returns not signed signature-fields
	 * 
	 * @param document
	 *            the pdf document
	 * @return the list of empty signature fields
	 */
	List<String> getAvailableSignatureFields(final DSSDocument document);
	
	/**
	 * Returns not-signed signature fields from an encrypted document
	 * 
	 * @param document
	 *            the pdf document
	 * @param pwd
	 *            the password protection phrase used to encrypt the document
	 * @return the list of not signed signature field names
	 */
	List<String> getAvailableSignatureFields(final DSSDocument document, final String pwd);

	/**
	 * This method allows to add a new signature field to an existing pdf document
	 * 
	 * @param document
	 *            the pdf document
	 * @param parameters
	 *            the parameters with the coordinates,... of the signature field
	 * @return the pdf document with the new added signature field
	 */
	DSSDocument addNewSignatureField(DSSDocument document, SignatureFieldParameters parameters);

	/**
	 * This method allows to add a new signature field to an existing encrypted pdf document
	 * 
	 * @param document
	 *            the pdf document
	 * @param parameters
	 *            the parameters with the coordinates,... of the signature field
	 * @param pwd
	 *            the password protection used to create the encrypted document
	 * @return the pdf document with the new added signature field
	 */
	DSSDocument addNewSignatureField(DSSDocument document, SignatureFieldParameters parameters, final String pwd);

}
