package com.epam.file;

import com.epam.common.Constants;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileTypes {

    private FileTypes() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("ConstantConditions")
    public static FileType parse(String extension) {
        return Stream.of(FileType.values())
                .filter(fileType -> fileType.extension().equals(extension))
                .findFirst().get();
    }

    public static List<File> listFiles(String location, int levelInside) {
        return listFiles(new File(location), levelInside);
    }

    @SuppressWarnings("ConstantConditions")
    private static List<File> listFiles(File dir, int level) {
        if (dir == null || !dir.isDirectory()) {
            return Collections.emptyList();
        }
        List<String> extensions = Stream.of(FileType.values()).map(FileType::extension).collect(Collectors.toList());

        List<File> files = Stream.of(dir.listFiles(file ->
                file.isFile() && extensions.stream().anyMatch(extension -> file.getName().endsWith(extension)))
        ).collect(Collectors.toList());

        if (level != 0) {
            Stream.of(dir.listFiles(File::isDirectory))
                    .forEach(innerDir -> files.addAll(listFiles(innerDir, level - 1)));
        }
        return files;
    }

    public enum FileType {
        pdf(Constants.FileTypes.PDF, file -> new PDFTextStripper().getText(PDDocument.load(file))),
        doc(Constants.FileTypes.DOC, file -> new HWPFDocument(new FileInputStream(file)).getText().toString());

        private final String extension;
        private final Parser parser;

        FileType(String extension, Parser parser) {
            this.extension = extension;
            this.parser = parser;
        }

        public String parse(File file) throws IOException {
            return parser.parse(file);
        }

        private String extension() {
            return extension;
        }
    }

    interface Parser {
        String parse(File file) throws IOException;
    }
}
