package ar.edu.um.isa.oncall.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CorrelacionCambioIncidenteCriteriaTest {

    @Test
    void newCorrelacionCambioIncidenteCriteriaHasAllFiltersNullTest() {
        var correlacionCambioIncidenteCriteria = new CorrelacionCambioIncidenteCriteria();
        assertThat(correlacionCambioIncidenteCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void correlacionCambioIncidenteCriteriaFluentMethodsCreatesFiltersTest() {
        var correlacionCambioIncidenteCriteria = new CorrelacionCambioIncidenteCriteria();

        setAllFilters(correlacionCambioIncidenteCriteria);

        assertThat(correlacionCambioIncidenteCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void correlacionCambioIncidenteCriteriaCopyCreatesNullFilterTest() {
        var correlacionCambioIncidenteCriteria = new CorrelacionCambioIncidenteCriteria();
        var copy = correlacionCambioIncidenteCriteria.copy();

        assertThat(correlacionCambioIncidenteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(correlacionCambioIncidenteCriteria)
        );
    }

    @Test
    void correlacionCambioIncidenteCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var correlacionCambioIncidenteCriteria = new CorrelacionCambioIncidenteCriteria();
        setAllFilters(correlacionCambioIncidenteCriteria);

        var copy = correlacionCambioIncidenteCriteria.copy();

        assertThat(correlacionCambioIncidenteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(correlacionCambioIncidenteCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var correlacionCambioIncidenteCriteria = new CorrelacionCambioIncidenteCriteria();

        assertThat(correlacionCambioIncidenteCriteria).hasToString("CorrelacionCambioIncidenteCriteria{}");
    }

    private static void setAllFilters(CorrelacionCambioIncidenteCriteria correlacionCambioIncidenteCriteria) {
        correlacionCambioIncidenteCriteria.id();
        correlacionCambioIncidenteCriteria.origen();
        correlacionCambioIncidenteCriteria.estado();
        correlacionCambioIncidenteCriteria.detectadaEn();
        correlacionCambioIncidenteCriteria.minutosEntreDespliegueEIncidente();
        correlacionCambioIncidenteCriteria.nota();
        correlacionCambioIncidenteCriteria.incidenteId();
        correlacionCambioIncidenteCriteria.despliegueId();
        correlacionCambioIncidenteCriteria.confirmadaPorId();
        correlacionCambioIncidenteCriteria.distinct();
    }

    private static Condition<CorrelacionCambioIncidenteCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getOrigen()) &&
                condition.apply(criteria.getEstado()) &&
                condition.apply(criteria.getDetectadaEn()) &&
                condition.apply(criteria.getMinutosEntreDespliegueEIncidente()) &&
                condition.apply(criteria.getNota()) &&
                condition.apply(criteria.getIncidenteId()) &&
                condition.apply(criteria.getDespliegueId()) &&
                condition.apply(criteria.getConfirmadaPorId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CorrelacionCambioIncidenteCriteria> copyFiltersAre(
        CorrelacionCambioIncidenteCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getOrigen(), copy.getOrigen()) &&
                condition.apply(criteria.getEstado(), copy.getEstado()) &&
                condition.apply(criteria.getDetectadaEn(), copy.getDetectadaEn()) &&
                condition.apply(criteria.getMinutosEntreDespliegueEIncidente(), copy.getMinutosEntreDespliegueEIncidente()) &&
                condition.apply(criteria.getNota(), copy.getNota()) &&
                condition.apply(criteria.getIncidenteId(), copy.getIncidenteId()) &&
                condition.apply(criteria.getDespliegueId(), copy.getDespliegueId()) &&
                condition.apply(criteria.getConfirmadaPorId(), copy.getConfirmadaPorId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
