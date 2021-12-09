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

describe('Inventory e2e test', () => {
  const inventoryPageUrl = '/inventory';
  const inventoryPageUrlPattern = new RegExp('/inventory(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const inventorySample = { inventoryId: 74798, storeId: 51319, lastUpdate: '2021-12-08T16:44:39.922Z' };

  let inventory: any;
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
      url: '/api/films',
      body: {"filmId":85155,"title":"Specialist optical","description":"Nepalese digital","releaseYear":19639,"rentalDuration":42529,"rentalRate":97345,"length":99986,"replacementCost":64424,"rating":"bleeding-edge","lastUpdate":"2021-12-08T13:26:38.607Z","specialFeatures":"Cote Senior Handcrafted","fulltext":"Auto plum process"},
    }).then(({ body }) => {
      film = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/inventories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/inventories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/inventories/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/films', {
      statusCode: 200,
      body: [film],
    });

    cy.intercept('GET', '/api/rentals', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (inventory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/inventories/${inventory.id}`,
      }).then(() => {
        inventory = undefined;
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
  });
   */

  it('Inventories menu should load Inventories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('inventory');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Inventory').should('exist');
    cy.url().should('match', inventoryPageUrlPattern);
  });

  describe('Inventory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(inventoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Inventory page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/inventory/new$'));
        cy.getEntityCreateUpdateHeading('Inventory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', inventoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/inventories',
  
          body: {
            ...inventorySample,
            film: film,
          },
        }).then(({ body }) => {
          inventory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/inventories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [inventory],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(inventoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(inventoryPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response!.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Inventory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('inventory');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', inventoryPageUrlPattern);
      });

      it('edit button click should load edit Inventory page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Inventory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', inventoryPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Inventory', () => {
        cy.intercept('GET', '/api/inventories/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('inventory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', inventoryPageUrlPattern);

        inventory = undefined;
      });
    });
  });

  describe('new Inventory page', () => {
    beforeEach(() => {
      cy.visit(`${inventoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Inventory');
    });

    it.skip('should create an instance of Inventory', () => {
      cy.get(`[data-cy="inventoryId"]`).type('86254').should('have.value', '86254');

      cy.get(`[data-cy="storeId"]`).type('65249').should('have.value', '65249');

      cy.get(`[data-cy="lastUpdate"]`).type('2021-12-08T18:21').should('have.value', '2021-12-08T18:21');

      cy.get(`[data-cy="film"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        inventory = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', inventoryPageUrlPattern);
    });
  });
});
