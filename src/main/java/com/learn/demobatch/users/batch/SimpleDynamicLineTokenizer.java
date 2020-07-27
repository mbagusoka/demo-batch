package com.learn.demobatch.users.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.file.transform.AbstractLineTokenizer;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SimpleDynamicLineTokenizer extends AbstractLineTokenizer {

    private static final String DEFAULT_DELIMITER = ",";
    private static final char DEFAULT_QUOTE_CHARACTER = '"';

    private final Resource resource;

    private String[] fileHeaders;
    private int[] indexNamesInHeaders;
    private String delimiter = DEFAULT_DELIMITER;
    private char quoteCharacter = DEFAULT_QUOTE_CHARACTER;

    @Override
    protected List<String> doTokenize(String line) {
        getHeadersFromResource();
        getNamesIndexInHeaders();
        String[] lines = line.split(delimiter);
        List<String> tokens = new ArrayList<>();
        String token;
        for (int i : indexNamesInHeaders) {
            token = lines[i];
            if (isQuoted(token)) {
                token = token.replace(String.valueOf(quoteCharacter), "");
            }
            tokens.add(token);
        }
        return tokens;
    }

    private void getHeadersFromResource() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line = br.readLine();
            fileHeaders = line.split(delimiter);
        } catch (IOException e) {
            throw new ParseException("Error when parsing header", e);
        }
    }

    private void getNamesIndexInHeaders() {
        indexNamesInHeaders = new int[names.length];
        for (int nameIndex = 0; nameIndex < names.length; nameIndex++) {
            searchForHeaderEquivalent(nameIndex);
        }
    }

    private void searchForHeaderEquivalent(int nameIndex) {
        for (int headerIndex = 0; headerIndex < fileHeaders.length; headerIndex++) {
            if (names[nameIndex].equals(fileHeaders[headerIndex])) {
                indexNamesInHeaders[nameIndex] = headerIndex;
            }
        }
    }

    private boolean isQuoted(String line) {
        if (!StringUtils.isEmpty(line)) {
            char firstChar = line.charAt(0);
            char secondChar = line.charAt(line.length() - 1);
            return firstChar == secondChar && firstChar == quoteCharacter;
        } else {
            return false;
        }
    }
}
