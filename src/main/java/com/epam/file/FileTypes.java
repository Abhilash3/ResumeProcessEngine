package com.epam.file;

import com.epam.common.Constants;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FileTypes {

    private static final Logger logger = LoggerFactory.getLogger(FileTypes.class);

    private FileTypes() {
        throw new UnsupportedOperationException();
    }

    public static FileType parser(String extension) {
        return FileType.valueOf(extension.toUpperCase());
    }

    public static List<File> listFiles(String location, int levelInside) {
        List<File> files = listFiles(new File(location), levelInside);

        logger.debug("Identified {} file(s) from {}", files.size(), location);

        return files;
    }

    @SuppressWarnings("ConstantConditions")
    private static List<File> listFiles(File dir, int level) {
        if (dir == null || !dir.isDirectory()) {
            return Collections.emptyList();
        }
        List<String> extensions = Arrays.stream(FileType.values())
                .map(FileType::extension).collect(Collectors.toList());

        List<File> files = Arrays.stream(dir.listFiles(file ->
                file.isFile() && extensions.stream().anyMatch(extension -> file.getName().endsWith(extension)))
        ).collect(Collectors.toList());

        if (level != 0) {
            Arrays.stream(dir.listFiles(File::isDirectory))
                    .forEach(innerDir -> files.addAll(listFiles(innerDir, level - 1)));
        }
        return files;
    }

    public enum FileType {
        PDF(Constants.FileTypes.PDF, file -> {
            PDDocument doc = PDDocument.load(file);
            String content = new PDFTextStripper().getText(doc);
            doc.close();
            return content;
        }),
        DOC(Constants.FileTypes.DOC, file -> {
            InputStream doc = new FileInputStream(file);
            String content = new HWPFDocument(doc).getText().toString();
            doc.close();
            return content;
        });

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
