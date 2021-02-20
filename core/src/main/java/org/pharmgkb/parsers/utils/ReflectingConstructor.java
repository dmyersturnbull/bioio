package org.pharmgkb.parsers.utils;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class RuntimeReflectionException extends RuntimeException {
	public RuntimeReflectionException() { }
	public RuntimeReflectionException(String message) { super(message); }
	public RuntimeReflectionException(String message, Throwable cause) { super(message, cause); }
	public RuntimeReflectionException(Throwable cause) { super(cause); }
	public RuntimeReflectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}

/**
 * Helper to deal with type erasure problems.
 * A contrived example:
 * {@code
 * class FruitPicker<T extends Fruit> {
 *     private T myFruit;
 *     private ReflectingConstructor<T> reflector;
 *     public FruitPicker(T t, Class<T> clazz) {
 *         this.myFruit = fruit;
 *         this.reflector = new ReflectingConstructor(clazz, String.class);
 *     }
 *     public T propagate(String color) {
 *         if (color != myFruit.color) print("A new color of fruit!");
 *         return reflector.instance(color);
 *     }
 * }
 * }
 * @param <C> The type of the class
 */
@Immutable
public class ReflectingConstructor<C> {

	private final Class<C> m_clazz;
	private final Class<?>[] m_signature;
	private final Constructor<C> m_constructor;

	public ReflectingConstructor(@Nonnull Class<C> clazz, @Nonnull Class<?>... signature) {
		m_clazz = clazz;
		m_signature = signature;
		try {
			m_constructor = clazz.getConstructor(String.class);
		} catch (NoSuchMethodException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@Nonnull
	public C instance(@Nonnull Object... args) {
		try {
			return m_constructor.newInstance(args);
		} catch (InstantiationException | IllegalAccessException | SecurityException e) {
			throw new UnsupportedOperationException("Failed to find constructor with signature (String)", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeReflectionException("Failed calling constructor (String) for " + this.m_clazz.getName(), e);
		}
	}

}
