/*
 * Copyright 2016 Aroma Tech.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.sirwellington.alchemy.arguments.assertions;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import tech.sirwellington.alchemy.arguments.AlchemyAssertion;
import tech.sirwellington.alchemy.test.junit.runners.*;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static tech.sirwellington.alchemy.generator.AlchemyGenerator.Get.one;
import static tech.sirwellington.alchemy.generator.NumberGenerators.integers;
import static tech.sirwellington.alchemy.generator.NumberGenerators.negativeIntegers;
import static tech.sirwellington.alchemy.generator.StringGenerators.alphanumericString;
import static tech.sirwellington.alchemy.test.junit.runners.GenerateInteger.Type.RANGE;


/**
 * @author SirWellington
 */
@Repeat(1000)
@RunWith(AlchemyTestRunner.class)
public class NetworkAssertionsTest
{
    @GenerateURL
    private URL url;

    private static final int MAX_PORT = 65535;

    @GenerateInteger(value = RANGE, min = 1, max = MAX_PORT)
    private int port;

    @Before
    public void setUp()
    {
    }

    @Test
    public void testValidURL()
    {
        AlchemyAssertion<String> assertion = NetworkAssertions.validURL();
        assertThat(assertion, notNullValue());

        assertion.check(url.toString());


    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidURL() throws Exception
    {
        AlchemyAssertion<String> assertion = NetworkAssertions.validURL();
        String badUrl = one(alphanumericString(100));

        assertion.check(badUrl);
    }

    @Test
    public void testValidPort()
    {
        AlchemyAssertion<Integer> assertion = NetworkAssertions.validPort();
        assertThat(assertion, notNullValue());

        assertion.check(port);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPort1() throws Exception
    {
        AlchemyAssertion<Integer> assertion = NetworkAssertions.validPort();
        int negative = one(negativeIntegers());
        assertion.check(negative);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPort2() throws Exception
    {
        AlchemyAssertion<Integer> assertion = NetworkAssertions.validPort();
        int tooHigh = one(integers(MAX_PORT + 1, Integer.MAX_VALUE));
        assertion.check(tooHigh);
    }

}