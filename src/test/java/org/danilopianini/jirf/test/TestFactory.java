package org.danilopianini.jirf.test;

import com.google.common.collect.ImmutableList;
import org.danilopianini.jirf.Factory;
import org.danilopianini.jirf.FactoryBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *
 */
final class TestFactory {

    /**
     * 
     */
    @Test
    public void testWideningPrimitivesAndWrappers() {
        final Factory f = new FactoryBuilder()
                .withWideningConversions()
                .build();
        assertNotNull(f.build(MyObj.class, 1, 2, (byte) 3).getCreatedObjectOrThrowException());
    }

    /**
     * 
     */
    @Test
    public void testNonWidening() {
        final Factory f = new FactoryBuilder()
                .withNarrowingConversions()
                .build();
        assertNotNull(f.build(MyObj.class, 1, 2, 3.0).getCreatedObjectOrThrowException());
    }

    /**
     * 
     */
    @Test
    public void testEmptyVarargs() {
        final Factory f = new FactoryBuilder()
                .withAutoBoxing()
                .withWideningConversions()
                .build();
        assertNotNull(f.build(MyObj.class, "").getCreatedObjectOrThrowException());
    }

    /**
     * 
     */
    @Test
    public void testSingleValuedVarargs() {
        final Factory f = new FactoryBuilder()
                .withAutoBoxing()
                .withWideningConversions()
                .build();
        assertNotNull(f.build(MyObj.class, "", 1).getCreatedObjectOrThrowException());
    }

    /**
     * 
     */
    @Test
    public void testExpandedVarargs() {
        final Factory f = new FactoryBuilder()
                .withAutoBoxing()
                .withWideningConversions()
                .build();
        assertNotNull(f.build(MyObj.class, "", 1, 2, 3, 4).getCreatedObjectOrThrowException());
    }

    /**
     * 
     */
    @Test
    public void testListEmbeddedVarargs() {
        final Factory f = new FactoryBuilder()
                .withAutoBoxing()
                .withWideningConversions()
                .withArrayListConversions(double[].class)
                .build();
        assertNotNull(
            f.build(MyObj.class, "", ImmutableList.of(1, 2, 3, 4)).getCreatedObjectOrThrowException()
        );
    }

    /**
     * 
     */
    @Test
    public void testSuperclasses() {
        final Factory f = new FactoryBuilder()
                .withAutoBoxing()
                .build();
        f.registerImplicit(CharSequence.class, double.class, s -> Double.parseDouble(s.toString()));
        assertNotNull(f.build(MyObj.class, "1", "2", (byte) 3).getCreatedObjectOrThrowException());
    }

    /**
     * 
     */
    @Test
    public void testSingleton() {
        final Factory f = new FactoryBuilder()
                .build();
        final Object o = new Object();
        f.registerSingleton(o);
        Assert.assertSame(o, f.build(Object.class).getCreatedObjectOrThrowException());
        final BigInteger s = new BigInteger("25");
        f.registerSingleton(Number.class, Object.class, s);
        Assert.assertSame(s, f.build(Number.class).getCreatedObjectOrThrowException());
        Assert.assertSame(s, f.build(Object.class).getCreatedObjectOrThrowException());
        final var bigInteger = f.build(BigInteger.class);
        assertTrue(bigInteger.getCreatedObject().isEmpty());
        assertNotNull(bigInteger.getExceptions());
        Assert.assertFalse(bigInteger.getExceptions().isEmpty());
        final var bigInteger2 = f.build(BigInteger.class, "ciao");
        assertTrue(bigInteger2.getCreatedObject().isEmpty());
        Assert.assertFalse(bigInteger2.getExceptions().isEmpty());
    }

    @Test
    public void testVarArgsWithSingletons() {
        final Factory f = new FactoryBuilder()
            .withNarrowingConversions()
            .withArrayBooleanIntConversions()
            .withArrayListConversions(String[].class, Number[].class)
            .withArrayNarrowingConversions()
            .withAutomaticToString().build();
        f.registerSingleton(Calendar.class, GregorianCalendar.getInstance());
        f.registerSingleton(TimeZone.class, TimeZone.getDefault());
        assertTrue(f.build(ReproduceGPSTrace.class, "gpsTrace", true, "AlignToSimulationTime").getCreatedObject().isPresent());
    }

    // CHECKSTYLE: EmptyStatement OFF
    // CHECKSTYLE: NeedBraces OFF
    // CHECKSTYLE: JavadocType OFF
    // CHECKSTYLE: JavadocMethod OFF
    static final class MyObj {
        MyObj(final double a, final Double b, final byte c) { } // NOPMD
        MyObj(final String a, final double... b) { // NOPMD
            for (int i = 0; i < b.length; i++); // NOPMD
        }
    }
    static final class ReproduceGPSTrace {
        ReproduceGPSTrace(
            final Calendar calendar,
            final TimeZone timezone,
            final String path,
            final boolean cycle,
            final String normalizer,
            final Object... normalizerArgs
        ) {
            this(calendar, timezone, 0, path, cycle, normalizer, normalizerArgs);
        }

        ReproduceGPSTrace(
            final Calendar calendar,
            final TimeZone timezone,
            final double speed,
            final String path,
            final boolean cycle,
            final String normalizer,
            final Object... normalizerArgs
        ) {
            Objects.requireNonNull(calendar);
            Objects.requireNonNull(timezone);
            assertEquals(0, speed, 0d);
            Objects.requireNonNull(path);
            assertTrue(cycle);
            Objects.requireNonNull(normalizer);
            Objects.requireNonNull(normalizerArgs);
        }
    }

}
