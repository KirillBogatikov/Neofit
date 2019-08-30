package org.kllbff.neofit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.kllbff.neofit.bodies.FormBodyTest;
import org.kllbff.neofit.bodies.MultipartTest;
import org.kllbff.neofit.bodies.PlainBodyTest;
import org.kllbff.neofit.converters.ConvertersTest;
import org.kllbff.neofit.url.UrlBuildingTest;

@RunWith(Suite.class)
@SuiteClasses({ ConvertersTest.class, UrlBuildingTest.class, PlainBodyTest.class, FormBodyTest.class, MultipartTest.class })
public class FullTest {

}
