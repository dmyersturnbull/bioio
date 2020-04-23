package org.pharmgkb.parsers.utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The results of an operation that may have been successful, or thrown an exception.
 * Stores either the value, or the thrown exception.
 * Provides methods for mapping, composition, and recovery of the values.
 * Inspired by Scala's Try.
 * For example, this will download a resource, upload its text, and return the response:
 * <code>
 *     String response = Try.attempt(getWebResource)
 *                          .recover(getWebResourceFromMirror)
 *                          .map(resource -> resource.text)
 *                          .compose(text -> upload(text))
 *                          .orElseGet(error -> "Failed! " + error.getMessage()));
 *
 * </code>
 * @param <T> The type of the result of the operation
 */
public class Try<T, E extends Throwable> {

	private final T value;
	private final E exception;
	private final Class<E> clazz;

	@Nonnull
	public static <U> Try<U,Exception> succeed(@Nonnull U t) {
		return new Try<>(t, null, Exception.class);
	}
	@Nonnull
	public static <U,V extends Throwable> Try<U,V> succeed(@Nonnull U t, Class<V> clazz) {
		return new Try<>(t, null, clazz);
	}
	@Nonnull
	public static <U> Try<U, Exception> fail(@Nonnull Exception e) {
		return new Try<>(null, e, Exception.class);
	}
	@Nonnull
	public static <U,V extends Throwable> Try<U,V> fail(@Nonnull V e, @Nonnull Class<V> clazz) {
		return new Try<>(null, e, clazz);
	}

	@Nonnull
	public static <U> Try<U,Exception> attempt(@Nonnull Supplier<U> t) {
		return attempt(t, Exception.class);
	}
	public static <U,V extends Throwable> Try<U,V> attempt(@Nonnull Supplier<U> t, @Nonnull Class<V> clazz) {
		try {
			return new Try<>(t.get(), null, clazz);
		} catch (Throwable e) {
			if (clazz.isInstance(e)) {
				return new Try<>(null, (V) e, clazz);
			} else {
				throw e;
			}
		}
	}

	protected Try(@Nullable T value, @Nullable E exception, @Nonnull Class<E> clazz) {
		assert (value ==null)^(exception ==null);
		this.value = value;
		this.exception = exception;
		this.clazz = clazz;
	}

	/**
	 * Maps the result of this Try through a function, if it's a success.
	 * Otherwise (if it's a failure), just returns a copy.
	 * Will re-throw exceptions thrown by {@code fn}.
	 * @param fn A function to apply to this if it's a success
	 * @param <Z> The new type
	 * @return A new Try
	 * @see Try#compose
	 */
	@Nonnull
	public <Z> Try<Z,E> map(@Nonnull Function<T, Z> fn) {
		if (value == null) {
			return new Try<>(null, exception, clazz);
		} else {
			return new Try<>(fn.apply(value), null, clazz);
		}
	}

	/**
	 * Returns this try if it is a success.
	 * Otherwise, returns a successful Try containing {@code value2}.
	 * @param value2 A value to fill in
	 * @return A new Try
	 * @see Try#recover(Supplier)
	 * @see Try#recover(Function)
	 * @see Try#orElse(T)
	 */
	@Nonnull
	public Try<T,E> recover(@Nonnull T value2) {
		if (value == null) {
			return new Try<>(value2, null, clazz);
		} else {
			return this;
		}
	}
	/**
	 * Attempt to recover failures.
	 * If this succeeded, returns it.
	 * If it failed, try filling it with {@code supplier.get()} instead.
	 * If calling {@code supplier()} throws Exception @{code e},
	 * will return @{code Try.fail(e)} with {@code this.getException()} added as suppressed.
	 * @param sup Called when this Try failed
	 * @return A new Try
	 * @see Try#recover(T)
	 * @see Try#recover(Function)
	 * @see Try#orElseGet(Supplier)
	 */
	@Nonnull
	public Try<T,E> recover(@Nonnull Supplier<T> sup) {
	    if (value == null) {
			return _map(z -> sup.get());
        } else {
			return this;
        }
    }

	/**
	 * Attempt to recover failures.
	 * If {@code this} is a success, returns it.
	 * If it is a failure, try filling it with {@code fn.apply(this.e)} instead.
	 * If calling {@code supplier()} throws Exception @{code e},
	 * will return @{code Try.fail(e)} with {@code this.getException()} added as suppressed.
	 * @param fn Called when this Try failed
	 * @return A new Try
	 * @see Try#recover(Supplier)
	 * @see Try#recover(Object)
	 * @see Try#orElseGet(Function)
	 */
	@Nonnull
	public Try<T,E> recover(@Nonnull Function<E, T> fn) {
		if (value == null) {
			return _map(z -> fn.apply(exception));
		} else {
			return this;
		}
	}

	/**
	 * Compose this Try with a function.
	 * If {@code this} Try is a success, tries to map its result to {@code fn(this.value)}.
	 * If {@code this} Try is a failure, returns a copy of it (with type &lt;Z&gt;).
	 * If calling {@code fn} throws an exception {@code}, will add {@code this.exception} as a suppressed.
	 * @param fn A function to be called on {@code this.t}
	 * @param <Z> The new type
	 * @return A new @{code Try&lt;Z&gt;}
	 * @see Try#map
	 */
	@Nonnull
	public <Z> Try<Z,E> compose(@Nonnull Function<T, Z> fn) {
		if (value == null) {
			return new Try<>(null, exception, clazz);
		} else {
			return _map(fn);
		}
	}

	@Nonnull
	protected <Z> Try<Z,E> _map(@Nonnull Function<T, Z> fn) {
		Z z;
		try {
			z = fn.apply(value);
		} catch (Throwable e) {
			if (!clazz.isInstance(e)) throw e;
			if (this.exception != null) { // must always be true though!
				e.addSuppressed(this.exception);
			}
			//noinspection unchecked
			return new Try<>(null, (E) e, clazz);
		}
		return new Try<>(z, null, clazz);
	}

	public boolean isDefined() {
		return this.value != null;
	}
	public boolean isEmpty() {
		return this.value == null;
	}
	public boolean succeeded() {
		return this.value != null;
	}
	public boolean failed() {
		return this.value == null;
	}

	@Nonnull
	public Optional<T> get() {
		return Optional.ofNullable(value);
	}
	@Nonnull
	public T orElse(@Nonnull T value2) {
		return Optional.ofNullable(value).orElse(value2);
	}
	@Nonnull
	public T orElseGet(@Nonnull Supplier<T> supplier) {
		return Optional.ofNullable(value).orElseGet(supplier);
	}
	@Nonnull
	public T orElseGet(@Nonnull Function<E, T> fn) {
		if (value == null) {
			return fn.apply(exception);
		} else {
			return value;
		}
	}
	@Nonnull
	public T orElseThrow() throws E {
		if (value != null) {
			return value;
		} else {
			throw exception;
		}
	}
	@Nonnull
	public T orElseThrow(@Nonnull RuntimeException e) throws RuntimeException {
		if (value != null) {
			return value;
		} else {
			throw e;
		}
	}
	@Nonnull
	public T orElseThrow(@Nonnull Function<E, ? extends E> fn) throws E {
		if (value != null) {
			return value;
		} else {
			throw fn.apply(exception);
		}
	}

	@Nonnull
	public Optional<? extends E> getException() {
		return Optional.ofNullable(exception);
	}

	@Override
	public String toString() {
		if (this.value != null) {
			return "Success[" + value + "]";
		} else {
			return "Failure[" + exception + "]";
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Try<?,?> aTry = (Try<?,?>) o;
		return Objects.equals(value, aTry.value) &&
				Objects.equals(exception, aTry.exception);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, exception);
	}
}
