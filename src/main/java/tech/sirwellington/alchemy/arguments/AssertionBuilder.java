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

import tech.sirwellington.alchemy.annotations.arguments.NonEmpty;
import tech.sirwellington.alchemy.annotations.arguments.NonNull;
import tech.sirwellington.alchemy.annotations.designs.FluidAPIDesign;

/**
 * The {@link AssertionBuilder} allows compositions of rich argument checks.
 *
 * <pre>
 *
 * {@code
 * checkThat(password)
 *      .usingMessage("Invalid Password")
 *      .throwing(InvalidCredentialsException.class)
 *      .is(notNull())
 *      .is(nonEmptyString())
 *      .is(stringIsAtLeastOfLength(10));
 * }
 * </pre> 
 * Alternatively using custom Exception Thrower:
 *
 * <pre>
 * {@code
 * checkThat(password)
      .throwing(ex -> new BadRequestException("Bad Password", ex))
      .is(notNull())
      .is(nonEmptyString())
      .is(stringIsAtLeastOfLength(10));
 }
 * </pre>
 *
 * If no {@link ExceptionMapper} is used, the original {@link FailedAssertionException} is thrown.
 *
 * @param <Argument> The type of the argument being checked
 * @param <Ex>       The type of {@link Exception} that will be thrown if the given assertion fails.
 *
 * @author SirWellington
 */
@FluidAPIDesign
public interface AssertionBuilder<Argument, Ex extends Throwable>
{

    /**
     * Makes it easy to override the
     * {@linkplain FailedAssertionException#getMessage() error message} in the Exception thrown, in
     * case the argument fails the assertion.
     *
     * @param message
     *
     * @return
     */
    AssertionBuilder<Argument, Ex> usingMessage(@NonEmpty String message);

    /**
     * Provide the behavior that responds to an argument failing an {@link AlchemyAssertion}. If the
     * provided {@code ExceptionMapper} returns null, no exception will be thrown, and it will be as
     * if the assertion was passed.
     *
     * @param <Ex>
     * @param exceptionMapper
     *
     * @return
     *
     * @see ExceptionMapper
     */
    <Ex extends Throwable> AssertionBuilder<Argument, Ex> throwing(@NonNull ExceptionMapper<Ex> exceptionMapper);

    /**
     * This operation runs the specified assertion on the {@code Argument}. This operation is
     * chain-able, to allow for multiple assertions on a single argument.
     *
     * @param assertion The assertion to run throw the argument. Must be non-null.
     *
     * @return
     *
     * @throws Ex Throws the desired exception if the assertion fails.
     */
    AssertionBuilder<Argument, Ex> is(@NonNull AlchemyAssertion<Argument> assertion) throws Ex;

    /**
     * Grammatical sugar in case multiple arguments are used.
     * <br>
     * It reads better to say
     * <pre>
     *  checkThat(first, second, third)
     *      .are(notEmpty());
     * </pre> than to say
     * <pre>
     * checkThat(first, second, third)
     *      .is(notEmpty());
     * </pre> but the result is the same.
     *
     * @param assertion
     * @return
     * @throws Ex
     */
    default AssertionBuilder<Argument, Ex> are(@NonNull AlchemyAssertion<Argument> assertion) throws Ex
    {
        return is(assertion);
    }

    /**
     * This is an alternate way to specify an Exception, using instead a class. The library will
     * create the exception instances for you, provided that the exception followed the common Java
     * Exception constructor convention. See {@link IllegalArgumentException} for an example of a
     * common Exception.
     *
     * @param <Ex>           The type of the Exception that will be thrown.
     * @param exceptionClass The class of the Exception that will be thrown. Must be non-null.
     *
     * @return
     */
    default <Ex extends Throwable> AssertionBuilder<Argument, Ex> throwing(@NonNull Class<Ex> exceptionClass)
    {
        return AssertionBuilder.this.throwing(new DynamicExceptionSupplier<>(exceptionClass, ""));
    }

}
