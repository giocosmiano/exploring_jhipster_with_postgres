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

describe('Country e2e test', () => {
  const countryPageUrl = '/country';
  const countryPageUrlPattern = new RegExp('/country(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const countrySample = { countryId: 66938, country: 'Saint Vincent and the Grenadines', lastUpdate: '2021-12-08T13:36:15.704Z' };

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
    cy.intercept('GET', '/api/countries+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/countries').as('postEntityRequest');
    cy.intercept('DELETE', '/api/countries/*').as('deleteEntityRequest');
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

  it('Countries menu should load Countries page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('country');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Country').should('exist');
    cy.url().should('match', countryPageUrlPattern);
  });

  describe('Country page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(countryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Country page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/country/new$'));
        cy.getEntityCreateUpdateHeading('Country');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', countryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/countries',
          body: countrySample,
        }).then(({ body }) => {
          country = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/countries+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [country],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(countryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Country page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('country');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', countryPageUrlPattern);
      });

      it('edit button click should load edit Country page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Country');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', countryPageUrlPattern);
      });

      it('last delete button click should delete instance of Country', () => {
        cy.intercept('GET', '/api/countries/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('country').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', countryPageUrlPattern);

        country = undefined;
      });
    });
  });

  describe('new Country page', () => {
    beforeEach(() => {
      cy.visit(`${countryPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Country');
    });

    it('should create an instance of Country', () => {
      cy.get(`[data-cy="countryId"]`).type('86217').should('have.value', '86217');

      cy.get(`[data-cy="country"]`).type('Mauritania').should('have.value', 'Mauritania');

      cy.get(`[data-cy="lastUpdate"]`).type('2021-12-08T20:00').should('have.value', '2021-12-08T20:00');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        country = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', countryPageUrlPattern);
    });
  });
});
