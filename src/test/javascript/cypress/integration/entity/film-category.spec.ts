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

describe('FilmCategory e2e test', () => {
  const filmCategoryPageUrl = '/film-category';
  const filmCategoryPageUrlPattern = new RegExp('/film-category(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const filmCategorySample = { lastUpdate: '2021-12-08T01:34:24.022Z' };

  let filmCategory: any;
  //let film: any;
  //let category: any;

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
      url: '/api/films',
      body: {"filmId":96119,"title":"Consultant","description":"wireless context-sensitive","releaseYear":50555,"rentalDuration":71268,"rentalRate":97311,"length":59756,"replacementCost":90998,"rating":"Birr","lastUpdate":"2021-12-08T19:57:45.886Z","specialFeatures":"Metrics Rustic","fulltext":"platforms"},
    }).then(({ body }) => {
      film = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/categories',
      body: {"categoryId":66437,"name":"Avon open-source","lastUpdate":"2021-12-08T01:32:05.441Z"},
    }).then(({ body }) => {
      category = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/film-categories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/film-categories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/film-categories/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/films', {
      statusCode: 200,
      body: [film],
    });

    cy.intercept('GET', '/api/categories', {
      statusCode: 200,
      body: [category],
    });

  });
   */

  afterEach(() => {
    if (filmCategory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/film-categories/${filmCategory.id}`,
      }).then(() => {
        filmCategory = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (film) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/films/${film.id}`,
      }).then(() => {
        film = undefined;
      });
    }
    if (category) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/categories/${category.id}`,
      }).then(() => {
        category = undefined;
      });
    }
  });
   */

  it('FilmCategories menu should load FilmCategories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('film-category');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('FilmCategory').should('exist');
    cy.url().should('match', filmCategoryPageUrlPattern);
  });

  describe('FilmCategory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(filmCategoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create FilmCategory page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/film-category/new$'));
        cy.getEntityCreateUpdateHeading('FilmCategory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', filmCategoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/film-categories',
  
          body: {
            ...filmCategorySample,
            film: film,
            category: category,
          },
        }).then(({ body }) => {
          filmCategory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/film-categories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [filmCategory],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(filmCategoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(filmCategoryPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response!.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details FilmCategory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('filmCategory');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', filmCategoryPageUrlPattern);
      });

      it('edit button click should load edit FilmCategory page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('FilmCategory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', filmCategoryPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of FilmCategory', () => {
        cy.intercept('GET', '/api/film-categories/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('filmCategory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', filmCategoryPageUrlPattern);

        filmCategory = undefined;
      });
    });
  });

  describe('new FilmCategory page', () => {
    beforeEach(() => {
      cy.visit(`${filmCategoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('FilmCategory');
    });

    it.skip('should create an instance of FilmCategory', () => {
      cy.get(`[data-cy="lastUpdate"]`).type('2021-12-08T17:43').should('have.value', '2021-12-08T17:43');

      cy.get(`[data-cy="film"]`).select(1);
      cy.get(`[data-cy="category"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        filmCategory = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', filmCategoryPageUrlPattern);
    });
  });
});
