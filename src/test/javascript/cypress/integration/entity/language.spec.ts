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

describe('Language e2e test', () => {
  const languagePageUrl = '/language';
  const languagePageUrlPattern = new RegExp('/language(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const languageSample = { languageId: 88686, name: 'panel indexing', lastUpdate: '2021-12-08T15:09:21.626Z' };

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
    cy.intercept('GET', '/api/languages+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/languages').as('postEntityRequest');
    cy.intercept('DELETE', '/api/languages/*').as('deleteEntityRequest');
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

  it('Languages menu should load Languages page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('language');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Language').should('exist');
    cy.url().should('match', languagePageUrlPattern);
  });

  describe('Language page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(languagePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Language page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/language/new$'));
        cy.getEntityCreateUpdateHeading('Language');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', languagePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/languages',
          body: languageSample,
        }).then(({ body }) => {
          language = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/languages+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [language],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(languagePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Language page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('language');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', languagePageUrlPattern);
      });

      it('edit button click should load edit Language page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Language');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', languagePageUrlPattern);
      });

      it('last delete button click should delete instance of Language', () => {
        cy.intercept('GET', '/api/languages/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('language').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', languagePageUrlPattern);

        language = undefined;
      });
    });
  });

  describe('new Language page', () => {
    beforeEach(() => {
      cy.visit(`${languagePageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Language');
    });

    it('should create an instance of Language', () => {
      cy.get(`[data-cy="languageId"]`).type('74471').should('have.value', '74471');

      cy.get(`[data-cy="name"]`).type('AI').should('have.value', 'AI');

      cy.get(`[data-cy="lastUpdate"]`).type('2021-12-08T19:46').should('have.value', '2021-12-08T19:46');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        language = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', languagePageUrlPattern);
    });
  });
});
