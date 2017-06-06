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

package tech.sirwellington.alchemy.arguments.assertions

import java.net.URL
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import tech.sirwellington.alchemy.arguments.AlchemyAssertion
import tech.sirwellington.alchemy.arguments.FailedAssertionException
import tech.sirwellington.alchemy.test.junit.runners.AlchemyTestRunner
import tech.sirwellington.alchemy.test.junit.runners.GenerateInteger
import tech.sirwellington.alchemy.test.junit.runners.GenerateURL
import tech.sirwellington.alchemy.test.junit.runners.Repeat

import org.hamcrest.Matchers.notNullValue
import org.junit.Assert.assertThat
import tech.sirwellington.alchemy.generator.AlchemyGenerator.one
import tech.sirwellington.alchemy.generator.NumberGenerators.integers
import tech.sirwellington.alchemy.generator.NumberGenerators.negativeIntegers
import tech.sirwellington.alchemy.generator.StringGenerators.alphanumericString
import tech.sirwellington.alchemy.test.junit.ThrowableAssertion.assertThrows
import tech.sirwellington.alchemy.test.junit.runners.GenerateInteger.Type.RANGE


/**

 * @author SirWellington
 */
@Repeat(1000)
@RunWith(AlchemyTestRunner::class)
class NetworkAssertionsTest
{
    @GenerateURL
    private val url: URL? = null

    @GenerateInteger(value = RANGE, min = 1, max = MAX_PORT)
    private val port: Int = 0

    @Before
    fun setUp()
    {
    }

    @Test
    fun testValidURL()
    {
        val assertion = NetworkAssertions.validURL()
        assertThat(assertion, notNullValue())

        assertion.check(url!!.toString())

        val badUrl = one(alphanumericString(100))

        assertThrows { assertion.check(badUrl) }
                .isInstanceOf(FailedAssertionException::class.java)
    }

    @Test
    fun testValidPort()
    {
        val assertion = NetworkAssertions.validPort()
        assertThat(assertion, notNullValue())

        assertion.check(port)

        val negative = one(negativeIntegers())
        assertThrows { assertion.check(negative) }
                .isInstanceOf(FailedAssertionException::class.java)

        val tooHigh = one(integers(MAX_PORT + 1, Integer.MAX_VALUE))
        assertThrows { assertion.check(tooHigh) }
                .isInstanceOf(FailedAssertionException::class.java)
    }

    companion object
    {

        private val MAX_PORT = 65535
    }

}