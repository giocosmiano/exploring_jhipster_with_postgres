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

describe('Rental e2e test', () => {
  const rentalPageUrl = '/rental';
  const rentalPageUrlPattern = new RegExp('/rental(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const rentalSample = { rentalId: 58395, rentalDate: '2021-12-08T04:06:25.438Z', lastUpdate: '2021-12-08T03:15:08.524Z' };

  let rental: any;
  //let inventory: any;
  //let customer: any;
  //let staff: any;

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
      url: '/api/inventories',
      body: {"inventoryId":88983,"storeId":63482,"lastUpdate":"2021-12-08T14:56:36.074Z"},
    }).then(({ body }) => {
      inventory = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/customers',
      body: {"customerId":48566,"storeId":54371,"firstName":"Charley","lastName":"Turner","email":"Verlie.Runolfsdottir53@gmail.com","activebool":false,"createDate":"2021-12-08","lastUpdate":"2021-12-08T19:38:49.556Z","active":37094},
    }).then(({ body }) => {
      customer = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/staff',
      body: {"staffId":4887,"firstName":"Ricardo","lastName":"Jast","email":"Jaquelin2@hotmail.com","storeId":57520,"active":false,"username":"Lock FTP Burg","password":"Moroccan","lastUpdate":"2021-12-08T03:59:52.906Z","picture":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=","pictureContentType":"unknown"},
    }).then(({ body }) => {
      staff = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/rentals+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/rentals').as('postEntityRequest');
    cy.intercept('DELETE', '/api/rentals/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/inventories', {
      statusCode: 200,
      body: [inventory],
    });

    cy.intercept('GET', '/api/customers', {
      statusCode: 200,
      body: [customer],
    });

    cy.intercept('GET', '/api/staff', {
      statusCode: 200,
      body: [staff],
    });

    cy.intercept('GET', '/api/payments', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (rental) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/rentals/${rental.id}`,
      }).then(() => {
        rental = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (inventory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/inventories/${inventory.id}`,
      }).then(() => {
        inventory = undefined;
      });
    }
    if (customer) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/customers/${customer.id}`,
      }).then(() => {
        customer = undefined;
      });
    }
    if (staff) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/staff/${staff.id}`,
      }).then(() => {
        staff = undefined;
      });
    }
  });
   */

  it('Rentals menu should load Rentals page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('rental');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Rental').should('exist');
    cy.url().should('match', rentalPageUrlPattern);
  });

  describe('Rental page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(rentalPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Rental page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/rental/new$'));
        cy.getEntityCreateUpdateHeading('Rental');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', rentalPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/rentals',
  
          body: {
            ...rentalSample,
            inventory: inventory,
            customer: customer,
            staff: staff,
          },
        }).then(({ body }) => {
          rental = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/rentals+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [rental],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(rentalPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(rentalPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response!.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Rental page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('rental');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', rentalPageUrlPattern);
      });

      it('edit button click should load edit Rental page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Rental');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', rentalPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Rental', () => {
        cy.intercept('GET', '/api/rentals/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('rental').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', rentalPageUrlPattern);

        rental = undefined;
      });
    });
  });

  describe('new Rental page', () => {
    beforeEach(() => {
      cy.visit(`${rentalPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Rental');
    });

    it.skip('should create an instance of Rental', () => {
      cy.get(`[data-cy="rentalId"]`).type('61411').should('have.value', '61411');

      cy.get(`[data-cy="rentalDate"]`).type('2021-12-08T18:09').should('have.value', '2021-12-08T18:09');

      cy.get(`[data-cy="returnDate"]`).type('2021-12-08T17:32').should('have.value', '2021-12-08T17:32');

      cy.get(`[data-cy="lastUpdate"]`).type('2021-12-08T20:41').should('have.value', '2021-12-08T20:41');

      cy.get(`[data-cy="inventory"]`).select(1);
      cy.get(`[data-cy="customer"]`).select(1);
      cy.get(`[data-cy="staff"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        rental = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', rentalPageUrlPattern);
    });
  });
});
