package com.github.jtail.utils.xfn;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;


/**
 * A version of {@link java.util.Optional} with exception support.
 */
public final class XOptional<T> {
    /**
     * Common instance for {@code empty()}.
     */
    private static final XOptional<?> EMPTY = new XOptional<>();

    /**
     * If non-null, the value; if null, indicates no value is present
     */
    private final T value;

    /**
     * Constructs an empty instance.
     *
     * @implNote Generally only one empty instance, {@link XOptional#EMPTY},
     * should exist per VM.
     */
    private XOptional() {
        this.value = null;
    }

    /**
     * Returns an empty {@code XOptional} instance.  No value is present for this
     * XOptional.
     *
     * @apiNote Though it may be tempting to do so, avoid testing if an object
     * is empty by comparing with {@code ==} against instances returned by
     * {@code Option.empty()}. There is no guarantee that it is a singleton.
     * Instead, use {@link #isPresent()}.
     *
     * @param <T> Type of the non-existent value
     * @return an empty {@code XOptional}
     */
    @SuppressWarnings("unchecked")
    public static<T> XOptional<T> empty() {
        return (XOptional<T>) EMPTY;
    }

    /**
     * Constructs an instance with the value present.
     *
     * @param value the non-null value to be present
     * @throws NullPointerException if value is null
     */
    private XOptional(T value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Returns {@code XOptional} with the specified present non-null value.
     *
     * @param <T> the class of the value
     * @param value the value to be present, which must be non-null
     * @return {@code XOptional} with the value present
     * @throws NullPointerException if value is null
     */
    public static <T> XOptional<T> of(T value) {
        return new XOptional<>(value);
    }

    /**
     * Transforms {@code Optional} into {@code XOptional}
     * @param optional optional to be transformed
     * @param <T> the class of the value
     * @return {@code XOptional} built from a given {@code Optional}
     */
    public static <T> XOptional<T> from(Optional<T> optional) {
        return optional.map(XOptional::of).orElse(empty());
    }

    /**
     * Returns {@code XOptional} describing the specified value, if non-null,
     * otherwise returns an empty {@code XOptional}.
     *
     * @param <T> the class of the value
     * @param value the possibly-null value to describe
     * @return {@code XOptional} with a present value if the specified value
     * is non-null, otherwise an empty {@code XOptional}
     */
    public static <T> XOptional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * If a value is present in this {@code XOptional}, returns the value,
     * otherwise throws {@code NoSuchElementException}.
     *
     * @return the non-null value held by this {@code XOptional}
     * @throws NoSuchElementException if there is no value present
     *
     * @see XOptional#isPresent()
     */
    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * If a value is present, invoke the specified consumer with the value,
     * otherwise do nothing.
     *
     * @param consumer block to be executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is
     * null
     */
    public <X extends Exception> Condition ifPresent(XConsumer<? super T, X> consumer) throws X {
        if (value != null) {
            consumer.accept(value);
            return Condition.present();
        } else {
            return Condition.absent();
        }
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * return {@code XOptional} describing the value, otherwise return an
     * empty {@code XOptional}.
     *
     * @param predicate a predicate to apply to the value, if present
     * @return {@code XOptional} describing the value of this {@code XOptional}
     * if a value is present and the value matches the given predicate,
     * otherwise an empty {@code XOptional}
     * @throws NullPointerException if the predicate is null
     */
    public XOptional<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return isPresent() ? predicate.test(value) ? this : empty() : this;
    }

    /**
     * If a value is present, apply the provided mapping function to it,
     * and if the result is non-null, return {@code XOptional} describing the
     * result.  Otherwise return an empty {@code XOptional}.
     *
     * @apiNote This method supports post-processing on XOptional values, without
     * the need to explicitly check for a return status.  For example, the
     * following code traverses a stream of file names, selects one that has
     * not yet been processed, and then opens that file, returning an
     * {@code XOptional<FileInputStream>}:
     *
     * <pre>{@code
     *     XOptional<FileInputStream> fis =
     *         names.stream().filter(name -> !isProcessedYet(name))
     *                       .findFirst()
     *                       .map(name -> new FileInputStream(name));
     * }</pre>
     *
     * Here, {@code findFirst} returns {@code XOptional<String>}, and then
     * {@code map} returns {@code XOptional<FileInputStream>} for the desired
     * file if one exists.
     *
     * @param <U> The type of the result of the mapping function
     * @param mapper a mapping function to apply to the value, if present
     * @return {@code XOptional} describing the result of applying a mapping
     * function to the value of this {@code XOptional}, if a value is present,
     * otherwise an empty {@code XOptional}
     * @throws NullPointerException if the mapping function is null
     */
    public <U, X extends Exception> XOptional<U> map(XFunction<? super T, ? extends U, X> mapper) throws X {
        Objects.requireNonNull(mapper);
        return isPresent() ? XOptional.ofNullable(mapper.apply(value)) : empty();
    }

    /**
     * If a value is present, apply the provided {@code XOptional}-bearing
     * mapping function to it, return that result, otherwise return an empty
     * {@code XOptional}.  This method is similar to {@link #map(XFunction)},
     * but the provided mapper is one whose result is already {@code XOptional},
     * and if invoked, {@code flatMap} does not wrap it with an additional
     * {@code XOptional}.
     *
     * @param <U> The type parameter to the {@code XOptional} returned by
     * @param mapper a mapping function to apply to the value, if present
     *           the mapping function
     * @return the result of applying {@code XOptional}-bearing mapping
     * function to the value of this {@code XOptional}, if a value is present,
     * otherwise an empty {@code XOptional}
     * @throws NullPointerException if the mapping function is null or returns
     * a null result
     */
    public <U, X extends Exception> XOptional<U> flatMap(XFunction<? super T, XOptional<U>, X> mapper) throws X {
        Objects.requireNonNull(mapper);
        return isPresent() ? Objects.requireNonNull(mapper.apply(value)) : empty();
    }

    /**
     * Return the value if present, otherwise return {@code other}.
     *
     * @param other the value to be returned if there is no value present, may
     * be null
     * @return the value, if present, otherwise {@code other}
     */
    public T orElse(T other) {
        return value != null ? value : other;
    }

    /**
     * Return the value if present, otherwise invoke {@code other} and return
     * the result of that invocation.
     *
     * @param other a {@code Supplier} whose result is returned if no value
     * is present
     * @return the value if present otherwise the result of {@code other.get()}
     * @throws NullPointerException if value is not present and {@code other} is
     * null
     */
    public <X extends Exception> T orElseGet(XSupplier<? extends T, X> other) throws X {
        return value != null ? value : other.get();
    }

    /**
     * Return the contained value, if present, otherwise throw an exception
     * to be created by the provided supplier.
     *
     * @apiNote A method reference to the exception constructor with an empty
     * argument list can be used as the supplier. For example,
     * {@code IllegalStateException::new}
     *
     * @param <X> Type of the exception to be thrown
     * @param exceptionSupplier The supplier which will return the exception to
     * be thrown
     * @return the present value
     * @throws X if there is no value present
     * @throws NullPointerException if no value is present and
     * {@code exceptionSupplier} is null
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * Indicates whether some other object is "equal to" this XOptional. The
     * other object is considered equal if:
     * <ul>
     * <li>it is also {@code XOptional} and;
     * <li>both instances have no value present or;
     * <li>the present values are "equal to" each other via {@code equals()}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {code true} if the other object is "equal to" this object
     * otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if ((obj instanceof XOptional)) {
            XOptional<?> other = (XOptional<?>) obj;
            return Objects.equals(value, other.value);
        } else {
            return false;
        }
    }

    /**
     * Returns the hash code value of the present value, if any, or 0 (zero) if
     * no value is present.
     *
     * @return hash code value of the present value or 0 if no value is present
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * Returns a non-empty string representation of this XOptional suitable for
     * debugging. The exact presentation format is unspecified and may vary
     * between implementations and versions.
     *
     * @implSpec If a value is present the result must include its string
     * representation in the result. Empty and present XOptionals must be
     * unambiguously differentiable.
     *
     * @return the string representation of this instance
     */
    @Override
    public String toString() {
        return value != null ? String.format("XOptional[%s]", value) : "XOptional.empty";
    }
}
