package org.cuba.neofit;

import org.cuba.neofit.bodies.FormBodyTest;
import org.cuba.neofit.bodies.MultipartTest;
import org.cuba.neofit.bodies.PlainBodyTest;
import org.cuba.neofit.converters.ConvertersTest;
import org.cuba.neofit.headers.HeadersTest;
import org.cuba.neofit.url.UrlBuildingTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ConvertersTest.class, UrlBuildingTest.class, PlainBodyTest.class, FormBodyTest.class, MultipartTest.class, HeadersTest.class })
public class FullTest {

}
