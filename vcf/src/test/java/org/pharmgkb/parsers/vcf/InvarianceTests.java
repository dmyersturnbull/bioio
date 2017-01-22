package org.pharmgkb.parsers.vcf;

/**
 * Tests that the composition of the following tasks equals the identity:
 * <ol>
 *     <li>Parse a VCF file</li>
 *     <li>Apply some transformation to the VCF streams</li>
 *     <li>Aply the inverse transformation to the VCF streams</li>
 *     <li>Write the VCF file</li>
 * </ol>
 * The result should be the same as the original file.
 *
 */
public class InvarianceTests {
}
