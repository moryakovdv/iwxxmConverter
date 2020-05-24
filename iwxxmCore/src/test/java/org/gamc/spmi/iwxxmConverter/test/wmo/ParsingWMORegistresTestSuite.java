package org.gamc.spmi.iwxxmConverter.test.wmo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ParseWMOCloudsTest.class, ParseWMOCloudTypesTest.class, ParseWMOContaminationTest.class,
		ParseWMODepositsTest.class, ParseWMOFrictionRegisterTest.class, ParseWMONilReasonTest.class,
		ParseWMOPreciptationsTest.class, ParseWMOSigConvectiveCloudsTest.class, ParseWMOSigWXPhenomenaTest.class,
		ParseWMOSpaceWeatherLocationTest.class, ParseWMOSpaceWeatherPhenomenaTest.class })
public class ParsingWMORegistresTestSuite {

}
