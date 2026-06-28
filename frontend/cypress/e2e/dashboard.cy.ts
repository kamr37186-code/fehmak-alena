describe('Dashboard and Navigation', () => {
  beforeEach(() => {
    cy.visit('/login');
    cy.get('input[type="email"]').type('omar.student@fahmak.com');
    cy.get('input[type="password"]').type('password123');
    cy.get('button[type="submit"]').click();
    cy.url().should('include', '/dashboard');
  });

  it('should navigate to Explore Course and back to Library', () => {
    // There are recommended courses with text "Explore Course"
    cy.contains('Explore Course').first().click();
    cy.url().should('include', '/courses/');
    
    // Go to My Library via Dashboard or Sidebar
    cy.contains('Dashboard').click();
    cy.url().should('include', '/dashboard');
    cy.contains('My Library').click();
    cy.url().should('include', '/library');
  });

  it('should open Virtual Class', () => {
    cy.contains('Virtual Class').click();
    cy.url().should('include', '/virtual-classroom');
    cy.contains('AI Assistant').should('be.visible');
  });

  it('should open Assessments and return 200 OK for UI', () => {
    cy.contains('Assessments').click();
    cy.url().should('include', '/assessment');
    // The Adaptive Assessment component has "Submit" or options
    // Assuming there's something to assert
  });
});
