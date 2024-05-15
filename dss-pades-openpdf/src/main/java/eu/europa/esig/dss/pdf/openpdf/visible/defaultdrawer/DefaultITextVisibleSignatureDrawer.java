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
package eu.europa.esig.dss.pdf.openpdf.visible.defaultdrawer;

import eu.europa.esig.dss.exception.IllegalInputException;
import eu.europa.esig.dss.pades.DSSFont;
import eu.europa.esig.dss.pades.SignatureFieldParameters;
import eu.europa.esig.dss.pades.SignatureImageTextParameters;
import eu.europa.esig.dss.pdf.AnnotationBox;
import eu.europa.esig.dss.pdf.openpdf.visible.AbstractITextSignatureDrawer;
import eu.europa.esig.dss.pdf.visible.ImageRotationUtils;
import eu.europa.esig.dss.pdf.visible.ImageUtils;
import eu.europa.esig.dss.pdf.visible.SignatureFieldDimensionAndPosition;
import eu.europa.esig.dss.utils.Utils;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfTemplate;

/**
 * The default PDFBox signature drawer.
 * Creates an image for a text content of the signature.
 */
public class DefaultITextVisibleSignatureDrawer extends AbstractITextSignatureDrawer {

	/** Cached instance of Image representation */
	private Image combinedImage;

	/**
	 * Default constructor
	 */
	public DefaultITextVisibleSignatureDrawer() {
		// empty
	}

	@Override
	protected JavaDSSFontMetrics getDSSFontMetrics() {
		SignatureImageTextParameters textParameters = parameters.getTextParameters();
		DSSFont dssFont = textParameters.getFont();

		Font javaFont = dssFont.getJavaFont();
		float properSize = dssFont.getSize() * ImageUtils.getScaleFactor(parameters.getZoom()); // scale text block
		Font properFont = javaFont.deriveFont(properSize);

		return new JavaDSSFontMetrics(properFont);
	}

	@Override
	public void draw() {
		Image currentImage = getImage();

		SignatureFieldParameters fieldParameters = parameters.getFieldParameters();
		String signatureFieldId = fieldParameters.getFieldId();

		SignatureFieldDimensionAndPosition dimensionAndPosition = buildSignatureFieldBox();

		if (Utils.isStringNotBlank(signatureFieldId)) {
			appearance.setVisibleSignature(signatureFieldId);
		} else {
			AnnotationBox annotationBox = toAnnotationBox(dimensionAndPosition);
			annotationBox = getRotatedAnnotationRelativelyPageRotation(annotationBox);
			appearance.setVisibleSignature(toITextRectangle(annotationBox), fieldParameters.getPage());
		}
		
		float x = dimensionAndPosition.getImageX();
		float y = dimensionAndPosition.getImageY();
		float width = dimensionAndPosition.getImageWidth();
		float height = dimensionAndPosition.getImageHeight();

		int finalRotation = getFinalRotation(dimensionAndPosition.getGlobalRotation(), getPageRotation());
		if (ImageRotationUtils.isSwapOfDimensionsRequired(finalRotation)) {
			x = dimensionAndPosition.getImageY();
			y = dimensionAndPosition.getImageX();
		}
		currentImage.setAbsolutePosition(x, y);
		currentImage.scaleAbsolute(width, height);

		currentImage.setRotationDegrees((float) ImageRotationUtils.ANGLE_360 - finalRotation); // opposite rotation

		PdfTemplate layer = appearance.getLayer(2);
		Rectangle boundingBox = layer.getBoundingBox();
		boundingBox.setBackgroundColor(parameters.getBackgroundColor());
		layer.rectangle(boundingBox);
		layer.addImage(currentImage);
	}

	private Image getImage() {
		if (combinedImage == null) {
			SignatureFieldDimensionAndPosition dimensionAndPosition = buildSignatureFieldBox();
			BufferedImage image = null;
			BufferedImage textImage = null;
			if (parameters.getImage() != null) {
				image = DefaultImageDrawerUtils.toBufferedImage(parameters.getImage());
			}
			if (parameters.getTextParameters() != null && !parameters.getTextParameters().isEmpty()) {
				textImage = DefaultImageDrawerUtils.createTextImage(parameters, dimensionAndPosition, getDSSFontMetrics());
			}
			if (image == null && textImage == null) {
				throw new IllegalArgumentException("Image or text shall be defined in order to build a visual signature!");
			}

			BufferedImage bufferedImage = DefaultImageDrawerUtils.mergeImages(image, textImage, dimensionAndPosition, parameters);
			bufferedImage = DefaultImageDrawerUtils.rotate(bufferedImage, dimensionAndPosition.getGlobalRotation());
			
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				ImageIO.write(bufferedImage, "png", baos);
				byte[] bufferedImageBytes = baos.toByteArray();
				combinedImage = Image.getInstance(bufferedImageBytes);
			} catch (IOException e) {
				throw new IllegalInputException(String.format("Unable to read the generated text+image file. Reason : %s", e.getMessage()), e);
			}
		}

		return combinedImage;
	}

	@Override
	protected PdfName getExpectedColorSpaceName() {
		/*
		 * see {@code com.lowagie.text.pdf.PdfImage}
		 */
		switch (getImage().getColorspace()) {
			case 1:
				return PdfName.DEVICEGRAY;
			case 3:
				return PdfName.DEVICERGB;
			default:
				return PdfName.DEVICECMYK;
		}
	}

}
