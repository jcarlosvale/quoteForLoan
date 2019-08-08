package com.zopa.rate.api.parser;

import com.zopa.rate.api.domain.Lender;
import com.zopa.rate.api.exceptions.ParserException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    /**
     * Read the file and generate List of Lender
     * @param filePath
     * @return
     * @throws ParserException
     */
    public List<Lender> parseFile(String filePath) throws ParserException {
        List<Lender> listLender = new ArrayList<>();
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(filePath));
            for(int index = 1; index < lines.size(); index++) {
                String [] values = lines.get(index).split(",");
                listLender.add(new Lender(values[0], Double.valueOf(values[1]), Double.valueOf(values[2])));
            }
            return listLender;
        } catch (IOException e) {
            throw new ParserException(String.format("Invalid file, filepath: %s\n Error: %s", filePath, e.getMessage()));
        } catch (NumberFormatException e) {
            throw new ParserException(String.format("Invalid value in the CSV file %s\n Error: %s", filePath, e.getMessage()));
        }
    }
}