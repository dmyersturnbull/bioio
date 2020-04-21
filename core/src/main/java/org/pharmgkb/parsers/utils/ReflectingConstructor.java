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

@Immutable
public class ReflectingConstructor<C> {

	private Class<C> m_clazz;
	private Class<?>[] m_signature;
	private Constructor<C> m_constructor;

	public ReflectingConstructor(@Nonnull Class<C> clazz, Class<?>... signature) {
		this.m_clazz = clazz;
		this.m_signature = signature;
		try {
			this.m_constructor = clazz.getConstructor(String.class);
		} catch (NoSuchMethodException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@Nonnull
	public C instance(Object... args) {
		try {
			return this.m_constructor.newInstance(args);
		} catch (InstantiationException | IllegalAccessException | SecurityException e) {
			throw new UnsupportedOperationException("Failed to find constructor with signature (String)", e);
		} catch (InvocationTargetException e) {
			throw new RuntimeReflectionException("Failed calling constructor (String) for " + this.m_clazz.getName(), e);
		}
	}

}
