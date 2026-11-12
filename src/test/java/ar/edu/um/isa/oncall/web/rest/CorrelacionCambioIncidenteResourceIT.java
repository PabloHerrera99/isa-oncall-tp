package ar.edu.um.isa.oncall.web.rest;

import static ar.edu.um.isa.oncall.domain.CorrelacionCambioIncidenteAsserts.*;
import static ar.edu.um.isa.oncall.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.CorrelacionCambioIncidente;
import ar.edu.um.isa.oncall.domain.DespliegueDeServicio;
import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.domain.User;
import ar.edu.um.isa.oncall.domain.enumeration.EstadoCorrelacion;
import ar.edu.um.isa.oncall.domain.enumeration.OrigenCorrelacion;
import ar.edu.um.isa.oncall.repository.CorrelacionCambioIncidenteRepository;
import ar.edu.um.isa.oncall.repository.UserRepository;
import ar.edu.um.isa.oncall.service.CorrelacionCambioIncidenteService;
import ar.edu.um.isa.oncall.service.dto.CorrelacionCambioIncidenteDTO;
import ar.edu.um.isa.oncall.service.mapper.CorrelacionCambioIncidenteMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CorrelacionCambioIncidenteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CorrelacionCambioIncidenteResourceIT {

    private static final OrigenCorrelacion DEFAULT_ORIGEN = OrigenCorrelacion.AUTOMATICA;
    private static final OrigenCorrelacion UPDATED_ORIGEN = OrigenCorrelacion.MANUAL;

    private static final EstadoCorrelacion DEFAULT_ESTADO = EstadoCorrelacion.SUGERIDA;
    private static final EstadoCorrelacion UPDATED_ESTADO = EstadoCorrelacion.CONFIRMADA;

    private static final Instant DEFAULT_DETECTADA_EN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DETECTADA_EN = Instant.ofEpochMilli(1701729143509L);

    private static final Integer DEFAULT_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE = 0;
    private static final Integer UPDATED_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE = 1;
    private static final Integer SMALLER_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE = 0 - 1;

    private static final String DEFAULT_NOTA = "AAAAAAAAAA";
    private static final String UPDATED_NOTA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/correlacion-cambio-incidentes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CorrelacionCambioIncidenteRepository correlacionCambioIncidenteRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private CorrelacionCambioIncidenteRepository correlacionCambioIncidenteRepositoryMock;

    @Autowired
    private CorrelacionCambioIncidenteMapper correlacionCambioIncidenteMapper;

    @Mock
    private CorrelacionCambioIncidenteService correlacionCambioIncidenteServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCorrelacionCambioIncidenteMockMvc;

    private CorrelacionCambioIncidente correlacionCambioIncidente;

    private CorrelacionCambioIncidente insertedCorrelacionCambioIncidente;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CorrelacionCambioIncidente createEntity(EntityManager em) {
        CorrelacionCambioIncidente correlacionCambioIncidente = new CorrelacionCambioIncidente()
            .origen(DEFAULT_ORIGEN)
            .estado(DEFAULT_ESTADO)
            .detectadaEn(DEFAULT_DETECTADA_EN)
            .minutosEntreDespliegueEIncidente(DEFAULT_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE)
            .nota(DEFAULT_NOTA);
        // Add required entity
        Incidente incidente;
        if (TestUtil.findAll(em, Incidente.class).isEmpty()) {
            incidente = IncidenteResourceIT.createEntity();
            em.persist(incidente);
            em.flush();
        } else {
            incidente = TestUtil.findAll(em, Incidente.class).get(0);
        }
        correlacionCambioIncidente.setIncidente(incidente);
        // Add required entity
        DespliegueDeServicio despliegueDeServicio;
        if (TestUtil.findAll(em, DespliegueDeServicio.class).isEmpty()) {
            despliegueDeServicio = DespliegueDeServicioResourceIT.createEntity(em);
            em.persist(despliegueDeServicio);
            em.flush();
        } else {
            despliegueDeServicio = TestUtil.findAll(em, DespliegueDeServicio.class).get(0);
        }
        correlacionCambioIncidente.setDespliegue(despliegueDeServicio);
        return correlacionCambioIncidente;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CorrelacionCambioIncidente createUpdatedEntity(EntityManager em) {
        CorrelacionCambioIncidente updatedCorrelacionCambioIncidente = new CorrelacionCambioIncidente()
            .origen(UPDATED_ORIGEN)
            .estado(UPDATED_ESTADO)
            .detectadaEn(UPDATED_DETECTADA_EN)
            .minutosEntreDespliegueEIncidente(UPDATED_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE)
            .nota(UPDATED_NOTA);
        // Add required entity
        Incidente incidente;
        if (TestUtil.findAll(em, Incidente.class).isEmpty()) {
            incidente = IncidenteResourceIT.createUpdatedEntity();
            em.persist(incidente);
            em.flush();
        } else {
            incidente = TestUtil.findAll(em, Incidente.class).get(0);
        }
        updatedCorrelacionCambioIncidente.setIncidente(incidente);
        // Add required entity
        DespliegueDeServicio despliegueDeServicio;
        if (TestUtil.findAll(em, DespliegueDeServicio.class).isEmpty()) {
            despliegueDeServicio = DespliegueDeServicioResourceIT.createUpdatedEntity(em);
            em.persist(despliegueDeServicio);
            em.flush();
        } else {
            despliegueDeServicio = TestUtil.findAll(em, DespliegueDeServicio.class).get(0);
        }
        updatedCorrelacionCambioIncidente.setDespliegue(despliegueDeServicio);
        return updatedCorrelacionCambioIncidente;
    }

    @BeforeEach
    void initTest() {
        correlacionCambioIncidente = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCorrelacionCambioIncidente != null) {
            correlacionCambioIncidenteRepository.delete(insertedCorrelacionCambioIncidente);
            insertedCorrelacionCambioIncidente = null;
        }
    }

    @Test
    @Transactional
    void createCorrelacionCambioIncidente() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CorrelacionCambioIncidente
        CorrelacionCambioIncidenteDTO correlacionCambioIncidenteDTO = correlacionCambioIncidenteMapper.toDto(correlacionCambioIncidente);
        var returnedCorrelacionCambioIncidenteDTO = om.readValue(
            restCorrelacionCambioIncidenteMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(correlacionCambioIncidenteDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CorrelacionCambioIncidenteDTO.class
        );

        // Validate the CorrelacionCambioIncidente in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCorrelacionCambioIncidente = correlacionCambioIncidenteMapper.toEntity(returnedCorrelacionCambioIncidenteDTO);
        assertCorrelacionCambioIncidenteUpdatableFieldsEquals(
            returnedCorrelacionCambioIncidente,
            getPersistedCorrelacionCambioIncidente(returnedCorrelacionCambioIncidente)
        );

        insertedCorrelacionCambioIncidente = returnedCorrelacionCambioIncidente;
    }

    @Test
    @Transactional
    void createCorrelacionCambioIncidenteWithExistingId() throws Exception {
        // Create the CorrelacionCambioIncidente with an existing ID
        correlacionCambioIncidente.setId(1L);
        CorrelacionCambioIncidenteDTO correlacionCambioIncidenteDTO = correlacionCambioIncidenteMapper.toDto(correlacionCambioIncidente);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCorrelacionCambioIncidenteMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(correlacionCambioIncidenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CorrelacionCambioIncidente in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkOrigenIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        correlacionCambioIncidente.setOrigen(null);

        // Create the CorrelacionCambioIncidente, which fails.
        CorrelacionCambioIncidenteDTO correlacionCambioIncidenteDTO = correlacionCambioIncidenteMapper.toDto(correlacionCambioIncidente);

        restCorrelacionCambioIncidenteMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(correlacionCambioIncidenteDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEstadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        correlacionCambioIncidente.setEstado(null);

        // Create the CorrelacionCambioIncidente, which fails.
        CorrelacionCambioIncidenteDTO correlacionCambioIncidenteDTO = correlacionCambioIncidenteMapper.toDto(correlacionCambioIncidente);

        restCorrelacionCambioIncidenteMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(correlacionCambioIncidenteDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDetectadaEnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        correlacionCambioIncidente.setDetectadaEn(null);

        // Create the CorrelacionCambioIncidente, which fails.
        CorrelacionCambioIncidenteDTO correlacionCambioIncidenteDTO = correlacionCambioIncidenteMapper.toDto(correlacionCambioIncidente);

        restCorrelacionCambioIncidenteMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(correlacionCambioIncidenteDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentes() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList
        restCorrelacionCambioIncidenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(correlacionCambioIncidente.getId().intValue())))
            .andExpect(jsonPath("$.[*].origen").value(hasItem(DEFAULT_ORIGEN.toString())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].detectadaEn").value(hasItem(DEFAULT_DETECTADA_EN.toString())))
            .andExpect(jsonPath("$.[*].minutosEntreDespliegueEIncidente").value(hasItem(DEFAULT_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE)))
            .andExpect(jsonPath("$.[*].nota").value(hasItem(DEFAULT_NOTA)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCorrelacionCambioIncidentesWithEagerRelationshipsIsEnabled() throws Exception {
        when(correlacionCambioIncidenteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCorrelacionCambioIncidenteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(correlacionCambioIncidenteServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCorrelacionCambioIncidentesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(correlacionCambioIncidenteServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCorrelacionCambioIncidenteMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(correlacionCambioIncidenteRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCorrelacionCambioIncidente() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get the correlacionCambioIncidente
        restCorrelacionCambioIncidenteMockMvc
            .perform(get(ENTITY_API_URL_ID, correlacionCambioIncidente.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(correlacionCambioIncidente.getId().intValue()))
            .andExpect(jsonPath("$.origen").value(DEFAULT_ORIGEN.toString()))
            .andExpect(jsonPath("$.estado").value(DEFAULT_ESTADO.toString()))
            .andExpect(jsonPath("$.detectadaEn").value(DEFAULT_DETECTADA_EN.toString()))
            .andExpect(jsonPath("$.minutosEntreDespliegueEIncidente").value(DEFAULT_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE))
            .andExpect(jsonPath("$.nota").value(DEFAULT_NOTA));
    }

    @Test
    @Transactional
    void getCorrelacionCambioIncidentesByIdFiltering() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        Long id = correlacionCambioIncidente.getId();

        defaultCorrelacionCambioIncidenteFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCorrelacionCambioIncidenteFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCorrelacionCambioIncidenteFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByOrigenIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where origen equals to
        defaultCorrelacionCambioIncidenteFiltering("origen.equals=" + DEFAULT_ORIGEN, "origen.equals=" + UPDATED_ORIGEN);
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByOrigenIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where origen in
        defaultCorrelacionCambioIncidenteFiltering("origen.in=" + DEFAULT_ORIGEN + "," + UPDATED_ORIGEN, "origen.in=" + UPDATED_ORIGEN);
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByOrigenIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where origen is not null
        defaultCorrelacionCambioIncidenteFiltering("origen.specified=true", "origen.specified=false");
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByEstadoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where estado equals to
        defaultCorrelacionCambioIncidenteFiltering("estado.equals=" + DEFAULT_ESTADO, "estado.equals=" + UPDATED_ESTADO);
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByEstadoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where estado in
        defaultCorrelacionCambioIncidenteFiltering("estado.in=" + DEFAULT_ESTADO + "," + UPDATED_ESTADO, "estado.in=" + UPDATED_ESTADO);
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByEstadoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where estado is not null
        defaultCorrelacionCambioIncidenteFiltering("estado.specified=true", "estado.specified=false");
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByDetectadaEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where detectadaEn equals to
        defaultCorrelacionCambioIncidenteFiltering(
            "detectadaEn.equals=" + DEFAULT_DETECTADA_EN,
            "detectadaEn.equals=" + UPDATED_DETECTADA_EN
        );
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByDetectadaEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where detectadaEn in
        defaultCorrelacionCambioIncidenteFiltering(
            "detectadaEn.in=" + DEFAULT_DETECTADA_EN + "," + UPDATED_DETECTADA_EN,
            "detectadaEn.in=" + UPDATED_DETECTADA_EN
        );
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByDetectadaEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where detectadaEn is not null
        defaultCorrelacionCambioIncidenteFiltering("detectadaEn.specified=true", "detectadaEn.specified=false");
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByMinutosEntreDespliegueEIncidenteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where minutosEntreDespliegueEIncidente equals to
        defaultCorrelacionCambioIncidenteFiltering(
            "minutosEntreDespliegueEIncidente.equals=" + DEFAULT_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE,
            "minutosEntreDespliegueEIncidente.equals=" + UPDATED_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE
        );
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByMinutosEntreDespliegueEIncidenteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where minutosEntreDespliegueEIncidente in
        defaultCorrelacionCambioIncidenteFiltering(
            "minutosEntreDespliegueEIncidente.in=" +
                DEFAULT_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE +
                "," +
                UPDATED_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE,
            "minutosEntreDespliegueEIncidente.in=" + UPDATED_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE
        );
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByMinutosEntreDespliegueEIncidenteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where minutosEntreDespliegueEIncidente is not null
        defaultCorrelacionCambioIncidenteFiltering(
            "minutosEntreDespliegueEIncidente.specified=true",
            "minutosEntreDespliegueEIncidente.specified=false"
        );
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByMinutosEntreDespliegueEIncidenteIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where minutosEntreDespliegueEIncidente is greater than or equal to
        defaultCorrelacionCambioIncidenteFiltering(
            "minutosEntreDespliegueEIncidente.greaterThanOrEqual=" + DEFAULT_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE,
            "minutosEntreDespliegueEIncidente.greaterThanOrEqual=" + UPDATED_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE
        );
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByMinutosEntreDespliegueEIncidenteIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where minutosEntreDespliegueEIncidente is less than or equal to
        defaultCorrelacionCambioIncidenteFiltering(
            "minutosEntreDespliegueEIncidente.lessThanOrEqual=" + DEFAULT_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE,
            "minutosEntreDespliegueEIncidente.lessThanOrEqual=" + SMALLER_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE
        );
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByMinutosEntreDespliegueEIncidenteIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where minutosEntreDespliegueEIncidente is less than
        defaultCorrelacionCambioIncidenteFiltering(
            "minutosEntreDespliegueEIncidente.lessThan=" + UPDATED_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE,
            "minutosEntreDespliegueEIncidente.lessThan=" + DEFAULT_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE
        );
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByMinutosEntreDespliegueEIncidenteIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where minutosEntreDespliegueEIncidente is greater than
        defaultCorrelacionCambioIncidenteFiltering(
            "minutosEntreDespliegueEIncidente.greaterThan=" + SMALLER_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE,
            "minutosEntreDespliegueEIncidente.greaterThan=" + DEFAULT_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE
        );
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByNotaIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where nota equals to
        defaultCorrelacionCambioIncidenteFiltering("nota.equals=" + DEFAULT_NOTA, "nota.equals=" + UPDATED_NOTA);
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByNotaIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where nota in
        defaultCorrelacionCambioIncidenteFiltering("nota.in=" + DEFAULT_NOTA + "," + UPDATED_NOTA, "nota.in=" + UPDATED_NOTA);
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByNotaIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where nota is not null
        defaultCorrelacionCambioIncidenteFiltering("nota.specified=true", "nota.specified=false");
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByNotaContainsSomething() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where nota contains
        defaultCorrelacionCambioIncidenteFiltering("nota.contains=" + DEFAULT_NOTA, "nota.contains=" + UPDATED_NOTA);
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByNotaNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        // Get all the correlacionCambioIncidenteList where nota does not contain
        defaultCorrelacionCambioIncidenteFiltering("nota.doesNotContain=" + UPDATED_NOTA, "nota.doesNotContain=" + DEFAULT_NOTA);
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByIncidenteIsEqualToSomething() throws Exception {
        Incidente incidente;
        if (TestUtil.findAll(em, Incidente.class).isEmpty()) {
            correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);
            incidente = IncidenteResourceIT.createEntity();
        } else {
            incidente = TestUtil.findAll(em, Incidente.class).get(0);
        }
        em.persist(incidente);
        em.flush();
        correlacionCambioIncidente.setIncidente(incidente);
        correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);
        Long incidenteId = incidente.getId();
        // Get all the correlacionCambioIncidenteList where incidente equals to incidenteId
        defaultCorrelacionCambioIncidenteShouldBeFound("incidenteId.equals=" + incidenteId);

        // Get all the correlacionCambioIncidenteList where incidente equals to (incidenteId + 1)
        defaultCorrelacionCambioIncidenteShouldNotBeFound("incidenteId.equals=" + (incidenteId + 1));
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByDespliegueIsEqualToSomething() throws Exception {
        DespliegueDeServicio despliegue;
        if (TestUtil.findAll(em, DespliegueDeServicio.class).isEmpty()) {
            correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);
            despliegue = DespliegueDeServicioResourceIT.createEntity(em);
        } else {
            despliegue = TestUtil.findAll(em, DespliegueDeServicio.class).get(0);
        }
        em.persist(despliegue);
        em.flush();
        correlacionCambioIncidente.setDespliegue(despliegue);
        correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);
        Long despliegueId = despliegue.getId();
        // Get all the correlacionCambioIncidenteList where despliegue equals to despliegueId
        defaultCorrelacionCambioIncidenteShouldBeFound("despliegueId.equals=" + despliegueId);

        // Get all the correlacionCambioIncidenteList where despliegue equals to (despliegueId + 1)
        defaultCorrelacionCambioIncidenteShouldNotBeFound("despliegueId.equals=" + (despliegueId + 1));
    }

    @Test
    @Transactional
    void getAllCorrelacionCambioIncidentesByConfirmadaPorIsEqualToSomething() throws Exception {
        User confirmadaPor;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);
            confirmadaPor = UserResourceIT.createEntity();
        } else {
            confirmadaPor = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(confirmadaPor);
        em.flush();
        correlacionCambioIncidente.setConfirmadaPor(confirmadaPor);
        correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);
        Long confirmadaPorId = confirmadaPor.getId();
        // Get all the correlacionCambioIncidenteList where confirmadaPor equals to confirmadaPorId
        defaultCorrelacionCambioIncidenteShouldBeFound("confirmadaPorId.equals=" + confirmadaPorId);

        // Get all the correlacionCambioIncidenteList where confirmadaPor equals to (confirmadaPorId + 1)
        defaultCorrelacionCambioIncidenteShouldNotBeFound("confirmadaPorId.equals=" + (confirmadaPorId + 1));
    }

    private void defaultCorrelacionCambioIncidenteFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCorrelacionCambioIncidenteShouldBeFound(shouldBeFound);
        defaultCorrelacionCambioIncidenteShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCorrelacionCambioIncidenteShouldBeFound(String filter) throws Exception {
        restCorrelacionCambioIncidenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(correlacionCambioIncidente.getId().intValue())))
            .andExpect(jsonPath("$.[*].origen").value(hasItem(DEFAULT_ORIGEN.toString())))
            .andExpect(jsonPath("$.[*].estado").value(hasItem(DEFAULT_ESTADO.toString())))
            .andExpect(jsonPath("$.[*].detectadaEn").value(hasItem(DEFAULT_DETECTADA_EN.toString())))
            .andExpect(jsonPath("$.[*].minutosEntreDespliegueEIncidente").value(hasItem(DEFAULT_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE)))
            .andExpect(jsonPath("$.[*].nota").value(hasItem(DEFAULT_NOTA)));

        // Check, that the count call also returns 1
        restCorrelacionCambioIncidenteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCorrelacionCambioIncidenteShouldNotBeFound(String filter) throws Exception {
        restCorrelacionCambioIncidenteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCorrelacionCambioIncidenteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCorrelacionCambioIncidente() throws Exception {
        // Get the correlacionCambioIncidente
        restCorrelacionCambioIncidenteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCorrelacionCambioIncidente() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the correlacionCambioIncidente
        CorrelacionCambioIncidente updatedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository
            .findById(correlacionCambioIncidente.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedCorrelacionCambioIncidente are not directly saved in db
        em.detach(updatedCorrelacionCambioIncidente);
        updatedCorrelacionCambioIncidente
            .origen(UPDATED_ORIGEN)
            .estado(UPDATED_ESTADO)
            .detectadaEn(UPDATED_DETECTADA_EN)
            .minutosEntreDespliegueEIncidente(UPDATED_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE)
            .nota(UPDATED_NOTA);
        CorrelacionCambioIncidenteDTO correlacionCambioIncidenteDTO = correlacionCambioIncidenteMapper.toDto(
            updatedCorrelacionCambioIncidente
        );

        restCorrelacionCambioIncidenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, correlacionCambioIncidenteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(correlacionCambioIncidenteDTO))
            )
            .andExpect(status().isOk());

        // Validate the CorrelacionCambioIncidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCorrelacionCambioIncidenteToMatchAllProperties(updatedCorrelacionCambioIncidente);
    }

    @Test
    @Transactional
    void putNonExistingCorrelacionCambioIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        correlacionCambioIncidente.setId(longCount.incrementAndGet());

        // Create the CorrelacionCambioIncidente
        CorrelacionCambioIncidenteDTO correlacionCambioIncidenteDTO = correlacionCambioIncidenteMapper.toDto(correlacionCambioIncidente);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCorrelacionCambioIncidenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, correlacionCambioIncidenteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(correlacionCambioIncidenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CorrelacionCambioIncidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCorrelacionCambioIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        correlacionCambioIncidente.setId(longCount.incrementAndGet());

        // Create the CorrelacionCambioIncidente
        CorrelacionCambioIncidenteDTO correlacionCambioIncidenteDTO = correlacionCambioIncidenteMapper.toDto(correlacionCambioIncidente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCorrelacionCambioIncidenteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(correlacionCambioIncidenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CorrelacionCambioIncidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCorrelacionCambioIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        correlacionCambioIncidente.setId(longCount.incrementAndGet());

        // Create the CorrelacionCambioIncidente
        CorrelacionCambioIncidenteDTO correlacionCambioIncidenteDTO = correlacionCambioIncidenteMapper.toDto(correlacionCambioIncidente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCorrelacionCambioIncidenteMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(correlacionCambioIncidenteDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CorrelacionCambioIncidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCorrelacionCambioIncidenteWithPatch() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the correlacionCambioIncidente using partial update
        CorrelacionCambioIncidente partialUpdatedCorrelacionCambioIncidente = new CorrelacionCambioIncidente();
        partialUpdatedCorrelacionCambioIncidente.setId(correlacionCambioIncidente.getId());

        partialUpdatedCorrelacionCambioIncidente.detectadaEn(UPDATED_DETECTADA_EN).nota(UPDATED_NOTA);

        restCorrelacionCambioIncidenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCorrelacionCambioIncidente.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCorrelacionCambioIncidente))
            )
            .andExpect(status().isOk());

        // Validate the CorrelacionCambioIncidente in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCorrelacionCambioIncidenteUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCorrelacionCambioIncidente, correlacionCambioIncidente),
            getPersistedCorrelacionCambioIncidente(correlacionCambioIncidente)
        );
    }

    @Test
    @Transactional
    void fullUpdateCorrelacionCambioIncidenteWithPatch() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the correlacionCambioIncidente using partial update
        CorrelacionCambioIncidente partialUpdatedCorrelacionCambioIncidente = new CorrelacionCambioIncidente();
        partialUpdatedCorrelacionCambioIncidente.setId(correlacionCambioIncidente.getId());

        partialUpdatedCorrelacionCambioIncidente
            .origen(UPDATED_ORIGEN)
            .estado(UPDATED_ESTADO)
            .detectadaEn(UPDATED_DETECTADA_EN)
            .minutosEntreDespliegueEIncidente(UPDATED_MINUTOS_ENTRE_DESPLIEGUE_E_INCIDENTE)
            .nota(UPDATED_NOTA);

        restCorrelacionCambioIncidenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCorrelacionCambioIncidente.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCorrelacionCambioIncidente))
            )
            .andExpect(status().isOk());

        // Validate the CorrelacionCambioIncidente in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCorrelacionCambioIncidenteUpdatableFieldsEquals(
            partialUpdatedCorrelacionCambioIncidente,
            getPersistedCorrelacionCambioIncidente(partialUpdatedCorrelacionCambioIncidente)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCorrelacionCambioIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        correlacionCambioIncidente.setId(longCount.incrementAndGet());

        // Create the CorrelacionCambioIncidente
        CorrelacionCambioIncidenteDTO correlacionCambioIncidenteDTO = correlacionCambioIncidenteMapper.toDto(correlacionCambioIncidente);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCorrelacionCambioIncidenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, correlacionCambioIncidenteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(correlacionCambioIncidenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CorrelacionCambioIncidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCorrelacionCambioIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        correlacionCambioIncidente.setId(longCount.incrementAndGet());

        // Create the CorrelacionCambioIncidente
        CorrelacionCambioIncidenteDTO correlacionCambioIncidenteDTO = correlacionCambioIncidenteMapper.toDto(correlacionCambioIncidente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCorrelacionCambioIncidenteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(correlacionCambioIncidenteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CorrelacionCambioIncidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCorrelacionCambioIncidente() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        correlacionCambioIncidente.setId(longCount.incrementAndGet());

        // Create the CorrelacionCambioIncidente
        CorrelacionCambioIncidenteDTO correlacionCambioIncidenteDTO = correlacionCambioIncidenteMapper.toDto(correlacionCambioIncidente);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCorrelacionCambioIncidenteMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(correlacionCambioIncidenteDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CorrelacionCambioIncidente in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCorrelacionCambioIncidente() throws Exception {
        // Initialize the database
        insertedCorrelacionCambioIncidente = correlacionCambioIncidenteRepository.saveAndFlush(correlacionCambioIncidente);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the correlacionCambioIncidente
        restCorrelacionCambioIncidenteMockMvc
            .perform(delete(ENTITY_API_URL_ID, correlacionCambioIncidente.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return correlacionCambioIncidenteRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected CorrelacionCambioIncidente getPersistedCorrelacionCambioIncidente(CorrelacionCambioIncidente correlacionCambioIncidente) {
        return correlacionCambioIncidenteRepository.findById(correlacionCambioIncidente.getId()).orElseThrow();
    }

    protected void assertPersistedCorrelacionCambioIncidenteToMatchAllProperties(
        CorrelacionCambioIncidente expectedCorrelacionCambioIncidente
    ) {
        assertCorrelacionCambioIncidenteAllPropertiesEquals(
            expectedCorrelacionCambioIncidente,
            getPersistedCorrelacionCambioIncidente(expectedCorrelacionCambioIncidente)
        );
    }

    protected void assertPersistedCorrelacionCambioIncidenteToMatchUpdatableProperties(
        CorrelacionCambioIncidente expectedCorrelacionCambioIncidente
    ) {
        assertCorrelacionCambioIncidenteAllUpdatablePropertiesEquals(
            expectedCorrelacionCambioIncidente,
            getPersistedCorrelacionCambioIncidente(expectedCorrelacionCambioIncidente)
        );
    }
}
