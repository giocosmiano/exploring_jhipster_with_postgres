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

describe('Payment e2e test', () => {
  const paymentPageUrl = '/payment';
  const paymentPageUrlPattern = new RegExp('/payment(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const paymentSample = { paymentId: 62596, amount: 24811, paymentDate: '2021-12-08T10:22:37.666Z' };

  let payment: any;
  //let customer: any;
  //let staff: any;
  //let rental: any;

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
      url: '/api/customers',
      body: {"customerId":88386,"storeId":33350,"firstName":"Maximillian","lastName":"Green","email":"Elsie99@yahoo.com","activebool":true,"createDate":"2021-12-08","lastUpdate":"2021-12-08T13:02:36.846Z","active":37677},
    }).then(({ body }) => {
      customer = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/staff',
      body: {"staffId":59273,"firstName":"Candido","lastName":"Kulas","email":"Wava.Gerlach@hotmail.com","storeId":24370,"active":false,"username":"Object-based","password":"Frozen Analyst","lastUpdate":"2021-12-08T02:00:28.763Z","picture":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=","pictureContentType":"unknown"},
    }).then(({ body }) => {
      staff = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/rentals',
      body: {"rentalId":98419,"rentalDate":"2021-12-08T08:52:16.154Z","returnDate":"2021-12-08T21:14:27.502Z","lastUpdate":"2021-12-08T02:27:39.145Z"},
    }).then(({ body }) => {
      rental = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/payments+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/payments').as('postEntityRequest');
    cy.intercept('DELETE', '/api/payments/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/customers', {
      statusCode: 200,
      body: [customer],
    });

    cy.intercept('GET', '/api/staff', {
      statusCode: 200,
      body: [staff],
    });

    cy.intercept('GET', '/api/rentals', {
      statusCode: 200,
      body: [rental],
    });

  });
   */

  afterEach(() => {
    if (payment) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/payments/${payment.id}`,
      }).then(() => {
        payment = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
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
    if (rental) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/rentals/${rental.id}`,
      }).then(() => {
        rental = undefined;
      });
    }
  });
   */

  it('Payments menu should load Payments page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('payment');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Payment').should('exist');
    cy.url().should('match', paymentPageUrlPattern);
  });

  describe('Payment page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(paymentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Payment page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/payment/new$'));
        cy.getEntityCreateUpdateHeading('Payment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', paymentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/payments',
  
          body: {
            ...paymentSample,
            customer: customer,
            staff: staff,
            rental: rental,
          },
        }).then(({ body }) => {
          payment = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/payments+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [payment],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(paymentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(paymentPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response!.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Payment page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('payment');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', paymentPageUrlPattern);
      });

      it('edit button click should load edit Payment page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Payment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', paymentPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Payment', () => {
        cy.intercept('GET', '/api/payments/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('payment').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', paymentPageUrlPattern);

        payment = undefined;
      });
    });
  });

  describe('new Payment page', () => {
    beforeEach(() => {
      cy.visit(`${paymentPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Payment');
    });

    it.skip('should create an instance of Payment', () => {
      cy.get(`[data-cy="paymentId"]`).type('22078').should('have.value', '22078');

      cy.get(`[data-cy="amount"]`).type('90698').should('have.value', '90698');

      cy.get(`[data-cy="paymentDate"]`).type('2021-12-08T22:42').should('have.value', '2021-12-08T22:42');

      cy.get(`[data-cy="customer"]`).select(1);
      cy.get(`[data-cy="staff"]`).select(1);
      cy.get(`[data-cy="rental"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        payment = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', paymentPageUrlPattern);
    });
  });
});
