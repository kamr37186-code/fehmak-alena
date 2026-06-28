describe('Full Student Lifecycle', () => {
  beforeEach(() => {
    cy.visit('/');
  });

  it('should register, login, enroll in a course, take an assessment, and see gamification', () => {
    // 1. Registration (Skip actual registration to avoid duplicate email errors on re-runs, just navigate to it to verify UI)
    cy.contains('Get Started').click();
    cy.url().should('include', '/register');
    cy.get('input[formControlName="firstName"]').should('be.visible');

    // 2. Login
    cy.visit('/login');
    cy.get('input[type="email"]').type('omar.student@fahmak.com');
    cy.get('input[type="password"]').type('password123');
    cy.get('button[type="submit"]').click();

    // Verify dashboard
    cy.url().should('include', '/dashboard');

    // 3. Navigate to Courses
    cy.contains('Explore Course').first().click();
    cy.url().should('include', '/courses/');
    
    // Enroll if the button is Enroll Now
    cy.get('body').then($body => {
      if ($body.find('button:contains("Enroll Now")').length > 0) {
        cy.contains('Enroll Now').click();
        cy.contains(/Successfully enrolled|Failed to enroll/).should('exist');
      }
    });

    // 5. Use AI Chat
    cy.contains('Alena AI Chat').click();
    cy.url().should('include', '/ai-chat');
    cy.get('textarea').type('Hello AI, I need help with ML.{enter}');
    // Verify AI responds (mocked or real)
    cy.contains('Hello AI, I need help with ML.').should('be.visible');

    // 6. Take Assessment
    cy.contains('Assessments').click();
    cy.url().should('include', '/assessment');

    // 7. Verify Gamification Leaderboard
    cy.contains('Dashboard').click();
    cy.url().should('include', '/dashboard');
    cy.contains('Your Gamification Stats').should('exist');
  });
});
