package com.generalbytes.batm.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

class WriteLines extends DefaultTask {
    private final List<Object> lines = new LinkedList<>();
    private Object outputFile;
    private String encoding = "UTF-8";

    @Input
    public List<String> getLines() {
        final List<String> ret = new ArrayList<>(lines.size());
        for (Object line : lines) {
            String printableLine;
            if (line instanceof Callable) {
                try {
                    printableLine = ((Callable<String>) line).call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                printableLine = String.valueOf(line);
            }
            ret.add(printableLine);
        }
        return ret;
    }

    public void setLines(List<Object> lines) {
        this.lines.clear();
        lines(lines);
    }

    @OutputFile
    public File getOutputFile() {
        return getProject().file(outputFile);
    }

    public void setOutputFile(Object outputFile) {
        this.outputFile = outputFile;
    }

    @Input
    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void line(final Object value) {
        lines.add(value);
    }

    public void lines(List<Object> lines) {
        this.lines.addAll(lines);
    }

    @TaskAction
    public void writeFile() throws IOException {
        getOutputFile().withWriter(getEncoding()) {
            for (String line : getLines()) {
                it.println(line)
            }
        }
    }
}
