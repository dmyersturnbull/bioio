package org.pharmgkb.parsers.bed;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.pharmgkb.parsers.model.Strand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Tests {@link BedWriter}.
 * @author Douglas Myers-Turnbull
 */
public class BedWriterTest {
  private List<BedFeature> m_data;
  private List<String> m_expectedLines;


  @Before
  public void before() throws Exception {

    BedFeature first = new BedFeature.Builder("chr1", 0, 5).build();
    BedFeature second = new BedFeature.Builder("chr2", 10, 20)
        .setName("xxx")
        .setScore(0)
        .setStrand(Strand.PLUS)
        .setThickStart(12L).setThickEnd(18L)
        .setColor(Color.BLACK)
        .build();
    BedFeature third = new BedFeature.Builder("chr2", 30, 50)
        .setName("yyy")
        .setScore(1000)
        .setStrand(Strand.MINUS)
        .setThickStart(30L).setThickEnd(40L)
        .setColor(Color.WHITE)
        .addBlock(0, 5).addBlock(10, 20)
        .build();
    m_data = Arrays.asList(first, second, third);

    // read file to String
    Path expectedFile = Paths.get(getClass().getResource("bed1.bed").toURI()).toAbsolutePath();
    m_expectedLines = Files.lines(expectedFile)
        .collect(Collectors.toList());
    assertEquals(3, m_expectedLines.size());
  }


	@Test
	public void testWrite() {
    // convert to BedFeature to String
    List<String> lines = m_data.stream()
        .map(new BedWriter())
        .collect(Collectors.toList());
      assertEquals(lines, m_expectedLines);
	}


  @Test
  public void testWriteToFile() throws Exception {
    // write to file
    Path tmpFile = Files.createTempFile(getClass().getSimpleName(), ".bed");
    BedWriter writer = new BedWriter();
    writer.writeToFile(m_data, tmpFile);
    List<String> lines = Files.lines(tmpFile).collect(Collectors.toList());
    assertEquals(lines, m_expectedLines);
  }
}
