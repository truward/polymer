package com.truward.polymer.app;

import com.google.common.collect.ImmutableList;
import com.truward.polymer.app.util.ClassScanner;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Alexander Shabanov
 */
public class AppTest {
  @Test
  public void shouldRunApp() {
    App.generateCode(new CliOptionsParser.ProcessSpecResult("com.truward.polymer.testspec.p1", "/tmp/testSpec"));
  }
}
