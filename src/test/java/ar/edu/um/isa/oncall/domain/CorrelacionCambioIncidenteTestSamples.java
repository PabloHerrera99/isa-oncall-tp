package ar.edu.um.isa.oncall.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CorrelacionCambioIncidenteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static CorrelacionCambioIncidente getCorrelacionCambioIncidenteSample1() {
        return new CorrelacionCambioIncidente().id(1L).minutosEntreDespliegueEIncidente(1).nota("nota1");
    }

    public static CorrelacionCambioIncidente getCorrelacionCambioIncidenteSample2() {
        return new CorrelacionCambioIncidente().id(2L).minutosEntreDespliegueEIncidente(2).nota("nota2");
    }

    public static CorrelacionCambioIncidente getCorrelacionCambioIncidenteRandomSampleGenerator() {
        return new CorrelacionCambioIncidente()
            .id(longCount.incrementAndGet())
            .minutosEntreDespliegueEIncidente(intCount.incrementAndGet())
            .nota(UUID.randomUUID().toString());
    }
}
