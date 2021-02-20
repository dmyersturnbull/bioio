package org.pharmgkb.parsers.vcf;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.pharmgkb.parsers.ObjectBuilder;
import org.pharmgkb.parsers.vcf.model.VcfMetadataCollection;
import org.pharmgkb.parsers.vcf.model.VcfPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.io.Serial;
import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Checks errors arising from a contradiction between metadata and VCF positions.
 * This class is implemented to {@link Consumer consume} a {@link VcfPosition VcfPositions} and perform a specified action for each error found.
 * The recommended use is with {@link java.util.stream.Stream#peek(Consumer)} before reading or before writing (but before both is likely unnecessary).
 * @author Douglas Myers-Turnbull
 */
@ThreadSafe
public class VcfValidator implements Consumer<VcfPosition> {

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final Consumer<? super InvalidProperty> m_action;

	private final VcfMetadataCollection m_metadata;

	private VcfValidator(@Nonnull Consumer<? super InvalidProperty> action, @Nonnull VcfMetadataCollection metadata) {
		m_action = action;
		m_metadata = metadata;
	}

	@Override
	public void accept(@Nonnull VcfPosition position) {
		Preconditions.checkNotNull(position, "VcfPosition cannot be null");
		m_metadata.getSample().keySet().stream()
				.filter(k -> m_metadata.getHeader().getSampleNames().contains(k))
				.map(s -> new InvalidProperty(position.getChromosome(), position.getPosition(), s, PropertyType.SAMPLE))
				.forEach(m_action);
		m_metadata.getHeader().getSampleNames().stream()
				.filter(k -> m_metadata.getSample().containsKey(k))
				.map(s -> new InvalidProperty(position.getChromosome(), position.getPosition(), s, PropertyType.SAMPLE))
				.forEach(m_action);
		position.getFilters().stream()
				.filter(s -> !m_metadata.getFilter().containsKey(s))
				.map(s -> new InvalidProperty(position.getChromosome(), position.getPosition(), s, PropertyType.FILTER))
				.forEach(m_action);
		position.getFormat().stream()
				.filter(s -> !m_metadata.getFormat().containsKey(s))
				.map(s -> new InvalidProperty(position.getChromosome(), position.getPosition(), s, PropertyType.FORMAT))
				.forEach(m_action);
		position.getInfo().entries().stream()
				.filter(e -> !m_metadata.getFormat().containsKey(e.getKey()))
				.map(e -> new InvalidProperty(position.getChromosome(), position.getPosition(), e.getKey(), PropertyType.INFO))
				.forEach(m_action);
	}

	@NotThreadSafe
	public static class Builder implements ObjectBuilder<VcfValidator> {

		private final VcfMetadataCollection m_metadata;

		private Consumer<InvalidProperty> m_action;

		/**
		 * <em>The default action is to throw a {@link ValidationException} for the first invalid property.</em>
		 */
		public Builder(@Nonnull VcfMetadataCollection metadata) {
			Preconditions.checkNotNull(metadata, "Metadata cannot be null");
			m_action = error -> {throw new ValidationException(error);};
			m_metadata = metadata;
		}

		/**
		 * Sets the {@link #setAction(Consumer) action} to logging a warning for each error.
		 */
		@Nonnull
		public Builder warnOnly() {
			m_action = error -> sf_logger.warn("Bad {}: \"{}\" for position {}:{}", error.getSource(), error.getKey(), error.getChromosome(), error.getPosition());
			return this;
		}

		/**
		 * Replaces the action with a new one.
		 */
		@Nonnull
		public Builder setAction(@Nonnull Consumer<InvalidProperty> action) {
			Preconditions.checkNotNull(action, "Action cannot be null");
			m_action = action;
			return this;
		}

		@Nonnull
		@Override
		public VcfValidator build() {
			return new VcfValidator(m_action, m_metadata);
		}
	}

	/**
	 * An aspect of a VCF position that is wrong because it contradicts the metadata.
	 */
	@Immutable
	public static class InvalidProperty {

		private final String m_chromosome;
		private final long m_position;
		private final String m_key;
		private final PropertyType m_source;

		public InvalidProperty(
				@Nonnull String chromosome,
				@Nonnegative long position,
				@Nonnull String key,
				@Nonnull PropertyType source
		) {
			m_chromosome = chromosome;
			m_position = position;
			m_key = key;
			m_source = source;
		}

		@Nonnull
		public String getChromosome() {
			return m_chromosome;
		}

		public long getPosition() {
			return m_position;
		}

		@Nonnull
		public PropertyType getSource() {
			return m_source;
		}

		@Nonnull
		public String getKey() {
			return m_key;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			InvalidProperty that = (InvalidProperty) o;
			return m_position == that.m_position &&
					Objects.equals(m_chromosome, that.m_chromosome) &&
					Objects.equals(m_key, that.m_key) &&
					m_source == that.m_source;
		}

		@Override
		public int hashCode() {
			return Objects.hash(m_chromosome, m_position, m_key, m_source);
		}

		@Nonnull
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("chromosome", m_chromosome)
					.add("position", m_position)
					.add("key", m_key)
					.add("source", m_source)
					.toString();
		}
	}

	/**
	 * What property is wrong: INFO, FORMAT, FILTER, or SAMPLE.
	 */
	public enum PropertyType {
		INFO, FORMAT, FILTER, SAMPLE
	}

	/**
	 * An exception caused by an {@link InvalidProperty}.
	 */
	public static class ValidationException extends RuntimeException {

		private final InvalidProperty m_invalid;

		public InvalidProperty getInvalidProperty() {
			return m_invalid;
		}

		public ValidationException(InvalidProperty error) {
			super("Bad " + error.getSource() + ": \"" + error.getKey() + "\" for position " + error.getChromosome() + ":" + error.getPosition());
			m_invalid = error;
		}

		@Serial
		private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
			throw new java.io.NotSerializableException("org.pharmgkb.parsers.vcf.VcfValidator.ValidationException");
		}

		@Serial
		private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
			throw new java.io.NotSerializableException("org.pharmgkb.parsers.vcf.VcfValidator.ValidationException");
		}
	}

	@Override
	public String toString() {
		return "VcfValidator{" +
				"action=" + m_action +
				", metadata: " + m_metadata.getLines().size() + " lines" +
				'}';
	}
}
