/*
 * Copyright 2015 SirWellington Tech.
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
package tech.sirwellington.alchemy.arguments;

import tech.sirwellington.alchemy.annotations.arguments.NonNull;
import tech.sirwellington.alchemy.annotations.arguments.Nullable;
import tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern;
import static tech.sirwellington.alchemy.annotations.designs.patterns.StrategyPattern.Role.INTERFACE;

/**
 * {@linkplain AlchemyAssertion Alchemy Assertions} analyze arguments for validity. It is very easy
 * to write your own assertions, making for powerful custom argument checks.
 *
 * @param <A> The type of argument an assertion checks
 *
 * @author SirWellington
 */
@FunctionalInterface
@StrategyPattern(role = INTERFACE)
public interface AlchemyAssertion<A>
{

    /**
     * Asserts the validity of the argument.
     *
     * @param argument The argument to validate
     *
     * @throws FailedAssertionException When the argument-check fails. Note that
     *                                  {@link FailedAssertionException} already extends
     *                                  {@link IllegalArgumentException}. Any other types of
     *                                  {@linkplain Exception Exceptions} thrown will be wrapped in
     *                                  a {@link FailedAssertionException}.
     */
    void check(@Nullable A argument) throws FailedAssertionException;

    /**
     * Allows combinations of multiple {@linkplain AlchemyAssertion assertions} in one.
     *
     * For example, a {@code validAge} assertion could be constructed dynamically using:
     * <pre>
     *
     * {@code
     * AlchemyAssertion<Integer> validAge = positiveInteger()
     *      .and(greaterThanOrEqualTo(1))
     *      .and(lessThanOrEqualTo(120))
     *
     * checkThat(age)
     * .is(validAge);
     * }
     *
     * </pre>
     *
     * This allows you to save and store Assertions that are commonly used together to perform
     * argument checks, and to do so at runtime.
     * <p>
     * Note that due to limitations of the Java Compiler, the first
     * {@linkplain AlchemyAssertion Assertion} that you make must match the type of the argument. So
     * for example,
     * <pre>
     * {@code
     *  notNull()
     *      .and(positiveInteger())
     *      .check(age);
     * }
     * </pre>
     * would not work because notNull references a vanilla {@code Object}.
     * <p>
     * {@link #combine(tech.sirwellington.alchemy.arguments.AlchemyAssertion, tech.sirwellington.alchemy.arguments.AlchemyAssertion...)} 
     * does not have these limitations.
     *
     * @param other
     *
     * @return
     * 
     * @see #combine(tech.sirwellington.alchemy.arguments.AlchemyAssertion, tech.sirwellington.alchemy.arguments.AlchemyAssertion...) 
     */
    @NonNull
    default AlchemyAssertion<A> and(@NonNull AlchemyAssertion<A> other) throws IllegalArgumentException
    {
        Checks.Internal.checkNotNull(other, "assertion cannot be null");

        return argument ->
        {
            this.check(argument);
            other.check(argument);
        };
    }

    /**
     * Combines multiple {@linkplain AlchemyAssertion assertions} into one.
     *
     * For example, a {@code validAge} assertion could be constructed dynamically using:
     * <pre>
     *
     * {@code
     *   AlchemyAssertion<Integer> validAge = combine
     * (
     *      notNull(),
     *      greaterThanOrEqualTo(1),
     *      lessThanOrEqualTo(120),
     *      positiveInteger()
     * );
     *
     * checkThat(age)
     *      .is(validAge);
     * }
     *
     * </pre>
     *
     * This allows you to <b>combine and store</b> Assertions that are commonly used together to perform
     * argument checks.
     *
     * @param first
     * @param other
     * @param <T>
     *
     * @return
     * 
     * @see #and(tech.sirwellington.alchemy.arguments.AlchemyAssertion) 
     */
    static <T> AlchemyAssertion<T> combine(@NonNull AlchemyAssertion<T> first, AlchemyAssertion<T>... other)
    {
        Checks.Internal.checkNotNull(first, "the first AlchemyAssertion cannot be null");
        Checks.Internal.checkNotNull(other, "null varargs");

        return (argument) ->
        {
            first.check(argument);
            
            for (AlchemyAssertion<T> assertion : other)
            {
                assertion.check(argument);
            }
        };
    }

}
