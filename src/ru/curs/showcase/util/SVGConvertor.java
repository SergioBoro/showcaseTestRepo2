package ru.curs.showcase.util;

import java.awt.Color;
import java.io.*;

import org.apache.batik.transcoder.*;
import org.apache.batik.transcoder.image.*;

import ru.curs.showcase.app.api.geomap.GeoMapExportSettings;
import ru.curs.showcase.util.exception.SVGConvertException;

/**
 * Класс с утилитами для работы с SVG графикой. Используется библиотека Batik
 * (http://xmlgraphics.apache.org/batik/).
 * 
 * @author den
 * 
 */
public final class SVGConvertor {

	private final GeoMapExportSettings exportSettings;

	public SVGConvertor() {
		super();
		exportSettings = new GeoMapExportSettings();
	}

	public SVGConvertor(final GeoMapExportSettings aExportSettings) {
		super();
		exportSettings = aExportSettings;
	}

	public OutputStream svgStringToJPEG(final String svg) {
		InputStream is;
		try {
			is = TextUtils.stringToStream(svg);
		} catch (UnsupportedEncodingException e) {
			throw new SVGConvertException(e);
		}
		OutputStream output = new ByteArrayOutputStream();
		output = svgToJPEGBaseMethod(is, output);
		return output;
	}

	public OutputStream svgStringToPNG(final String svg) {
		InputStream is;
		try {
			is = TextUtils.stringToStream(svg);
		} catch (UnsupportedEncodingException e) {
			throw new SVGConvertException(e);
		}
		OutputStream output = new ByteArrayOutputStream();
		output = svgToPNGBaseMethod(is, output);
		return output;
	}

	private OutputStream svgToJPEGBaseMethod(final InputStream is, final OutputStream os) {
		try {
			ImageTranscoder t = new JPEGTranscoder();
			t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY,
					new Float(exportSettings.getJpegQuality() / 100.0));
			if (exportSettings.getBackgroundColor() != null) {
				t.addTranscodingHint(ImageTranscoder.KEY_BACKGROUND_COLOR,
						Color.decode(exportSettings.getBackgroundColor()));
			}
			setTranscodeOptions(t);
			TranscoderInput input = new TranscoderInput(is);
			TranscoderOutput output = new TranscoderOutput(os);
			t.transcode(input, output);
			os.flush();
		} catch (Exception e) {
			throw new SVGConvertException(e);
		}
		return os;
	}

	private void setTranscodeOptions(final ImageTranscoder t) {
		if (exportSettings.getHeight() != null) {
			t.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, new Float(exportSettings.getHeight()));
		}
		if (exportSettings.getWidth() != null) {
			t.addTranscodingHint(ImageTranscoder.KEY_WIDTH, new Float(exportSettings.getWidth()));
		}
		t.addTranscodingHint(ImageTranscoder.KEY_FORCE_TRANSPARENT_WHITE, true);
	}

	private OutputStream svgToPNGBaseMethod(final InputStream is, final OutputStream os) {
		try {
			ImageTranscoder t = new PNGTranscoder();
			setTranscodeOptions(t);
			TranscoderInput input = new TranscoderInput(is);
			TranscoderOutput output = new TranscoderOutput(os);
			t.transcode(input, output);
			os.flush();
		} catch (Exception e) {
			throw new SVGConvertException(e);
		}
		return os;
	}

	public void svgFileToJPEGFile(final String svgFile, final String jpegFile) {
		InputStream is = FileUtils.loadResToStream(svgFile);
		OutputStream output;
		try {
			output = new FileOutputStream(jpegFile);
			output = svgToJPEGBaseMethod(is, output);
			output.close();
		} catch (FileNotFoundException e) {
			throw new SVGConvertException(e);
		} catch (IOException e) {
			throw new SVGConvertException(e);
		}
	}

	public void svgFileToPNGFile(final String svgFile, final String pngFile) {
		InputStream is = FileUtils.loadResToStream(svgFile);
		OutputStream output;
		try {
			output = new FileOutputStream(pngFile);
			output = svgToPNGBaseMethod(is, output);
			output.close();
		} catch (FileNotFoundException e) {
			throw new SVGConvertException(e);
		} catch (IOException e) {
			throw new SVGConvertException(e);
		}
	}
}
