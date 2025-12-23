package com.artstore.utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal CSV helper.
 * <p>
 * Supports:
 * - Writing: escape(field) will quote fields containing comma, quote, newline, or carriage return.
 * - Reading: parseLine(line) splits a single CSV line into fields, honoring quoted fields.
 * <p>
 * This keeps your existing "one record per line" persistence format, but makes it safe for commas.
 */
public final class CsvUtil {

    private CsvUtil() {}

    /**
     * Escapes a single CSV field.
     * Rules:
     * - null becomes empty
     * - If the field contains comma, quote, or newline, wrap in quotes
     * - Inside quoted fields, double quotes are escaped as ""
     */
    public static String escape(String field) {
        if (field == null) return "";

        boolean mustQuote = field.indexOf(',') >= 0
                || field.indexOf('"') >= 0
                || field.indexOf('\n') >= 0
                || field.indexOf('\r') >= 0;

        if (!mustQuote) {
            return field;
        }

        String escapedQuotes = field.replace("\"", "\"\"");
        return "\"" + escapedQuotes + "\"";
    }

    /**
     * Parses a single CSV line into fields.
     * Handles:
     * - quoted fields with commas
     * - escaped quotes inside quoted fields ("")
     * <p>
     * Assumption: one record per line. Newlines in fields are allowed only if you preserve them
     * when writing/reading lines. (Most simple file writers still write one record per line.)
     */
    public static List<String> parseLine(String line) {
        List<String> out = new ArrayList<>();
        if (line == null) {
            out.add("");
            return out;
        }

        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (inQuotes) {
                if (c == '"') {
                    // Either end quote OR escaped quote
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++; // skip the second quote
                    } else {
                        inQuotes = false; // end quote
                    }
                } else {
                    cur.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    out.add(cur.toString());
                    cur.setLength(0);
                } else {
                    cur.append(c);
                }
            }
        }

        out.add(cur.toString());
        return out;
    }

    /**
     * Joins fields into a single CSV line, escaping each field as needed.
     */
    public static String join(List<String> fields) {
        if (fields == null || fields.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(escape(fields.get(i)));
        }
        return sb.toString();
    }

}

