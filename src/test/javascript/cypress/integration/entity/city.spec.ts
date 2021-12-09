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

describe('City e2e test', () => {
  const cityPageUrl = '/city';
  const cityPageUrlPattern = new RegExp('/city(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const citySample = { cityId: 86825, city: 'New Stuart', lastUpdate: '2021-12-08T01:37:13.273Z' };

  let city: any;
  let country: any;

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
      url: '/api/countries',
      body: { countryId: 98637, country: 'Faroe Islands', lastUpdate: '2021-12-08T16:16:41.853Z' },
    }).then(({ body }) => {
      country = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/cities+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/cities').as('postEntityRequest');
    cy.intercept('DELETE', '/api/cities/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/countries', {
      statusCode: 200,
      body: [country],
    });

    cy.intercept('GET', '/api/addresses', {
      statusCode: 200,
      body: [],
    });
  });

  afterEach(() => {
    if (city) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/cities/${city.id}`,
      }).then(() => {
        city = undefined;
      });
    }
  });

  afterEach(() => {
    if (country) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/countries/${country.id}`,
      }).then(() => {
        country = undefined;
      });
    }
  });

  it('Cities menu should load Cities page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('city');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('City').should('exist');
    cy.url().should('match', cityPageUrlPattern);
  });

  describe('City page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(cityPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create City page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/city/new$'));
        cy.getEntityCreateUpdateHeading('City');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', cityPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/cities',

          body: {
            ...citySample,
            country: country,
          },
        }).then(({ body }) => {
          city = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/cities+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [city],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(cityPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details City page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('city');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', cityPageUrlPattern);
      });

      it('edit button click should load edit City page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('City');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', cityPageUrlPattern);
      });

      it('last delete button click should delete instance of City', () => {
        cy.intercept('GET', '/api/cities/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('city').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', cityPageUrlPattern);

        city = undefined;
      });
    });
  });

  describe('new City page', () => {
    beforeEach(() => {
      cy.visit(`${cityPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('City');
    });

    it('should create an instance of City', () => {
      cy.get(`[data-cy="cityId"]`).type('8895').should('have.value', '8895');

      cy.get(`[data-cy="city"]`).type('Lionelchester').should('have.value', 'Lionelchester');

      cy.get(`[data-cy="lastUpdate"]`).type('2021-12-08T01:16').should('have.value', '2021-12-08T01:16');

      cy.get(`[data-cy="country"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        city = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', cityPageUrlPattern);
    });
  });
});
