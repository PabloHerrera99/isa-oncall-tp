package ar.edu.um.isa.oncall.config;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracion propia de las reglas de negocio
 */
@Configuration
public class ReglasConfiguration {

    /**
     * Reloj del sistema en UTC. Las reglas piden la hora a este bean y no a
     * Instant.now(), asi los tests pueden fijar el instante.
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
