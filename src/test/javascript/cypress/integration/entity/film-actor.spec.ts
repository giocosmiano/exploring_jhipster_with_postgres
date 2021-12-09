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

describe('FilmActor e2e test', () => {
  const filmActorPageUrl = '/film-actor';
  const filmActorPageUrlPattern = new RegExp('/film-actor(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const filmActorSample = { lastUpdate: '2021-12-08T11:00:38.947Z' };

  let filmActor: any;
  //let actor: any;
  //let film: any;

  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });
    cy.visit('');
    cy.login(username, password);
    cy.get(entityItemSelector).should('exist');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/actors',
      body: {"actorId":90910,"firstName":"Keon","lastName":"Stroman","lastUpdate":"2021-12-08T15:11:34.958Z"},
    }).then(({ body }) => {
      actor = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/films',
      body: {"filmId":9043,"title":"online","description":"leading-edge wireless Buckinghamshire","releaseYear":73372,"rentalDuration":45496,"rentalRate":87412,"length":84855,"replacementCost":48702,"rating":"AI Michigan","lastUpdate":"2021-12-08T10:33:40.669Z","specialFeatures":"Bedfordshire","fulltext":"up Gold"},
    }).then(({ body }) => {
      film = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/film-actors+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/film-actors').as('postEntityRequest');
    cy.intercept('DELETE', '/api/film-actors/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/actors', {
      statusCode: 200,
      body: [actor],
    });

    cy.intercept('GET', '/api/films', {
      statusCode: 200,
      body: [film],
    });

  });
   */

  afterEach(() => {
    if (filmActor) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/film-actors/${filmActor.id}`,
      }).then(() => {
        filmActor = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (actor) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/actors/${actor.id}`,
      }).then(() => {
        actor = undefined;
      });
    }
    if (film) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/films/${film.id}`,
      }).then(() => {
        film = undefined;
      });
    }
  });
   */

  it('FilmActors menu should load FilmActors page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('film-actor');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('FilmActor').should('exist');
    cy.url().should('match', filmActorPageUrlPattern);
  });

  describe('FilmActor page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(filmActorPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create FilmActor page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/film-actor/new$'));
        cy.getEntityCreateUpdateHeading('FilmActor');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', filmActorPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/film-actors',
  
          body: {
            ...filmActorSample,
            actor: actor,
            film: film,
          },
        }).then(({ body }) => {
          filmActor = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/film-actors+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [filmActor],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(filmActorPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(filmActorPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response!.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details FilmActor page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('filmActor');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', filmActorPageUrlPattern);
      });

      it('edit button click should load edit FilmActor page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FilmActor');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', filmActorPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of FilmActor', () => {
        cy.intercept('GET', '/api/film-actors/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('filmActor').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', filmActorPageUrlPattern);

        filmActor = undefined;
      });
    });
  });

  describe('new FilmActor page', () => {
    beforeEach(() => {
      cy.visit(`${filmActorPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('FilmActor');
    });

    it.skip('should create an instance of FilmActor', () => {
      cy.get(`[data-cy="lastUpdate"]`).type('2021-12-08T03:38').should('have.value', '2021-12-08T03:38');

      cy.get(`[data-cy="actor"]`).select(1);
      cy.get(`[data-cy="film"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        filmActor = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', filmActorPageUrlPattern);
    });
  });
});
