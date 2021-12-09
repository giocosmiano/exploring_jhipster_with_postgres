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

describe('Staff e2e test', () => {
  const staffPageUrl = '/staff';
  const staffPageUrlPattern = new RegExp('/staff(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';
  const staffSample = {
    staffId: 61337,
    firstName: 'Kamryn',
    lastName: 'Wintheiser',
    storeId: 71420,
    active: false,
    username: 'synthesize management Frozen',
    lastUpdate: '2021-12-08T20:36:23.629Z',
  };

  let staff: any;
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
      url: '/api/addresses',
      body: {"addressId":78344,"address":"pink payment","address2":"interface","district":"Berkshire Tuna","postalCode":"invoice innovate Loan","phone":"(376) 965-4927 x76475","lastUpdate":"2021-12-08T18:08:16.990Z"},
    }).then(({ body }) => {
      address = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/staff+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/staff').as('postEntityRequest');
    cy.intercept('DELETE', '/api/staff/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/addresses', {
      statusCode: 200,
      body: [address],
    });

    cy.intercept('GET', '/api/payments', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/rentals', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/stores', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (staff) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/staff/${staff.id}`,
      }).then(() => {
        staff = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
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

  it('Staff menu should load Staff page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('staff');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Staff').should('exist');
    cy.url().should('match', staffPageUrlPattern);
  });

  describe('Staff page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(staffPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Staff page', () => {
        cy.get(entityCreateButtonSelector).click({ force: true });
        cy.url().should('match', new RegExp('/staff/new$'));
        cy.getEntityCreateUpdateHeading('Staff');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', staffPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/staff',
  
          body: {
            ...staffSample,
            address: address,
          },
        }).then(({ body }) => {
          staff = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/staff+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [staff],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(staffPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(staffPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response!.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Staff page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('staff');
        cy.get(entityDetailsBackButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', staffPageUrlPattern);
      });

      it('edit button click should load edit Staff page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Staff');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click({ force: true });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', staffPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Staff', () => {
        cy.intercept('GET', '/api/staff/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('staff').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', staffPageUrlPattern);

        staff = undefined;
      });
    });
  });

  describe('new Staff page', () => {
    beforeEach(() => {
      cy.visit(`${staffPageUrl}`);
      cy.get(entityCreateButtonSelector).click({ force: true });
      cy.getEntityCreateUpdateHeading('Staff');
    });

    it.skip('should create an instance of Staff', () => {
      cy.get(`[data-cy="staffId"]`).type('74029').should('have.value', '74029');

      cy.get(`[data-cy="firstName"]`).type('Elmore').should('have.value', 'Elmore');

      cy.get(`[data-cy="lastName"]`).type('Harber').should('have.value', 'Harber');

      cy.get(`[data-cy="email"]`).type('Elisa41@gmail.com').should('have.value', 'Elisa41@gmail.com');

      cy.get(`[data-cy="storeId"]`).type('60792').should('have.value', '60792');

      cy.get(`[data-cy="active"]`).should('not.be.checked');
      cy.get(`[data-cy="active"]`).click().should('be.checked');

      cy.get(`[data-cy="username"]`).type('Clothing base').should('have.value', 'Clothing base');

      cy.get(`[data-cy="password"]`).type('B2B Oregon').should('have.value', 'B2B Oregon');

      cy.get(`[data-cy="lastUpdate"]`).type('2021-12-08T12:52').should('have.value', '2021-12-08T12:52');

      cy.setFieldImageAsBytesOfEntity('picture', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="address"]`).select(1);

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        staff = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', staffPageUrlPattern);
    });
  });
});
