package com.epam.file;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class FileTypes {

    private static FileTypes pdf = new FileTypes("pdf") {
        @Override
        public String parse(File file) throws IOException {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(false);
            stripper.setShouldSeparateByBeads(true);
            stripper.setStartPage(1);
            stripper.setEndPage(Integer.MAX_VALUE);
            return stripper.getText(PDDocument.load(file));
        }
    };

    private static FileTypes doc = new FileTypes("doc") {
        @Override
        public String parse(File file) throws IOException {
            StringBuilder sb = new StringBuilder();
            Range range = new HWPFDocument(new FileInputStream(file)).getRange();
            for (int i = 0; i < range.numParagraphs(); i++) {
                sb.append(range.getParagraph(i).text());
            }
            return sb.toString();
        }
    };

    private final String extension;

    private FileTypes(String extension) {
        this.extension = extension;
    }

    public abstract String parse(File file) throws IOException;

    private String extension() {
        return extension;
    }

    @SuppressWarnings("ConstantConditions")
    public static FileTypes parse(String extension) {
        return Stream.of(pdf, doc).filter(fileType -> fileType.extension().equals(extension)).findFirst().get();
    }

    public static List<File> listFiles(String location, int levelInside) {
        return listFiles(new File(location), levelInside);
    }

    @SuppressWarnings("ConstantConditions")
    private static List<File> listFiles(File file, int level) {
        if (file == null || !file.isDirectory()) {
            return Collections.emptyList();
        }
        List<String> extensions = Stream.of(pdf, doc).map(FileTypes::extension).collect(Collectors.toList());

        List<File> files = Arrays.stream(file.listFiles(
                fileToCheck -> fileToCheck.isFile() && extensions.stream().anyMatch(
                        extension -> fileToCheck.getName().endsWith(extension)))).collect(Collectors.toList());

        if (level != 0) {
            Arrays.stream(file.listFiles(File::isDirectory))
                    .forEach(dir -> files.addAll(listFiles(dir, level - 1)));
        }
        return files;
    }
}
