package ar.edu.um.isa.oncall.web.rest;

import static ar.edu.um.isa.oncall.domain.CambioAsserts.*;
import static ar.edu.um.isa.oncall.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.um.isa.oncall.IntegrationTest;
import ar.edu.um.isa.oncall.domain.Cambio;
import ar.edu.um.isa.oncall.domain.User;
import ar.edu.um.isa.oncall.domain.enumeration.NivelRiesgo;
import ar.edu.um.isa.oncall.domain.enumeration.TipoCambio;
import ar.edu.um.isa.oncall.repository.CambioRepository;
import ar.edu.um.isa.oncall.repository.UserRepository;
import ar.edu.um.isa.oncall.service.CambioService;
import ar.edu.um.isa.oncall.service.dto.CambioDTO;
import ar.edu.um.isa.oncall.service.mapper.CambioMapper;
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
 * Integration tests for the {@link CambioResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CambioResourceIT {

    private static final String DEFAULT_TITULO = "AAAAAAAAAA";
    private static final String UPDATED_TITULO = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final TipoCambio DEFAULT_TIPO = TipoCambio.CODIGO;
    private static final TipoCambio UPDATED_TIPO = TipoCambio.CONFIGURACION;

    private static final NivelRiesgo DEFAULT_RIESGO = NivelRiesgo.BAJO;
    private static final NivelRiesgo UPDATED_RIESGO = NivelRiesgo.MEDIO;

    private static final String DEFAULT_TICKET_URL = "AAAAAAAAAA";
    private static final String UPDATED_TICKET_URL = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREADO_EN = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREADO_EN = Instant.ofEpochMilli(1701729143509L);

    private static final String ENTITY_API_URL = "/api/cambios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CambioRepository cambioRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private CambioRepository cambioRepositoryMock;

    @Autowired
    private CambioMapper cambioMapper;

    @Mock
    private CambioService cambioServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCambioMockMvc;

    private Cambio cambio;

    private Cambio insertedCambio;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cambio createEntity(EntityManager em) {
        Cambio cambio = new Cambio()
            .titulo(DEFAULT_TITULO)
            .descripcion(DEFAULT_DESCRIPCION)
            .tipo(DEFAULT_TIPO)
            .riesgo(DEFAULT_RIESGO)
            .ticketUrl(DEFAULT_TICKET_URL)
            .creadoEn(DEFAULT_CREADO_EN);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        cambio.setAutor(user);
        return cambio;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cambio createUpdatedEntity(EntityManager em) {
        Cambio updatedCambio = new Cambio()
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .tipo(UPDATED_TIPO)
            .riesgo(UPDATED_RIESGO)
            .ticketUrl(UPDATED_TICKET_URL)
            .creadoEn(UPDATED_CREADO_EN);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedCambio.setAutor(user);
        return updatedCambio;
    }

    @BeforeEach
    void initTest() {
        cambio = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCambio != null) {
            cambioRepository.delete(insertedCambio);
            insertedCambio = null;
        }
    }

    @Test
    @Transactional
    void createCambio() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Cambio
        CambioDTO cambioDTO = cambioMapper.toDto(cambio);
        var returnedCambioDTO = om.readValue(
            restCambioMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cambioDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CambioDTO.class
        );

        // Validate the Cambio in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCambio = cambioMapper.toEntity(returnedCambioDTO);
        assertCambioUpdatableFieldsEquals(returnedCambio, getPersistedCambio(returnedCambio));

        insertedCambio = returnedCambio;
    }

    @Test
    @Transactional
    void createCambioWithExistingId() throws Exception {
        // Create the Cambio with an existing ID
        cambio.setId(1L);
        CambioDTO cambioDTO = cambioMapper.toDto(cambio);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCambioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cambioDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Cambio in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTituloIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cambio.setTitulo(null);

        // Create the Cambio, which fails.
        CambioDTO cambioDTO = cambioMapper.toDto(cambio);

        restCambioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cambioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTipoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cambio.setTipo(null);

        // Create the Cambio, which fails.
        CambioDTO cambioDTO = cambioMapper.toDto(cambio);

        restCambioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cambioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRiesgoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cambio.setRiesgo(null);

        // Create the Cambio, which fails.
        CambioDTO cambioDTO = cambioMapper.toDto(cambio);

        restCambioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cambioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreadoEnIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cambio.setCreadoEn(null);

        // Create the Cambio, which fails.
        CambioDTO cambioDTO = cambioMapper.toDto(cambio);

        restCambioMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cambioDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCambios() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList
        restCambioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cambio.getId().intValue())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].tipo").value(hasItem(DEFAULT_TIPO.toString())))
            .andExpect(jsonPath("$.[*].riesgo").value(hasItem(DEFAULT_RIESGO.toString())))
            .andExpect(jsonPath("$.[*].ticketUrl").value(hasItem(DEFAULT_TICKET_URL)))
            .andExpect(jsonPath("$.[*].creadoEn").value(hasItem(DEFAULT_CREADO_EN.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCambiosWithEagerRelationshipsIsEnabled() throws Exception {
        when(cambioServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCambioMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(cambioServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCambiosWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(cambioServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCambioMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(cambioRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCambio() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get the cambio
        restCambioMockMvc
            .perform(get(ENTITY_API_URL_ID, cambio.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cambio.getId().intValue()))
            .andExpect(jsonPath("$.titulo").value(DEFAULT_TITULO))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.tipo").value(DEFAULT_TIPO.toString()))
            .andExpect(jsonPath("$.riesgo").value(DEFAULT_RIESGO.toString()))
            .andExpect(jsonPath("$.ticketUrl").value(DEFAULT_TICKET_URL))
            .andExpect(jsonPath("$.creadoEn").value(DEFAULT_CREADO_EN.toString()));
    }

    @Test
    @Transactional
    void getCambiosByIdFiltering() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        Long id = cambio.getId();

        defaultCambioFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCambioFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCambioFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCambiosByTituloIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where titulo equals to
        defaultCambioFiltering("titulo.equals=" + DEFAULT_TITULO, "titulo.equals=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllCambiosByTituloIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where titulo in
        defaultCambioFiltering("titulo.in=" + DEFAULT_TITULO + "," + UPDATED_TITULO, "titulo.in=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllCambiosByTituloIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where titulo is not null
        defaultCambioFiltering("titulo.specified=true", "titulo.specified=false");
    }

    @Test
    @Transactional
    void getAllCambiosByTituloContainsSomething() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where titulo contains
        defaultCambioFiltering("titulo.contains=" + DEFAULT_TITULO, "titulo.contains=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllCambiosByTituloNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where titulo does not contain
        defaultCambioFiltering("titulo.doesNotContain=" + UPDATED_TITULO, "titulo.doesNotContain=" + DEFAULT_TITULO);
    }

    @Test
    @Transactional
    void getAllCambiosByDescripcionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where descripcion equals to
        defaultCambioFiltering("descripcion.equals=" + DEFAULT_DESCRIPCION, "descripcion.equals=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllCambiosByDescripcionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where descripcion in
        defaultCambioFiltering(
            "descripcion.in=" + DEFAULT_DESCRIPCION + "," + UPDATED_DESCRIPCION,
            "descripcion.in=" + UPDATED_DESCRIPCION
        );
    }

    @Test
    @Transactional
    void getAllCambiosByDescripcionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where descripcion is not null
        defaultCambioFiltering("descripcion.specified=true", "descripcion.specified=false");
    }

    @Test
    @Transactional
    void getAllCambiosByDescripcionContainsSomething() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where descripcion contains
        defaultCambioFiltering("descripcion.contains=" + DEFAULT_DESCRIPCION, "descripcion.contains=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllCambiosByDescripcionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where descripcion does not contain
        defaultCambioFiltering("descripcion.doesNotContain=" + UPDATED_DESCRIPCION, "descripcion.doesNotContain=" + DEFAULT_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllCambiosByTipoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where tipo equals to
        defaultCambioFiltering("tipo.equals=" + DEFAULT_TIPO, "tipo.equals=" + UPDATED_TIPO);
    }

    @Test
    @Transactional
    void getAllCambiosByTipoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where tipo in
        defaultCambioFiltering("tipo.in=" + DEFAULT_TIPO + "," + UPDATED_TIPO, "tipo.in=" + UPDATED_TIPO);
    }

    @Test
    @Transactional
    void getAllCambiosByTipoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where tipo is not null
        defaultCambioFiltering("tipo.specified=true", "tipo.specified=false");
    }

    @Test
    @Transactional
    void getAllCambiosByRiesgoIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where riesgo equals to
        defaultCambioFiltering("riesgo.equals=" + DEFAULT_RIESGO, "riesgo.equals=" + UPDATED_RIESGO);
    }

    @Test
    @Transactional
    void getAllCambiosByRiesgoIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where riesgo in
        defaultCambioFiltering("riesgo.in=" + DEFAULT_RIESGO + "," + UPDATED_RIESGO, "riesgo.in=" + UPDATED_RIESGO);
    }

    @Test
    @Transactional
    void getAllCambiosByRiesgoIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where riesgo is not null
        defaultCambioFiltering("riesgo.specified=true", "riesgo.specified=false");
    }

    @Test
    @Transactional
    void getAllCambiosByTicketUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where ticketUrl equals to
        defaultCambioFiltering("ticketUrl.equals=" + DEFAULT_TICKET_URL, "ticketUrl.equals=" + UPDATED_TICKET_URL);
    }

    @Test
    @Transactional
    void getAllCambiosByTicketUrlIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where ticketUrl in
        defaultCambioFiltering("ticketUrl.in=" + DEFAULT_TICKET_URL + "," + UPDATED_TICKET_URL, "ticketUrl.in=" + UPDATED_TICKET_URL);
    }

    @Test
    @Transactional
    void getAllCambiosByTicketUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where ticketUrl is not null
        defaultCambioFiltering("ticketUrl.specified=true", "ticketUrl.specified=false");
    }

    @Test
    @Transactional
    void getAllCambiosByTicketUrlContainsSomething() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where ticketUrl contains
        defaultCambioFiltering("ticketUrl.contains=" + DEFAULT_TICKET_URL, "ticketUrl.contains=" + UPDATED_TICKET_URL);
    }

    @Test
    @Transactional
    void getAllCambiosByTicketUrlNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where ticketUrl does not contain
        defaultCambioFiltering("ticketUrl.doesNotContain=" + UPDATED_TICKET_URL, "ticketUrl.doesNotContain=" + DEFAULT_TICKET_URL);
    }

    @Test
    @Transactional
    void getAllCambiosByCreadoEnIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where creadoEn equals to
        defaultCambioFiltering("creadoEn.equals=" + DEFAULT_CREADO_EN, "creadoEn.equals=" + UPDATED_CREADO_EN);
    }

    @Test
    @Transactional
    void getAllCambiosByCreadoEnIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where creadoEn in
        defaultCambioFiltering("creadoEn.in=" + DEFAULT_CREADO_EN + "," + UPDATED_CREADO_EN, "creadoEn.in=" + UPDATED_CREADO_EN);
    }

    @Test
    @Transactional
    void getAllCambiosByCreadoEnIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        // Get all the cambioList where creadoEn is not null
        defaultCambioFiltering("creadoEn.specified=true", "creadoEn.specified=false");
    }

    @Test
    @Transactional
    void getAllCambiosByAutorIsEqualToSomething() throws Exception {
        User autor;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            cambioRepository.saveAndFlush(cambio);
            autor = UserResourceIT.createEntity();
        } else {
            autor = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(autor);
        em.flush();
        cambio.setAutor(autor);
        cambioRepository.saveAndFlush(cambio);
        Long autorId = autor.getId();
        // Get all the cambioList where autor equals to autorId
        defaultCambioShouldBeFound("autorId.equals=" + autorId);

        // Get all the cambioList where autor equals to (autorId + 1)
        defaultCambioShouldNotBeFound("autorId.equals=" + (autorId + 1));
    }

    private void defaultCambioFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCambioShouldBeFound(shouldBeFound);
        defaultCambioShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCambioShouldBeFound(String filter) throws Exception {
        restCambioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cambio.getId().intValue())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].tipo").value(hasItem(DEFAULT_TIPO.toString())))
            .andExpect(jsonPath("$.[*].riesgo").value(hasItem(DEFAULT_RIESGO.toString())))
            .andExpect(jsonPath("$.[*].ticketUrl").value(hasItem(DEFAULT_TICKET_URL)))
            .andExpect(jsonPath("$.[*].creadoEn").value(hasItem(DEFAULT_CREADO_EN.toString())));

        // Check, that the count call also returns 1
        restCambioMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCambioShouldNotBeFound(String filter) throws Exception {
        restCambioMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCambioMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCambio() throws Exception {
        // Get the cambio
        restCambioMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCambio() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cambio
        Cambio updatedCambio = cambioRepository.findById(cambio.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCambio are not directly saved in db
        em.detach(updatedCambio);
        updatedCambio
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .tipo(UPDATED_TIPO)
            .riesgo(UPDATED_RIESGO)
            .ticketUrl(UPDATED_TICKET_URL)
            .creadoEn(UPDATED_CREADO_EN);
        CambioDTO cambioDTO = cambioMapper.toDto(updatedCambio);

        restCambioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cambioDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cambioDTO))
            )
            .andExpect(status().isOk());

        // Validate the Cambio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCambioToMatchAllProperties(updatedCambio);
    }

    @Test
    @Transactional
    void putNonExistingCambio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cambio.setId(longCount.incrementAndGet());

        // Create the Cambio
        CambioDTO cambioDTO = cambioMapper.toDto(cambio);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCambioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cambioDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cambioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cambio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCambio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cambio.setId(longCount.incrementAndGet());

        // Create the Cambio
        CambioDTO cambioDTO = cambioMapper.toDto(cambio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCambioMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cambioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cambio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCambio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cambio.setId(longCount.incrementAndGet());

        // Create the Cambio
        CambioDTO cambioDTO = cambioMapper.toDto(cambio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCambioMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cambioDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cambio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCambioWithPatch() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cambio using partial update
        Cambio partialUpdatedCambio = new Cambio();
        partialUpdatedCambio.setId(cambio.getId());

        partialUpdatedCambio.descripcion(UPDATED_DESCRIPCION).tipo(UPDATED_TIPO).ticketUrl(UPDATED_TICKET_URL).creadoEn(UPDATED_CREADO_EN);

        restCambioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCambio.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCambio))
            )
            .andExpect(status().isOk());

        // Validate the Cambio in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCambioUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCambio, cambio), getPersistedCambio(cambio));
    }

    @Test
    @Transactional
    void fullUpdateCambioWithPatch() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cambio using partial update
        Cambio partialUpdatedCambio = new Cambio();
        partialUpdatedCambio.setId(cambio.getId());

        partialUpdatedCambio
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .tipo(UPDATED_TIPO)
            .riesgo(UPDATED_RIESGO)
            .ticketUrl(UPDATED_TICKET_URL)
            .creadoEn(UPDATED_CREADO_EN);

        restCambioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCambio.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCambio))
            )
            .andExpect(status().isOk());

        // Validate the Cambio in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCambioUpdatableFieldsEquals(partialUpdatedCambio, getPersistedCambio(partialUpdatedCambio));
    }

    @Test
    @Transactional
    void patchNonExistingCambio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cambio.setId(longCount.incrementAndGet());

        // Create the Cambio
        CambioDTO cambioDTO = cambioMapper.toDto(cambio);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCambioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cambioDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cambioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cambio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCambio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cambio.setId(longCount.incrementAndGet());

        // Create the Cambio
        CambioDTO cambioDTO = cambioMapper.toDto(cambio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCambioMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cambioDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Cambio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCambio() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cambio.setId(longCount.incrementAndGet());

        // Create the Cambio
        CambioDTO cambioDTO = cambioMapper.toDto(cambio);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCambioMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cambioDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Cambio in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCambio() throws Exception {
        // Initialize the database
        insertedCambio = cambioRepository.saveAndFlush(cambio);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cambio
        restCambioMockMvc
            .perform(delete(ENTITY_API_URL_ID, cambio.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cambioRepository.count();
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

    protected Cambio getPersistedCambio(Cambio cambio) {
        return cambioRepository.findById(cambio.getId()).orElseThrow();
    }

    protected void assertPersistedCambioToMatchAllProperties(Cambio expectedCambio) {
        assertCambioAllPropertiesEquals(expectedCambio, getPersistedCambio(expectedCambio));
    }

    protected void assertPersistedCambioToMatchUpdatableProperties(Cambio expectedCambio) {
        assertCambioAllUpdatablePropertiesEquals(expectedCambio, getPersistedCambio(expectedCambio));
    }
}
