package org.jfree.report;

import junit.framework.TestCase;
import org.jfree.report.data.FastGlobalView;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class Pre397Test extends TestCase
{
  public Pre397Test()
  {
  }

  public Pre397Test(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    JFreeReportBoot.getInstance().start();
  }

  public void testPre397() throws DataSourceException
  {
    final FastGlobalView fgv = new FastGlobalView();
    fgv.putField("col1", "A", false);
    fgv.putField("col2", "A", false);
    fgv.putField("col3", "A", false);
    final FastGlobalView globalView = fgv.advance();
    globalView.putField("col1", "A", true);
    globalView.putField("col2", "B", true);
    globalView.putField("col3", "B", true);
    assertFalse("Col1 no change", globalView.getFlags("col1").isChanged());
    assertTrue("Col2 has change", globalView.getFlags("col2").isChanged());
    assertTrue("Col3 has change", globalView.getFlags("col3").isChanged());
  }
}
