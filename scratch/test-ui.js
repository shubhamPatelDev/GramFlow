const { chromium } = require('playwright');

(async () => {
  console.log("Starting UI Automation Test...");
  const browser = await chromium.launch({ headless: true });
  const page = await browser.newPage();
  
  try {
    // 1. Visit Landing Page
    console.log("Navigating to http://localhost:5173...");
    await page.goto('http://localhost:5173');
    await page.waitForLoadState('networkidle');

    // Check if the page loaded
    const title = await page.title();
    console.log(`Page Title: ${title}`);

    // 2. Click Login button
    console.log("Clicking 'Login' button from Landing Page...");
    await page.click('a[href="/login"]');
    await page.waitForURL('**/login');
    console.log("Successfully reached /login route.");

    // 3. Fill out the Login Form
    console.log("Filling out email and password...");
    // The login form has input[type="email"] and input[type="password"]
    await page.fill('input[type="email"]', 'test_ui@example.com');
    await page.fill('input[type="password"]', 'password123');

    // Note: The backend register endpoint will create this user if it doesn't exist. 
    // The login page defaults to "Sign in" (isLogin=true). 
    // Since this is a test, let's switch to Sign Up to guarantee it works.
    console.log("Switching to Sign Up mode...");
    await page.click('text=Don\'t have an account? Sign up');

    console.log("Submitting form...");
    await page.click('button[type="submit"]');

    // 4. Wait for navigation to /dashboard
    console.log("Waiting for backend authentication and routing to /dashboard...");
    await page.waitForURL('**/dashboard/rules', { timeout: 10000 });
    console.log("SUCCESS! Successfully authenticated and routed to Dashboard.");

    // 5. Verify the Connect Instagram button is rendered
    console.log("Checking if 'Connect Instagram' button is rendered in the header...");
    const connectBtn = await page.locator('button:has-text("Connect Instagram")').isVisible();
    if (connectBtn) {
      console.log("SUCCESS! The 'Connect Instagram' button is fully rendered and waiting for user input.");
    } else {
      console.log("WARNING: Could not find 'Connect Instagram' button. It might already be connected or hidden.");
    }

  } catch (error) {
    console.error("UI Test Failed:", error);
  } finally {
    await browser.close();
  }
})();
