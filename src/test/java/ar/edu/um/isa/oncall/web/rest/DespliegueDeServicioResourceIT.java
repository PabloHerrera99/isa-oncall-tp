package ar.edu.um.isa.oncall.web.rest;

import static ar.edu.um.isa.oncall.domain.DespliegueDeServicioAsserts.*;
import static ar.edu.um.isa.oncall.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.Cambio;
import ar.edu.um.isa.oncall.domain.DespliegueDeServicio;
import ar.edu.um.isa.oncall.domain.Servicio;
import ar.edu.um.isa.oncall.domain.User;
import ar.edu.um.isa.oncall.domain.enumeration.Entorno;
import ar.edu.um.isa.oncall.domain.enumeration.ResultadoDespliegue;
import ar.edu.um.isa.oncall.repository.DespliegueDeServicioRepository;
import ar.edu.um.isa.oncall.repository.UserRepository;
import ar.edu.um.isa.oncall.service.DespliegueDeServicioService;
import ar.edu.um.isa.oncall.service.dto.DespliegueDeServicioDTO;
import ar.edu.um.isa.oncall.service.mapper.DespliegueDeServicioMapper;
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
 * Integration tests for the {@link DespliegueDeServicioResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DespliegueDeServicioResourceIT {

    private static final String DEFAULT_VERSION = "AAAAAAAAAA";
    private static final String UPDATED_VERSION = "BBBBBBBBBB";

    private static final Entorno DEFAULT_ENTORNO = Entorno.PRODUCCION;
    private static final Entorno UPDATED_ENTORNO = Entorno.STAGING;

    private static final Instant DEFAULT_INICIADO_EN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_INICIADO_EN = Instant.ofEpochMilli(1701729143509L);

    private static final Instant DEFAULT_FINALIZADO_EN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FINALIZADO_EN = Instant.ofEpochMilli(1701729143509L);

    private static final ResultadoDespliegue DEFAULT_RESULTADO = ResultadoDespliegue.EN_CURSO;
    private static final ResultadoDespliegue UPDATED_RESULTADO = ResultadoDespliegue.EXITOSO;

    private static final String ENTITY_API_URL = "/api/despliegue-de-servicios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DespliegueDeServicioRepository despliegueDeServicioRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private DespliegueDeServicioRepository despliegueDeServicioRepositoryMock;

    @Autowired
    private DespliegueDeServicioMapper despliegueDeServicioMapper;

    @Mock
    private DespliegueDeServicioService despliegueDeServicioServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDespliegueDeServicioMockMvc;

    private DespliegueDeServicio despliegueDeServicio;

    private DespliegueDeServicio insertedDespliegueDeServicio;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DespliegueDeServicio createEntity(EntityManager em) {
        DespliegueDeServicio despliegueDeServicio = new DespliegueDeServicio()
            .version(DEFAULT_VERSION)
            .entorno(DEFAULT_ENTORNO)
            .iniciadoEn(DEFAULT_INICIADO_EN)
            .finalizadoEn(DEFAULT_FINALIZADO_EN)
            .resultado(DEFAULT_RESULTADO);
        // Add required entity
        Cambio cambio;
        if (TestUtil.findAll(em, Cambio.class).isEmpty()) {
            cambio = CambioResourceIT.createEntity(em);
            em.persist(cambio);
            em.flush();
        } else {
            cambio = TestUtil.findAll(em, Cambio.class).get(0);
        }
        despliegueDeServicio.setCambio(cambio);
        // Add required entity
        Servicio servicio;
        if (TestUtil.findAll(em, Servicio.class).isEmpty()) {
            servicio = ServicioResourceIT.createEntity(em);
            em.persist(servicio);
            em.flush();
        } else {
            servicio = TestUtil.findAll(em, Servicio.class).get(0);
        }
        despliegueDeServicio.setServicio(servicio);
        return despliegueDeServicio;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DespliegueDeServicio createUpdatedEntity(EntityManager em) {
        DespliegueDeServicio updatedDespliegueDeServicio = new DespliegueDeServicio()
            .version(UPDATED_VERSION)
            .entorno(UPDATED_ENTORNO)
            .iniciadoEn(UPDATED_INICIADO_EN)
            .finalizadoEn(UPDATED_FINALIZADO_EN)
            .resultado(UPDATED_RESULTADO);
        // Add required entity
        Cambio cambio;
        if (TestUtil.findAll(em, Cambio.class).isEmpty()) {
            cambio = CambioResourceIT.createUpdatedEntity(em);
            em.persist(cambio);
            em.flush();
        } else {
            cambio = TestUtil.findAll(em, Cambio.class).get(0);
        }
        updatedDespliegueDeServicio.setCambio(cambio);
        // Add required entity
        Servicio servicio;
        if (TestUtil.findAll(em, Servicio.class).isEmpty()) {
            servicio = ServicioResourceIT.createUpdatedEntity(em);
            em.persist(servicio);
            em.flush();
        } else {
            servicio = TestUtil.findAll(em, Servicio.class).get(0);
        }
        updatedDespliegueDeServicio.setServicio(servicio);
        return updatedDespliegueDeServicio;
    }

    @BeforeEach
    void initTest() {
        despliegueDeServicio = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedDespliegueDeServicio != null) {
            despliegueDeServicioRepository.delete(insertedDespliegueDeServicio);
            insertedDespliegueDeServicio = null;
        }
    }

    @Test
    @Transactional
    void createDespliegueDeServicio() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DespliegueDeServicio
        DespliegueDeServicioDTO despliegueDeServicioDTO = despliegueDeServicioMapper.toDto(despliegueDeServicio);
        var returnedDespliegueDeServicioDTO = om.readValue(
            restDespliegueDeServicioMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(despliegueDeServicioDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DespliegueDeServicioDTO.class
        );

        // Validate the DespliegueDeServicio in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDespliegueDeServicio = despliegueDeServicioMapper.toEntity(returnedDespliegueDeServicioDTO);
        assertDespliegueDeServicioUpdatableFieldsEquals(
            returnedDespliegueDeServicio,
            getPersistedDespliegueDeServicio(returnedDespliegueDeServicio)
        );

        insertedDespliegueDeServicio = returnedDespliegueDeServicio;
    }

    @Test
    @Transactional
    void createDespliegueDeServicioWithExistingId() throws Exception {
        // Create the DespliegueDeServicio with an existing ID
        despliegueDeServicio.setId(1L);
        DespliegueDeServicioDTO despliegueDeServicioDTO = despliegueDeServicioMapper.toDto(despliegueDeServicio);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDespliegueDeServicioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(despliegueDeServicioDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DespliegueDeServicio in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        despliegueDeServicio.setVersion(null);

        // Create the DespliegueDeServicio, which fails.
        DespliegueDeServicioDTO despliegueDeServicioDTO = despliegueDeServicioMapper.toDto(despliegueDeServicio);

        restDespliegueDeServicioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(despliegueDeServicioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEntornoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        despliegueDeServicio.setEntorno(null);

        // Create the DespliegueDeServicio, which fails.
        DespliegueDeServicioDTO despliegueDeServicioDTO = despliegueDeServicioMapper.toDto(despliegueDeServicio);

        restDespliegueDeServicioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(despliegueDeServicioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIniciadoEnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        despliegueDeServicio.setIniciadoEn(null);

        // Create the DespliegueDeServicio, which fails.
        DespliegueDeServicioDTO despliegueDeServicioDTO = despliegueDeServicioMapper.toDto(despliegueDeServicio);

        restDespliegueDeServicioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(despliegueDeServicioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkResultadoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        despliegueDeServicio.setResultado(null);

        // Create the DespliegueDeServicio, which fails.
        DespliegueDeServicioDTO despliegueDeServicioDTO = despliegueDeServicioMapper.toDto(despliegueDeServicio);

        restDespliegueDeServicioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(despliegueDeServicioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDespliegueDeServicios() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList
        restDespliegueDeServicioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(despliegueDeServicio.getId().intValue())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].entorno").value(hasItem(DEFAULT_ENTORNO.toString())))
            .andExpect(jsonPath("$.[*].iniciadoEn").value(hasItem(DEFAULT_INICIADO_EN.toString())))
            .andExpect(jsonPath("$.[*].finalizadoEn").value(hasItem(DEFAULT_FINALIZADO_EN.toString())))
            .andExpect(jsonPath("$.[*].resultado").value(hasItem(DEFAULT_RESULTADO.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDespliegueDeServiciosWithEagerRelationshipsIsEnabled() throws Exception {
        when(despliegueDeServicioServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDespliegueDeServicioMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(despliegueDeServicioServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDespliegueDeServiciosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(despliegueDeServicioServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDespliegueDeServicioMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(despliegueDeServicioRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDespliegueDeServicio() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get the despliegueDeServicio
        restDespliegueDeServicioMockMvc
            .perform(get(ENTITY_API_URL_ID, despliegueDeServicio.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(despliegueDeServicio.getId().intValue()))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.entorno").value(DEFAULT_ENTORNO.toString()))
            .andExpect(jsonPath("$.iniciadoEn").value(DEFAULT_INICIADO_EN.toString()))
            .andExpect(jsonPath("$.finalizadoEn").value(DEFAULT_FINALIZADO_EN.toString()))
            .andExpect(jsonPath("$.resultado").value(DEFAULT_RESULTADO.toString()));
    }

    @Test
    @Transactional
    void getDespliegueDeServiciosByIdFiltering() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        Long id = despliegueDeServicio.getId();

        defaultDespliegueDeServicioFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDespliegueDeServicioFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDespliegueDeServicioFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList where version equals to
        defaultDespliegueDeServicioFiltering("version.equals=" + DEFAULT_VERSION, "version.equals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByVersionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList where version in
        defaultDespliegueDeServicioFiltering("version.in=" + DEFAULT_VERSION + "," + UPDATED_VERSION, "version.in=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList where version is not null
        defaultDespliegueDeServicioFiltering("version.specified=true", "version.specified=false");
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByVersionContainsSomething() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList where version contains
        defaultDespliegueDeServicioFiltering("version.contains=" + DEFAULT_VERSION, "version.contains=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByVersionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList where version does not contain
        defaultDespliegueDeServicioFiltering("version.doesNotContain=" + UPDATED_VERSION, "version.doesNotContain=" + DEFAULT_VERSION);
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByEntornoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList where entorno equals to
        defaultDespliegueDeServicioFiltering("entorno.equals=" + DEFAULT_ENTORNO, "entorno.equals=" + UPDATED_ENTORNO);
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByEntornoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList where entorno in
        defaultDespliegueDeServicioFiltering("entorno.in=" + DEFAULT_ENTORNO + "," + UPDATED_ENTORNO, "entorno.in=" + UPDATED_ENTORNO);
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByEntornoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList where entorno is not null
        defaultDespliegueDeServicioFiltering("entorno.specified=true", "entorno.specified=false");
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByIniciadoEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList where iniciadoEn equals to
        defaultDespliegueDeServicioFiltering("iniciadoEn.equals=" + DEFAULT_INICIADO_EN, "iniciadoEn.equals=" + UPDATED_INICIADO_EN);
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByIniciadoEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList where iniciadoEn in
        defaultDespliegueDeServicioFiltering(
            "iniciadoEn.in=" + DEFAULT_INICIADO_EN + "," + UPDATED_INICIADO_EN,
            "iniciadoEn.in=" + UPDATED_INICIADO_EN
        );
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByIniciadoEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList where iniciadoEn is not null
        defaultDespliegueDeServicioFiltering("iniciadoEn.specified=true", "iniciadoEn.specified=false");
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByFinalizadoEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList where finalizadoEn equals to
        defaultDespliegueDeServicioFiltering(
            "finalizadoEn.equals=" + DEFAULT_FINALIZADO_EN,
            "finalizadoEn.equals=" + UPDATED_FINALIZADO_EN
        );
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByFinalizadoEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList where finalizadoEn in
        defaultDespliegueDeServicioFiltering(
            "finalizadoEn.in=" + DEFAULT_FINALIZADO_EN + "," + UPDATED_FINALIZADO_EN,
            "finalizadoEn.in=" + UPDATED_FINALIZADO_EN
        );
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByFinalizadoEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList where finalizadoEn is not null
        defaultDespliegueDeServicioFiltering("finalizadoEn.specified=true", "finalizadoEn.specified=false");
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByResultadoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList where resultado equals to
        defaultDespliegueDeServicioFiltering("resultado.equals=" + DEFAULT_RESULTADO, "resultado.equals=" + UPDATED_RESULTADO);
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByResultadoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList where resultado in
        defaultDespliegueDeServicioFiltering(
            "resultado.in=" + DEFAULT_RESULTADO + "," + UPDATED_RESULTADO,
            "resultado.in=" + UPDATED_RESULTADO
        );
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByResultadoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        // Get all the despliegueDeServicioList where resultado is not null
        defaultDespliegueDeServicioFiltering("resultado.specified=true", "resultado.specified=false");
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByCambioIsEqualToSomething() throws Exception {
        Cambio cambio;
        if (TestUtil.findAll(em, Cambio.class).isEmpty()) {
            despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);
            cambio = CambioResourceIT.createEntity(em);
        } else {
            cambio = TestUtil.findAll(em, Cambio.class).get(0);
        }
        em.persist(cambio);
        em.flush();
        despliegueDeServicio.setCambio(cambio);
        despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);
        Long cambioId = cambio.getId();
        // Get all the despliegueDeServicioList where cambio equals to cambioId
        defaultDespliegueDeServicioShouldBeFound("cambioId.equals=" + cambioId);

        // Get all the despliegueDeServicioList where cambio equals to (cambioId + 1)
        defaultDespliegueDeServicioShouldNotBeFound("cambioId.equals=" + (cambioId + 1));
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByServicioIsEqualToSomething() throws Exception {
        Servicio servicio;
        if (TestUtil.findAll(em, Servicio.class).isEmpty()) {
            despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);
            servicio = ServicioResourceIT.createEntity(em);
        } else {
            servicio = TestUtil.findAll(em, Servicio.class).get(0);
        }
        em.persist(servicio);
        em.flush();
        despliegueDeServicio.setServicio(servicio);
        despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);
        Long servicioId = servicio.getId();
        // Get all the despliegueDeServicioList where servicio equals to servicioId
        defaultDespliegueDeServicioShouldBeFound("servicioId.equals=" + servicioId);

        // Get all the despliegueDeServicioList where servicio equals to (servicioId + 1)
        defaultDespliegueDeServicioShouldNotBeFound("servicioId.equals=" + (servicioId + 1));
    }

    @Test
    @Transactional
    void getAllDespliegueDeServiciosByEjecutadoPorIsEqualToSomething() throws Exception {
        User ejecutadoPor;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);
            ejecutadoPor = UserResourceIT.createEntity();
        } else {
            ejecutadoPor = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(ejecutadoPor);
        em.flush();
        despliegueDeServicio.setEjecutadoPor(ejecutadoPor);
        despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);
        Long ejecutadoPorId = ejecutadoPor.getId();
        // Get all the despliegueDeServicioList where ejecutadoPor equals to ejecutadoPorId
        defaultDespliegueDeServicioShouldBeFound("ejecutadoPorId.equals=" + ejecutadoPorId);

        // Get all the despliegueDeServicioList where ejecutadoPor equals to (ejecutadoPorId + 1)
        defaultDespliegueDeServicioShouldNotBeFound("ejecutadoPorId.equals=" + (ejecutadoPorId + 1));
    }

    private void defaultDespliegueDeServicioFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDespliegueDeServicioShouldBeFound(shouldBeFound);
        defaultDespliegueDeServicioShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDespliegueDeServicioShouldBeFound(String filter) throws Exception {
        restDespliegueDeServicioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(despliegueDeServicio.getId().intValue())))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].entorno").value(hasItem(DEFAULT_ENTORNO.toString())))
            .andExpect(jsonPath("$.[*].iniciadoEn").value(hasItem(DEFAULT_INICIADO_EN.toString())))
            .andExpect(jsonPath("$.[*].finalizadoEn").value(hasItem(DEFAULT_FINALIZADO_EN.toString())))
            .andExpect(jsonPath("$.[*].resultado").value(hasItem(DEFAULT_RESULTADO.toString())));

        // Check, that the count call also returns 1
        restDespliegueDeServicioMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDespliegueDeServicioShouldNotBeFound(String filter) throws Exception {
        restDespliegueDeServicioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDespliegueDeServicioMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDespliegueDeServicio() throws Exception {
        // Get the despliegueDeServicio
        restDespliegueDeServicioMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDespliegueDeServicio() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the despliegueDeServicio
        DespliegueDeServicio updatedDespliegueDeServicio = despliegueDeServicioRepository
            .findById(despliegueDeServicio.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedDespliegueDeServicio are not directly saved in db
        em.detach(updatedDespliegueDeServicio);
        updatedDespliegueDeServicio
            .version(UPDATED_VERSION)
            .entorno(UPDATED_ENTORNO)
            .iniciadoEn(UPDATED_INICIADO_EN)
            .finalizadoEn(UPDATED_FINALIZADO_EN)
            .resultado(UPDATED_RESULTADO);
        DespliegueDeServicioDTO despliegueDeServicioDTO = despliegueDeServicioMapper.toDto(updatedDespliegueDeServicio);

        restDespliegueDeServicioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, despliegueDeServicioDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(despliegueDeServicioDTO))
            )
            .andExpect(status().isOk());

        // Validate the DespliegueDeServicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDespliegueDeServicioToMatchAllProperties(updatedDespliegueDeServicio);
    }

    @Test
    @Transactional
    void putNonExistingDespliegueDeServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        despliegueDeServicio.setId(longCount.incrementAndGet());

        // Create the DespliegueDeServicio
        DespliegueDeServicioDTO despliegueDeServicioDTO = despliegueDeServicioMapper.toDto(despliegueDeServicio);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDespliegueDeServicioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, despliegueDeServicioDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(despliegueDeServicioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DespliegueDeServicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDespliegueDeServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        despliegueDeServicio.setId(longCount.incrementAndGet());

        // Create the DespliegueDeServicio
        DespliegueDeServicioDTO despliegueDeServicioDTO = despliegueDeServicioMapper.toDto(despliegueDeServicio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDespliegueDeServicioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(despliegueDeServicioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DespliegueDeServicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDespliegueDeServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        despliegueDeServicio.setId(longCount.incrementAndGet());

        // Create the DespliegueDeServicio
        DespliegueDeServicioDTO despliegueDeServicioDTO = despliegueDeServicioMapper.toDto(despliegueDeServicio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDespliegueDeServicioMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(despliegueDeServicioDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DespliegueDeServicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDespliegueDeServicioWithPatch() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the despliegueDeServicio using partial update
        DespliegueDeServicio partialUpdatedDespliegueDeServicio = new DespliegueDeServicio();
        partialUpdatedDespliegueDeServicio.setId(despliegueDeServicio.getId());

        partialUpdatedDespliegueDeServicio.iniciadoEn(UPDATED_INICIADO_EN);

        restDespliegueDeServicioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDespliegueDeServicio.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDespliegueDeServicio))
            )
            .andExpect(status().isOk());

        // Validate the DespliegueDeServicio in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDespliegueDeServicioUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDespliegueDeServicio, despliegueDeServicio),
            getPersistedDespliegueDeServicio(despliegueDeServicio)
        );
    }

    @Test
    @Transactional
    void fullUpdateDespliegueDeServicioWithPatch() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the despliegueDeServicio using partial update
        DespliegueDeServicio partialUpdatedDespliegueDeServicio = new DespliegueDeServicio();
        partialUpdatedDespliegueDeServicio.setId(despliegueDeServicio.getId());

        partialUpdatedDespliegueDeServicio
            .version(UPDATED_VERSION)
            .entorno(UPDATED_ENTORNO)
            .iniciadoEn(UPDATED_INICIADO_EN)
            .finalizadoEn(UPDATED_FINALIZADO_EN)
            .resultado(UPDATED_RESULTADO);

        restDespliegueDeServicioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDespliegueDeServicio.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDespliegueDeServicio))
            )
            .andExpect(status().isOk());

        // Validate the DespliegueDeServicio in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDespliegueDeServicioUpdatableFieldsEquals(
            partialUpdatedDespliegueDeServicio,
            getPersistedDespliegueDeServicio(partialUpdatedDespliegueDeServicio)
        );
    }

    @Test
    @Transactional
    void patchNonExistingDespliegueDeServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        despliegueDeServicio.setId(longCount.incrementAndGet());

        // Create the DespliegueDeServicio
        DespliegueDeServicioDTO despliegueDeServicioDTO = despliegueDeServicioMapper.toDto(despliegueDeServicio);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDespliegueDeServicioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, despliegueDeServicioDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(despliegueDeServicioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DespliegueDeServicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDespliegueDeServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        despliegueDeServicio.setId(longCount.incrementAndGet());

        // Create the DespliegueDeServicio
        DespliegueDeServicioDTO despliegueDeServicioDTO = despliegueDeServicioMapper.toDto(despliegueDeServicio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDespliegueDeServicioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(despliegueDeServicioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DespliegueDeServicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDespliegueDeServicio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        despliegueDeServicio.setId(longCount.incrementAndGet());

        // Create the DespliegueDeServicio
        DespliegueDeServicioDTO despliegueDeServicioDTO = despliegueDeServicioMapper.toDto(despliegueDeServicio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDespliegueDeServicioMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(despliegueDeServicioDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DespliegueDeServicio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDespliegueDeServicio() throws Exception {
        // Initialize the database
        insertedDespliegueDeServicio = despliegueDeServicioRepository.saveAndFlush(despliegueDeServicio);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the despliegueDeServicio
        restDespliegueDeServicioMockMvc
            .perform(delete(ENTITY_API_URL_ID, despliegueDeServicio.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return despliegueDeServicioRepository.count();
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

    protected DespliegueDeServicio getPersistedDespliegueDeServicio(DespliegueDeServicio despliegueDeServicio) {
        return despliegueDeServicioRepository.findById(despliegueDeServicio.getId()).orElseThrow();
    }

    protected void assertPersistedDespliegueDeServicioToMatchAllProperties(DespliegueDeServicio expectedDespliegueDeServicio) {
        assertDespliegueDeServicioAllPropertiesEquals(
            expectedDespliegueDeServicio,
            getPersistedDespliegueDeServicio(expectedDespliegueDeServicio)
        );
    }

    protected void assertPersistedDespliegueDeServicioToMatchUpdatableProperties(DespliegueDeServicio expectedDespliegueDeServicio) {
        assertDespliegueDeServicioAllUpdatablePropertiesEquals(
            expectedDespliegueDeServicio,
            getPersistedDespliegueDeServicio(expectedDespliegueDeServicio)
        );
    }
}
