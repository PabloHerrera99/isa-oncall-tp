package ar.edu.um.isa.oncall.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CambioCriteriaTest {

    @Test
    void newCambioCriteriaHasAllFiltersNullTest() {
        var cambioCriteria = new CambioCriteria();
        assertThat(cambioCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void cambioCriteriaFluentMethodsCreatesFiltersTest() {
        var cambioCriteria = new CambioCriteria();

        setAllFilters(cambioCriteria);

        assertThat(cambioCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void cambioCriteriaCopyCreatesNullFilterTest() {
        var cambioCriteria = new CambioCriteria();
        var copy = cambioCriteria.copy();

        assertThat(cambioCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(cambioCriteria)
        );
    }

    @Test
    void cambioCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var cambioCriteria = new CambioCriteria();
        setAllFilters(cambioCriteria);

        var copy = cambioCriteria.copy();

        assertThat(cambioCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(cambioCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var cambioCriteria = new CambioCriteria();

        assertThat(cambioCriteria).hasToString("CambioCriteria{}");
    }

    private static void setAllFilters(CambioCriteria cambioCriteria) {
        cambioCriteria.id();
        cambioCriteria.titulo();
        cambioCriteria.descripcion();
        cambioCriteria.tipo();
        cambioCriteria.riesgo();
        cambioCriteria.ticketUrl();
        cambioCriteria.creadoEn();
        cambioCriteria.autorId();
        cambioCriteria.despliegueId();
        cambioCriteria.distinct();
    }

    private static Condition<CambioCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitulo()) &&
                condition.apply(criteria.getDescripcion()) &&
                condition.apply(criteria.getTipo()) &&
                condition.apply(criteria.getRiesgo()) &&
                condition.apply(criteria.getTicketUrl()) &&
                condition.apply(criteria.getCreadoEn()) &&
                condition.apply(criteria.getAutorId()) &&
                condition.apply(criteria.getDespliegueId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CambioCriteria> copyFiltersAre(CambioCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitulo(), copy.getTitulo()) &&
                condition.apply(criteria.getDescripcion(), copy.getDescripcion()) &&
                condition.apply(criteria.getTipo(), copy.getTipo()) &&
                condition.apply(criteria.getRiesgo(), copy.getRiesgo()) &&
                condition.apply(criteria.getTicketUrl(), copy.getTicketUrl()) &&
                condition.apply(criteria.getCreadoEn(), copy.getCreadoEn()) &&
                condition.apply(criteria.getAutorId(), copy.getAutorId()) &&
                condition.apply(criteria.getDespliegueId(), copy.getDespliegueId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
