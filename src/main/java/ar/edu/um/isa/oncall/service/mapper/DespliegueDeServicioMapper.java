package ar.edu.um.isa.oncall.service.mapper;

import ar.edu.um.isa.oncall.domain.Cambio;
import ar.edu.um.isa.oncall.domain.DespliegueDeServicio;
import ar.edu.um.isa.oncall.domain.Servicio;
import ar.edu.um.isa.oncall.domain.User;
import ar.edu.um.isa.oncall.service.dto.CambioDTO;
import ar.edu.um.isa.oncall.service.dto.DespliegueDeServicioDTO;
import ar.edu.um.isa.oncall.service.dto.ServicioDTO;
import ar.edu.um.isa.oncall.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DespliegueDeServicio} and its DTO {@link DespliegueDeServicioDTO}.
 */
@Mapper(componentModel = "spring")
public interface DespliegueDeServicioMapper extends EntityMapper<DespliegueDeServicioDTO, DespliegueDeServicio> {
    @Mapping(target = "cambio", source = "cambio", qualifiedByName = "cambioTitulo")
    @Mapping(target = "servicio", source = "servicio", qualifiedByName = "servicioNombre")
    @Mapping(target = "ejecutadoPor", source = "ejecutadoPor", qualifiedByName = "userLogin")
    DespliegueDeServicioDTO toDto(DespliegueDeServicio s);

    @Named("cambioTitulo")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    CambioDTO toDtoCambioTitulo(Cambio cambio);

    @Named("servicioNombre")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    ServicioDTO toDtoServicioNombre(Servicio servicio);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
