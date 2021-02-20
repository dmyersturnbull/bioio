package org.pharmgkb.parsers.vcf.utils;

import org.pharmgkb.parsers.vcf.model.VcfMetadataCollection;
import org.pharmgkb.parsers.vcf.model.metadata.VcfMetadata;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Collects a stream of {@link VcfMetadata} into a {@link VcfMetadataCollection}.
 * @author Douglas Myers-Turnbull
 */
public class VcfMetadataCollector implements Collector<VcfMetadata, VcfMetadataCollection.Builder, VcfMetadataCollection> {

	private VcfMetadataCollection.Builder m_builder = new VcfMetadataCollection.Builder();

	@Override
	public Supplier<VcfMetadataCollection.Builder> supplier() {
		return () -> m_builder;
	}

	@Override
	public BiConsumer<VcfMetadataCollection.Builder, VcfMetadata> accumulator() {
		return VcfMetadataCollection.Builder::addLine;
	}

	@Override
	public BinaryOperator<VcfMetadataCollection.Builder> combiner() {
		return VcfMetadataCollection.Builder::new;
	}

	@Override
	public Function<VcfMetadataCollection.Builder, VcfMetadataCollection> finisher() {
		return VcfMetadataCollection.Builder::build;
	}

	@Override
	public Set<Characteristics> characteristics() {
		return EnumSet.noneOf(Characteristics.class);
	}
}
