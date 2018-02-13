package com.epam.file;

import com.epam.common.Constants;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class FileTypes {

    private static final FileTypes pdf = new FileTypes(Constants.FileTypes.PDF) {
        @Override
        public String parse(File file) throws IOException {
            return new PDFTextStripper().getText(PDDocument.load(file));
        }
    };

    private static final FileTypes doc = new FileTypes(Constants.FileTypes.DOC) {
        @Override
        public String parse(File file) throws IOException {
            return new HWPFDocument(new FileInputStream(file)).getText().toString();
        }
    };

    private final String extension;

    private FileTypes(String extension) {
        this.extension = extension;
    }

    @SuppressWarnings("ConstantConditions")
    public static FileTypes parse(String extension) {
        return Stream.of(pdf, doc).filter(fileType -> fileType.extension().equals(extension)).findFirst().get();
    }

    public static List<File> listFiles(String location, int levelInside) {
        return listFiles(new File(location), levelInside);
    }

    @SuppressWarnings("ConstantConditions")
    private static List<File> listFiles(File dir, int level) {
        if (dir == null || !dir.isDirectory()) {
            return Collections.emptyList();
        }
        List<String> extensions = Stream.of(pdf, doc).map(FileTypes::extension).collect(Collectors.toList());

        List<File> files = Arrays.stream(dir.listFiles(file ->
                file.isFile() && extensions.stream().anyMatch(extension -> file.getName().endsWith(extension)))
        ).collect(Collectors.toList());

        if (level != 0) {
            Arrays.stream(dir.listFiles(File::isDirectory))
                    .forEach(innerDir -> files.addAll(listFiles(innerDir, level - 1)));
        }
        return files;
    }

    public abstract String parse(File file) throws IOException;

    private String extension() {
        return extension;
    }
}
