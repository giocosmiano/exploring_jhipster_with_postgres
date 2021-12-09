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

describe('Actor e2e test', () => {
  const actorPageUrl = '/actor';
  const actorPageUrlPattern = new RegExp('/actor(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const actorSample = { actorId: 58505, firstName: 'Dorian', lastName: 'Lindgren', lastUpdate: '2021-12-08T17:46:50.917Z' };

  let actor: any;

  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });
    cy.visit('');
    cy.login(username, password);
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/actors+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/actors').as('postEntityRequest');
    cy.intercept('DELETE', '/api/actors/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (actor) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/actors/${actor.id}`,
      }).then(() => {
        actor = undefined;
      });
    }
  });

  it('Actors menu should load Actors page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('actor');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Actor').should('exist');
    cy.url().should('match', actorPageUrlPattern);
  });

  describe('Actor page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(actorPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Actor page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/actor/new$'));
        cy.getEntityCreateUpdateHeading('Actor');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', actorPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/actors',
          body: actorSample,
        }).then(({ body }) => {
          actor = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/actors+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [actor],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(actorPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Actor page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('actor');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', actorPageUrlPattern);
      });

      it('edit button click should load edit Actor page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Actor');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', actorPageUrlPattern);
      });

      it('last delete button click should delete instance of Actor', () => {
        cy.intercept('GET', '/api/actors/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('actor').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', actorPageUrlPattern);

        actor = undefined;
      });
    });
  });

  describe('new Actor page', () => {
    beforeEach(() => {
      cy.visit(`${actorPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Actor');
    });

    it('should create an instance of Actor', () => {
      cy.get(`[data-cy="actorId"]`).type('11203').should('have.value', '11203');

      cy.get(`[data-cy="firstName"]`).type('Tavares').should('have.value', 'Tavares');

      cy.get(`[data-cy="lastName"]`).type('Muller').should('have.value', 'Muller');

      cy.get(`[data-cy="lastUpdate"]`).type('2021-12-08T22:45').should('have.value', '2021-12-08T22:45');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        actor = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', actorPageUrlPattern);
    });
  });
});
