import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Film e2e test', () => {
  const filmPageUrl = '/film';
  const filmPageUrlPattern = new RegExp('/film(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const filmSample = {
    filmId: 90450,
    title: 'quantifying Shirt',
    rentalDuration: 64171,
    rentalRate: 58639,
    replacementCost: 15306,
    lastUpdate: '2021-12-08T14:36:43.973Z',
    fulltext: 'Rubber',
  };

  let film: any;
  let language: any;

  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });
    cy.visit('');
    cy.login(username, password);
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/languages',
      body: { languageId: 36224, name: 'Credit', lastUpdate: '2021-12-08T22:23:41.918Z' },
    }).then(({ body }) => {
      language = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/films+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/films').as('postEntityRequest');
    cy.intercept('DELETE', '/api/films/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/languages', {
      statusCode: 200,
      body: [language],
    });

    cy.intercept('GET', '/api/film-actors', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/film-categories', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/inventories', {
      statusCode: 200,
      body: [],
    });
  });

  afterEach(() => {
    if (film) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/films/${film.id}`,
      }).then(() => {
        film = undefined;
      });
    }
  });

  afterEach(() => {
    if (language) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/languages/${language.id}`,
      }).then(() => {
        language = undefined;
      });
    }
  });

  it('Films menu should load Films page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('film');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Film').should('exist');
    cy.url().should('match', filmPageUrlPattern);
  });

  describe('Film page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(filmPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Film page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/film/new$'));
        cy.getEntityCreateUpdateHeading('Film');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', filmPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/films',

          body: {
            ...filmSample,
            language: language,
          },
        }).then(({ body }) => {
          film = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/films+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [film],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(filmPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Film page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('film');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', filmPageUrlPattern);
      });

      it('edit button click should load edit Film page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Film');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', filmPageUrlPattern);
      });

      it('last delete button click should delete instance of Film', () => {
        cy.intercept('GET', '/api/films/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('film').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', filmPageUrlPattern);

        film = undefined;
      });
    });
  });

  describe('new Film page', () => {
    beforeEach(() => {
      cy.visit(`${filmPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Film');
    });

    it('should create an instance of Film', () => {
      cy.get(`[data-cy="filmId"]`).type('87462').should('have.value', '87462');

      cy.get(`[data-cy="title"]`).type('communities Functionality').should('have.value', 'communities Functionality');

      cy.get(`[data-cy="description"]`).type('Home Technician Virginia').should('have.value', 'Home Technician Virginia');

      cy.get(`[data-cy="releaseYear"]`).type('99464').should('have.value', '99464');

      cy.get(`[data-cy="rentalDuration"]`).type('81481').should('have.value', '81481');

      cy.get(`[data-cy="rentalRate"]`).type('57024').should('have.value', '57024');

      cy.get(`[data-cy="length"]`).type('24048').should('have.value', '24048');

      cy.get(`[data-cy="replacementCost"]`).type('70825').should('have.value', '70825');

      cy.get(`[data-cy="rating"]`).type('solutions').should('have.value', 'solutions');

      cy.get(`[data-cy="lastUpdate"]`).type('2021-12-08T18:54').should('have.value', '2021-12-08T18:54');

      cy.get(`[data-cy="specialFeatures"]`).type('payment Checking primary').should('have.value', 'payment Checking primary');

      cy.get(`[data-cy="fulltext"]`).type('best-of-breed').should('have.value', 'best-of-breed');

      cy.get(`[data-cy="language"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        film = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', filmPageUrlPattern);
    });
  });
});
