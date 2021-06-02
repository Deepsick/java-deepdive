package academy.kovalevskyi.javadeepdive.week0.day0;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

public class StdBufferedReader implements Closeable {
  private static int defaultCharBufferSize = 8192;
  private final Reader reader;
  private char[] buffer;
  private boolean hasNext = true;
  private int bufferLength = 0;
  private int cursor = 0;

  public StdBufferedReader(final Reader reader, final int bufferSize) {
    if (reader == null) {
      throw new NullPointerException("Reader can't be null");
    }
    if (bufferSize <= 0) {
      throw new IllegalArgumentException("Buffer size can't be negative");
    }
    this.reader = reader;
    this.buffer = new char[bufferSize];
  }

  public StdBufferedReader(final Reader reader) {
    this(reader, StdBufferedReader.defaultCharBufferSize);
  }

  public boolean hasNext() throws IOException {
    if (this.buffer == null) {
      this.fill();
    }

    if (this.bufferLength == -1) {
      return false;
    }

    if (this.bufferLength == 0) {
      return this.reader.ready();
    }

    return this.hasNext;
  }

  private boolean isLastLineSymbol(final char symbol) {
    return symbol == '\n' || symbol == '\r';
  }

  private char[] collectLine(final int start, final int endExclusive) {
    if (start == endExclusive && this.isLastLineSymbol(this.buffer[start])) {
      return new char[0];
    }
    char[] line = new char[endExclusive - start];
    for (int i = start; i < endExclusive; i++) {
      line[i - start] = this.buffer[i];
    }
    return line;
  }

  private char[] concatLines(final char[] first, final char[] second) {
    char[] line = new char[first.length + second.length];
    for (int i = 0; i < first.length; i++) {
      line[i] = first[i];
    }
    for (int j = 0; j < second.length; j++) {
      line[j + first.length] = second[j];
    }

    return line;
  }

  private void fill() throws IOException {
    if (!this.reader.ready()) {
      this.bufferLength = -1;
      return;
    }
    this.bufferLength = this.reader.read(this.buffer);
    this.cursor = 0;
  }

  public char[] readLine() throws IOException {
    if (!this.hasNext()) {
      return new char[0];
    }

    if (this.buffer == null) {
      this.fill();
    }

    if (this.cursor >= this.bufferLength) {
      this.fill();
    }

    if (this.bufferLength == -1) {
      this.hasNext = false;
      return new char[0];
    }


    for (int i = this.cursor; i < this.bufferLength; i++) {
      if (this.isLastLineSymbol(this.buffer[i])) {
        char[] line = this.collectLine(this.cursor, i);
        this.cursor = i + 1;
        return line;
      }
    }

    char[] oldBufferLeftLine = this.collectLine(this.cursor, this.bufferLength);
    this.cursor = this.bufferLength;
    return this.hasNext()
            ? this.concatLines(oldBufferLeftLine, this.readLine())
            : oldBufferLeftLine;
  }


  public void close() throws IOException {
    this.reader.close();
  }
}
