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

describe('Store e2e test', () => {
  const storePageUrl = '/store';
  const storePageUrlPattern = new RegExp('/store(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const storeSample = { storeId: 23533, lastUpdate: '2021-12-08T22:29:14.927Z' };

  let store: any;
  //let staff: any;
  //let address: any;

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
      url: '/api/staff',
      body: {"staffId":89334,"firstName":"Antone","lastName":"Wolf","email":"Austen.McCullough5@gmail.com","storeId":29721,"active":true,"username":"Engineer Plastic","password":"Inlet connect capacitor","lastUpdate":"2021-12-08T03:12:30.413Z","picture":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=","pictureContentType":"unknown"},
    }).then(({ body }) => {
      staff = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/addresses',
      body: {"addressId":53647,"address":"Borders HDD","address2":"Rand","district":"secondary e-services","postalCode":"sky","phone":"468-460-9528 x794","lastUpdate":"2021-12-08T11:19:55.523Z"},
    }).then(({ body }) => {
      address = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/stores+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/stores').as('postEntityRequest');
    cy.intercept('DELETE', '/api/stores/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/staff', {
      statusCode: 200,
      body: [staff],
    });

    cy.intercept('GET', '/api/addresses', {
      statusCode: 200,
      body: [address],
    });

  });
   */

  afterEach(() => {
    if (store) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/stores/${store.id}`,
      }).then(() => {
        store = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (staff) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/staff/${staff.id}`,
      }).then(() => {
        staff = undefined;
      });
    }
    if (address) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/addresses/${address.id}`,
      }).then(() => {
        address = undefined;
      });
    }
  });
   */

  it('Stores menu should load Stores page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('store');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Store').should('exist');
    cy.url().should('match', storePageUrlPattern);
  });

  describe('Store page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(storePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Store page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/store/new$'));
        cy.getEntityCreateUpdateHeading('Store');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', storePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/stores',
  
          body: {
            ...storeSample,
            managerStaff: staff,
            address: address,
          },
        }).then(({ body }) => {
          store = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/stores+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [store],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(storePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(storePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response!.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Store page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('store');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', storePageUrlPattern);
      });

      it('edit button click should load edit Store page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Store');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', storePageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Store', () => {
        cy.intercept('GET', '/api/stores/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('store').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', storePageUrlPattern);

        store = undefined;
      });
    });
  });

  describe('new Store page', () => {
    beforeEach(() => {
      cy.visit(`${storePageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Store');
    });

    it.skip('should create an instance of Store', () => {
      cy.get(`[data-cy="storeId"]`).type('4191').should('have.value', '4191');

      cy.get(`[data-cy="lastUpdate"]`).type('2021-12-08T20:51').should('have.value', '2021-12-08T20:51');

      cy.get(`[data-cy="managerStaff"]`).select(1);
      cy.get(`[data-cy="address"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        store = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', storePageUrlPattern);
    });
  });
});
