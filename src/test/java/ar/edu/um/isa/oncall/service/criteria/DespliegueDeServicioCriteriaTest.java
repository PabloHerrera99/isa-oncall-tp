package ar.edu.um.isa.oncall.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DespliegueDeServicioCriteriaTest {

    @Test
    void newDespliegueDeServicioCriteriaHasAllFiltersNullTest() {
        var despliegueDeServicioCriteria = new DespliegueDeServicioCriteria();
        assertThat(despliegueDeServicioCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void despliegueDeServicioCriteriaFluentMethodsCreatesFiltersTest() {
        var despliegueDeServicioCriteria = new DespliegueDeServicioCriteria();

        setAllFilters(despliegueDeServicioCriteria);

        assertThat(despliegueDeServicioCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void despliegueDeServicioCriteriaCopyCreatesNullFilterTest() {
        var despliegueDeServicioCriteria = new DespliegueDeServicioCriteria();
        var copy = despliegueDeServicioCriteria.copy();

        assertThat(despliegueDeServicioCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(despliegueDeServicioCriteria)
        );
    }

    @Test
    void despliegueDeServicioCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var despliegueDeServicioCriteria = new DespliegueDeServicioCriteria();
        setAllFilters(despliegueDeServicioCriteria);

        var copy = despliegueDeServicioCriteria.copy();

        assertThat(despliegueDeServicioCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(despliegueDeServicioCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var despliegueDeServicioCriteria = new DespliegueDeServicioCriteria();

        assertThat(despliegueDeServicioCriteria).hasToString("DespliegueDeServicioCriteria{}");
    }

    private static void setAllFilters(DespliegueDeServicioCriteria despliegueDeServicioCriteria) {
        despliegueDeServicioCriteria.id();
        despliegueDeServicioCriteria.version();
        despliegueDeServicioCriteria.entorno();
        despliegueDeServicioCriteria.iniciadoEn();
        despliegueDeServicioCriteria.finalizadoEn();
        despliegueDeServicioCriteria.resultado();
        despliegueDeServicioCriteria.cambioId();
        despliegueDeServicioCriteria.servicioId();
        despliegueDeServicioCriteria.ejecutadoPorId();
        despliegueDeServicioCriteria.correlacionId();
        despliegueDeServicioCriteria.distinct();
    }

    private static Condition<DespliegueDeServicioCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getVersion()) &&
                condition.apply(criteria.getEntorno()) &&
                condition.apply(criteria.getIniciadoEn()) &&
                condition.apply(criteria.getFinalizadoEn()) &&
                condition.apply(criteria.getResultado()) &&
                condition.apply(criteria.getCambioId()) &&
                condition.apply(criteria.getServicioId()) &&
                condition.apply(criteria.getEjecutadoPorId()) &&
                condition.apply(criteria.getCorrelacionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DespliegueDeServicioCriteria> copyFiltersAre(
        DespliegueDeServicioCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getVersion(), copy.getVersion()) &&
                condition.apply(criteria.getEntorno(), copy.getEntorno()) &&
                condition.apply(criteria.getIniciadoEn(), copy.getIniciadoEn()) &&
                condition.apply(criteria.getFinalizadoEn(), copy.getFinalizadoEn()) &&
                condition.apply(criteria.getResultado(), copy.getResultado()) &&
                condition.apply(criteria.getCambioId(), copy.getCambioId()) &&
                condition.apply(criteria.getServicioId(), copy.getServicioId()) &&
                condition.apply(criteria.getEjecutadoPorId(), copy.getEjecutadoPorId()) &&
                condition.apply(criteria.getCorrelacionId(), copy.getCorrelacionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
